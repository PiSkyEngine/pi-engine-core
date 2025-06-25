/*
 * MIT License
 * 
 * Copyright (c) 2025 Sly Technologies Inc
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.piengine.core.plugin.registry;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.StructuredTaskScope;
import java.util.stream.Stream;

import org.piengine.core.engine.impl.EngineContextBuilder;
import org.piengine.core.plugin.Plugin;
import org.piengine.core.plugin.PluginId;
import org.piengine.core.plugin.PluginLifecycle;
import org.piengine.core.plugin.PluginMetadata;

/**
 * Manages the discovery, loading, and lifecycle of plugins in the PIEngine framework.
 * <p>
 * The {@code PluginRegistry} interface provides methods to find, retrieve, load, and unload
 * plugins, which are instances of classes implementing the {@link Plugin} interface. It is
 * responsible for orchestrating plugin lifecycle callbacks defined in {@link PluginLifecycle},
 * such as {@link PluginLifecycle#initialize(EngineContextBuilder)} and {@link PluginLifecycle#shutdown()},
 * when plugins are loaded or unloaded in response to user actions. User-initiated state
 * changes, such as pausing or resuming plugins, are performed via {@link Plugin#pause()} and
 * {@link Plugin#unpause()}, which trigger corresponding lifecycle callbacks.
 * </p>
 * <p>
 * Plugins are typically developed by extending the {@code BasePlugin} class, which implements
 * both {@link Plugin} and {@link PluginLifecycle} interfaces, providing default lifecycle
 * method implementations. The registry identifies plugins using {@link PluginId} and supports
 * querying plugins by their metadata, type, or Maven-style identifier patterns (e.g.,
 * "org.example:*"). It also provides methods to list configured, local, online, and loaded
 * plugins, enabling discovery within the application's ecosystem.
 * </p>
 * <p>
 * Example usage:
 * 
 * <pre>
 * PluginRegistry registry = PluginRegistry.newInstance();
 * 
 * // List all available plugins
 * List<PluginMetadata> plugins = registry.listAllPlugins();
 * plugins.forEach(p -> System.out.println(p.pluginId()));
 * 
 * // Load a plugin
 * MyPlugin plugin = registry.loadPlugin(MyPlugin.class, "org.example:my-plugin:1.0.0");
 * // Triggers PluginLifecycle#initialize and #start
 * 
 * // Pause the plugin
 * plugin.pause(); // Triggers PluginLifecycle#pause
 * 
 * // Unload the plugin
 * registry.unloadPlugin(plugin); // Triggers PluginLifecycle#shutdown
 * </pre>
 * </p>
 *
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 * @see Plugin
 * @see PluginLifecycle
 * @see PluginId
 * @see PluginMetadata
 * @see PluginVerifier
 * @since 1.0
 */
public interface PluginRegistry {

    /**
     * Creates a new instance of the plugin registry.
     * <p>
     * This factory method returns a default implementation of the {@code PluginRegistry},
     * typically a {@link LocalPluginRegistry}, configured to manage plugins within the
     * PIEngine framework. The instance is ready to discover, load, and manage plugins.
     * </p>
     *
     * @return a new {@code PluginRegistry} instance
     */
    static PluginRegistry newInstance() {
        return new LocalPluginRegistry();
    }

    /**
     * Checks if an online plugin registry, such as a marketplace, is available for the
     * application's ecosystem and current configuration.
     * <p>
     * This method determines whether the system is configured to access an online plugin
     * registry (e.g., a marketplace or remote repository) for discovering and loading
     * plugins. It checks the application's configuration and network connectivity to the
     * online registry. If an online registry is available, methods like
     * {@link #listAllOnlinePlugins(String...)} and {@link #listAllPlugins(String...)} can
     * include online plugins in their results. If unavailable, those methods will rely
     * solely on local plugins.
     * </p>
     * <p>
     * Example:
     * 
     * <pre>
     * PluginRegistry registry = PluginRegistry.newInstance();
     * if (registry.hasPluginRegistryOnline()) {
     *     System.out.println("Online plugin registry is available");
     *     List<PluginMetadata> online = registry.listAllOnlinePlugins();
     * } else {
     *     System.out.println("Only local plugins are available");
     * }
     * </pre>
     * </p>
     *
     * @return {@code true} if an online plugin registry is available, {@code false} otherwise
     */
    boolean hasPluginRegistryOnline();

    /**
     * Lists all plugins available locally and online.
     * <p>
     * This method combines the results of {@link #listAllLocalPlugins(String...)} and
     * {@link #listAllOnlinePlugins(String...)} to return a list of {@link PluginMetadata}
     * for all plugins available for loading in the application's ecosystem. If an online
     * plugin registry is available, as determined by {@link #hasPluginRegistryOnline()},
     * it includes both local and online plugins; otherwise, it returns only local plugins.
     * The optional pattern parameter filters results based on Maven-style plugin identifiers
     * (e.g., "org.example:*"). If no patterns are provided, all available plugins are returned.
     * </p>
     * <p>
     * The method uses a {@link StructuredTaskScope.ShutdownOnFailure} to concurrently fetch
     * local and online plugin lists, ensuring efficient execution. The results are merged,
     * with duplicates removed based on plugin identity.
     * </p>
     * <p>
     * Example:
     * 
     * <pre>
     * PluginRegistry registry = PluginRegistry.newInstance();
     * try {
     *     List<PluginMetadata> all = registry.listAllPlugins("org.example:*");
     *     all.forEach(p -> System.out.println("Available plugin: " + p.pluginId()));
     * } catch (InterruptedException e) {
     *     System.err.println("Plugin listing interrupted: " + e.getMessage());
     * }
     * </pre>
     * </p>
     *
     * @param pattern optional Maven-style patterns to filter plugin identifiers
     * @return a list of metadata for all available plugins
     * @throws InterruptedException if the operation is interrupted while fetching plugin lists
     */
    @SuppressWarnings("preview")
	default List<PluginMetadata> listAllPlugins(String... pattern) throws InterruptedException {
        if (!hasPluginRegistryOnline())
            return listAllLocalPlugins(pattern);

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var local = scope.fork(() -> listAllLocalPlugins(pattern));
            var online = scope.fork(() -> listAllOnlinePlugins(pattern));

            scope.join();

            return Stream.concat(local.get().stream(), online.get().stream())
                    .distinct()
                    .toList();
        }
    }

    /**
     * Finds a plugin's metadata by its identifiers without loading it.
     * <p>
     * This method searches for a plugin's metadata matching the specified Maven-style
     * {@link PluginId} strings (e.g., "org.example:my-plugin"). If found, it returns the
     * {@link PluginMetadata} wrapped in an {@link Optional}; otherwise, it returns
     * {@link Optional#empty()}. This method does not load the plugin or trigger any
     * {@link PluginLifecycle} callbacks, making it suitable for querying plugin
     * availability without affecting system state.
     * </p>
     * <p>
     * Example:
     * 
     * <pre>
     * PluginRegistry registry = PluginRegistry.newInstance();
     * Optional<PluginMetadata> metadata = registry.findPlugin("org.example:my-plugin");
     * metadata.ifPresent(m -> System.out.println("Found plugin: " + m.pluginId()));
     * </pre>
     * </p>
     *
     * @param pluginIds optional Maven-style plugin identifiers (e.g., "org.example:my-plugin")
     * @return an {@link Optional} containing the plugin's metadata if found, or empty if not
     */
    Optional<PluginMetadata> findPlugin(String... pluginIds);

    /**
     * Lists plugins configured locally but not necessarily available on the module path.
     * <p>
     * This method returns a list of {@link PluginMetadata} for plugins that are configured
     * in the system (e.g., in configuration files) but may not be present on the module
     * path. The optional pattern parameter filters results based on Maven-style plugin
     * identifiers (e.g., "org.example:*"). If no patterns are provided, all configured
     * plugins are returned.
     * </p>
     * <p>
     * Example:
     * 
     * <pre>
     * PluginRegistry registry = PluginRegistry.newInstance();
     * List<PluginMetadata> configured = registry.listConfiguredPlugins("org.example:*");
     * configured.forEach(p -> System.out.println(p.pluginId()));
     * </pre>
     * </p>
     *
     * @param pattern optional Maven-style patterns to filter plugin identifiers
     * @return a list of metadata for configured plugins
     */
    List<PluginMetadata> listConfiguredPlugins(String... pattern);

    /**
     * Lists all plugins available locally on the module path.
     * <p>
     * This method returns a list of {@link PluginMetadata} for plugins found on the module
     * path, representing locally available plugins. The optional pattern parameter filters
     * results based on Maven-style plugin identifiers (e.g., "*:my-plugin"). If no patterns
     * are provided, all local plugins are returned.
     * </p>
     * <p>
     * Example:
     * 
     * <pre>
     * PluginRegistry registry = PluginRegistry.newInstance();
     * List<PluginMetadata> local = registry.listAllLocalPlugins();
     * local.forEach(p -> System.out.println("Local plugin: " + p.pluginId()));
     * </pre>
     * </p>
     *
     * @param pattern optional Maven-style patterns to filter plugin identifiers
     * @return a list of metadata for local plugins
     */
    List<PluginMetadata> listAllLocalPlugins(String... pattern);

    /**
     * Lists all plugins available online, including marketplace plugins.
     * <p>
     * This method returns a list of {@link PluginMetadata} for plugins available online,
     * including those in a configured marketplace, if applicable. The optional pattern
     * parameter filters results based on Maven-style plugin identifiers. If no patterns are
     * provided, all online plugins are returned. This method may involve network operations
     * to query remote repositories.
     * </p>
     * <p>
     * Example:
     * 
     * <pre>
     * PluginRegistry registry = PluginRegistry.newInstance();
     * List<PluginMetadata> online = registry.listAllOnlinePlugins("com.acme:*");
     * online.forEach(p -> System.out.println("Online plugin: " + p.pluginId()));
     * </pre>
     * </p>
     *
     * @param pattern optional Maven-style patterns to filter plugin identifiers
     * @return a list of metadata for online plugins
     */
    List<PluginMetadata> listAllOnlinePlugins(String... pattern);

    /**
     * Lists all currently loaded plugins.
     * <p>
     * This method returns a list of {@link Plugin} instances for all plugins currently
     * loaded in the system. The optional pattern parameter filters results based on
     * Maven-style plugin identifiers. If no patterns are provided, all loaded plugins are
     * returned. This method is useful for inspecting the current state of the plugin system.
     * </p>
     * <p>
     * Example:
     * 
     * <pre>
     * PluginRegistry registry = PluginRegistry.newInstance();
     * List<Plugin> loaded = registry.listLoadedPlugins("org.example:*");
     * loaded.forEach(p -> System.out.println("Loaded plugin: " + p.pluginId()));
     * </pre>
     * </p>
     *
     * @param pattern optional Maven-style patterns to filter plugin identifiers
     * @return a list of currently loaded plugins
     */
    List<Plugin> listLoadedPlugins(String... pattern);

    /**
     * Retrieves the metadata of a loaded plugin by its identifiers.
     * <p>
     * This method returns the {@link PluginMetadata} of a plugin matching the specified
     * Maven-style {@link PluginId} strings. If the plugin is not already loaded, it throws
     * a {@link PluginNotFound} exception. This method does not trigger any
     * {@link PluginLifecycle} callbacks, as it only accesses existing plugins.
     * </p>
     * <p>
     * Example:
     * 
     * <pre>
     * PluginRegistry registry = PluginRegistry.newInstance();
     * try {
     *     PluginMetadata metadata = registry.getPlugin("org.example:my-plugin");
     *     System.out.println("Plugin version: " + metadata.getVersion());
     * } catch (PluginNotFound e) {
     *     System.err.println("Plugin not found: " + e.getMessage());
     * }
     * </pre>
     * </p>
     *
     * @param pluginIds optional Maven-style plugin identifiers (e.g., "org.example:my-plugin")
     * @return the metadata of the matching plugin
     * @throws PluginNotFound if the plugin is not loaded
     */
    PluginMetadata getPlugin(String... pluginIds) throws PluginNotFound;

    /**
     * Loads a plugin by its type and optional identifiers.
     * <p>
     * This method loads a plugin matching the specified type and optional Maven-style
     * {@link PluginId} strings, invoking {@link PluginLifecycle#initialize(EngineContextBuilder)} and
     * {@link PluginLifecycle#start()} callbacks during the process. If the plugin is
     * already loaded, it returns the existing instance. If the plugin cannot be found or
     * loaded, it throws a {@link PluginNotFound} exception. If the plugin fails
     * verification (e.g., due to an invalid signature), a {@link PluginVerificationFailure}
     * is thrown.
     * </p>
     * <p>
     * Example:
     * 
     * <pre>
     * PluginRegistry registry = PluginRegistry.newInstance();
     * try {
     *     MyPlugin plugin = registry.loadPlugin(MyPlugin.class, "org.example:my-plugin:1.0.0");
     *     System.out.println("Loaded plugin: " + plugin.pluginId());
     * } catch (PluginNotFound | PluginVerificationFailure e) {
     *     System.err.println("Failed to load plugin: " + e.getMessage());
     * }
     * </pre>
     * </p>
     *
     * @param <T>        the plugin type
     * @param pluginType the class of the plugin to load
     * @param pluginIds  optional Maven-style plugin identifiers (e.g., "org.example:my-plugin")
     * @return the loaded plugin
     * @throws PluginNotFound          if the plugin cannot be found or loaded
     * @throws PluginVerificationFailure if the plugin fails verification
     */
    <T extends Plugin> T loadPlugin(Class<T> pluginType, String... pluginIds) throws PluginNotFound,
            PluginVerificationFailure;

    /**
     * Loads a plugin by its type and metadata.
     * <p>
     * This method loads a plugin matching the specified type and {@link PluginMetadata},
     * invoking {@link PluginLifecycle#initialize(EngineContextBuilder)} and
     * {@link PluginLifecycle#start()} callbacks. If the plugin cannot be loaded (e.g., due
     * to missing dependencies or invalid metadata), it throws a {@link PluginNotFound}
     * exception. If the plugin fails verification (e.g., due to an invalid signature), a
     * {@link PluginVerificationFailure} is thrown. This method is useful when metadata is
     * obtained from methods like {@link #findPlugin(String...)} or
     * {@link #listAllPlugins(String...)}.
     * </p>
     * <p>
     * Example:
     * 
     * <pre>
     * PluginRegistry registry = PluginRegistry.newInstance();
     * Optional<PluginMetadata> metadata = registry.findPlugin("org.example:my-plugin");
     * if (metadata.isPresent()) {
     *     MyPlugin plugin = registry.loadPlugin(MyPlugin.class, metadata.get());
     *     System.out.println("Loaded plugin: " + plugin.pluginId());
     * }
     * </pre>
     * </p>
     *
     * @param <T>            the plugin type
     * @param pluginType     the class of the plugin to load
     * @param pluginMetaData the metadata of the plugin to load
     * @return the loaded plugin
     * @throws PluginNotFound          if the plugin cannot be loaded
     * @throws PluginVerificationFailure if the plugin fails verification
     */
    <T extends Plugin> T loadPlugin(Class<T> pluginType, PluginMetadata pluginMetaData) throws PluginNotFound,
            PluginVerificationFailure;

    /**
     * Unloads a plugin and its unused dependencies.
     * <p>
     * This method unloads the specified plugin, invoking the
     * {@link PluginLifecycle#shutdown()} callback to release resources and terminate
     * dependencies. Unused dependencies may also be unloaded. This method is typically
     * called when a user requests to remove a plugin from the system.
     * </p>
     * <p>
     * Example:
     * 
     * <pre>
     * PluginRegistry registry = PluginRegistry.newInstance();
     * MyPlugin plugin = registry.loadPlugin(MyPlugin.class, "org.example:my-plugin");
     * registry.unloadPlugin(plugin); // Triggers PluginLifecycle#shutdown
     * </pre>
     * </p>
     *
     * @param plugin the plugin to unload
     * @throws NullPointerException if the plugin is null
     */
    void unloadPlugin(Plugin plugin);

    /**
     * Retrieves the plugin verifier used by this registry.
     * <p>
     * This method returns the {@link PluginVerifier} instance responsible for validating
     * the authenticity and integrity of plugins loaded by this registry. The verifier is
     * used during plugin loading to check digital signatures, as specified in the plugin's
     * {@link PluginMetadata}.
     * </p>
     *
     * @return the {@link PluginVerifier} used by this registry
     */
    PluginVerifier getPluginVerifier();
}
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
package org.piengine.core.plugin.registry.spi;

import org.piengine.core.plugin.HasPluginId;
import org.piengine.core.plugin.Plugin;
import org.piengine.core.plugin.PluginFactory;
import org.piengine.core.plugin.PluginId;
import org.piengine.core.plugin.PluginMetadata;

/**
 * Service provider interface for creating and managing plugins in the PIEngine
 * framework.
 * <p>
 * The {@code PluginService} interface defines the contract for service
 * providers that instantiate {@link Plugin} instances and provide their
 * associated metadata. It is used by the
 * {@link org.piengine.core.plugin.registry.PluginRegistry} to discover and load
 * plugins via the Java Service Provider Interface (SPI). Implementations, such
 * as the nested {@link PluginProvider} class, supply a {@link PluginFactory} to
 * create plugins and a {@link PluginMetadata} instance to describe the plugin's
 * identity and attributes.
 * </p>
 * <p>
 * This interface extends {@link HasPluginId} to provide access to the plugin's
 * {@link PluginId}, which is derived from the plugin's metadata. The default
 * implementation of {@link #pluginId()} retrieves the identifier from the
 * metadata.
 * </p>
 * <p>
 * Example usage in a service provider configuration (e.g.,
 * {@code META-INF/services/org.piengine.core.plugin.registry.spi.PluginService}):
 * 
 * <pre>
 * org.example.MyPluginProvider
 * </pre>
 * 
 * Example implementation:
 * 
 * <pre>
 * public class MyPluginProvider implements PluginService {
 * 	private final PluginMetadata metadata;
 * 	private final PluginFactory<MyPlugin> factory;
 * 
 * 	public MyPluginProvider() {
 * 		this.metadata = new PluginMetadata(new PluginId("org.example:my-plugin:1.0.0"), 0);
 * 		this.factory = context -> new MyPlugin();
 * 	}
 * 
 * 	&#064;Override
 * 	public PluginMetadata pluginMetadata() {
 * 		return metadata;
 * 	}
 * 
 * 	&#064;Override
 * 	public Plugin newPlugin() {
 * 		return factory.createPlugin(metadata);
 * 	}
 * }
 * </pre>
 * </p>
 *
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 * @see Plugin
 * @see PluginFactory
 * @see PluginMetadata
 * @see org.piengine.core.plugin.registry.PluginRegistry
 * @since 1.0
 */
public interface PluginService extends HasPluginId {

	/**
	 * Default extensible base implementation of a plugin service provider.
	 * <p>
	 * This class provides a concrete base implementation of {@link PluginService},
	 * encapsulating a {@link PluginMetadata} instance and a {@link PluginFactory}
	 * to create plugin instances. It is typically used in the Java Service Provider
	 * Interface (SPI) to register plugins with the
	 * {@link org.piengine.core.plugin.registry.PluginRegistry}.
	 * </p>
	 */
	abstract class PluginProvider implements PluginService {

		private final PluginMetadata pluginMetadata;
		private final PluginFactory<?> factory;

		/**
		 * Constructs a new plugin provider with the specified metadata and factory.
		 *
		 * @param pluginMetadata the metadata describing the plugin
		 * @param factory        the factory used to create plugin instances
		 * @throws NullPointerException if either {@code pluginMetadata} or
		 *                              {@code factory} is null
		 */
		public PluginProvider(PluginMetadata pluginMetadata, PluginFactory<?> factory) {
			this.pluginMetadata = pluginMetadata;
			this.factory = factory;
		}

		/**
		 * Creates a new plugin instance using the associated factory.
		 * <p>
		 * This method invokes the {@link PluginFactory#createPlugin(PluginMetadata)}
		 * method to instantiate a {@link Plugin} with the provider's metadata. The
		 * created plugin is ready for initialization and use by the
		 * {@link org.piengine.core.plugin.registry.PluginRegistry}.
		 * </p>
		 *
		 * @return a new {@link Plugin} instance
		 */
		@Override
		public final Plugin newPlugin() {
			return factory.createPlugin(pluginMetadata);
		}

		/**
		 * Retrieves the metadata associated with this plugin provider.
		 * <p>
		 * This method returns the {@link PluginMetadata} that describes the plugin,
		 * including its {@link PluginId}, version, and other attributes. The metadata
		 * is used by the {@link org.piengine.core.plugin.registry.PluginRegistry} to
		 * identify and manage the plugin.
		 * </p>
		 *
		 * @return the {@link PluginMetadata} for this plugin
		 */
		@Override
		public final PluginMetadata pluginMetadata() {
			return pluginMetadata;
		}

		/**
		 * Retrieves the plugin's unique identifier.
		 * <p>
		 * This default implementation returns the {@link PluginId} from the plugin's
		 * metadata, as defined by {@link #pluginMetadata()}.
		 * </p>
		 *
		 * @return the {@link PluginId} of the plugin
		 */
		@Override
		public PluginId pluginId() {
			return pluginMetadata().pluginId();
		}

		/**
		 * Retrieves the priority of the plugin.
		 * <p>
		 * This default implementation returns the priority from the plugin's metadata,
		 * as defined by {@link PluginMetadata#priority()}. The priority determines the
		 * order in which plugins are loaded or processed by the
		 * {@link org.piengine.core.plugin.registry.PluginRegistry}.
		 * </p>
		 *
		 * @return the priority of the plugin
		 */
		@Override
		public int priority() {
			return pluginMetadata().priority();
		}
	}

	/**
	 * Retrieves the plugin's unique identifier.
	 * <p>
	 * This default implementation returns the {@link PluginId} from the plugin's
	 * metadata, as provided by {@link #pluginMetadata()}. The identifier follows a
	 * Maven-style coordinate format (e.g., "org.example:my-plugin:1.0.0").
	 * </p>
	 *
	 * @return the {@link PluginId} of the plugin
	 */
	@Override
	default PluginId pluginId() {
		return pluginMetadata().pluginId();
	}

	/**
	 * Retrieves the metadata associated with this plugin service.
	 * <p>
	 * This method returns the {@link PluginMetadata} that describes the plugin,
	 * including its {@link PluginId}, version, priority, and other attributes. The
	 * metadata is used by the
	 * {@link org.piengine.core.plugin.registry.PluginRegistry} to identify and
	 * manage the plugin.
	 * </p>
	 *
	 * @return the {@link PluginMetadata} for this plugin
	 */
	PluginMetadata pluginMetadata();

	/**
	 * Creates a new plugin instance.
	 * <p>
	 * This method instantiates a new {@link Plugin} instance for this service
	 * provider. The created plugin is ready for initialization and use by the
	 * {@link org.piengine.core.plugin.registry.PluginRegistry}. Implementations
	 * typically use a {@link PluginFactory} to create the plugin instance.
	 * </p>
	 *
	 * @return a new {@link Plugin} instance
	 */
	Plugin newPlugin();

	/**
	 * Retrieves the priority of the plugin.
	 * <p>
	 * This default implementation returns the priority from the plugin's metadata,
	 * as defined by {@link PluginMetadata#priority()}. The priority determines the
	 * order in which plugins are loaded or processed by the
	 * {@link org.piengine.core.plugin.registry.PluginRegistry}. Higher priority
	 * values indicate earlier loading or processing.
	 * </p>
	 *
	 * @return the priority of the plugin
	 */
	default int priority() {
		return pluginMetadata().priority();
	}
}
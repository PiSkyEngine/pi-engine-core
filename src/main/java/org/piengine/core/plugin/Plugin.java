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
package org.piengine.core.plugin;

import org.piengine.core.plugin.registry.PluginRegistry;
import org.piengine.core.plugin.registry.PluginVerifier;
import org.piengine.util.Prioritizable;

/**
 * Defines the core interface for plugins in the PIEngine framework.
 * <p>
 * This interface provides methods for accessing a plugin's identity and
 * metadata, as well as user-initiated actions to pause or resume plugin
 * operations. Plugins implementing this interface are managed by the
 * {@link PluginRegistry} and typically extend the {@code BasePlugin} class,
 * which implements both this interface and {@link PluginLifecycle}. The methods
 * in this interface are intended for use by the game engine or end users to
 * interact with the plugin, while lifecycle methods in {@link PluginLifecycle}
 * serve as callbacks for the plugin to respond to state changes initiated by
 * the plugin manager.
 * </p>
 * <p>
 * The {@link #pause()} and {@link #unpause()} methods allow users to
 * temporarily suspend or resume plugin operations, respectively. These actions
 * trigger corresponding lifecycle callbacks ({@link PluginLifecycle#pause()}
 * and {@link PluginLifecycle#unpause()}) in the plugin to handle the state
 * change. Other lifecycle methods, such as initialization, starting, stopping,
 * and shutdown, are managed by the plugin manager and invoked as callbacks when
 * the plugin is loaded or unloaded in response to user requests via the
 * {@link PluginRegistry}.
 * </p>
 * <p>
 * Plugin developers should extend {@code BasePlugin} to inherit default
 * implementations of lifecycle methods and override them as needed to respond
 * to state changes. For example:
 * 
 * <pre>
 * public class MyPlugin extends BasePlugin {
 * 	public MyPlugin() {
 * 		super(new PluginId("org.example", "my-plugin"), new PluginMetadata("1.0.0"));
 * 	}
 * 
 * 	&#064;Override
 * 	public void pause() throws InterruptedException, IllegalStateException {
 * 		super.pause(); // Call default implementation
 * 		// Suspend plugin-specific operations
 * 		System.out.println("Plugin paused");
 * 	}
 * 
 * 	&#064;Override
 * 	public void initialize(EngineContextBuilder context) throws IllegalStateException {
 * 		super.initialize(context);
 * 		// Initialize plugin resources
 * 		System.out.println("Plugin initialized with context: " + context);
 * 	}
 * }
 * </pre>
 * </p>
 *
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 * @see PluginLifecycle
 * @see PluginRegistry
 * @see PluginId
 * @see PluginMetadata
 * @since 1.0
 */
public interface Plugin extends HasPluginId, Prioritizable {

	/**
	 * Retrieves the unique identifier of the plugin.
	 * <p>
	 * The plugin identifier follows a Maven-style coordinate format (e.g.,
	 * "org.example:my-plugin:1.0.0") as defined by the {@link PluginId} class. This
	 * identifier is used by the {@link PluginRegistry} to locate, manage, and
	 * filter plugins within the PIEngine framework.
	 * </p>
	 *
	 * @return the unique {@link PluginId} of the plugin
	 */
	@Override
	PluginId pluginId();

	/**
	 * Retrieves the metadata associated with the plugin.
	 * <p>
	 * The metadata includes information such as the plugin's version, digital
	 * signature, and other descriptive attributes. This information is used by the
	 * {@link PluginRegistry} and {@link PluginVerifier} to validate and manage the
	 * plugin, ensuring its authenticity and compatibility.
	 * </p>
	 *
	 * @return the {@link PluginMetadata} containing the plugin's metadata
	 */
	PluginMetadata getMetadata();

	/**
	 * Requests the plugin to pause its operations.
	 * <p>
	 * This method is intended for use by the game engine or end users to
	 * temporarily suspend the plugin's operations, transitioning it to the
	 * {@link PluginState#PAUSED} state. It triggers the
	 * {@link PluginLifecycle#pause()} callback, allowing the plugin to suspend
	 * worker threads, release non-critical resources, or perform other
	 * pause-related tasks. This method should only be called when the plugin is in
	 * the {@link PluginState#RUNNING} state.
	 * </p>
	 * <p>
	 * Plugin developers extending {@code BasePlugin} can override the corresponding
	 * {@link PluginLifecycle#pause()} method to implement custom pause behavior.
	 * </p>
	 *
	 * @throws InterruptedException  if the pause operation is interrupted
	 * @throws IllegalStateException if the plugin is not in the
	 *                               {@link PluginState#RUNNING} state
	 * @see PluginLifecycle#pause()
	 */
	void pause() throws InterruptedException, IllegalStateException;

	/**
	 * Requests the plugin to resume its operations.
	 * <p>
	 * This method is intended for use by the game engine or end users to resume a
	 * paused plugin, transitioning it from the {@link PluginState#PAUSED} state
	 * back to the {@link PluginState#RUNNING} state. It triggers the
	 * {@link PluginLifecycle#unpause()} callback, allowing the plugin to restore
	 * operations, resume worker threads, or reallocate resources. This method
	 * should only be called when the plugin is in the {@link PluginState#PAUSED}
	 * state.
	 * </p>
	 * <p>
	 * Plugin developers extending {@code BasePlugin} can override the corresponding
	 * {@link PluginLifecycle#unpause()} method to implement custom resume behavior.
	 * </p>
	 *
	 * @throws InterruptedException  if the unpause operation is interrupted
	 * @throws IllegalStateException if the plugin is not in the
	 *                               {@link PluginState#PAUSED} state
	 * @see PluginLifecycle#unpause()
	 */
	void unpause() throws InterruptedException, IllegalStateException;

	/**
	 * Starts the plugin, making its services immediately available.
	 * <p>
	 * This method transitions the plugin to the {@link PluginState#RUNNING} state
	 * by starting worker threads, allocating resources (e.g., memory tables, GPU
	 * resources), and activating services. It should only be called when the plugin
	 * is in the {@link PluginState#STOPPED} or {@link PluginState#SHUTDOWN} state
	 * after successful initialization.
	 * </p>
	 * <p>
	 * If the plugin is interrupted during startup (e.g., by thread interruption),
	 * an {@link InterruptedException} is thrown, and the plugin may be in an
	 * inconsistent state. If the plugin is not in a valid state, an
	 * {@link IllegalStateException} is thrown.
	 * </p>
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * public void start() throws InterruptedException, IllegalStateException {
	 * 	if (currentState != PluginState.STOPPED) {
	 * 		throw new IllegalStateException("Plugin must be in STOPPED state");
	 * 	}
	 * 	workerThread = new Thread(this::runTasks);
	 * 	workerThread.start();
	 * 	currentState = PluginState.RUNNING;
	 * }
	 * </pre>
	 * </p>
	 *
	 * @throws InterruptedException  if the plugin is interrupted during startup
	 * @throws IllegalStateException if the plugin is not in
	 *                               {@link PluginState#STOPPED} or
	 *                               {@link PluginState#SHUTDOWN} state
	 */
	void start() throws InterruptedException, IllegalStateException;

	/**
	 * Stops the plugin, terminating its services.
	 * <p>
	 * This method transitions the plugin to the {@link PluginState#STOPPED} state
	 * by stopping worker threads, releasing connections, and freeing runtime
	 * resources (e.g., GPU allocations). After stopping, the plugin's services are
	 * unavailable, but it can be restarted using {@link #start()} without
	 * reinitialization.
	 * </p>
	 * <p>
	 * This method should only be called when the plugin is in the
	 * {@link PluginState#RUNNING} state. If interrupted during stopping, an
	 * {@link InterruptedException} is thrown, and some resources may not be fully
	 * released. If the plugin is not in a valid state, an
	 * {@link IllegalStateException} is thrown.
	 * </p>
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * public void stop() throws InterruptedException, IllegalStateException {
	 * 	if (currentState != PluginState.RUNNING) {
	 * 		throw new IllegalStateException("Plugin must be in RUNNING state");
	 * 	}
	 * 	workerThread.interrupt();
	 * 	workerThread.join();
	 * 	releaseGpuResources();
	 * 	currentState = PluginState.STOPPED;
	 * }
	 * </pre>
	 * </p>
	 *
	 * @throws InterruptedException  if the plugin is interrupted while stopping
	 * @throws IllegalStateException if the plugin is not in
	 *                               {@link PluginState#RUNNING} state
	 */
	void stop() throws InterruptedException, IllegalStateException;
	
	PluginState state();
}
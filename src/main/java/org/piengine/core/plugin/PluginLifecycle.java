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

import org.piengine.core.engine.impl.EngineContextBuilder;

/**
 * Defines the lifecycle management interface for plugins in the PIEngine
 * framework.
 * <p>
 * This interface provides methods to control the lifecycle of a plugin,
 * including initialization, starting, stopping, pausing, unpausing, and
 * shutting down. Implementations of this interface are responsible for managing
 * plugin resources, dependencies, and state transitions in accordance with the
 * {@link PluginState} enum. Each method corresponds to a specific phase in the
 * plugin's lifecycle and may throw exceptions if called in an invalid state or
 * if operations are interrupted.
 * </p>
 * <p>
 * The lifecycle of a plugin typically follows this sequence:
 * <ol>
 * <li><b>Initialization</b>: The plugin is loaded, and its dependencies are
 * resolved via {@link #initialize(EngineContextBuilder)}.</li>
 * <li><b>Starting</b>: The plugin starts its services and allocates resources
 * via {@link #start()}.</li>
 * <li><b>Running</b>: The plugin is active and providing services (state:
 * {@link PluginState#RUNNING}).</li>
 * <li><b>Pausing/Unpausing</b>: The plugin may be temporarily paused
 * ({@link #pause()}) or resumed ({@link #unpause()}) to conserve resources
 * while maintaining state.</li>
 * <li><b>Stopping</b>: The plugin stops its services and releases resources via
 * {@link #stop()}.</li>
 * <li><b>Shutdown</b>: The plugin is fully terminated and unloaded via
 * {@link #shutdown()}.</li>
 * </ol>
 * </p>
 * <p>
 * Example usage:
 * 
 * <pre>
 * public class MyPlugin implements PluginLifecycle {
 * 	private EngineContextBuilder context;
 * 	private Thread workerThread;
 * 
 * 	&#064;Override
 * 	public void initialize(EngineContextBuilder context) throws IllegalStateException {
 * 		this.context = context;
 * 		// Load configuration and dependencies
 * 		System.out.println("Plugin initialized with context: " + context);
 * 	}
 * 
 * 	&#064;Override
 * 	public void start() throws InterruptedException, IllegalStateException {
 * 		workerThread = new Thread(() -> {
 * 			// Perform plugin tasks
 * 			System.out.println("Plugin running");
 * 		});
 * 		workerThread.start();
 * 	}
 * 
 * 	// Implement other lifecycle methods...
 * }
 * </pre>
 * </p>
 *
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 * @see PluginState
 * @see EngineContextBuilder
 * @since 1.0
 */
public interface PluginLifecycle {

	/**
	 * Initializes the plugin after its code has been loaded.
	 * <p>
	 * This method is called once after the plugin has been loaded, its metadata has
	 * been fetched, and all dependencies (such as other plugins) have been
	 * resolved. The plugin should use this opportunity to load resources, configure
	 * settings, and prepare for operation. The provided {@link EngineContextBuilder}
	 * offers access to the plugin's environment, including configuration data and
	 * dependency management.
	 * </p>
	 * <p>
	 * Implementations should validate the context and ensure the plugin is in the
	 * {@link PluginState#SHUTDOWN} state before proceeding. If the plugin is
	 * already initialized or in an invalid state, an {@link IllegalStateException}
	 * should be thrown.
	 * </p>
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * public void initialize(EngineContextBuilder context) throws IllegalStateException {
	 * 	if (context == null) {
	 * 		throw new IllegalStateException("Plugin context cannot be null");
	 * 	}
	 * 	this.config = context.getConfiguration();
	 * 	this.dependencies = context.getDependencies();
	 * 	System.out.println("Plugin initialized with config: " + config);
	 * }
	 * </pre>
	 * </p>
	 *
	 * @param context the plugin context providing access to configuration and
	 *                dependencies
	 * @throws IllegalStateException if the plugin is already initialized or in an
	 *                               invalid state
	 */
	void initialize(PluginContext context) throws IllegalStateException;

	/**
	 * Shuts down the plugin, releasing all resources and dependencies.
	 * <p>
	 * This method is called when the plugin is no longer needed and should be
	 * unloaded. It releases all resources (e.g., memory, files, network
	 * connections) and terminates any dependencies, which may themselves be shut
	 * down. After this method is called, the plugin is in the
	 * {@link PluginState#SHUTDOWN} state and cannot be restarted without
	 * reinitialization.
	 * </p>
	 * <p>
	 * This method is distinct from {@link #pause()}, which temporarily suspends
	 * plugin operations. Shutdown is a terminal operation, and the plugin should
	 * not expect to resume operation afterward. If the plugin is not initialized,
	 * an {@link IllegalStateException} should be thrown.
	 * </p>
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * public void shutdown() throws IllegalStateException {
	 * 	if (!isInitialized) {
	 * 		throw new IllegalStateException("Plugin is not initialized");
	 * 	}
	 * 	releaseResources();
	 * 	dependencies.clear();
	 * 	System.out.println("Plugin shut down");
	 * }
	 * </pre>
	 * </p>
	 *
	 * @throws IllegalStateException if the plugin is not initialized
	 */
	void shutdown() throws IllegalStateException;

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

	/**
	 * Pauses the plugin, temporarily suspending its operations.
	 * <p>
	 * This method transitions the plugin to the {@link PluginState#PAUSED} state,
	 * suspending worker threads and releasing non-critical resources while
	 * maintaining the plugin's state for quick resumption. It is useful for
	 * temporary suspension, such as during game state changes, and should only be
	 * called when the plugin is in the {@link PluginState#RUNNING} state.
	 * </p>
	 * <p>
	 * If interrupted during pausing, an {@link InterruptedException} is thrown, and
	 * the plugin may be in an inconsistent state. If the plugin is not in a valid
	 * state, an {@link IllegalStateException} is thrown.
	 * </p>
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * public void pause() throws InterruptedException, IllegalStateException {
	 * 	if (currentState != PluginState.RUNNING) {
	 * 		throw new IllegalStateException("Plugin must be in RUNNING state");
	 * 	}
	 * 	workerThread.suspend();
	 * 	currentState = PluginState.PAUSED;
	 * }
	 * </pre>
	 * </p>
	 *
	 * @throws InterruptedException  if the plugin is interrupted while pausing
	 * @throws IllegalStateException if the plugin is not in
	 *                               {@link PluginState#RUNNING} state
	 */
	void pause() throws InterruptedException, IllegalStateException;

	/**
	 * Resumes a paused plugin, restoring its operations.
	 * <p>
	 * This method transitions the plugin from the {@link PluginState#PAUSED} state
	 * back to the {@link PluginState#RUNNING} state, resuming worker threads and
	 * reallocating necessary resources. It should only be called when the plugin is
	 * in the {@link PluginState#PAUSED} state.
	 * </p>
	 * <p>
	 * If interrupted during resumption, an {@link InterruptedException} is thrown,
	 * and the plugin may be in an inconsistent state. Unlike other methods, this
	 * method does not throw an {@link IllegalStateException} to allow for more
	 * flexible error handling in resumption scenarios.
	 * </p>
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * public void unpause() throws InterruptedException {
	 * 	if (currentState != PluginState.PAUSED) {
	 * 		return; // Silently ignore if not paused
	 * 	}
	 * 	workerThread.resume();
	 * 	currentState = PluginState.RUNNING;
	 * }
	 * </pre>
	 * </p>
	 *
	 * @throws InterruptedException if the plugin is interrupted while resuming
	 */
	void unpause() throws InterruptedException;
}
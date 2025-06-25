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
package org.piengine.core.engine;

import org.piengine.core.plugin.PluginState;

/**
 * Interface defining the overall engine lifecycle as a state machine. Supports
 * multiple concurrent apps with start/stop/pause/unpause.
 */
public interface EngineLifecycle extends AutoCloseable {

	void initialize();

	/**
	 * @throws InterruptedException
	 * @throws IllegalStateException
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	void close() throws IllegalStateException, InterruptedException;

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

}
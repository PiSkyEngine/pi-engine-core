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
package org.piengine.core.execution;

import org.piengine.core.engine.impl.EngineContextBuilder;

/**
 * Defines the lifecycle management interface for tasks in the PIEngine
 * framework.
 * <p>
 * This interface provides methods to control the lifecycle of a task,
 * including initialization, starting, stopping, pausing, unpausing, and
 * shutting down. Implementations of this interface are responsible for managing
 * task resources, dependencies, and state transitions in accordance with the
 * {@link taskState} enum. Each method corresponds to a specific phase in the
 * task's lifecycle and may throw exceptions if called in an invalid state or
 * if operations are interrupted.
 * </p>
 * <p>
 * The lifecycle of a task typically follows this sequence:
 * <ol>
 * <li><b>Initialization</b>: The task is loaded, and its dependencies are
 * resolved via {@link #initialize(EngineContextBuilder)}.</li>
 * <li><b>Starting</b>: The task starts its services and allocates resources
 * via {@link #start()}.</li>
 * <li><b>Running</b>: The task is active and providing services (state:
 * {@link taskState#RUNNING}).</li>
 * <li><b>Pausing/Unpausing</b>: The task may be temporarily paused
 * ({@link #pause()}) or resumed ({@link #unpause()}) to conserve resources
 * while maintaining state.</li>
 * <li><b>Stopping</b>: The task stops its services and releases resources via
 * {@link #stop()}.</li>
 * <li><b>Shutdown</b>: The task is fully terminated and unloaded via
 * {@link #shutdown()}.</li>
 * </ol>
 * </p>
 * <p>
 * Example usage:
 * 
 * <pre>
 * public class Mytask implements taskLifecycle {
 * 	private EngineContextBuilder context;
 * 	private Thread workerThread;
 * 
 * 	&#064;Override
 * 	public void initialize(EngineContextBuilder context) throws IllegalStateException {
 * 		this.context = context;
 * 		// Load configuration and dependencies
 * 		System.out.println("task initialized with context: " + context);
 * 	}
 * 
 * 	&#064;Override
 * 	public void start() throws InterruptedException, IllegalStateException {
 * 		workerThread = new Thread(() -> {
 * 			// Perform task tasks
 * 			System.out.println("task running");
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
 * @see taskState
 * @see EngineContextBuilder
 * @since 1.0
 */
public interface TaskLifecycle {
	
	void initialize(TaskContext context);

	/**
	 * Shuts down the task, releasing all resources and dependencies.
	 * <p>
	 * This method is called when the task is no longer needed and should be
	 * unloaded. It releases all resources (e.g., memory, files, network
	 * connections) and terminates any dependencies, which may themselves be shut
	 * down. After this method is called, the task is in the
	 * {@link taskState#SHUTDOWN} state and cannot be restarted without
	 * reinitialization.
	 * </p>
	 * <p>
	 * This method is distinct from {@link #pause()}, which temporarily suspends
	 * task operations. Shutdown is a terminal operation, and the task should
	 * not expect to resume operation afterward. If the task is not initialized,
	 * an {@link IllegalStateException} should be thrown.
	 * </p>
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * public void shutdown() throws IllegalStateException {
	 * 	if (!isInitialized) {
	 * 		throw new IllegalStateException("task is not initialized");
	 * 	}
	 * 	releaseResources();
	 * 	dependencies.clear();
	 * 	System.out.println("task shut down");
	 * }
	 * </pre>
	 * </p>
	 *
	 * @throws IllegalStateException if the task is not initialized
	 */
	void shutdown() throws IllegalStateException;

	/**
	 * Starts the task, making its services immediately available.
	 * <p>
	 * This method transitions the task to the {@link taskState#RUNNING} state
	 * by starting worker threads, allocating resources (e.g., memory tables, GPU
	 * resources), and activating services. It should only be called when the task
	 * is in the {@link taskState#STOPPED} or {@link taskState#SHUTDOWN} state
	 * after successful initialization.
	 * </p>
	 * <p>
	 * If the task is interrupted during startup (e.g., by thread interruption),
	 * an {@link InterruptedException} is thrown, and the task may be in an
	 * inconsistent state. If the task is not in a valid state, an
	 * {@link IllegalStateException} is thrown.
	 * </p>
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * public void start() throws InterruptedException, IllegalStateException {
	 * 	if (currentState != taskState.STOPPED) {
	 * 		throw new IllegalStateException("task must be in STOPPED state");
	 * 	}
	 * 	workerThread = new Thread(this::runTasks);
	 * 	workerThread.start();
	 * 	currentState = taskState.RUNNING;
	 * }
	 * </pre>
	 * </p>
	 *
	 * @throws InterruptedException  if the task is interrupted during startup
	 * @throws IllegalStateException if the task is not in
	 *                               {@link taskState#STOPPED} or
	 *                               {@link taskState#SHUTDOWN} state
	 */
	void start() throws InterruptedException, IllegalStateException;

	/**
	 * Stops the task, terminating its services.
	 * <p>
	 * This method transitions the task to the {@link taskState#STOPPED} state
	 * by stopping worker threads, releasing connections, and freeing runtime
	 * resources (e.g., GPU allocations). After stopping, the task's services are
	 * unavailable, but it can be restarted using {@link #start()} without
	 * reinitialization.
	 * </p>
	 * <p>
	 * This method should only be called when the task is in the
	 * {@link taskState#RUNNING} state. If interrupted during stopping, an
	 * {@link InterruptedException} is thrown, and some resources may not be fully
	 * released. If the task is not in a valid state, an
	 * {@link IllegalStateException} is thrown.
	 * </p>
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * public void stop() throws InterruptedException, IllegalStateException {
	 * 	if (currentState != taskState.RUNNING) {
	 * 		throw new IllegalStateException("task must be in RUNNING state");
	 * 	}
	 * 	workerThread.interrupt();
	 * 	workerThread.join();
	 * 	releaseGpuResources();
	 * 	currentState = taskState.STOPPED;
	 * }
	 * </pre>
	 * </p>
	 *
	 * @throws InterruptedException  if the task is interrupted while stopping
	 * @throws IllegalStateException if the task is not in
	 *                               {@link taskState#RUNNING} state
	 */
	void stop() throws InterruptedException, IllegalStateException;

	/**
	 * Pauses the task, temporarily suspending its operations.
	 * <p>
	 * This method transitions the task to the {@link taskState#PAUSED} state,
	 * suspending worker threads and releasing non-critical resources while
	 * maintaining the task's state for quick resumption. It is useful for
	 * temporary suspension, such as during game state changes, and should only be
	 * called when the task is in the {@link taskState#RUNNING} state.
	 * </p>
	 * <p>
	 * If interrupted during pausing, an {@link InterruptedException} is thrown, and
	 * the task may be in an inconsistent state. If the task is not in a valid
	 * state, an {@link IllegalStateException} is thrown.
	 * </p>
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * public void pause() throws InterruptedException, IllegalStateException {
	 * 	if (currentState != taskState.RUNNING) {
	 * 		throw new IllegalStateException("task must be in RUNNING state");
	 * 	}
	 * 	workerThread.suspend();
	 * 	currentState = taskState.PAUSED;
	 * }
	 * </pre>
	 * </p>
	 *
	 * @throws InterruptedException  if the task is interrupted while pausing
	 * @throws IllegalStateException if the task is not in
	 *                               {@link taskState#RUNNING} state
	 */
	void pause() throws InterruptedException, IllegalStateException;

	/**
	 * Resumes a paused task, restoring its operations.
	 * <p>
	 * This method transitions the task from the {@link taskState#PAUSED} state
	 * back to the {@link taskState#RUNNING} state, resuming worker threads and
	 * reallocating necessary resources. It should only be called when the task is
	 * in the {@link taskState#PAUSED} state.
	 * </p>
	 * <p>
	 * If interrupted during resumption, an {@link InterruptedException} is thrown,
	 * and the task may be in an inconsistent state. Unlike other methods, this
	 * method does not throw an {@link IllegalStateException} to allow for more
	 * flexible error handling in resumption scenarios.
	 * </p>
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * public void unpause() throws InterruptedException {
	 * 	if (currentState != taskState.PAUSED) {
	 * 		return; // Silently ignore if not paused
	 * 	}
	 * 	workerThread.resume();
	 * 	currentState = taskState.RUNNING;
	 * }
	 * </pre>
	 * </p>
	 *
	 * @throws InterruptedException if the task is interrupted while resuming
	 */
	void unpause() throws InterruptedException;
}
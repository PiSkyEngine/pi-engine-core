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

package org.piengine.core.engine.dispatcher;

import org.piengine.core.Updatable;
import org.piengine.core.engine.EngineContext;
import org.piengine.util.Prioritizable;

/**
 * Defines a worker that can be registered with the PIEngine framework for
 * managed lifecycle and execution.
 * <p>
 * {@code EngineWorker} provides a contract for components that need to be
 * managed by the engine's worker system. The engine supports two distinct
 * execution modes for workers:
 * </p>
 * <ol>
 * <li><strong>Per-frame execution</strong>: If the worker also implements
 * {@link Updatable}, it will be called via the {@code updateFrame()} method on
 * each engine loop cycle</li>
 * <li><strong>Continuous execution</strong>: If the worker does not implement
 * {@code Updatable}, it will be assigned its own thread and the {@code run()}
 * method will be invoked</li>
 * </ol>
 * <p>
 * All workers are managed through a complete lifecycle with priority-based
 * ordering: {@code initialize() → start() → [execution] → stop() → shutdown()}.
 * The engine wraps each worker in an internal {@code ThreadedRunnableWorker} class that
 * handles thread management, including calling {@code Thread.interrupt()} when
 * {@code stop()} is invoked for continuous workers.
 * </p>
 * <p>
 * Common use cases include:
 * <ul>
 * <li><strong>Per-frame workers</strong>: Rendering systems, physics updates,
 * animation processing</li>
 * <li><strong>Continuous workers</strong>: Asset loading, network I/O,
 * background processing, audio streaming</li>
 * </ul>
 * </p>
 * <p>
 * Example per-frame worker (implements both interfaces):
 * 
 * <pre>
 * public class RenderWorker implements EngineWorker, Updatable {
 * 	&#64;Override
 * 	public UpdateStatus updateFrame(float tpf) {
 * 		// Called each frame by the engine
 * 		renderScene(tpf);
 * 		return UpdateStatus.COMPLETED;
 * 	}
 * 
 * 	&#64;Override
 * 	public void run() {
 * 		// Not called - updateFrame() is used instead
 * 	}
 * 
 * 	&#64;Override
 * 	public int priority() {
 * 		return 50;
 * 	}
 * }
 * </pre>
 * </p>
 * <p>
 * Example continuous worker (EngineWorker only):
 * 
 * <pre>
 * public class AssetLoaderWorker implements EngineWorker {
 * 	private volatile boolean running = false;
 * 
 * 	&#64;Override
 * 	public void start() {
 * 		running = true;
 * 	}
 * 
 * 	&#64;Override
 * 	public void run() {
 * 		// Runs in its own thread
 * 		while (running && !Thread.currentThread().isInterrupted()) {
 * 			try {
 * 				loadNextAsset();
 * 			} catch (InterruptedException e) {
 * 				Thread.currentThread().interrupt();
 * 				break;
 * 			}
 * 		}
 * 	}
 * 
 * 	&#64;Override
 * 	public void stop() {
 * 		running = false;
 * 		// PiEngine calls Thread.interrupt() on the worker thread
 * 	}
 * 
 * 	&#64;Override
 * 	public int priority() {
 * 		return 100;
 * 	}
 * }
 * </pre>
 * </p>
 *
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 * @see Updatable
 * @see EngineApp
 * @see EngineContext
 * @since 1.0
 */
public interface DispatchableWorker extends Prioritizable {

	/**
	 * Is work dispatch from multiple dispatchers allowed. A flag to indicate that
	 * multiple dispatchers are allowed to dispatch work, likely in different
	 * threads.
	 *
	 * @return true, if is multi displatch allowed
	 */
	boolean isMultiDisplatchAllowed();

	/**
	 * Performs final cleanup and resource deallocation.
	 * <p>
	 * This method is called during engine shutdown to ensure all worker resources
	 * are properly released. It should close files, release memory, disconnect from
	 * external services, and perform any other cleanup necessary to prevent
	 * resource leaks. This method is called after {@link #stop()} and represents
	 * the final lifecycle stage.
	 * </p>
	 * <p>
	 * Implementations should be defensive and handle partial initialization states,
	 * as shutdown may be called even if initialization or startup failed.
	 * </p>
	 */
	void shutdown();

	/**
	 * Initializes the worker with the provided engine context.
	 * <p>
	 * This method is called once during engine startup to provide the worker with
	 * access to engine services and configuration. Workers should prepare their
	 * internal state, validate configuration, and acquire any resources needed for
	 * operation, but should not start active processing until {@link #start()} is
	 * called.
	 * </p>
	 * <p>
	 * The engine context provides access to configuration, logging, asset
	 * management, and other engine subsystems that the worker may need.
	 * </p>
	 *
	 * @param context the engine context providing access to engine services
	 * @throws IllegalStateException if the worker cannot be properly initialized
	 */
	void initialize(EngineContext context, DispatcherContext dispatcherContext);

	/**
	 * Starts the worker, transitioning it to active state.
	 * <p>
	 * This method is called by the engine to activate the worker. For workers that
	 * also implement {@link Updatable}, this signals that per-frame updates will
	 * begin. For continuous workers, this prepares the worker for its
	 * {@link #run()} method to be invoked in a separate thread.
	 * </p>
	 * <p>
	 * This method should return quickly as it may be called from the main engine
	 * thread. The actual work execution occurs either through {@code updateFrame()}
	 * calls or in the {@code run()} method depending on the worker type.
	 * </p>
	 *
	 * @throws IllegalStateException if the worker is not in a valid state to start
	 */
	void start();

	/**
	 * Stops the worker's active processing.
	 * <p>
	 * This method signals the worker to cease active processing. For workers that
	 * implement {@link Updatable}, per-frame updates will stop. For continuous
	 * workers, the engine's internal {@code ThreadedRunnableWorker} wrapper will call
	 * {@code Thread.interrupt()} on the worker's thread, and the worker should
	 * handle this interruption gracefully to exit its {@link #run()} method.
	 * </p>
	 * <p>
	 * Continuous workers should check
	 * {@code Thread.currentThread().isInterrupted()} regularly and exit their run
	 * loop when interrupted. This method should return quickly and not block
	 * waiting for thread completion.
	 * </p>
	 *
	 * @throws IllegalStateException if the worker is not currently running
	 */
	void stop();
}
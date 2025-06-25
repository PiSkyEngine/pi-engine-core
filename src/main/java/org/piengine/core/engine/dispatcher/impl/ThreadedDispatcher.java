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

package org.piengine.core.engine.dispatcher.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

import org.piengine.core.Updatable;
import org.piengine.core.engine.EngineConfig;
import org.piengine.core.engine.EngineContext;
import org.piengine.core.engine.EngineRuntimeException;
import org.piengine.core.engine.EngineState;
import org.piengine.core.engine.EngineWorker;
import org.piengine.core.engine.dispatcher.BaseDispatcher;
import org.piengine.core.engine.dispatcher.DispatchableWorker;
import org.piengine.core.engine.dispatcher.DispatcherContext;
import org.piengine.core.engine.impl.EngineStateSupport;
import org.piengine.core.logging.PiEngineLogging;
import org.piengine.core.plugin.PluginMetadata;

/**
 * Multi-threaded plugin dispatcher.
 * 
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 */
public final class ThreadedDispatcher
		extends BaseDispatcher
		implements EngineDispatcher, EngineWorker {

	public static final PluginMetadata META = PluginMetadata.of("org.piengine:pi-engine-threaded-dispatcher");

	private static final Logger logger = PiEngineLogging.getLogger(ThreadedDispatcher.class);

	private final List<DispatchableWorker> runnableWorkers = new ArrayList<>();
	private final List<Thread> runnableThreads = new ArrayList<>();
	private Updatable[] updatableWorkers = new Updatable[0];
	private EngineStateSupport engineState;
	private Phaser workerPhaser;
	private DispatcherContext dispatcherContext;

	public ThreadedDispatcher(PluginMetadata meta) {
		super(meta);
	}

	public ThreadedDispatcher() {
		super(META);
	}

	private boolean addRunnableWorker(DispatchableWorker worker) {
		if (!(worker instanceof Runnable))
			return false;

		runnableWorkers.add(worker);

		return true;
	}

	private EngineRuntimeException errorMaxThreadCount(int maxCount) {
		return new EngineRuntimeException("too many workers for max [%d] number of threads"
				.formatted(maxCount));
	}

	private EngineRuntimeException errorMinThreadCount(int minCount) {
		return new EngineRuntimeException("too few workers for min [%d] number of threads"
				.formatted(minCount));
	}

	/**
	 * @see org.piengine.core.engine.EngineWorker#initialize(org.piengine.core.engine.EngineContext)
	 */
	@Override
	public void initialize(EngineContext context) {
		logger.finest("Initializing dispatcher");
		
		EngineConfig config = context.engineConfig();
		this.engineState = context.engineState();
		this.dispatcherContext = new DispatcherContext();
		this.workerPhaser = dispatcherContext.workerPhaser();
		
		this.workerPhaser.register();

		assert engineState.current() == EngineState.SHUTDOWN;

		ThreadFactory threadFactory = config.get("engine.threads.factory").asInstance();
		int minCount = config.get("engine.threads.min").asInt();
		int maxCount = config.get("engine.threads.max").asInt();

		if (runnableWorkers.size() < minCount)
			throw errorMinThreadCount(minCount);

		if (runnableWorkers.size() > maxCount)
			throw errorMaxThreadCount(maxCount);

		for (var worker : runnableWorkers) {
			if (!(worker instanceof Runnable runnable))
				throw new IllegalStateException("expecting a runnable worker");

			worker.initialize(context, dispatcherContext);

			var thread = threadFactory.newThread(runnable);

			runnableThreads.add(thread);
		}
		
		logger.finest("Initialization done");
	}

	/**
	 * @see org.piengine.core.engine.dispatcher.impl.EngineDispatcher#registerWorkers(java.util.List)
	 */
	@Override
	public List<DispatchableWorker> registerWorkers(List<DispatchableWorker> worker) {
		worker.forEach(this::addRunnableWorker);

		return Collections.unmodifiableList(runnableWorkers);
	}

	/**
	 * @see org.piengine.core.engine.EngineWorker#shutdown()
	 */
	@Override
	public void shutdown() {
		logger.finest("Shutting down");
		// Make this phaser unusable so we can replace it
		workerPhaser.forceTermination();

		runnableWorkers.forEach(DispatchableWorker::shutdown);
		runnableWorkers.clear();
		runnableThreads.clear();

		this.workerPhaser = null;
		this.dispatcherContext = null;
		logger.finest("Shut down");
	}

	/**
	 * @see org.piengine.core.engine.EngineWorker#start()
	 */
	@Override
	public void start() {
		logger.finest("Starting");
		assert engineState.current() == EngineState.STOPPED;
		assert workerPhaser.getRegisteredParties() >= 1;

		runnableThreads.forEach(Thread::start);
		workerPhaser.arriveAndAwaitAdvance();
		logger.finest("Started");
	}

	/**
	 * @see org.piengine.core.engine.EngineWorker#stop()
	 */
	@Override
	public void stop() {
		logger.finest("Stopping");
		assert engineState.current() == EngineState.STOPPED;
		assert workerPhaser.getRegisteredParties() >= 1;

		runnableThreads.forEach(Thread::interrupt);

		// Stop barrier, wait for all threads to stop
		workerPhaser.arriveAndAwaitAdvance();
		logger.finest("Stopped");
	}

	/**
	 * @return
	 * @see org.piengine.core.engine.dispatcher.impl.EngineDispatcher#updateFrame(float)
	 */
	@Override
	public void updateFrame(float tpf) {

		for (var worker : updatableWorkers) {
			worker.updateFrame(tpf);
		}
	}

	/**
	 * @see org.piengine.core.engine.dispatcher.impl.EngineDispatcher#registerWorker(org.piengine.core.engine.dispatcher.DispatchableWorker)
	 */
	@Override
	public boolean registerWorker(DispatchableWorker worker) {
		return addRunnableWorker(worker);
	}

}

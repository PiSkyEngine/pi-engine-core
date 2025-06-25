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

import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicReference;

import org.piengine.core.engine.EngineContext;
import org.piengine.core.engine.dispatcher.DispatchablePlugin;
import org.piengine.core.engine.dispatcher.DispatchableWorker;
import org.piengine.core.engine.dispatcher.DispatcherContext;

/**
 * The Class BaseThreadedWorker.
 */
public class BaseThreadedWorker implements DispatchableWorker {

	/** The worker phaser. */

	/** The atomic tpf. */
	private final AtomicReference<Float> atomicTpf = new AtomicReference<>();

	private final DispatchablePlugin plugin;

	private final Phaser barrier;

	/**
	 * Instantiates a new base threaded worker.
	 *
	 * @param priority the priority
	 */
	public BaseThreadedWorker(DispatchablePlugin plugin, Phaser barrier) {
		this.plugin = plugin;
		this.barrier = barrier;
	}

	/**
	 * @see org.piengine.core.engine.dispatcher.DispatchableWorker#initialize(org.piengine.core.engine.EngineContext,
	 *      org.piengine.core.engine.dispatcher.DispatcherContext)
	 */
	@Override
	public void initialize(EngineContext context, DispatcherContext dispatcherContext) {
		atomicTpf.set(0.f);
	}

	/**
	 * @see org.piengine.util.Prioritizable#priority()
	 */
	@Override
	public int priority() {
		return plugin.priority();
	}

	/**
	 * @see org.piengine.core.engine.dispatcher.DispatchableWorker#shutdown()
	 */
	@Override
	public void shutdown() {
		atomicTpf.set(0.f);
	}

	/**
	 * @see org.piengine.core.engine.dispatcher.DispatchableWorker#start()
	 */
	@Override
	public void start() {} // Nothing to do

	/**
	 * @see org.piengine.core.engine.dispatcher.DispatchableWorker#stop()
	 */
	@Override
	public void stop() {} // Nothing to do

	/**
	 * @see org.piengine.core.engine.dispatcher.DispatchableWorker#isMultiDisplatchAllowed()
	 */
	@Override
	public boolean isMultiDisplatchAllowed() {
		return false;
	}

}
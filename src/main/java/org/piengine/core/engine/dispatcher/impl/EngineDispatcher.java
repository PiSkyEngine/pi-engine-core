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

import java.util.List;

import org.piengine.core.Updatable;
import org.piengine.core.engine.dispatcher.DispatchableWorker;

/**
 * PiEngine work dispatcher. This interface defines dispatchers which the engine
 * uses to dispatch work to specific
 */
public interface EngineDispatcher extends Updatable {

	/**
	 * Register workers which are dispatchable by this dispatcher. The method
	 * returns a list of only the workers which are compatible with this dispatcher
	 * and which were registered.
	 *
	 * @param worker a list of workers which may or may not be compatible with this
	 *               dispatcher
	 * @return a list of workers which were registered
	 */
	List<DispatchableWorker> registerWorkers(List<DispatchableWorker> worker);
	
	/**
	 * Register worker.
	 *
	 * @param worker the worker
	 * @return true, if successful
	 */
	boolean registerWorker(DispatchableWorker worker);

	/**
	 * Main update loop.
	 *
	 * @param tpf time per frame as fraction of a second since last frame
	 */
	@Override
	void updateFrame(float tpf);
}

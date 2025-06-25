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

import org.piengine.core.engine.EngineContext;
import org.piengine.core.engine.EngineWorker;
import org.piengine.core.engine.dispatcher.impl.EngineDispatcher;
import org.piengine.core.plugin.BasePlugin;
import org.piengine.core.plugin.PluginContext;
import org.piengine.core.plugin.PluginMetadata;

/**
 * 
 *
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 */
public abstract class BaseDispatcher
		extends BasePlugin
		implements EngineDispatcher, EngineWorker {

	protected BaseDispatcher(PluginMetadata meta) {
		super(meta);
	}

	/**
	 * @see org.piengine.core.plugin.BasePlugin#initialize(org.piengine.core.plugin.PluginContext)
	 */
	@Override
	public final void initialize(PluginContext context) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see org.piengine.core.plugin.BasePlugin#initialize(org.piengine.core.engine.EngineContext)
	 */
	@Override
	public abstract void initialize(EngineContext context);

	/**
	 * @see org.piengine.core.plugin.BasePlugin#pause()
	 */
	@Override
	public void pause() throws InterruptedException, IllegalStateException {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.BasePlugin#shutdown()
	 */
	@Override
	public void shutdown() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.BasePlugin#start()
	 */
	@Override
	public void start() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.BasePlugin#stop()
	 */
	@Override
	public void stop() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.BasePlugin#unpause()
	 */
	@Override
	public void unpause() throws InterruptedException {
		throw new UnsupportedOperationException("not implemented yet");
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Dispatcher [" + pluginId() + ""
				+ ", impl=" + getClass().getSimpleName() + ".class"
				+ "]";
	}
}

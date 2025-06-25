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

public final class WorkerController implements PluginController {
	private PluginState current = PluginState.SHUTDOWN;
	private final PluginLifecycle target;
	private final PluginContext context;

	public WorkerController(PluginLifecycle target, PluginContext context) {
		this.target = target;
		this.context = context;
	}

	private IllegalStateException invalidState(PluginState oldState, PluginState newState) {
		return new IllegalStateException("invalid plugin state [%s]".formatted(newState));
	}

	@Override
	public String toString() {
		return current.name();
	}

	private void validate(PluginState state) throws IllegalStateException {
		if (current != state)
			throw invalidState(current, state);
	}

	private PluginState validateAndSet(PluginState newState) throws IllegalStateException {
		return switch (newState) {
		case PluginState.SHUTDOWN -> {
			if (current != PluginState.STOPPED)
				throw invalidState(current, newState);

			yield newState;
		}
		case PluginState.RUNNING -> {
			if (current != PluginState.STOPPED)
				throw invalidState(current, newState);

			yield newState;
		}
		case PluginState.STOPPED -> {
			if (current != PluginState.RUNNING && current != PluginState.SHUTDOWN)
				throw invalidState(current, newState);

			yield newState;
		}
		case PluginState.PAUSED -> {
			if (current != PluginState.RUNNING)
				throw invalidState(current, newState);

			yield newState;
		}
		};
	}

	/**
	 * @see org.piengine.core.plugin.PluginLifecycle#initialize(org.piengine.core.plugin.PluginContext)
	 */
	@Override
	public void initialize(PluginContext context) throws IllegalStateException {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.PluginLifecycle#shutdown()
	 */
	@Override
	public void shutdown() throws IllegalStateException {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.PluginLifecycle#start()
	 */
	@Override
	public void start() throws InterruptedException, IllegalStateException {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.PluginLifecycle#stop()
	 */
	@Override
	public void stop() throws InterruptedException, IllegalStateException {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.PluginLifecycle#pause()
	 */
	@Override
	public void pause() throws InterruptedException, IllegalStateException {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.PluginLifecycle#unpause()
	 */
	@Override
	public void unpause() throws InterruptedException {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.PluginController#state()
	 */
	@Override
	public PluginState state() {
		return current;
	}
}
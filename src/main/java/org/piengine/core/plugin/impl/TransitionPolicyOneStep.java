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

package org.piengine.core.plugin.impl;

import org.piengine.core.plugin.PluginContext;
import org.piengine.core.plugin.PluginLifecycle;
import org.piengine.core.plugin.PluginState;

/**
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 */
public class TransitionPolicyOneStep extends BaseTransitionStatePolicy {

	public static final String NAME = "one-step";

	/**
	 * @param name
	 */
	public TransitionPolicyOneStep() {
		super(NAME);
	}

	/**
	 * @see org.piengine.core.plugin.Policy#checkPolicy(org.piengine.core.plugin.PluginState,
	 *      org.piengine.core.plugin.PluginState)
	 */
	@Override
	public boolean checkPolicy(PluginState current, PluginState desired) {
		if (current == PluginState.SHUTDOWN && desired != PluginState.STOPPED)
			return false;

		if (current == PluginState.PAUSED && desired != PluginState.RUNNING)
			return false;

		int delta = Math.abs(desired.ordinal() - current.ordinal());

		return delta == 1;
	}

	/**
	 * @see org.piengine.core.plugin.Policy#transition(org.piengine.core.plugin.PluginState,
	 *      org.piengine.core.plugin.PluginState,
	 *      org.piengine.core.plugin.PluginLifecycle,
	 *      org.piengine.core.plugin.PluginContext)
	 */
	@Override
	public void transition(PluginState current, PluginState end, PluginLifecycle plugin, PluginContext context)
			throws InterruptedException, IllegalStateException {
		validatePolicy(current, end);

		switch (end) {
		case SHUTDOWN -> plugin.shutdown();
		case STOPPED -> plugin.initialize(context);
		case RUNNING -> plugin.start();
		case PAUSED -> plugin.pause();
		};
	}

	/**
	 * @see org.piengine.core.plugin.Policy#validatePolicy(org.piengine.core.plugin.PluginState,
	 *      org.piengine.core.plugin.PluginState)
	 */
	@Override
	public void validatePolicy(PluginState current, PluginState desired) throws IllegalStateException {
		if (!checkPolicy(current, desired))
			throw new IllegalStateException("invalid state change [%s \u02192] %s"
					.formatted(current, desired));
	}

}

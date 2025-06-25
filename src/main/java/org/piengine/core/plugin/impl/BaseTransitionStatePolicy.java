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

import org.piengine.core.plugin.PluginState.Policy;

/**
 * 
 *
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 */
public abstract class BaseTransitionStatePolicy implements Policy {

	/** Predefined policy allowing only single-step transitions. */
	public static final Policy ONE_STOP_TRANSITION = new TransitionPolicyOneStep();

	/** Predefined policy allowing direct jumps to any state. */
	public static final Policy JUMP_TO_TRANSITION = new TransitionPolicyJumpTo();

	/** Predefined policy requiring transitions through all intermediate states. */
	public static final Policy STEP_THROUGH_TRANSITION = new TransitionPolicyStepThrough();

	private final String name;

	protected BaseTransitionStatePolicy(String name) {
		this.name = name;
	}

	/**
	 * @see org.piengine.core.plugin.Policy#name()
	 */
	@Override
	public String name() {
		return this.name;
	}

	@Override
	public String toString() {
		return "Policy [" + name() + "]";
	}
}

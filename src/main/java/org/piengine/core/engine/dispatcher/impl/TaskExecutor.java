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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

import org.piengine.core.Updatable;
import org.piengine.core.engine.dispatcher.impl.TaskExecutor.MethodInvoker;

/**
 * 
 *
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 */
public sealed class TaskExecutor permits MethodInvoker {
	public static final class MethodInvoker extends TaskExecutor {
		private final Object target;
		private final Method method;

		public MethodInvoker(Method staticMethod) {
			assert (staticMethod.getModifiers() & Modifier.STATIC) != 0;

			this.target = null;
			this.method = staticMethod;

		}

		public MethodInvoker(Object target, Method dynamicMethod) {
			assert (dynamicMethod.getModifiers() & Modifier.STATIC) == 0;

			this.target = Objects.requireNonNull(target, "dynamic target");
			this.method = dynamicMethod;

		}

		public void run() {
			try {
				method.invoke(target);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new IllegalStateException(e);
			}
		}

		public void updateFrame(float tpf) {
			try {
				method.invoke(target, tpf);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	private final Runnable runnable;
	private final Updatable updatable;

	protected TaskExecutor() {
		this.updatable = null;
		this.runnable = null;
	}

	public TaskExecutor(Runnable runnable) {
		this.runnable = runnable;
		this.updatable = null;

	}

	public TaskExecutor(Updatable updatable) {
		this.updatable = updatable;
		this.runnable = null;

	}
	
	public void invoke() {
		
	}

	public Updatable updatable() {
		return updatable;
	}

	public Runnable runnable() {
		return runnable;
	}
}

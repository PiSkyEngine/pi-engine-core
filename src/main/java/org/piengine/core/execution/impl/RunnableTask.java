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

package org.piengine.core.execution.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.piengine.core.execution.TaskExecution;
import org.piengine.core.execution.TaskSchedule;

/**
 * 
 *
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 */
public final class RunnableTask extends BaseTask implements Runnable {

	private record MethodInvoker(Object instance, Method method, BuiltinTaskInjector injector) implements Runnable {

		/**
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				if (injector == null) {
					method.invoke(instance);
					return;
				}

				Object[] params = injector.injectParameters();

				method.invoke(instance, params);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	private final Runnable dst;

	public RunnableTask(Object instance, Method method) {
		super(instance, taskExecution(method), taskSchedule(method));

		this.dst = new MethodInvoker(instance, method, BuiltinTaskInjector.of(this, method));
	}

	public RunnableTask(Object instance, Method method, TaskExecution execution, TaskSchedule schedule) {
		super(instance, execution, schedule);
		this.dst = new MethodInvoker(instance, method, BuiltinTaskInjector.of(this, method));
	}

	public RunnableTask(Runnable dst) {
		super(DEFAULT_EXECUTION, DEFAULT_SCHEDULE);
		this.dst = dst;
	}

	public RunnableTask(Runnable dst, TaskExecution execution, TaskSchedule schedule) {
		super(execution, schedule);
		this.dst = dst;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		dst.run();
	}

}

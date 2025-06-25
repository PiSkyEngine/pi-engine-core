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

import java.lang.reflect.Method;
import java.util.logging.Logger;

import org.piengine.core.execution.TaskInjector;
import org.piengine.core.execution.TaskSchedule;
import org.piengine.inject.Inject;

/**
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 */
public class BuiltinTaskInjector implements TaskInjector {

	private interface ParamBuilder {

		Object buildParameter(float tpf);
	}

	public static boolean isInjectable(Method method) {
		return method.isAnnotationPresent(Inject.class);
	}

	public static BuiltinTaskInjector of(BaseTask task, Method method) {
		if (!isInjectable(method))
			return null;

		return new BuiltinTaskInjector(task, method);
	}

	private final BaseTask task;

	private final ParamBuilder[] paramBuilderArray;
	private Object[] cachedParameters = null;
	private final Logger logger;

	private BuiltinTaskInjector(BaseTask task, Method target) {
		this.task = task;
		this.paramBuilderArray = paramBuilderArray(target);
		this.logger = Logger.getLogger(task.taskContainer().getClass().getName());
	}

	private Object[] buildParameters(float tpf) {
		Object[] params = new Object[paramBuilderArray.length];

		for (int i = 0; i < params.length; i++) {
			params[i] = paramBuilderArray[i].buildParameter(tpf);
		}

		return params;
	}

	/**
	 * @see org.piengine.core.execution.TaskInjector#getInstance(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getInstance(Class<T> instanceClass) {
		return (T) getInstance(instanceClass, 0);
	}

	private Object getInstance(Class<?> instanceClass, float tpf) {

		Object inst = switch (instanceClass.getSimpleName()) {
		case "Float" -> tpf;
		case "Double" -> (double) tpf;
		case "Task" -> task;
		case "TaskInjector" -> this;
		case "TaskContext" -> task.taskContext();
		case "TaskExecution" -> task.taskExecution();
		case "Mode" -> task.taskExecution().value();
		case "TaskSchedule" -> task.taskSchedule();
		case "Logger" -> logger;
		case "Schedule" -> task.taskSchedule()
				.map(TaskSchedule::value)
				.orElse(null);
		default -> {
			yield null;
		}
		};

		assert instanceClass.isAssignableFrom(inst.getClass());

		if (!instanceClass.isAssignableFrom(inst.getClass()))
			return null;

		return inst;
	}

	public Object[] injectParameters() {
		return injectParameters(0);
	}

	public Object[] injectParameters(float tpf) {
		if (tpf != 0)
			return cachedParameters = buildParameters(tpf);

		if (cachedParameters == null)
			cachedParameters = buildParameters(tpf);

		return cachedParameters;
	}

	private ParamBuilder[] paramBuilderArray(Method target) {
		ParamBuilder[] injectors = new ParamBuilder[target.getParameterCount()];

		var parameterTypes = target.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++) {
			Class<?> paramClass = parameterTypes[i];

			injectors[i] = tpf -> getInstance(paramClass, tpf);
		}

		return injectors;
	}

}

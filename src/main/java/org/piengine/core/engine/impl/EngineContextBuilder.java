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
package org.piengine.core.engine.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.piengine.core.engine.EngineConfig;
import org.piengine.core.engine.EngineContext;
import org.piengine.core.engine.EngineInjector;
import org.piengine.core.engine.EngineState;
import org.piengine.core.engine.PiEngine;
import org.piengine.core.execution.Task;
import org.piengine.core.execution.TaskContext;
import org.piengine.core.execution.TaskInjector;
import org.piengine.core.plugin.PluginContext;
import org.piengine.core.plugin.PluginController;
import org.piengine.util.Registration;

/**
 * Main context implementation for all various context types.
 * 
 * <p>
 * This class is module private (non-exported) and it provides build methods for
 * all of the various types of contexts (engine, plugin, dispatcher, etc).
 * </p>
 * 
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 */
public class EngineContextBuilder implements EngineContext, PluginContext {

	private class TaskInjectorImpl implements TaskInjector {

		private final TaskContext taskContext;

		TaskInjectorImpl(TaskContext taskContext) {
			this.taskContext = taskContext;
		}

		/**
		 * @see org.piengine.core.execution.TaskInjector#getInstance(java.lang.Class)
		 */
		@Override
		public <T> T getInstance(Class<T> instanceClass) {
			var obj = mapTaskContextClass(instanceClass);
			if (obj != null)
				return obj;

			return EngineContextBuilder.this.mapEngineContextClass(instanceClass);
		}

		@SuppressWarnings("unchecked")
		private <T> T mapTaskContextClass(Class<T> type) {
			if (TaskContext.class.isAssignableFrom(type))
				return (T) taskContext;

			if (Task.class.isAssignableFrom(type))
				return (T) taskContext.task();

			if (TaskInjector.class.isAssignableFrom(type))
				return (T) this;

			return null;
		}
	}

	private final EngineStateSupport engineState = new EngineStateSupport();

	private EngineConfig engineConfig;
	private PluginController pluginController;

	private final EngineInjector injector = this::mapEngineContextClass;

	public EngineContextBuilder() {}

	public EngineContext asEngineContext() {
		return this;
	}

	public PluginContext asPluginContext() {
		return this;
	}

	/**
	 * @see org.piengine.core.engine.EngineContext#engine()
	 */
	@Override
	public PiEngine engine() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.engine.EngineContext#engineConfig()
	 */
	@Override
	public EngineConfig engineConfig() {
		return engineConfig;
	}

	@Override
	public EngineStateSupport engineState() {
		return engineState;
	}

	/**
	 * @see org.piengine.core.engine.EngineContext#injector()
	 */
	@Override
	public EngineInjector injector() {
		return injector;
	}

	@SuppressWarnings("unchecked")
	private <T> T mapEngineContextClass(Class<T> type) {
		if (EngineContext.class.isAssignableFrom(type))
			return (T) this;

		if (EngineConfig.class.isAssignableFrom(type))
			return (T) engineConfig();

		if (EngineState.class.isAssignableFrom(type))
			return (T) engineState().current();

		if (PiEngine.class.isAssignableFrom(type))
			return (T) engine();

		if (EngineInjector.class.isAssignableFrom(type))
			return (T) injector();

		for (Function<Class<?>, Object> function : externalInjectors) {
			var obj = function.apply(type);
			if (obj != null)
				return (T) obj;
		}

		return null;
	}

	/**
	 * @see org.piengine.core.engine.EngineContext#newTaskInjector(org.piengine.core.execution.TaskContext)
	 */
	@Override
	public TaskInjector newTaskInjector(TaskContext context) {
		return new TaskInjectorImpl(context);
	}

	/**
	 * @see org.piengine.core.plugin.PluginContext#pluginController()
	 */
	@Override
	public PluginController pluginController() {
		return pluginController;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EngineContextBuilder [engineState=" + engineState + ", engineConfig=" + engineConfig + "]";
	}

	public EngineContextBuilder usingConfig(EngineConfig engineConfig) {
		this.engineConfig = engineConfig;
		return this;
	}

	public EngineContextBuilder usingController(PluginController pluginController) {
		this.pluginController = pluginController;
		return this;
	}

	private final List<Function<Class<?>, Object>> externalInjectors = Collections.synchronizedList(new ArrayList<>());

	public Registration addExternalInjector(Function<Class<?>, Object> externalInjector) {
		externalInjectors.add(externalInjector);

		return () -> externalInjectors.remove(externalInjector);
	}
}

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.piengine.core.Updatable;
import org.piengine.core.engine.EngineContext;
import org.piengine.core.engine.EngineWorker;
import org.piengine.core.execution.Task;
import org.piengine.core.execution.TaskExecution;
import org.piengine.core.execution.TaskSchedule;
import org.piengine.core.logging.PiEngineLogging;
import org.piengine.core.plugin.Plugin;
import org.piengine.util.annotations.Methods;

/**
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 */
public class PluginExecutor implements EngineWorker {
	private static final Logger logger = PiEngineLogging.getLogger(PluginExecutor.class);

	private final List<Plugin> pluginList = new ArrayList<>();
	private final List<Task> taskList = new ArrayList<>();

	public boolean registerPlugin(Plugin plugin) {

		var scannedList = scanPlugin(plugin);
		if (taskList.isEmpty())
			return false; // Nothing to execute

		taskList.addAll(scannedList);
		pluginList.add(plugin);

		return true;
	}

	private List<Task> scanPlugin(Plugin plugin) {
		Method runMethod = plugin instanceof Runnable
				? Methods.parseMethod(plugin.getClass(), "run")
				: null;

		Method updateMethod = plugin instanceof Updatable
				? Methods.parseMethod(plugin.getClass(), "updateFrame", Float.class)
				: null;

		Method[] execArr = Methods.parseObject(TaskExecution.class, plugin);
		Method[] schedArr = Methods.parseObject(TaskSchedule.class, plugin);

		List<Method> mergedList = Stream.concat(
				Stream.of(execArr),
				Stream.of(schedArr))
				.distinct()
				.filter(m -> (m != runMethod) && (m != updateMethod))
				.toList();

		List<Task> taskList = new ArrayList<>();

		if (plugin instanceof Runnable run) {
			var execution = runMethod.getAnnotation(TaskExecution.class);
			var schedule = runMethod.getAnnotation(TaskSchedule.class);

			RunnableTask runTask = new RunnableTask(run, execution, schedule);
			taskList.add(runTask);
		}

		if (plugin instanceof Updatable up) {
			var execution = updateMethod.getAnnotation(TaskExecution.class);

			UpdatableTask upTask = new UpdatableTask(up, execution);
			taskList.add(upTask);
		}

		for (Method method : mergedList) {
			int paramCount = method.getParameterCount();
			if (paramCount > 1) {
				logger.warning("Skipping task, too many parameters: " + method);
				continue;
			}

			boolean isRun = (paramCount == 0);
			boolean isUp = (paramCount == 1)
					&& method.getParameterTypes()[0] == Float.class;

			assert (isRun && isUp) == false;

			if (isRun) {
				RunnableTask task = new RunnableTask(plugin, method);
				taskList.add(task);
			} else if (isUp) {
				UpdatableTask task = new UpdatableTask(plugin, method);
				taskList.add(task);
			} else
				throw new IllegalStateException("Conflicting plugin task states");
		}

		Collections.sort(taskList);

		return taskList;
	}

	/**
	 * @see org.piengine.util.Prioritizable#priority()
	 */
	@Override
	public int priority() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.engine.EngineWorker#shutdown()
	 */
	@Override
	public void shutdown() {
		pluginList.clear();
		taskList.clear();
	}

	/**
	 * @see org.piengine.core.engine.EngineWorker#initialize(org.piengine.core.engine.EngineContext)
	 */
	@Override
	public void initialize(EngineContext context) {
		List<RunnableTask> runDedicated = new ArrayList<>();
		List<RunnableTask> runShared = new ArrayList<>();

		List<UpdatableTask> updateDedicated = new ArrayList<>();
		List<UpdatableTask> updateShared = new ArrayList<>();

		taskList.stream()
				.filter(t -> t instanceof RunnableTask)
				.map(t -> (RunnableTask) t)
				.forEach(task -> {
					switch (task.taskExecution().value()) {
					case DEDICATED -> runDedicated.add(task);
					case SHARED -> runShared.add(task);
					}
				});

		taskList.stream()
				.filter(t -> t instanceof UpdatableTask)
				.map(t -> (UpdatableTask) t)
				.forEach(task -> {
					switch (task.taskExecution().value()) {
					case DEDICATED -> updateDedicated.add(task);
					case SHARED -> updateShared.add(task);
					}
				});

		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.engine.EngineWorker#start()
	 */
	@Override
	public void start() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.engine.EngineWorker#stop()
	 */
	@Override
	public void stop() throws InterruptedException, IllegalStateException {
		throw new UnsupportedOperationException("not implemented yet");
	}
}

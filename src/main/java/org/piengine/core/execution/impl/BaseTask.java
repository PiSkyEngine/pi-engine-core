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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.piengine.core.execution.Task;
import org.piengine.core.execution.TaskContext;
import org.piengine.core.execution.TaskExecution;
import org.piengine.core.execution.TaskExecution.Mode;
import org.piengine.core.execution.TaskSchedule;
import org.piengine.core.execution.TaskSchedule.Schedule;
import org.piengine.util.annotations.Annotations;

/**
 * 
 *
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 */
public sealed class BaseTask implements Task permits UpdatableTask, RunnableTask {

	private static final class Defaults {

		@TaskExecution // Default annotation
		@TaskSchedule // Default annotation
		void dummy() {

		}
	}

	public static final TaskExecution DEFAULT_EXECUTION = Annotations.parseMethod(
			TaskExecution.class,
			Defaults.class,
			"dummy");

	public static final TaskSchedule DEFAULT_SCHEDULE = Annotations.parseMethod(
			TaskSchedule.class,
			Defaults.class,
			"dummy");

	public static TaskExecution taskExecution(Method method) {
		var annotation = method.getAnnotation(TaskExecution.class);

		return annotation != null ? annotation : DEFAULT_EXECUTION;
	}

	public static TaskExecution taskExecution(Mode newMode, int priority) {
		return new TaskExecution() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return TaskExecution.class;
			}

			@Override
			public int priority() {
				return priority;
			}

			@Override
			public Mode value() {
				return newMode;
			}

		};
	}

	public static TaskSchedule taskSchedule(Method method) {
		var annotation = method.getAnnotation(TaskSchedule.class);

		return annotation != null ? annotation : DEFAULT_SCHEDULE;
	}

	public static TaskSchedule taskSchedule(Schedule schedule,
			long initialDelay,
			long period,
			TimeUnit unit) {
		return new TaskSchedule() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return TaskSchedule.class;
			}

			@Override
			public long initialDelay() {
				return initialDelay;
			}

			@Override
			public long period() {
				return period;
			}

			@Override
			public TimeUnit timeUnit() {
				return unit;
			}

			@Override
			public Schedule value() {
				return schedule;
			}

		};
	}

	private final TaskExecution execution;
	private final TaskSchedule schedule;
	private final Object instance;

	TaskContext context;

	protected BaseTask(Object instance, TaskExecution execution) {
		this.execution = execution;
		this.schedule = null;
		this.instance = instance;
	}

	protected BaseTask(Object instance, TaskExecution execution, TaskSchedule schedule) {
		this.execution = execution;
		this.schedule = schedule;
		this.instance = instance;
	}

	protected BaseTask(TaskExecution execution) {
		this.execution = execution;
		this.schedule = null;
		this.instance = this;
	}

	protected BaseTask(TaskExecution execution, TaskSchedule schedule) {
		this.execution = execution;
		this.schedule = schedule;
		this.instance = this;
	}

	/**
	 * @see org.piengine.core.execution.TaskLifecycle#initialize(org.piengine.core.execution.TaskContext)
	 */
	@Override
	public void initialize(TaskContext context) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.execution.TaskLifecycle#pause()
	 */
	@Override
	public void pause() throws InterruptedException, IllegalStateException {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.util.Prioritizable#priority()
	 */
	@Override
	public int priority() {
		return execution.priority();
	}

	/**
	 * @see org.piengine.core.execution.TaskLifecycle#shutdown()
	 */
	@Override
	public void shutdown() throws IllegalStateException {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.execution.TaskLifecycle#start()
	 */
	@Override
	public void start() throws InterruptedException, IllegalStateException {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.execution.TaskLifecycle#stop()
	 */
	@Override
	public void stop() throws InterruptedException, IllegalStateException {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.execution.impl.BoxedTask#taskContainer()
	 */
	public final Object taskContainer() {
		return instance;
	}

	@SuppressWarnings("unchecked")
	public final <T> T taskContainer(Class<T> containerClass) {
		return (T) instance;
	}

	/**
	 * @see org.piengine.core.execution.Task#taskContext()
	 */
	@Override
	public final TaskContext taskContext() {
		if (context == null)
			throw new IllegalStateException("Task not initialized");

		return context;
	}

	/**
	 * @see org.piengine.core.execution.Task#taskExecution()
	 */
	@Override
	public final TaskExecution taskExecution() {
		return execution;
	}

	/**
	 * @see org.piengine.core.execution.Task#taskSchedule()
	 */
	@Override
	public final Optional<TaskSchedule> taskSchedule() {
		return Optional.ofNullable(schedule);
	}

	/**
	 * @see org.piengine.core.execution.TaskLifecycle#unpause()
	 */
	@Override
	public void unpause() throws InterruptedException {
		throw new UnsupportedOperationException("not implemented yet");
	}

}

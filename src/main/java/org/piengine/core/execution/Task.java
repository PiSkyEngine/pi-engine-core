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

package org.piengine.core.execution;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.piengine.core.Updatable;
import org.piengine.core.execution.TaskExecution.Mode;
import org.piengine.core.execution.TaskSchedule.Schedule;
import org.piengine.core.execution.impl.BaseTask;
import org.piengine.core.execution.impl.RunnableTask;
import org.piengine.core.execution.impl.UpdatableTask;
import org.piengine.util.Prioritizable;

/**
 * Non main-loop running task such as background processing, periodic invocation
 * or one-time shot.
 *
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 */
public sealed interface Task
		extends Prioritizable, TaskLifecycle
		permits BaseTask {

	static Task ofDedicatedExecution(Runnable command) {
		return new RunnableTask(command);
	}

	static Task ofDedicatedExecution(Updatable updatable) {
		return new UpdatableTask(updatable);
	}

	static Task ofSharedExecution(Runnable command) {
		return new RunnableTask(command,
				BaseTask.taskExecution(Mode.SHARED, 0),
				BaseTask.DEFAULT_SCHEDULE);
	}

	static Task ofSharedExecution(Updatable updatable) {
		return new UpdatableTask(updatable, BaseTask.taskExecution(Mode.SHARED, 0));
	}

	static Task scheduleDedicatedExecution(Runnable command,
			long delay, TimeUnit unit) {
		return new RunnableTask(command,
				BaseTask.DEFAULT_EXECUTION,
				BaseTask.taskSchedule(Schedule.ONE_TIME,
						delay, 0, unit));
	}

	static Task scheduleDedicatedExecutionAtFixedRate(Runnable command,
			long initialDelay,
			long period,
			TimeUnit unit) {
		return new RunnableTask(command,
				BaseTask.DEFAULT_EXECUTION,
				BaseTask.taskSchedule(Schedule.PERIODIC,
						initialDelay, period, unit));
	}

	TaskExecution taskExecution();

	Optional<TaskSchedule> taskSchedule();

	TaskContext taskContext();
}

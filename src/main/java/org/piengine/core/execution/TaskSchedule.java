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

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Marks a method for execution on a shared background thread in the PiEngine
 * framework, independent of the main loop cycle. This annotation is designed
 * for lightweight, non-blocking tasks that can share a single thread with other
 * plugins, similar to idle-task execution in other GUI/graphic engines. The
 * execution mode is defined by the {@code Mode} enum, with a default of
 * {@code SHARED} to ensure shared thread execution.
 * <p>
 * Methods annotated with {@code @PluginSharedTask} are discovered via
 * reflection and executed according to the specified {@code schedule} type:
 * {@code CONTINUOUS} (looping execution), {@code ONE_TIME} (single execution),
 * or {@code PERIODIC} (repeated at intervals defined by {@code periodMillis}).
 * The method must have a signature of {@code void method()} (no parameters,
 * void return) and return promptly to avoid blocking other tasks on the shared
 * thread. The framework may log warnings or enforce timeouts for long-running
 * tasks to ensure fairness.
 * <p>
 * The {@code initialDelay} specifies a delay before the task starts, and
 * {@code timeUnit} defines the unit for both {@code initialDelay} and
 * {@code periodMillis}. For {@code PERIODIC} tasks, the first execution occurs
 * after {@code initialDelay}, followed by executions at {@code periodMillis}
 * intervals. The {@code ThreadFactory} determines the shared thread's
 * characteristics (e.g., pooled or new). Methods are managed by the plugin and
 * engine lifecycle, ensuring proper start and stop behavior. Unlike
 * {@code @TaskExecution}, shared tasks are not synchronized with the main loop
 * cycle. For long-running or blocking tasks, use {@code @PluginBackground} with
 * a dedicated thread.
 * <p>
 * Example usage:
 * 
 * <pre>
 * public class MonitorPlugin extends BasePlugin {
 * 	// Continuous lightweight logging on shared thread
 * 	&#64;PluginSharedTask
 * 	public void logStatus() {
 * 		// Quick status logging
 * 	}
 *
 * 	// One-time task after 5 seconds
 * 	&#64;PluginSharedTask(schedule = Schedule.ONE_TIME, initialDelay = 5, timeUnit = TimeUnit.SECONDS)
 * 	public void initializeCache() {
 * 		// Quick cache initialization
 * 	}
 *
 * 	// Periodic health check every 1 minute after 30 seconds
 * 	@PluginSharedTask(schedule = Schedule.PERIODIC,
 * 			initialDelay = 30,
 * 			periodMillis = 60,
 * 			timeUnit = TimeUnit.SECONDS)
 * 	public void checkHealth() {
 * 		// Quick health check
 * 	}
 * }
 * </pre>
 *
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface TaskSchedule {

	/**
	 * Defines the scheduling types for shared tasks.
	 */
	public enum Schedule {
		/**
		 * Executes the task continuously in a loop until termination.
		 */
		CONTINUOUS,

		/**
		 * Executes the task once and terminates.
		 */
		ONE_TIME,

		/**
		 * Executes the task periodically at the interval specified by
		 * {@code period}.
		 */
		PERIODIC
	}
	
	/**
	 * Specifies the scheduling type for the task.
	 *
	 * @return the scheduling type, defaulting to {@code Schedule.CONTINUOUS}
	 */
	Schedule value() default Schedule.CONTINUOUS;

	/**
	 * Specifies the interval for periodic tasks in the unit defined by
	 * {@code timeUnit}. Ignored unless {@code schedule=PERIODIC}.
	 *
	 * @return the period, defaulting to 0
	 */
	long period() default 0;

	/**
	 * Specifies the delay before the task starts in the unit defined by
	 * {@code timeUnit}. For {@code PERIODIC} tasks, this is the delay before the
	 * first execution.
	 *
	 * @return the initial delay, defaulting to 0 (immediate start)
	 */
	long initialDelay() default 0;

	/**
	 * Specifies the time unit for {@code initialDelay} and {@code periodMillis}.
	 *
	 * @return the time unit, defaulting to {@code TimeUnit.MILLISECONDS}
	 */
	TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
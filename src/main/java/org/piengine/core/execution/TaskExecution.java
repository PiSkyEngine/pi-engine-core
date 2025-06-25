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

/**
 * Marks a method for per-cycle updates in the PiEngine framework, synchronized
 * with the main loop cycle. The execution mode is defined by the {@code Mode}
 * enum, which includes {@code AUTO}, {@code SHARED}, {@code DEDICATED}, and
 * {@code HYBRID} modes. The default mode is {@code AUTO}, which selects the
 * main loop thread for updates unless the plugin implements {@code Runnable} or
 * specifies a dedicated thread.
 * <p>
 * Methods annotated with {@code @TaskExecution} are discovered via reflection
 * and executed once per engine loop cycle, similar to
 * {@code Updatable.updateFrame(float tpf)}. The method must have a signature of
 * {@code void method(float tpf)}, where {@code tpf} is the time elapsed since
 * the last frame in seconds. In multi-threaded modes ({@code DEDICATED},
 * {@code HYBRID}, or {@code AUTO} with {@code Runnable}), the method executes
 * in a dedicated thread (via a user-configurable {@code ThreadFactory},
 * defaulting to virtual threads) and is synchronized with other update methods
 * using a phaser barrier, ensuring completion before the next graphics loop
 * iteration. In {@code SHARED} or {@code AUTO} (without {@code Runnable}), the
 * method executes on the main loop thread.
 * <p>
 * Plugins using multi-threaded execution must be thread-safe to avoid
 * concurrency issues. This annotation is ideal for heavy per-cycle tasks (e.g.,
 * analytics, physics) that can run in parallel with the main update loop.
 * <p>
 * Example usage:
 * 
 * <pre>
 * public class AnalyticsPlugin extends BasePlugin implements Updatable {
 * 	@TaskExecution(mode = Mode.DEDICATED)
 * 	public void updateFrameAnalytics(float tpf) {
 * 		// Heavy per-cycle analytics
 * 	}
 *
 * 	public void updateFrame(float tpf) {
 * 		// Main loop updates
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
public @interface TaskExecution {

	/**
	 * Defines the execution modes for plugins in the PiEngine framework.
	 */
	public enum Mode {

		/**
		 * Executes plugin methods on the PiEngine's main loop thread. Only supported
		 * for {@code Updatable} plugins, as {@code Runnable.run()} cannot run on the
		 * main thread. The framework may throw an exception if used with
		 * {@code Runnable}.
		 */
		SHARED,

		/**
		 * Assigns a dedicated thread (typically a virtual thread via
		 * {@code ThreadFactory}) for all plugin methods ({@code run()} and
		 * {@code updateFrame()}). Suitable for both {@code Runnable} and
		 * {@code Updatable} plugins.
		 */
		DEDICATED,
	}

	/**
	 * Specifies the execution mode for the update method.
	 *
	 * @return the execution mode, defaulting to {@code Mode.AUTO}
	 */
	Mode value() default Mode.DEDICATED;

	int priority() default 0;
}
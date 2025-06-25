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

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.piengine.core.Updatable;
import org.piengine.core.execution.TaskExecution;
import org.piengine.core.execution.TaskExecution.Mode;
import org.piengine.core.execution.TaskInjector;
import org.piengine.core.execution.TaskSchedule;
import org.piengine.core.execution.TaskSchedule.Schedule;
import org.piengine.core.plugin.BasePlugin;
import org.piengine.core.plugin.PluginContext;
import org.piengine.core.plugin.PluginMetadata;
import org.piengine.inject.Inject;

/**
 * A minimal plugin implementation for testing or demonstration purposes in the
 * PiEngine framework. This plugin implements {@code Runnable} and
 * {@code Updatable} interfaces and uses annotations ({@code @PluginExecution},
 * {@code @PluginBackground}, {@code @TaskExecution}, {@code @PluginSharedTask})
 * to demonstrate various execution modes. The class-level
 * {@code @PluginExecution(mode = Mode.AUTO)} sets the default execution mode,
 * while method-level annotations override it for specific tasks. All methods
 * provide no actual functionality and serve as placeholders to illustrate the
 * framework's capabilities.
 *
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 */
public class DummyPlugin extends BasePlugin implements Runnable, Updatable {

	/**
	 * Metadata identifying this plugin.
	 */
	public final static PluginMetadata META = PluginMetadata.of("org.piengine:dummy-plugin:1.0.0");

	/**
	 * Constructs a new DummyPlugin with the specified metadata.
	 *
	 * @param meta the plugin metadata
	 */
	public DummyPlugin(PluginMetadata meta) {
		super(meta);
	}

	/**
	 * Initializes the plugin with the given context. This implementation does
	 * nothing.
	 *
	 * @param context the plugin context
	 */
	@Override
	public void initialize(PluginContext context) {}

	/**
	 * Executes the plugin's background task in a dedicated thread when using
	 * {@code Mode.AUTO}, {@code DEDICATED}, or {@code HYBRID}. This implementation
	 * does nothing.
	 */
	@Override
	public void run() {}

	/**
	 * Updates the plugin's state for the current frame, called once per engine loop
	 * cycle. In {@code Mode.AUTO} or {@code SHARED}, this executes on the main loop
	 * thread; in {@code DEDICATED} or {@code HYBRID}, it may use a dedicated
	 * thread. This implementation does nothing.
	 *
	 * @param tpf the time elapsed since the last frame in seconds
	 */
	@Override
	@TaskExecution(Mode.SHARED)
	public void updateFrame(float tpf) {}

	/**
	 * Performs per-cycle analytics updates in a dedicated thread, synchronized with
	 * the main loop cycle via a phaser barrier. This implementation does nothing.
	 *
	 * @param tpf the time elapsed since the last frame in seconds
	 */
	@TaskExecution(Mode.DEDICATED)
	public void updateFrameAnalytics(float tpf) {}

	/**
	 * Monitors resources in a dedicated background thread, independent of the main
	 * loop cycle. This implementation does nothing.
	 */
	@TaskExecution(Mode.DEDICATED)
	private void backgroundResourceMonitor() {}

	/**
	 * Monitors assets in a shared background thread, starting after a 500ms delay
	 * and executing continuously. The method executes quickly to avoid blocking
	 * other shared tasks. This implementation does nothing.
	 */
	@TaskExecution(Mode.DEDICATED)
	@TaskSchedule(initialDelay = 500)
	private void backgroundAssetMonitor() {}

	/**
	 * Initializes a cache in a shared background thread, executing once after a
	 * 5-second delay. This implementation does nothing.
	 */
	@TaskSchedule(value = Schedule.ONE_TIME, initialDelay = 500)
	@Inject
	private void initializeCache(TaskSchedule schedule, TaskInjector injector, Logger logger) {}

	/**
	 * Performs periodic health checks in a shared background thread, starting after
	 * a 10-second delay and repeating every 30 seconds. This implementation does
	 * nothing.
	 */
	@TaskExecution(value = Mode.DEDICATED)
	@TaskSchedule(value = Schedule.PERIODIC,
			initialDelay = 10,
			period = 30,
			timeUnit = TimeUnit.SECONDS)
	private void checkHealth() {}
}
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
package org.piengine.core.engine;

import org.piengine.core.engine.impl.EngineContextBuilder;
import org.piengine.core.plugin.PluginLifecycle;

/**
 * Enumerates the possible states of a plugin in the PIEngine framework.
 * <p>
 * This enum defines the lifecycle states that a plugin can be in, as managed by
 * the {@link PluginLifecycle} interface. Each state represents a distinct phase
 * in the plugin's operation, from initialization to shutdown. The states are
 * used to enforce valid transitions and ensure that lifecycle methods are
 * called in the correct context.
 * </p>
 * <p>
 * The defined states are:
 * <ul>
 * <li><b>SHUTDOWN</b>: The plugin is fully terminated and unloaded, with all
 * resources and dependencies released.</li>
 * <li><b>RUNNING</b>: The plugin is active, providing services and consuming
 * resources.</li>
 * <li><b>PAUSED</b>: The plugin is temporarily suspended, with some resources
 * released but state preserved for quick resumption.</li>
 * <li><b>STOPPED</b>: The plugin is stopped, with services unavailable but
 * capable of being restarted without reinitialization.</li>
 * </ul>
 * </p>
 * <p>
 * Example usage:
 * 
 * <pre>
 * public class MyPlugin implements PluginLifecycle {
 * 	private PluginState currentState = PluginState.SHUTDOWN;
 * 
 * 	public void start() throws IllegalStateException {
 * 		if (currentState != PluginState.STOPPED) {
 * 			throw new IllegalStateException("Plugin must be in STOPPED state");
 * 		}
 * 		currentState = PluginState.RUNNING;
 * 		System.out.println("Plugin is now " + currentState);
 * 	}
 * 
 * 	// Implement other lifecycle methods...
 * }
 * </pre>
 * </p>
 *
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 * @see PluginLifecycle
 * @since 1.0
 */
public enum EngineState {
	/**
	 * The plugin is fully shut down and unloaded.
	 * <p>
	 * In this state, all resources and dependencies have been released, and the
	 * plugin cannot be restarted without reinitialization via
	 * {@link PluginLifecycle#initialize(EngineContextBuilder)}. This is the initial state
	 * of a plugin before initialization and the final state after
	 * {@link PluginLifecycle#shutdown()}.
	 * </p>
	 */
	SHUTDOWN,

	/**
	 * The plugin is actively running and providing services.
	 * <p>
	 * In this state, the plugin has started via {@link PluginLifecycle#start()} and
	 * is fully operational, with worker threads, resources, and services active.
	 * The plugin can transition to {@link #PAUSED} or {@link #STOPPED} from this
	 * state.
	 * </p>
	 */
	RUNNING,

	/**
	 * The plugin is stopped, with services unavailable.
	 * <p>
	 * In this state, the plugin has been stopped via
	 * {@link PluginLifecycle#stop()}, with worker threads terminated and resources
	 * released. The plugin can be restarted via {@link PluginLifecycle#start()}
	 * without reinitialization.
	 * </p>
	 */
	STOPPED
}
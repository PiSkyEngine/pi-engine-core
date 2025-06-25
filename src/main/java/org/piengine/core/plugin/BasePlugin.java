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
package org.piengine.core.plugin;

import org.piengine.core.engine.dispatcher.DispatchablePlugin;

/**
 * The Class BasePlugin.
 */
public abstract class BasePlugin
		implements Plugin, PluginLifecycle, DispatchablePlugin {

	/** The meta. */
	private final PluginMetadata meta;
	private PluginController pluginController;

	/**
	 * Instantiates a new base plugin.
	 *
	 * @param pluginId the plugin id
	 * @param meta     the meta
	 */
	public BasePlugin(PluginMetadata meta) {
		this.meta = meta;
	}

	protected PluginController controller() {
		return pluginController;
	}

	/**
	 * @see org.piengine.core.plugin.Plugin#getMetadata()
	 */
	@Override
	public PluginMetadata getMetadata() {
		return meta;
	}

	/**
	 * Handle error.
	 *
	 * @param e the e
	 */
	public void handleError(Exception e) {}

	/**
	 * @see org.piengine.core.plugin.PluginLifecycle#initialize(org.piengine.core.Context)
	 */
	@Override
	public abstract void initialize(PluginContext context);

	/**
	 * @see org.piengine.core.engine.dispatcher.DispatchablePlugin#initialize(org.piengine.core.plugin.PluginContext,
	 *      org.piengine.core.plugin.PluginController)
	 */
	@Override
	public final void initialize(PluginContext pluginCtx, PluginController pluginController) {
		this.pluginController = pluginController;

		initialize(pluginCtx);
	}

	/**
	 * @see org.piengine.core.plugin.Plugin#pause()
	 */
	@Override
	public void pause() throws InterruptedException, IllegalStateException {

		assert pluginController != null;

		pluginController.pause();
	}

	protected PluginController pluginController() {
		return pluginController;
	}

	/**
	 * @see org.piengine.core.plugin.Plugin#pluginId()
	 */
	@Override
	public PluginId pluginId() {
		return meta.pluginId();
	}

	/**
	 * @see org.piengine.util.Prioritizable#priority()
	 */
	@Override
	public int priority() {
		return meta.priority();
	}

	/**
	 * @see org.piengine.core.plugin.PluginLifecycle#shutdown()
	 */
	@Override
	public void shutdown() throws IllegalStateException {
		assert pluginController != null;

		pluginController.shutdown();
	}

	/**
	 * @throws InterruptedException
	 * @throws IllegalStateException
	 * @see org.piengine.core.plugin.Plugin#start()
	 */
	@Override
	public void start() throws IllegalStateException, InterruptedException {
		assert pluginController != null;

		pluginController.start();

	}

	/**
	 * @see org.piengine.core.plugin.Plugin#state()
	 */
	@Override
	public PluginState state() {
		return pluginController.state();
	}

	/**
	 * @see org.piengine.core.plugin.Plugin#stop()
	 */
	@Override
	public void stop() throws InterruptedException, IllegalStateException {
		assert pluginController != null;

		pluginController.stop();

	}

	/**
	 * @see org.piengine.core.plugin.Plugin#unpause()
	 */
	@Override
	public void unpause() throws InterruptedException {
		assert pluginController != null;

		pluginController.unpause();

	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Plugin [" + meta.pluginId() + "]";
	}

}

package org.piengine.core.plugin.impl;

import org.piengine.core.plugin.BasePlugin;
import org.piengine.core.plugin.PluginContext;
import org.piengine.core.plugin.PluginMetadata;

public class TestPlugin extends BasePlugin implements Runnable {
	public final static PluginMetadata META = PluginMetadata.of(10, "org.piengine:pi-engine-test-plugin:1.0.0");

	public TestPlugin(PluginMetadata meta) {
		super(meta);
	}

	@Override
	public void initialize(PluginContext context) throws IllegalStateException {
		// Initialize plugin resources
		System.out.println("Plugin initialized with context: " + context);
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {}

}
package org.piengine.core.plugin.impl;

import org.piengine.core.plugin.BasePlugin;
import org.piengine.core.plugin.PluginContext;
import org.piengine.core.plugin.PluginMetadata;

public class MyPlugin extends BasePlugin {
	public final static PluginMetadata META = PluginMetadata.of(10, "com.example:my-plugin:2.0.0");
	private PluginContext context;

	public MyPlugin(PluginMetadata meta) {
		super(meta);
	}

	@Override
	public void initialize(PluginContext context) throws IllegalStateException {
		this.context = context;

		// Initialize plugin resources
		System.out.println("Plugin initialized with context: " + context);
		
		shutdown();
	}

}
package org.piengine.core.plugin;

public class DebugPlugin extends BasePlugin {
	public final static PluginMetadata META = PluginMetadata.of("org.piengine:pi-engine-debug-plugin:1.0.0");

	public DebugPlugin(PluginMetadata meta) {
		super(meta);
	}

	@Override
	public void initialize(PluginContext context) throws IllegalStateException {
		// Initialize plugin resources
		System.out.println("Plugin initialized with context: " + context);
	}

}
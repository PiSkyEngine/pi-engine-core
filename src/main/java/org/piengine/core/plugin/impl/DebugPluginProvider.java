package org.piengine.core.plugin.impl;

import org.piengine.core.plugin.DebugPlugin;
import org.piengine.core.plugin.registry.spi.PluginService.PluginProvider;

public class DebugPluginProvider extends PluginProvider {

	public DebugPluginProvider() {
		super(DebugPlugin.META, DebugPlugin::new);
	}

}
package org.piengine.core.plugin.impl;

import org.piengine.core.plugin.registry.spi.PluginService.PluginProvider;

public class MyPluginProvider extends PluginProvider {

	public MyPluginProvider() {
		super(MyPlugin.META, MyPlugin::new);
	}

}
package org.piengine.core.plugin.impl;

import org.piengine.core.plugin.registry.spi.PluginService.PluginProvider;

public class TestPluginProvider extends PluginProvider {

	public TestPluginProvider() {
		super(TestPlugin.META, TestPlugin::new);
	}

}
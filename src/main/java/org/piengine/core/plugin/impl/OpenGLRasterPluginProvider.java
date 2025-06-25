package org.piengine.core.plugin.impl;

import org.piengine.core.plugin.registry.spi.PluginService.PluginProvider;

public class OpenGLRasterPluginProvider extends PluginProvider {

	public OpenGLRasterPluginProvider() {
		super(OpenGLRasterPlugin.META, OpenGLRasterPlugin::new);
	}

}
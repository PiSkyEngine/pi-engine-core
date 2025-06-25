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
package org.piengine.core.plugin.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.piengine.core.plugin.Plugin;
import org.piengine.core.plugin.PluginMetadata;

/**
 * 
 *
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 */
public abstract class BasePluginRegistry implements PluginRegistry {

	private final List<PluginRegistry> localRegistries = new ArrayList<>();
	private final List<PluginRegistry> onlineRegistries = new ArrayList<>();

	public BasePluginRegistry() {}

	/**
	 * @see org.piengine.core.plugin.registry.PluginRegistry#hasPluginRegistryOnline()
	 */
	@Override
	public boolean hasPluginRegistryOnline() {
		return false;
	}

	/**
	 * @see org.piengine.core.plugin.registry.PluginRegistry#findPlugin(java.lang.String[])
	 */
	@Override
	public abstract Optional<PluginMetadata> findPlugin(String... pluginIds);

	/**
	 * @see org.piengine.core.plugin.registry.PluginRegistry#listConfiguredPlugins(java.lang.String[])
	 */
	@Override
	public List<PluginMetadata> listConfiguredPlugins(String... pattern) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.registry.PluginRegistry#listAllLocalPlugins(java.lang.String[])
	 */
	@Override
	public abstract List<PluginMetadata> listAllLocalPlugins(String... pattern);

	/**
	 * @see org.piengine.core.plugin.registry.PluginRegistry#listAllOnlinePlugins(java.lang.String[])
	 */
	@Override
	public List<PluginMetadata> listAllOnlinePlugins(String... pattern) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.registry.PluginRegistry#listLoadedPlugins(java.lang.String[])
	 */
	@Override
	public List<Plugin> listLoadedPlugins(String... pattern) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.registry.PluginRegistry#getPlugin(java.lang.String[])
	 */
	@Override
	public PluginMetadata getPlugin(String... pluginIds) throws PluginNotFound {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.registry.PluginRegistry#loadPlugin(java.lang.Class,
	 *      java.lang.String[])
	 */
	@Override
	public abstract <T extends Plugin> T loadPlugin(Class<T> pluginType, String... pluginIds) throws PluginNotFound;

	/**
	 * @see org.piengine.core.plugin.registry.PluginRegistry#loadPlugin(java.lang.Class,
	 *      org.piengine.core.plugin.PluginMetadata)
	 */
	@Override
	public <T extends Plugin> T loadPlugin(Class<T> pluginType, PluginMetadata pluginMetaData) throws PluginNotFound {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.registry.PluginRegistry#unloadPlugin(org.piengine.core.plugin.Plugin)
	 */
	@Override
	public void unloadPlugin(Plugin plugin) {
		throw new UnsupportedOperationException("not implemented yet");
	}

}

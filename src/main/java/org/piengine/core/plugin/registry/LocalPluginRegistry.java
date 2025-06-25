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

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;

import org.piengine.core.plugin.Plugin;
import org.piengine.core.plugin.PluginMetadata;
import org.piengine.core.plugin.Plugins;
import org.piengine.core.plugin.registry.spi.PluginService;

/**
 * 
 *
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 */
public class LocalPluginRegistry extends BasePluginRegistry implements Iterable<PluginService> {

	private final ServiceLoader<PluginService> loader;

	/**
	 * 
	 */
	public LocalPluginRegistry() {
		loader = ServiceLoader.load(PluginService.class);
	}

	/**
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<PluginService> iterator() {
		return loader.iterator();
	}

	/**
	 * @see org.piengine.core.plugin.registry.BasePluginRegistry#findPlugin(java.lang.String[])
	 */
	@Override
	public Optional<PluginMetadata> findPlugin(String... pluginIds) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.registry.BasePluginRegistry#listAllLocalPlugins(java.lang.String[])
	 */
	@Override
	public List<PluginMetadata> listAllLocalPlugins(String... pattern) {
		return loader.stream()
				.map(Provider::get)
				.filter(Plugins.filterHasPluginId(pattern))
				.map(PluginService::pluginMetadata)
				.toList();
	}

	/**
	 * @see org.piengine.core.plugin.registry.PluginRegistry#getPluginVerifier()
	 */
	@Override
	public PluginVerifier getPluginVerifier() {
		return new PluginVerifier() {

			@Override
			public void verifyPlugin(Plugin plugin, PluginSignature signature) throws PluginVerificationFailure {
				throw new UnsupportedOperationException("not implemented yet");
			}
		};
	}

	/**
	 * @see org.piengine.core.plugin.registry.BasePluginRegistry#loadPlugin(java.lang.Class,
	 *      java.lang.String[])
	 */
	@Override
	public <T extends Plugin> T loadPlugin(Class<T> pluginType, String... pattern) throws PluginNotFound {
		var service = loader.stream()
				.map(Provider::get)
				.filter(Plugins.filterHasPluginId(pattern))
				.findAny()
				.orElseThrow(() -> new PluginNotFound(pattern[0]));

		return (T) service.newPlugin();
	}

}

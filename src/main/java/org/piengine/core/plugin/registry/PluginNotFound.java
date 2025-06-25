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

import org.piengine.core.engine.EngineException;
import org.piengine.core.plugin.PluginId;
import org.piengine.core.plugin.PluginMetadata;

/**
 * Exception thrown when a plugin cannot be found or loaded in the PIEngine
 * framework.
 * <p>
 * This exception is raised by the {@link PluginRegistry} when a requested
 * plugin, identified by its Maven-style {@link PluginId} or
 * {@link PluginMetadata}, is not available locally or online, or cannot be
 * loaded due to missing dependencies or invalid configuration. It is typically
 * thrown by methods like {@link PluginRegistry#loadPlugin(Class, String...)} or
 * {@link PluginRegistry#getPlugin(String...)} when the plugin is not found in
 * the system's module path, configuration, or online registry.
 * </p>
 * <p>
 * Example usage:
 * 
 * <pre>
 * PluginRegistry registry = PluginRegistry.newInstance();
 * try {
 * 	PluginMetadata metadata = registry.getPlugin("org.example:my-plugin");
 * } catch (PluginNotFound e) {
 * 	System.err.println("Plugin not found: " + e.getMessage());
 * }
 * </pre>
 * </p>
 *
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 * @see PluginRegistry
 * @see PluginId
 * @see PluginMetadata
 * @since 1.0
 */
public class PluginNotFound extends EngineException {

	private static final long serialVersionUID = 191910260115794092L;

	/**
	 * Constructs a new plugin not found exception with the specified detail
	 * message.
	 *
	 * @param message the detail message describing why the plugin was not found
	 */
	public PluginNotFound(String message) {
		super(message);
	}
}
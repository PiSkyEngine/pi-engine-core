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
package org.piengine.core.plugin.impl;

import org.piengine.core.plugin.BasePlugin;
import org.piengine.core.plugin.PluginContext;
import org.piengine.core.plugin.PluginMetadata;

/**
 * Example OpenGL rendering plugin. Not exported, internal to the module.
 */
public class OpenGLRasterPlugin extends BasePlugin {

	public static final PluginMetadata META = PluginMetadata.of("org.piengine:pi-engine-opengl:1.0.0");

	public OpenGLRasterPlugin(PluginMetadata meta) {
		super(meta);
	}

	public OpenGLRasterPlugin() {
		super(META);
	}

	/**
	 * @see org.piengine.core.plugin.BasePlugin#initialize(org.piengine.core.plugin.PluginContext)
	 */
	@Override
	public void initialize(PluginContext context) {
		throw new UnsupportedOperationException("not implemented yet");
	}

}
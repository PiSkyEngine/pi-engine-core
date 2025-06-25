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
package org.piengine.core.plugin.app.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.piengine.core.plugin.Plugin;
import org.piengine.core.plugin.PluginState;
import org.piengine.core.plugin.app.AppPlugin;
import org.piengine.core.scene.Scene;

/**
 * Abstract base class for apps, supporting concurrent execution and pause/unpause.
 * Manages scene and plugins with StructuredTaskScope.
 * Not exported, internal to the module.
 */
public abstract class AbstractApp implements AppPlugin {
    
    /** The scene. */
    protected final Scene scene;
    
    /** The plugins. */
    protected final List<Plugin> plugins = new CopyOnWriteArrayList<>();
    
    /** The status. */
    protected PluginState status = PluginState.SHUTDOWN;

    /**
	 * Instantiates a new abstract app.
	 *
	 * @param scene the scene
	 */
    protected AbstractApp(Scene scene) {
        this.scene = scene;
    }

    /**
	 * Gets the scene.
	 *
	 * @return the scene
	 * @see org.piengine.core.plugin.app.AppPlugin#getScene()
	 */
    @Override
    public Scene getScene() {
        return scene;
    }

    /**
	 * Gets the status.
	 *
	 * @return the status
	 * @see org.piengine.core.plugin.app.AppPlugin#getStatus()
	 */
    @Override
    public PluginState getStatus() {
        return status;
    }

    /**
	 * Initialize scene.
	 */
    protected abstract void initializeScene();
    
    /**
	 * Start scene.
	 */
    protected abstract void startScene();
    
    /**
	 * Update scene.
	 *
	 * @param deltaTime the delta time
	 */
    protected abstract void updateScene(float deltaTime);
    
    /**
	 * Pause scene.
	 */
    protected abstract void pauseScene();
    
    /**
	 * Unpause scene.
	 */
    protected abstract void unpauseScene();
    
    /**
	 * Stop scene.
	 */
    protected abstract void stopScene();
    
    /**
	 * Cleanup scene.
	 */
    protected abstract void cleanupScene();
}
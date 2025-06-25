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

import org.piengine.core.plugin.Plugin;
import org.piengine.core.plugin.PluginId;
import org.piengine.core.plugin.PluginMetadata;
import org.piengine.core.plugin.PluginState;
import org.piengine.core.scene.Scene;

/**
 * AppPlugin for managing a HUD scene.
 * Not exported, internal to the module.
 */
public class HUDApp extends AbstractApp {
    
    /**
	 * Instantiates a new HUD app.
	 *
	 * @param scene the scene
	 */
    public HUDApp(Scene scene) {
        super(scene);
    }

    /**
	 * Initialize scene.
	 *
	 * @see org.piengine.core.plugin.app.impl.AbstractApp#initializeScene()
	 */
    @Override
    protected void initializeScene() {
        // Scene is pre-loaded by ConfigLoader
    }

    /**
	 * Start scene.
	 *
	 * @see org.piengine.core.plugin.app.impl.AbstractApp#startScene()
	 */
    @Override
    protected void startScene() {
        // Activate HUD rendering
    }

    /**
	 * Update scene.
	 *
	 * @param deltaTime the delta time
	 * @see org.piengine.core.plugin.app.impl.AbstractApp#updateScene(float)
	 */
    @Override
    protected void updateScene(float deltaTime) {
        // Update HUD elements
    }

    /**
	 * Pause scene.
	 *
	 * @see org.piengine.core.plugin.app.impl.AbstractApp#pauseScene()
	 */
    @Override
    protected void pauseScene() {
        // Hide HUD
    }

    /**
	 * Unpause scene.
	 *
	 * @see org.piengine.core.plugin.app.impl.AbstractApp#unpauseScene()
	 */
    @Override
    protected void unpauseScene() {
        // Show HUD
    }

    /**
	 * Stop scene.
	 *
	 * @see org.piengine.core.plugin.app.impl.AbstractApp#stopScene()
	 */
    @Override
    protected void stopScene() {
        // Deactivate HUD rendering
    }

    /**
	 * Cleanup scene.
	 *
	 * @see org.piengine.core.plugin.app.impl.AbstractApp#cleanupScene()
	 */
    @Override
    protected void cleanupScene() {
        // Release HUD resources
        scene.getNodes().clear();
    }

	/**
	 * @see org.piengine.core.plugin.app.AppPlugin#initialize()
	 */
	@Override
	public void initialize() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.app.AppPlugin#start()
	 */
	@Override
	public void start() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.app.AppPlugin#update(float)
	 */
	@Override
	public void update(float deltaTime) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.app.AppPlugin#pause()
	 */
	@Override
	public void pause() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.app.AppPlugin#unpause()
	 */
	@Override
	public void unpause() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.app.AppPlugin#stop()
	 */
	@Override
	public void stop() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.app.AppPlugin#cleanup()
	 */
	@Override
	public void cleanup() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.app.AppPlugin#addPlugin(org.piengine.core.plugin.Plugin)
	 */
	@Override
	public void addPlugin(Plugin plugin) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.app.AppPlugin#removePlugin(org.piengine.core.plugin.Plugin)
	 */
	@Override
	public void removePlugin(Plugin plugin) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.Plugin#pluginId()
	 */
	@Override
	public PluginId pluginId() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.Plugin#getMetadata()
	 */
	@Override
	public PluginMetadata getMetadata() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.core.plugin.Plugin#state()
	 */
	@Override
	public PluginState state() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see org.piengine.util.Prioritizable#priority()
	 */
	@Override
	public int priority() {
		throw new UnsupportedOperationException("not implemented yet");
	}
}
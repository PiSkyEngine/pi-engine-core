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
package org.piengine.core.app.impl;

import org.piengine.core.scene.Scene;

/**
 * App for managing a map scene.
 * Not exported, internal to the module.
 */
public class MapApp extends AbstractApp {
    
    /**
	 * Instantiates a new map app.
	 *
	 * @param scene the scene
	 */
    public MapApp(Scene scene) {
        super(scene);
    }

    /**
	 * Initialize scene.
	 *
	 * @see org.piengine.core.app.impl.AbstractApp#initializeScene()
	 */
    @Override
    protected void initializeScene() {
        // Scene is pre-loaded by ConfigLoader
    }

    /**
	 * Start scene.
	 *
	 * @see org.piengine.core.app.impl.AbstractApp#startScene()
	 */
    @Override
    protected void startScene() {
        // Activate map rendering
    }

    /**
	 * Update scene.
	 *
	 * @param deltaTime the delta time
	 * @see org.piengine.core.app.impl.AbstractApp#updateScene(float)
	 */
    @Override
    protected void updateScene(float deltaTime) {
        // Update map elements
    }

    /**
	 * Pause scene.
	 *
	 * @see org.piengine.core.app.impl.AbstractApp#pauseScene()
	 */
    @Override
    protected void pauseScene() {
        // Hide map
    }

    /**
	 * Unpause scene.
	 *
	 * @see org.piengine.core.app.impl.AbstractApp#unpauseScene()
	 */
    @Override
    protected void unpauseScene() {
        // Show map
    }

    /**
	 * Stop scene.
	 *
	 * @see org.piengine.core.app.impl.AbstractApp#stopScene()
	 */
    @Override
    protected void stopScene() {
        // Deactivate map rendering
    }

    /**
	 * Cleanup scene.
	 *
	 * @see org.piengine.core.app.impl.AbstractApp#cleanupScene()
	 */
    @Override
    protected void cleanupScene() {
        // Release map resources
        scene.getNodes().clear();
    }
}
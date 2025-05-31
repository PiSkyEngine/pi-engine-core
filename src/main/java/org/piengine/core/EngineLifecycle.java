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
package org.piengine.core;

import org.piengine.core.app.App;
import org.piengine.core.plugin.Plugin;

/**
 * Interface defining the overall engine lifecycle as a state machine.
 * Supports multiple concurrent apps with start/stop/pause/unpause.
 */
public interface EngineLifecycle extends AutoCloseable {
    
    /**
	 * Initialize.
	 */
    void initialize();
    
    /**
	 * Run.
	 */
    void run();
    
    /**
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    void close();
    
    /**
	 * Load plugin.
	 *
	 * @param plugin the plugin
	 */
    void loadPlugin(Plugin plugin);
    
    /**
	 * Unload plugin.
	 *
	 * @param plugin the plugin
	 */
    void unloadPlugin(Plugin plugin);
    
    /**
	 * Start app.
	 *
	 * @param app the app
	 */
    void startApp(App app);
    
    /**
	 * Stop app.
	 *
	 * @param app the app
	 */
    void stopApp(App app);
    
    /**
	 * Pause app.
	 *
	 * @param app the app
	 */
    void pauseApp(App app);
    
    /**
	 * Unpause app.
	 *
	 * @param app the app
	 */
    void unpauseApp(App app);
}
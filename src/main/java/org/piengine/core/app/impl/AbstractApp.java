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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

import org.piengine.core.app.App;
import org.piengine.core.app.AppStatus;
import org.piengine.core.plugin.Plugin;
import org.piengine.core.scene.Scene;

/**
 * Abstract base class for apps, supporting concurrent execution and pause/unpause.
 * Manages scene and plugins with StructuredTaskScope.
 * Not exported, internal to the module.
 */
public abstract class AbstractApp implements App {
    
    /** The scene. */
    protected final Scene scene;
    
    /** The plugins. */
    protected final List<Plugin> plugins = new CopyOnWriteArrayList<>();
    
    /** The status. */
    protected AppStatus status = AppStatus.UNINITIALIZED;

    /**
	 * Instantiates a new abstract app.
	 *
	 * @param scene the scene
	 */
    protected AbstractApp(Scene scene) {
        this.scene = scene;
    }

    /**
	 * Initialize.
	 *
	 * @see org.piengine.core.app.App#initialize()
	 */
    @Override
    public void initialize() {
        if (status != AppStatus.UNINITIALIZED) {
            throw new IllegalStateException("App already initialized");
        }
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            scope.fork(() -> {
                initializeScene();
                return null;
            });
            plugins.forEach(plugin -> scope.fork(() -> {
                plugin.init();
                plugin.initForApp(this);
                return null;
            }));
            scope.join().throwIfFailed();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("App initialization interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("App initialization failed", e);
        }
        status = AppStatus.INITIALIZED;
    }

    /**
	 * Start.
	 *
	 * @see org.piengine.core.app.App#start()
	 */
    @Override
    public void start() {
        if (status != AppStatus.INITIALIZED && status != AppStatus.STOPPED) {
            throw new IllegalStateException("App must be initialized or stopped to start");
        }
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            scope.fork(() -> {
                startScene();
                return null;
            });
            scope.join().throwIfFailed();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("App start interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("App start failed", e);
        }
        status = AppStatus.RUNNING;
    }

    /**
	 * Update.
	 *
	 * @param deltaTime the delta time
	 * @see org.piengine.core.app.App#update(float)
	 */
    @Override
    public void update(float deltaTime) {
        if (status != AppStatus.RUNNING) {
            return;
        }
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            scope.fork(() -> {
                updateScene(deltaTime);
                return null;
            });
            plugins.forEach(plugin -> scope.fork(() -> {
                plugin.update(deltaTime);
                plugin.processScene(scene);
                return null;
            }));
            scope.join().throwIfFailed();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("App update interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("App update failed", e);
        }
    }

    /**
	 * Pause.
	 *
	 * @see org.piengine.core.app.App#pause()
	 */
    @Override
    public void pause() {
        if (status != AppStatus.RUNNING) {
            throw new IllegalStateException("App must be running to pause");
        }
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            scope.fork(() -> {
                pauseScene();
                return null;
            });
            scope.join().throwIfFailed();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("App pause interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("App pause failed", e);
        }
        status = AppStatus.PAUSED;
    }

    /**
	 * Unpause.
	 *
	 * @see org.piengine.core.app.App#unpause()
	 */
    @Override
    public void unpause() {
        if (status != AppStatus.PAUSED) {
            throw new IllegalStateException("App must be paused to unpause");
        }
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            scope.fork(() -> {
                unpauseScene();
                return null;
            });
            scope.join().throwIfFailed();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("App unpause interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("App unpause failed", e);
        }
        status = AppStatus.RUNNING;
    }

    /**
	 * Stop.
	 *
	 * @see org.piengine.core.app.App#stop()
	 */
    @Override
    public void stop() {
        if (status != AppStatus.RUNNING && status != AppStatus.PAUSED) {
            throw new IllegalStateException("App must be running or paused to stop");
        }
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            scope.fork(() -> {
                stopScene();
                return null;
            });
            scope.join().throwIfFailed();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("App stop interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("App stop failed", e);
        }
        status = AppStatus.STOPPED;
    }

    /**
	 * Cleanup.
	 *
	 * @see org.piengine.core.app.App#cleanup()
	 */
    @Override
    public void cleanup() {
        if (status == AppStatus.UNINITIALIZED || status == AppStatus.TERMINATED) {
            return;
        }
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            scope.fork(() -> {
                cleanupScene();
                return null;
            });
            plugins.forEach(plugin -> scope.fork(() -> {
                plugin.shutdown();
                return null;
            }));
            scope.join().throwIfFailed();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("App cleanup interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("App cleanup failed", e);
        }
        plugins.clear();
        status = AppStatus.TERMINATED;
    }

    /**
	 * Gets the scene.
	 *
	 * @return the scene
	 * @see org.piengine.core.app.App#getScene()
	 */
    @Override
    public Scene getScene() {
        return scene;
    }

    /**
	 * Gets the status.
	 *
	 * @return the status
	 * @see org.piengine.core.app.App#getStatus()
	 */
    @Override
    public AppStatus getStatus() {
        return status;
    }

    /**
	 * Adds the plugin.
	 *
	 * @param plugin the plugin
	 * @see org.piengine.core.app.App#addPlugin(org.piengine.core.plugin.Plugin)
	 */
    @Override
    public void addPlugin(Plugin plugin) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            scope.fork(() -> {
                plugin.init();
                plugin.initForApp(this);
                plugins.add(plugin);
                return null;
            });
            scope.join().throwIfFailed();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Plugin add interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Plugin add failed", e);
        }
    }

    /**
	 * Removes the plugin.
	 *
	 * @param plugin the plugin
	 * @see org.piengine.core.app.App#removePlugin(org.piengine.core.plugin.Plugin)
	 */
    @Override
    public void removePlugin(Plugin plugin) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            scope.fork(() -> {
                plugin.shutdown();
                plugins.remove(plugin);
                return null;
            });
            scope.join().throwIfFailed();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Plugin remove interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Plugin remove failed", e);
        }
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
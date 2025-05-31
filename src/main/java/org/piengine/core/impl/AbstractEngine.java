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
package org.piengine.core.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

import org.piengine.core.EngineLifecycle;
import org.piengine.core.app.App;
import org.piengine.core.app.AppStatus;
import org.piengine.core.plugin.Plugin;

/**
 * Abstract base class for the engine lifecycle, supporting multiple concurrent apps.
 * Uses StructuredTaskScope for concurrent plugin and app management.
 * Not exported, internal to the module.
 */
public abstract class AbstractEngine implements EngineLifecycle {
    
    /** The plugins. */
    protected final List<Plugin> plugins = new CopyOnWriteArrayList<>();
    
    /** The active apps. */
    protected final List<App> activeApps = new CopyOnWriteArrayList<>();
    
    /** The is running. */
    protected boolean isRunning;
    
    /** The config loader. */
    protected final ConfigLoader configLoader = new ConfigLoader();

    /**
     * @see org.piengine.core.EngineLifecycle#initialize()
     */
    @Override
    public void initialize() {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            scope.fork(() -> {
                initializeCore();
                return null;
            });
            scope.fork(() -> {
                configLoader.loadEngineConfig(this, getClass().getClassLoader().getResourceAsStream("engine.yaml"));
                return null;
            });
            scope.join().throwIfFailed();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Initialization interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Initialization failed", e);
        }
    }

    /**
     * @see org.piengine.core.EngineLifecycle#run()
     */
    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {
            try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
                activeApps.stream()
                    .filter(app -> app.getStatus() == AppStatus.RUNNING)
                    .forEach(app -> scope.fork(() -> {
                        app.update(1.0f / 60.0f); // Assume 60 FPS
                        return null;
                    }));
                plugins.forEach(plugin -> scope.fork(() -> {
                    plugin.update(1.0f / 60.0f);
                    return null;
                }));
                scope.join().throwIfFailed();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                isRunning = false;
            } catch (ExecutionException e) {
                throw new RuntimeException("Update failed", e);
            }
        }
    }

    /**
     * @see org.piengine.core.EngineLifecycle#close()
     */
    @Override
    public void close() {
        isRunning = false;
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            activeApps.forEach(app -> scope.fork(() -> {
                app.stop();
                app.cleanup();
                return null;
            }));
            plugins.forEach(plugin -> scope.fork(() -> {
                plugin.shutdown();
                return null;
            }));
            scope.join().throwIfFailed();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Shutdown interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Shutdown failed", e);
        }
        activeApps.clear();
        plugins.clear();
    }

    /**
     * @see org.piengine.core.EngineLifecycle#loadPlugin(org.piengine.core.plugin.Plugin)
     */
    @Override
    public void loadPlugin(Plugin plugin) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            scope.fork(() -> {
                plugin.init();
                plugins.add(plugin);
                return null;
            });
            scope.join().throwIfFailed();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Plugin load interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Plugin load failed", e);
        }
    }

    /**
     * @see org.piengine.core.EngineLifecycle#unloadPlugin(org.piengine.core.plugin.Plugin)
     */
    @Override
    public void unloadPlugin(Plugin plugin) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            scope.fork(() -> {
                plugin.shutdown();
                plugins.remove(plugin);
                return null;
            });
            scope.join().throwIfFailed();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Plugin unload interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Plugin unload failed", e);
        }
    }

    /**
     * @see org.piengine.core.EngineLifecycle#startApp(org.piengine.core.app.App)
     */
    @Override
    public void startApp(App app) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            scope.fork(() -> {
                if (app.getStatus() == AppStatus.UNINITIALIZED) {
                    app.initialize();
                }
                app.start();
                activeApps.add(app);
                return null;
            });
            scope.join().throwIfFailed();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("App start interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("App start failed", e);
        }
    }

    /**
     * @see org.piengine.core.EngineLifecycle#stopApp(org.piengine.core.app.App)
     */
    @Override
    public void stopApp(App app) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            scope.fork(() -> {
                app.stop();
                app.cleanup();
                activeApps.remove(app);
                return null;
            });
            scope.join().throwIfFailed();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("App stop interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("App stop failed", e);
        }
    }

    /**
     * @see org.piengine.core.EngineLifecycle#pauseApp(org.piengine.core.app.App)
     */
    @Override
    public void pauseApp(App app) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            scope.fork(() -> {
                app.pause();
                return null;
            });
            scope.join().throwIfFailed();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("App pause interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("App pause failed", e);
        }
    }

    /**
     * @see org.piengine.core.EngineLifecycle#unpauseApp(org.piengine.core.app.App)
     */
    @Override
    public void unpauseApp(App app) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            scope.fork(() -> {
                app.unpause();
                return null;
            });
            scope.join().throwIfFailed();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("App unpause interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("App unpause failed", e);
        }
    }

    /**
     * Initialize core engine components (e.g., renderer, input).
     * Subclasses override to provide specific initialization.
     * @return null to satisfy StructuredTaskScope
     */
    protected abstract Void initializeCore();
}
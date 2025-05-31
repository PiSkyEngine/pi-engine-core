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

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

import org.piengine.commons.math.Vector3D.Vector3f;
import org.piengine.core.app.App;
import org.piengine.core.app.impl.WorldApp;
import org.piengine.core.plugin.Plugin;
import org.piengine.core.scene.Light;
import org.piengine.core.scene.Scene;
import org.piengine.core.scene.SceneNode;
import org.piengine.core.scene.Transform;
import org.yaml.snakeyaml.Yaml;

/**
 * Loads engine and app configurations from YAML files using SnakeYAML.
 * Not exported, internal to the module.
 */
public class ConfigLoader {
    
    /** The yaml. */
    private final Yaml yaml = new Yaml();

    /**
	 * Load engine config.
	 *
	 * @param engine       the engine
	 * @param configStream the config stream
	 */
    public void loadEngineConfig(AbstractEngine engine, InputStream configStream) {
        Map<String, Object> config = yaml.load(configStream);
        Map<String, Object> engineConfig = (Map<String, Object>) config.get("engine");

        List<Map<String, Object>> plugins = (List<Map<String, Object>>) engineConfig.get("plugins");
        if (plugins != null) {
            try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
                for (Map<String, Object> pluginConfig : plugins) {
                    scope.fork(() -> {
                        String path = (String) pluginConfig.get("path");
                        Plugin plugin = loadPlugin(path);
                        engine.loadPlugin(plugin);
                        return null;
                    });
                }
                scope.join().throwIfFailed();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Plugin loading interrupted", e);
            } catch (ExecutionException e) {
                throw new RuntimeException("Plugin loading failed", e);
            }
        }

        List<Map<String, Object>> apps = (List<Map<String, Object>>) engineConfig.get("apps");
        if (apps != null) {
            try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
                for (Map<String, Object> appConfig : apps) {
                    scope.fork(() -> {
                        App app = createApp(appConfig);
                        engine.startApp(app);
                        String status = (String) appConfig.get("status");
                        if ("PAUSED".equals(status)) {
                            engine.pauseApp(app);
                        }
                        return null;
                    });
                }
                scope.join().throwIfFailed();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("App loading interrupted", e);
            } catch (ExecutionException e) {
                throw new RuntimeException("App loading failed", e);
            }
        }
    }

    /**
	 * Load scene.
	 *
	 * @param scenePath the scene path
	 * @return the scene
	 */
    public Scene loadScene(String scenePath) {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(scenePath)) {
            Map<String, Object> sceneConfig = yaml.load(stream);
            Scene scene = new Scene("main");
            Map<String, Object> sceneData = (Map<String, Object>) sceneConfig.get("scene");
            List<Map<String, Object>> nodes = (List<Map<String, Object>>) sceneData.get("nodes");
            for (Map<String, Object> node : nodes) {
                SceneNode sceneNode = createSceneNode(node);
                scene.addNode(sceneNode);
            }
            return scene;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load scene: " + scenePath, e);
        }
    }

    /**
	 * Load plugin.
	 *
	 * @param path the path
	 * @return the plugin
	 */
    private Plugin loadPlugin(String path) {
        ServiceLoader<Plugin> loader = ServiceLoader.load(Plugin.class);
        for (Plugin plugin : loader) {
            if (plugin.getClass().getName().contains(path)) {
                return plugin;
            }
        }
        throw new RuntimeException("Plugin not found: " + path);
    }

    /**
	 * Creates the app.
	 *
	 * @param appConfig the app config
	 * @return the app
	 */
    private App createApp(Map<String, Object> appConfig) {
        String scenePath = (String) appConfig.get("scene");
        Scene scene = loadScene(scenePath);
        return new WorldApp(scene);
    }

    /**
	 * Creates the scene node.
	 *
	 * @param nodeConfig the node config
	 * @return the scene node
	 */
    private SceneNode createSceneNode(Map<String, Object> nodeConfig) {
        String id = (String) nodeConfig.get("id");
        String type = (String) nodeConfig.get("type");
        SceneNode node = new SceneNode(id);
        if ("mesh".equals(type)) {
            String geometry = (String) nodeConfig.get("geometry");
            String material = (String) nodeConfig.get("material");
            node.setMesh(geometry);
            node.setMaterial(material);
        } else if ("point_light".equals(type)) {
            List<Double> color = (List<Double>) nodeConfig.get("color");
            Double intensity = (Double) nodeConfig.get("intensity");
            node.setLight(new Light(Light.Type.POINT, color, intensity));
        }
        Map<String, List<Double>> transform = (Map<String, List<Double>>) nodeConfig.get("transform");
        if (transform != null) {
            List<Double> position = transform.get("position");
            List<Double> rotation = transform.get("rotation");
            List<Double> scale = transform.get("scale");
            node.setTransform(new Transform(
                new Vector3f(position.get(0).floatValue(), position.get(1).floatValue(), position.get(2).floatValue()),
                new Vector3f(rotation.get(0).floatValue(), rotation.get(1).floatValue(), rotation.get(2).floatValue()),
                new Vector3f(scale.get(0).floatValue(), scale.get(1).floatValue(), scale.get(2).floatValue())
            ));
        }
        return node;
    }
}
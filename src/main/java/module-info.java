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

/**
 * The {@code pi.game.engine.core} module is the central component of the
 * Pie in the Sky (PI) 3D graphics engine, providing the core APIs for building
 * 3D graphics applications. It manages scene graphs, plugins, application states,
 * task scheduling, input handling, and profiles.
 *
 * <h2>Purpose</h2>
 * This module serves as the backbone of the PI engine, orchestrating the
 * interaction between various components (e.g., rendering, physics, audio)
 * through a plugin system. It provides the foundational infrastructure for
 * creating modular and extensible 3D applications.
 *
 * <h2>Exported Packages</h2>
 * <ul>
 *   <li>{@code org.pi.game.engine.core}: Core classes, including {@code Scene}
 *       and {@code SceneNode} for scene graph management.</li>
 *   <li>{@code org.pi.game.engine.core.plugin}: Plugin system, including the
 *       {@code Plugin} interface and {@code PluginManager} for runtime loading
 *       of {@code .pip} JARs.</li>
 *   <li>{@code org.pi.game.engine.core.scene}: Scene management classes for
 *       handling 3D scenes and nodes.</li>
 *   <li>{@code org.pi.game.engine.core.state}: Application state management,
 *       including {@code AppState} and {@code StateManager} for managing game
 *       states (e.g., menu, gameplay).</li>
 *   <li>{@code org.pi.game.engine.core.task}: Task scheduling with {@code Task}
 *       and {@code TaskScheduler}, using StructuredTaskScope for multi-threading.</li>
 *   <li>{@code org.pi.game.engine.core.input}: Input handling with
 *       {@code InputManager} for processing keyboard, mouse, and other inputs.</li>
 *   <li>{@code org.pi.game.engine.core.profile}: Profile management with
 *       {@code Profile} and {@code ProfileManager} for different workflows
 *       (e.g., development, public release).</li>
 * </ul>
 *
 * <h2>Dependencies</h2>
 * <ul>
 *   <li>{@code java.base}: Required for core Java functionality, including
 *       Java 23 features like StructuredTaskScope and Virtual Threads.</li>
 *   <li>{@code pi.engine.math}: Provides mathematical abstractions (e.g.,
 *       {@code Cartesian3f}, {@code Vector3f}) for positioning and transformations.</li>
 *   <li>{@code pi.engine.util}: Provides utilities for concurrency (e.g.,
 *       {@code UpgradableReadWriteLock}), event handling (e.g., {@code Omnibus}),
 *       and resource management (e.g., {@code Registration}).</li>
 *   <li>{@code org.yaml.snakeyaml}: Used for parsing YAML configurations, such
 *       as {@code .pis} scene files and profiles.</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * This module is the primary entry point for building applications with the PI
 * engine. It integrates with other modules (e.g., rendering, physics) via plugins
 * and provides the core infrastructure for scene management and state transitions.
 *
 * <h3>Example: Creating a Scene with a Plugin</h3>
 * <pre>
 * import org.pi.game.engine.core.scene.Scene;
 * import org.pi.game.engine.core.scene.SceneNode;
 * import org.pi.game.engine.core.plugin.Plugin;
 * import org.pi.game.engine.core.state.AppState;
 * import org.pi.game.engine.core.state.StateManager;
 * import org.piengine.commons.math.coordinates.Cartesian3f;
 *
 * class SimplePlugin implements Plugin {
 *     public void init() { System.out.println("SimplePlugin initialized"); }
 *     public void update(float dt) { System.out.println("SimplePlugin updating"); }
 *     public void shutdown() { System.out.println("SimplePlugin shutdown"); }
 * }
 *
 * Scene scene = new Scene();
 * SceneNode node = scene.createNode("cube1");
 * node.setPosition(new Cartesian3f(1.0f, 2.0f, 3.0f));
 * node.setMesh("cube.pio");
 *
 * StateManager stateManager = new StateManager();
 * AppState gameState = new AppState("GameState");
 * gameState.addPlugin(new SimplePlugin());
 * stateManager.addState(gameState);
 * stateManager.setCurrentState("GameState");
 * stateManager.update(0.016f);
 * stateManager.shutdown();
 * </pre>
 *
 * <h2>Version</h2>
 * <ul>
 *   <li><b>Current Version</b>: 0.0.1-SNAPSHOT (as of May 30, 2025)</li>
 * </ul>
 *
 * @since 0.0.1
 */
module org.piengine.core {
    exports org.piengine.core;
    exports org.piengine.core.plugin;
    exports org.piengine.core.app;
    exports org.piengine.core.scene;
    
    requires org.yaml.snakeyaml;
    requires java.base;
    requires transitive org.piengine.math;
    requires transitive org.piengine.util;
}
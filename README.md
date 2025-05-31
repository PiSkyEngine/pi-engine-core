# PI Engine Core Module

**Version**: 0.0.1-SNAPSHOT  
**Date**: May 30, 2025  
**License**: MIT  

The `pi-engine-core` module is the central component of the [Pie in the Sky (PI) 3D Graphics Engine](https://www.piengine.org), providing the foundational APIs for building 3D applications. This module handles core engine functionality, including scene management, plugin loading, application states, task scheduling, input handling, and profiles, enabling developers to create modular and extensible 3D graphics applications.

## Overview

The `pi-engine-core` module includes:

- **Scene Management**: `Scene` and `SceneNode` classes for managing 3D scene graphs, loaded from `.pis` files.
- **Plugin System**: `Plugin` interface and `PluginManager` for runtime loading/unloading of `.pip` JARs, supporting components like rendering, physics, and audio.
- **Application States**: `AppState` and `StateManager` for managing game states (e.g., `MenuState`, `GameState`).
- **Task Scheduling**: `Task` and `TaskScheduler` using StructuredTaskScope and Virtual Threads for multi-threaded operations.
- **Input Handling**: `InputManager` for processing keyboard, mouse, and other input devices.
- **Profiles**: `Profile` and `ProfileManager` for managing different workflows (e.g., development, public release).

This module depends on `pi-engine-math` for coordinates and shapes, and `pi-engine-util` for concurrency and event handling utilities.

## Installation

The `pi-engine-core` module is available on Maven Central. Add it to your project’s `pom.xml`:

```xml
<dependency>
    <groupId>org.piengine</groupId>
    <artifactId>pi-engine-core</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### Requirements
- **Java**: 23 or higher (with `--enable-preview` for features like StructuredTaskScope and Virtual Threads).
- **Dependencies**:
  - `pi-engine-math` (for coordinates and shapes).
  - `pi-engine-util` (for concurrency and event handling).
  - SnakeYAML (for parsing `.pis` files and profiles).

## Usage Examples

### Example 1: Creating a Scene with SceneNode
Create a simple scene with a node representing a 3D object (e.g., a cube):

```java
import org.pi.game.engine.core.scene.Scene;
import org.pi.game.engine.core.scene.SceneNode;
import org.piengine.commons.math.coordinates.Cartesian3f;

public class SceneExample {
    public static void main(String[] args) {
        // Create a new scene
        Scene scene = new Scene();

        // Create a scene node for a cube
        SceneNode cubeNode = scene.createNode("cube1");

        // Set the cube's position using Cartesian3f
        cubeNode.setPosition(new Cartesian3f(1.0f, 2.0f, 3.0f));

        // Assign a mesh to the node
        cubeNode.setMesh("cube.pio");

        // Print the node's position
        Cartesian3f position = cubeNode.getPosition();
        System.out.println("Cube Position: (" + position.xf() + ", " + position.yf() + ", " + position.zf() + ")");
    }
}
```

**Output**:
```
Cube Position: (1.0, 2.0, 3.0)
```

### Example 2: Managing Application States with Plugins
Define and manage application states (e.g., `MenuState`, `GameState`) with plugins:

```java
import org.pi.game.engine.core.plugin.Plugin;
import org.pi.game.engine.core.state.AppState;
import org.pi.game.engine.core.state.StateManager;

public class AppStateExample {
    // Simple plugin for rendering
    static class RenderPlugin implements Plugin {
        @Override
        public void init() { System.out.println("RenderPlugin initialized"); }
        @Override
        public void update(float dt) { System.out.println("RenderPlugin updating with dt: " + dt); }
        @Override
        public void shutdown() { System.out.println("RenderPlugin shutdown"); }
    }

    public static void main(String[] args) {
        // Create a state manager
        StateManager stateManager = new StateManager();

        // Define a game state with a render plugin
        AppState gameState = new AppState("GameState");
        gameState.addPlugin(new RenderPlugin());

        // Add the state to the manager
        stateManager.addState(gameState);

        // Switch to the game state
        stateManager.setCurrentState("GameState");

        // Simulate an update loop
        stateManager.update(0.016f); // 60 FPS frame time

        // Shutdown the state manager
        stateManager.shutdown();
    }
}
```

**Output**:
```
RenderPlugin initialized
RenderPlugin updating with dt: 0.016
RenderPlugin shutdown
```

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository: `https://github.com/PiSkyEngine/pisky-engine`.
2. Create a branch for your feature (`git checkout -b feature/new-plugin`).
3. Commit your changes (`git commit -m "Add new input plugin"`).
4. Push to your branch (`git push origin feature/new-plugin`).
5. Open a pull request on GitHub.

For more details, see the [Contributing Guide](https://github.com/PiSkyEngine/pisky-engine/wiki/Contributing) in the project wiki.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

```text
MIT License

Copyright (c) 2025 Sly Technologies Inc.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```

## Links
- **Project Website**: [www.piengine.org](https://www.piengine.org)
- **GitHub Wiki**: [PiSkyEngine/pisky-engine Wiki](https://github.com/PiSkyEngine/pisky-engine/wiki)
- **Repository**: [PiSkyEngine/pisky-engine/pi-engine-core](https://github.com/PiSkyEngine/pisky-engine/tree/main/pi-engine-core)
- **Issue Tracker**: [GitHub Issues](https://github.com/PiSkyEngine/pisky-engine/issues)



---

### **Explanation of the `README.md`**

#### **Structure**
- **Header**: Includes the module name (`pi-engine-core`), version (`0.0.1-SNAPSHOT`), date (May 30, 2025, as per the current date), and license (MIT).
- **Overview**: Describes the module’s purpose (core engine APIs), key features (scene management, plugins, app states, task scheduling, input, profiles), and its dependencies on `pi-engine-math` and `pi-engine-util`.
- **Installation**:
  - Provides Maven dependency snippet for `pi-engine-core`.
  - Lists requirements (Java 23 with `--enable-preview`, dependencies on `pi-engine-math`, `pi-engine-util`, and SnakeYAML).
- **Usage Examples**:
  - **Example 1**: Demonstrates creating a `Scene` with a `SceneNode`, setting its position using `Cartesian3f` (from `pi-engine-math`), and assigning a mesh. This showcases scene management, a core feature of the PI engine.
    - Output: Prints the node’s position, confirming the setup.
  - **Example 2**: Shows managing application states with a simple `RenderPlugin`, demonstrating state transitions, plugin lifecycle, and updates. This highlights the plugin system and app state management.
    - Output: Shows the plugin’s lifecycle (init, update, shutdown) during a simulated game loop.
- **Contributing**: Provides standard GitHub contribution steps, linking to a wiki page for detailed guidelines.
- **License**: Includes the MIT license text, consistent with the project’s license.
- **Links**:
  - Project website: `www.piengine.org`.
  - GitHub wiki: `https://github.com/PiSkyEngine/pisky-engine/wiki`.
  - Repository path: Assumed as `PiSkyEngine/pisky-engine/pi-engine-core`.
  - Issue tracker: Links to GitHub issues.

#### **Alignment with PI Engine**
- **Module Name**: Reflects the `pi-engine-core` name.
- **Packages**: Uses `org.pi.game.engine.core.scene`, `org.pi.game.engine.core.plugin`, `org.pi.game.engine.core.state`, and `org.piengine.commons.math.coordinates`, as per the specification.
- **Version**: Matches the project version (`0.0.1-SNAPSHOT`).
- **Java 23**: Noted in requirements, with `--enable-preview` for features like StructuredTaskScope and Virtual Threads.
- **Dependencies**: Highlights reliance on `pi-engine-math` (for `Cartesian3f`) and `pi-engine-util` (for concurrency/event handling, implied in the app state example).

#### **Examples**
- **Scene and SceneNode**: Shows how to create and manipulate a scene graph, a fundamental feature for 3D graphics applications, using coordinates from `pi-engine-math`.
- **App State with Plugin**: Demonstrates the plugin system and state management, key aspects of the PI engine’s modular architecture, with a simple `RenderPlugin` to illustrate the lifecycle.
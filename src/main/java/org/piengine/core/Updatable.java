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

import org.piengine.core.engine.EngineWorker;

/**
 * Functional interface for components that require per-frame updates in the PIEngine framework.
 * <p>
 * This interface defines a lightweight contract for entities that need to perform work on each
 * engine loop cycle, such as rendering, physics calculations, animation updates, inverse kinematics,
 * or other frame-dependent processing. Implementations should be stateless or thread-safe as they
 * may be called from single or multi-threaded updaters.
 * </p>
 * <p>
 * The {@code Updatable} interface is designed as a functional interface, allowing for lambda
 * expressions and method references to be used for simple update logic:
 * </p>
 * <pre>
 * // Lambda expression
 * Updatable simpleUpdater = (tpf) -> {
 *     // Perform frame update logic
 * };
 * 
 * // Method reference
 * Updatable physicsUpdater = this::updatePhysics;
 * </pre>
 * <p>
 * For more complex use cases requiring full plugin lifecycle management, consider extending
 * {@code BaseUpdatePlugin} which implements both this interface and the {@code Plugin} interface,
 * providing initialization, start/stop, and other lifecycle methods.
 * </p>
 * <p>
 * The engine coordinates updates through a phaser barrier system, allowing implementations to
 * report their work status and participate in synchronized frame completion across multiple threads.
 * </p>
 *
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 * @see EngineApp
 * @see EngineWorker
 * @since 1.0
 */
@FunctionalInterface
public interface Updatable {

	/**
	 * Performs updates for a new frame, called once per engine loop cycle.
	 * <p>
	 * This method is invoked by the engine's update system and should perform all necessary
	 * work for the current frame. The implementation should use the provided time-per-frame
	 * value to ensure frame-rate independent behavior. For multi-threaded scenarios, this
	 * method should arrive at the phaser barrier and report the amount of work performed.
	 * </p>
	 * <p>
	 * Common use cases include:
	 * <ul>
	 * <li>Rendering scene objects and UI elements</li>
	 * <li>Updating physics simulations and collision detection</li>
	 * <li>Processing animation timelines and keyframes</li>
	 * <li>Calculating inverse kinematics for character rigs</li>
	 * <li>Updating particle systems and effects</li>
	 * <li>Processing input and updating game state</li>
	 * </ul>
	 * </p>
	 * <p>
	 * Example implementation:
	 * <pre>
	 * &#64;Override
	 * public void updateFrame(float tpf) {
	 *     // Update animation time
	 *     animationTime += tpf;
	 *     
	 *     // Process physics
	 *     physicsWorld.step(tpf);
	 *     
	 *     // Render scene
	 *     renderer.renderScene(scene, camera);
	 * }
	 * </pre>
	 * </p>
	 * 
	 * @param tpf the time elapsed since the last frame in seconds, used for frame-rate
	 *            independent calculations
	 */
	void updateFrame(float tpf);
}
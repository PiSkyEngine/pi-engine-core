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
package org.piengine.core.scene;

import org.piengine.commons.math.Vector3D.Vector3f;

/**
 * Represents a node in the scenegraph (e.g., mesh, light, camera).
 */
public class SceneNode {

	/** The id. */
	private final String id;

	/** The mesh. */
	private String mesh;

	/** The material. */
	private String material;

	/** The light. */
	private Light light;

	/** The transform. */
	private Transform transform;

	/**
	 * Instantiates a new scene node.
	 *
	 * @param id the id
	 */
	public SceneNode(String id) {
		this.id = id;
		this.transform = new Transform(
				new Vector3f(0, 0, 0),
				new Vector3f(0, 0, 0),
				new Vector3f(1, 1, 1));
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the mesh.
	 *
	 * @return the mesh
	 */
	public String getMesh() {
		return mesh;
	}

	/**
	 * Sets the mesh.
	 *
	 * @param mesh the new mesh
	 */
	public void setMesh(String mesh) {
		this.mesh = mesh;
	}

	/**
	 * Gets the material.
	 *
	 * @return the material
	 */
	public String getMaterial() {
		return material;
	}

	/**
	 * Sets the material.
	 *
	 * @param material the new material
	 */
	public void setMaterial(String material) {
		this.material = material;
	}

	/**
	 * Gets the light.
	 *
	 * @return the light
	 */
	public Light getLight() {
		return light;
	}

	/**
	 * Sets the light.
	 *
	 * @param light the new light
	 */
	public void setLight(Light light) {
		this.light = light;
	}

	/**
	 * Gets the transform.
	 *
	 * @return the transform
	 */
	public Transform getTransform() {
		return transform;
	}

	/**
	 * Sets the transform.
	 *
	 * @param transform the new transform
	 */
	public void setTransform(Transform transform) {
		this.transform = transform;
	}
}
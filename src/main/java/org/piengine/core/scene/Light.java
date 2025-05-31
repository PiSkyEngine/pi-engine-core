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

import java.util.List;

/**
 * Represents a light in the scene.
 */
public class Light {
    
    /**
	 * The Enum Type.
	 */
    public enum Type { 
 /** The point. */
 POINT, 
 /** The directional. */
 DIRECTIONAL, 
 /** The spot. */
 SPOT }
    
    /** The type. */
    private final Type type;
    
    /** The color. */
    private final List<Double> color;
    
    /** The intensity. */
    private final double intensity;

    /**
	 * Instantiates a new light.
	 *
	 * @param type      the type
	 * @param color     the color
	 * @param intensity the intensity
	 */
    public Light(Type type, List<Double> color, double intensity) {
        this.type = type;
        this.color = color;
        this.intensity = intensity;
    }

    /**
	 * Gets the type.
	 *
	 * @return the type
	 */
    public Type getType() {
        return type;
    }

    /**
	 * Gets the color.
	 *
	 * @return the color
	 */
    public List<Double> getColor() {
        return color;
    }

    /**
	 * Gets the intensity.
	 *
	 * @return the intensity
	 */
    public double getIntensity() {
        return intensity;
    }
}
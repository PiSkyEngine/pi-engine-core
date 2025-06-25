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

package org.piengine.core.engine;

import java.util.Collections;
import java.util.concurrent.ThreadFactory;

import org.piengine.util.config.Config;
import org.piengine.util.config.ConfigSchema;

/**
 * Configuration class for the Pie in the Sky (PI) 3D Graphics PiEngine, managing core engine settings
 * such as threading, window properties, and plugin configurations. Extends {@link Config} to provide
 * a YAML-based configuration system, as defined in the PI engine's modular architecture. This class
 * is part of the {@code pi-engine-core} module and supports the engine's multi-threaded pipeline
 * by configuring thread creation through a customizable thread factory.
 *
 * <p>The configuration schema includes critical settings like minimum and maximum thread counts,
 * window dimensions, fullscreen mode, and a thread factory for task execution. These settings are
 * accessible via a YAML configuration file or programmatically through this class's methods.</p>
 *
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 * @see Config
 * @see ConfigSchema
 * @see ThreadFactory
 */
public class EngineConfig extends Config {
    private static final int MAX_PROCESSORS = Integer.MAX_VALUE;
    private static final ConfigSchema SCHEMA = new ConfigSchema()
            .define("engine.threads.min", 1).critical()
            .define("engine.threads.max", MAX_PROCESSORS).useSystemProperty()
            .define("engine.window.width", 1280)
            .define("engine.window.height", 720)
            .define("engine.window.fullscreen", false)
            .define("engine.window.resizable", false)
            .define("engine.window.title", "")
            .defineList("engine.plugins", String.class, Collections.emptyList())
            .defineInstance("engine.threads.factory", ThreadFactory.class, Thread::startVirtualThread);

    /**
     * Constructs a new {@code EngineConfig} instance with the predefined configuration schema
     * for the PI engine. The schema defines default values for threading, window properties,
     * and the thread factory, which can be overridden via YAML configuration files or
     * programmatic updates. The default thread factory is configured to support the engine's
     * multi-threaded pipeline.
     *
     * @see ConfigSchema
     * @see ThreadFactory
     */
    public EngineConfig() {
        super(SCHEMA);
    }

    /**
     * Sets the thread factory used for creating threads in the PI engine's multi-threaded pipeline.
     * The thread factory determines how tasks, such as rendering, physics, or plugin processing,
     * are executed, allowing flexibility in thread creation strategies.
     *
     * @param threadFactory the {@link ThreadFactory} to use for creating engine threads
     * @return this {@code EngineConfig} instance for method chaining
     * @throws IllegalArgumentException if the thread factory is null
     * @see ThreadFactory
     */
    public EngineConfig threadsFactory(ThreadFactory threadFactory) {
        return (EngineConfig) super.putInstance("engine.threads.factory", threadFactory);
    }

    /**
     * Retrieves the thread factory configured for the PI engine's task execution.
     * The factory defines how threads are created for tasks like rendering, physics, and plugin updates,
     * supporting the engine's multi-threaded architecture.
     *
     * @return the {@link ThreadFactory} instance configured for thread creation
     * @throws IllegalStateException if the thread factory is not set or improperly configured
     * @see ThreadFactory
     */
    public ThreadFactory threadsFactory() {
        return super.get("engine.threads.factory").asInstance();
    }

    /**
     * Sets the minimum number of threads the PI engine should use in its multi-threaded pipeline.
     * This setting ensures a baseline level of parallelism for tasks such as scene updates,
     * rendering, and plugin processing. The value must be at least 1, as enforced by the
     * configuration schema.
     *
     * @param minThreadCount the minimum number of threads (must be at least 1)
     * @return this {@code EngineConfig} instance for method chaining
     * @throws IllegalArgumentException if the thread count is less than 1
     */
    public EngineConfig threadsMin(int minThreadCount) {
        return (EngineConfig) super.put("engine.threads.min", minThreadCount);
    }

    /**
     * Sets the maximum number of threads the PI engine should use in its multi-threaded pipeline.
     * This setting caps the number of concurrent tasks to optimize resource usage while leveraging
     * modern hardware. The maximum value is capped at {@link Integer#MAX_VALUE}, and the setting
     * can be influenced by system properties as defined in the configuration schema.
     *
     * @param maxThreadCount the maximum number of threads
     * @return this {@code EngineConfig} instance for method chaining
     * @throws IllegalArgumentException if the thread count is less than the minimum thread count
     */
    public EngineConfig threadsMax(int maxThreadCount) {
        return (EngineConfig) super.put("engine.threads.max", maxThreadCount);
    }
}
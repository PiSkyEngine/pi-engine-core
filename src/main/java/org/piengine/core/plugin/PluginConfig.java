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
package org.piengine.core.plugin;

import org.piengine.util.config.Config;
import org.piengine.util.config.ConfigSchema;

/**
 * Plugin configuration for the PiEngine framework, defining properties for plugin
 * execution and threading behavior. The schema provides factory default values
 * inherited by all plugins, unless overridden at the subclass level. This class
 * extends Config to include executor-level properties for task execution modes
 * and scheduling.
 *
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 */
public class PluginConfig extends Config {
    private static final ConfigSchema SCHEMA = new ConfigSchema()
            /*
             * Enables multi-threaded execution for the plugin in a plugin-engine
             * container. When false, the plugin runs in the single-threaded main
             * engine loop, potentially blocking other plugins. PiEngine is designed
             * for multi-threaded execution, so this should typically be true.
             */
            .define("plugin.threads.multi", true)
            /*
             * Specifies the state transition policy for the plugin lifecycle:
             * - "one-step": Allows only direct transitions between adjacent states
             *   (e.g., STOPPED to RUNNING).
             * - "jump-to": Permits direct transitions to any state.
             * - "step-through": Requires passing through all intermediate states.
             */
            .define("plugin.state.policy", "one-step")
            /*
             * Number of threads in the pool for SHARED execution mode, used for
             * @PluginSharedTask and @TaskExecution in SHARED or AUTO modes.
             * Minimum value is 1.
             */
            .define("plugin.execution.sharedPoolSize", 1)
            /*
             * Enables virtual threads for DEDICATED execution mode, used for
             * @PluginBackground, @TaskExecution in DEDICATED or HYBRID modes,
             * and Runnable plugins. Virtual threads improve scalability for
             * lightweight tasks.
             */
            .define("plugin.execution.useVirtualThreads", true)
            /*
             * Timeout in milliseconds for @PluginSharedTask methods in SHARED mode.
             * Tasks exceeding this duration may be logged or interrupted to ensure
             * non-blocking behavior. A value of 0 disables the timeout.
             */
            .define("plugin.execution.sharedTaskTimeoutMillis", 100L)
            /*
             * Specifies the executor type for task execution:
             * - "JDK": Uses JDK ExecutorService implementations.
             * - "CUSTOM": Uses a custom TaskExecutor implementation.
             */
            .define("plugin.execution.executorType", "JDK");

    /**
     * Instantiates a new plugin config with the default schema.
     */
    protected PluginConfig() {
        super(SCHEMA);
    }

    /**
     * Instantiates a new plugin config with a custom schema.
     *
     * @param schema the schema to use
     */
    protected PluginConfig(ConfigSchema schema) {
        super(schema);
    }

    /**
     * Checks if multi-threaded execution is enabled for the plugin.
     *
     * @return true if multi-threaded execution is enabled, false otherwise
     */
    public boolean isMultiThreaded() {
        return get("plugin.threads.multi").asBoolean();
    }

    /**
     * Gets the state transition policy for the plugin lifecycle.
     *
     * @return the state policy ("one-step", "jump-to", or "step-through")
     */
    public String getStatePolicy() {
        return get("plugin.state.policy").asString();
    }

    /**
     * Gets the number of threads in the pool for SHARED execution mode.
     *
     * @return the shared pool size, guaranteed to be at least 1
     */
    public int getSharedPoolSize() {
        return Math.max(1, get("plugin.execution.sharedPoolSize").asInt());
    }

    /**
     * Checks if virtual threads are enabled for DEDICATED execution mode.
     *
     * @return true if virtual threads are enabled, false otherwise
     */
    public boolean useVirtualThreads() {
        return get("plugin.execution.useVirtualThreads").asBoolean();
    }

    /**
     * Gets the timeout in milliseconds for @PluginSharedTask in SHARED mode.
     *
     * @return the timeout in milliseconds, 0 if disabled
     */
    public long getSharedTaskTimeoutMillis() {
        return Math.max(0, get("plugin.execution.sharedTaskTimeoutMillis").asLong());
    }

    /**
     * Gets the executor type for task execution.
     *
     * @return the executor type ("JDK" or "CUSTOM")
     */
    public String getExecutorType() {
        return get("plugin.execution.executorType").asString();
    }
}
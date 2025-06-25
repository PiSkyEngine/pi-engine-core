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
package org.piengine.core.plugin.registry;

import org.piengine.core.engine.EngineException;

/**
 * Exception thrown when a plugin fails verification in the PIEngine framework.
 * <p>
 * This exception is raised by a {@link PluginVerifier} when a plugin's authenticity or
 * integrity cannot be verified, typically due to an invalid or untrusted
 * {@link PluginSignature}. It prevents the plugin from being loaded by the
 * {@link PluginRegistry}, ensuring that only trusted plugins are used. Common causes
 * include mismatched signatures, expired certificates, or tampered plugin files.
 * </p>
 * <p>
 * Example usage:
 * 
 * <pre>
 * PluginRegistry registry = PluginRegistry.newInstance();
 * try {
 *     MyPlugin plugin = registry.loadPlugin(MyPlugin.class, "org.example:my-plugin:1.0.0");
 * } catch (PluginVerificationFailure e) {
 *     System.err.println("Plugin verification failed: " + e.getMessage());
 * }
 * </pre>
 * </p>
 *
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 * @see PluginVerifier
 * @see PluginSignature
 * @see PluginRegistry
 * @since 1.0
 */
public class PluginVerificationFailure extends EngineException {

    private static final long serialVersionUID = -6393382519706632571L;

    /**
     * Constructs a new verification failure exception with no detail message.
     */
    public PluginVerificationFailure() {
        super();
    }

    /**
     * Constructs a new verification failure exception with the specified detail message.
     *
     * @param message the detail message describing the verification failure
     */
    public PluginVerificationFailure(String message) {
        super(message);
    }

    /**
     * Constructs a new verification failure exception with the specified cause.
     *
     * @param cause the underlying cause of the verification failure
     */
    public PluginVerificationFailure(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new verification failure exception with the specified detail message
     * and cause.
     *
     * @param message the detail message describing the verification failure
     * @param cause   the underlying cause of the verification failure
     */
    public PluginVerificationFailure(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new verification failure exception with the specified detail message,
     * cause, suppression enablement, and writable stack trace.
     *
     * @param message            the detail message describing the verification failure
     * @param cause              the underlying cause of the verification failure
     * @param enableSuppression  whether suppression is enabled or disabled
     * @param writableStackTrace whether the stack trace is writable
     */
    public PluginVerificationFailure(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
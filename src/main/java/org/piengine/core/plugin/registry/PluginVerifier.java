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

import java.util.Optional;

import org.piengine.core.plugin.Plugin;

/**
 * Interface for verifying the integrity and authenticity of plugins in the PIEngine framework.
 * <p>
 * Implementations of this interface are responsible for validating plugins, typically by checking
 * their digital signatures against a trusted certificate. The verification process ensures that
 * plugins loaded into the {@link PluginRegistry} are authentic and have not been tampered with.
 * If verification fails, a {@link PluginVerificationFailure} is thrown to prevent the plugin from
 * being used.
 * </p>
 * <p>
 * The default implementation of {@link #verifyPlugin(Plugin)} checks for the presence of a
 * {@link PluginSignature} in the plugin's metadata and delegates to the
 * {@link #verifyPlugin(Plugin, PluginSignature)} method if a signature is present. Implementors
 * must provide the logic for verifying the plugin against its signature.
 * </p>
 *
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 * @see Plugin
 * @see PluginSignature
 * @see PluginVerificationFailure
 * @since 1.0
 */
public interface PluginVerifier {

    /**
     * Verifies a plugin's integrity and authenticity using its metadata.
     * <p>
     * This default method checks if the plugin's metadata contains a {@link PluginSignature}.
     * If a signature is present, it calls {@link #verifyPlugin(Plugin, PluginSignature)} to
     * perform the verification. If no signature is present, the method returns without
     * throwing an exception, assuming no verification is required.
     * </p>
     *
     * @param plugin the plugin to verify
     * @throws PluginVerificationFailure if the verification process fails
     * @throws NullPointerException if the plugin is null
     */
    default void verifyPlugin(Plugin plugin) throws PluginVerificationFailure {
        Optional<PluginSignature> signature = plugin.getMetadata().signature();
        if (signature.isEmpty())
            return;

        verifyPlugin(plugin, signature.get());
    }

    /**
     * Verifies a plugin's integrity and authenticity using its digital signature.
     * <p>
     * Implementations must validate the provided {@link PluginSignature} against the plugin,
     * typically by checking the signature's byte array and certificate. This may involve
     * cryptographic operations to ensure the plugin's authenticity and integrity. If
     * verification fails, a {@link PluginVerificationFailure} is thrown.
     * </p>
     *
     * @param plugin    the plugin to verify
     * @param signature the digital signature associated with the plugin
     * @throws PluginVerificationFailure if the verification process fails
     * @throws NullPointerException if the plugin or signature is null
     */
    void verifyPlugin(Plugin plugin, PluginSignature signature) throws PluginVerificationFailure;
}
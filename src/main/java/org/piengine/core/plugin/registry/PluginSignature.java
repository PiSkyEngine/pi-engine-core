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

import java.security.cert.Certificate;

/**
 * Represents a digital signature for a plugin in the PIEngine framework.
 * <p>
 * This record encapsulates the cryptographic signature and associated
 * certificate used to verify the authenticity and integrity of a plugin. The
 * signature is typically generated using a private key and can be validated
 * using the public key contained in the certificate. This information is stored
 * in the plugin's metadata and used by a {@link PluginVerifier} to ensure the
 * plugin has not been tampered with and originates from a trusted source.
 * </p>
 *
 * @param signature   the byte array containing the cryptographic signature
 * @param certificate the certificate containing the public key for signature
 *                    verification
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 * @since 1.0
 */
public record PluginSignature(byte[] signature, Certificate certificate) {}
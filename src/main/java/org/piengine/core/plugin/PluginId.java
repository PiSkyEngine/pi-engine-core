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

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import org.piengine.core.plugin.registry.PluginRegistry;
import org.piengine.util.InvalidVersionException;
import org.piengine.util.Version;

/**
 * Represents a Maven-style plugin identifier with coordinates in the format
 * {@code group:artifact[:version]}.
 * <p>
 * This class encapsulates a plugin's unique identifier, used by the {@link PluginRegistry}
 * to locate and manage plugins in the PIEngine framework. The identifier consists of a
 * mandatory group and artifact, with an optional version component that adheres to
 * Semantic Versioning 2.0.0 (<a href="https://semver.org">semver.org</a>). The
 * {@link Plugin} interface requires a {@code PluginId} to be returned by
 * {@link Plugin#pluginId()}, and plugin developers typically provide this identifier
 * when extending the {@code BasePlugin} class, which implements both {@link Plugin} and
 * {@link PluginLifecycle} interfaces.
 * </p>
 * <p>
 * The class provides methods for parsing, validating, and extracting plugin identifiers,
 * as well as accessing their components. It enforces strict validation rules to ensure
 * compatibility with Maven conventions and Semantic Versioning.
 * </p>
 * <h2>Identifier Format</h2>
 * A plugin identifier must be in one of the following forms:
 * <ul>
 * <li>{@code group:artifact} (e.g., "org.example:my-plugin")</li>
 * <li>{@code group:artifact:version} (e.g., "org.example:my-plugin:1.0.0")</li>
 * </ul>
 * Where:
 * <ul>
 * <li><b>Group</b>: A dot-separated identifier (e.g., "org.example")</li>
 * <li><b>Artifact</b>: A hyphenated or underscored string (e.g., "my-plugin")</li>
 * <li><b>Version</b>: An optional Semantic Versioning string (e.g., "1.0.0")</li>
 * </ul>
 * <h2>Validation Rules</h2>
 * <ul>
 * <li>Group and artifact must contain only alphanumeric characters, dots (group),
 *     hyphens, or underscores, and must not be empty.</li>
 * <li>Version, if provided, must be a valid SemVer string validated by {@link Version}.</li>
 * </ul>
 * <h2>Usage in Plugins</h2>
 * <p>
 * Plugin developers extending {@code BasePlugin} provide a {@code PluginId} during
 * construction to uniquely identify the plugin. The plugin manager uses this identifier
 * to invoke lifecycle callbacks (e.g., {@link PluginLifecycle#initialize(EngineContextBuilder)},
 * {@link PluginLifecycle#shutdown()}) when the plugin is loaded or unloaded via the
 * {@link PluginRegistry}. For example:
 * <pre>
 * public class MyPlugin extends BasePlugin {
 *     public MyPlugin() {
 *         super(new PluginId("org.example", "my-plugin", new Version("1.0.0")),
 *               new PluginMetadata("1.0.0"));
 *     }
 * 
 *     &#064;Override
 *     public void start() throws InterruptedException, IllegalStateException {
 *         // Respond to start callback
 *         System.out.println("Starting plugin: " + getPluginId());
 *     }
 * }
 * </pre>
 * </p>
 * <h2>Examples of Valid Identifiers</h2>
 * <pre>
 * org.example:my-plugin
 * org.example:my-plugin:1.0.0
 * com.acme:core-utils:2.1.0-alpha.1
 * net.sly:plugin-base:1.0.0-rc.1+build.123
 * </pre>
 *
 * @author Sly Technologies
 * @version 2.0
 * @see Plugin
 * @see PluginRegistry
 * @see Version
 * @see <a href="https://maven.apache.org/pom.html#Maven_Coordinates">Maven Coordinates</a>
 * @see <a href="https://semver.org">Semantic Versioning 2.0.0 Specification</a>
 * @since 1.0
 */
public class PluginId {

	private final String group;
	private final String artifact;
	private final Version version;

	/**
	 * Regular expression pattern for validating Maven-style coordinates.
	 * Matches: group:artifact[:version]
	 * <ul>
	 * <li>Group: [a-zA-Z0-9][a-zA-Z0-9.-]*[a-zA-Z0-9]</li>
	 * <li>Artifact: [a-zA-Z0-9][a-zA-Z0-9_-]*[a-zA-Z0-9]</li>
	 * <li>Version: Optional, validated by Version class</li>
	 * </ul>
	 */
	private static final String PLUGIN_ID_PATTERN = "^([a-zA-Z0-9][a-zA-Z0-9.-]*[a-zA-Z0-9]):([a-zA-Z0-9][a-zA-Z0-9_-]*[a-zA-Z0-9])(?::(.+))?$";

	/**
	 * Regular expression pattern for extracting PluginId from a string.
	 */
	private static final String EXTRACT_PATTERN = "([a-zA-Z0-9][a-zA-Z0-9.-]*[a-zA-Z0-9]):([a-zA-Z0-9][a-zA-Z0-9_-]*[a-zA-Z0-9])(?::([^\\s:]+))?";

	/**
	 * Exception thrown when a plugin identifier string is invalid or malformed.
	 */
	public static class InvalidPluginIdException extends RuntimeException {
		/**
		 * Constructs a new InvalidPluginIdException with the specified message.
		 *
		 * @param message the detail message
		 */
		public InvalidPluginIdException(String message) {
			super(message);
		}

		/**
		 * Constructs a new InvalidPluginIdException with the specified message and cause.
		 *
		 * @param message the detail message
		 * @param cause   the cause of the exception
		 */
		public InvalidPluginIdException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	/**
	 * Extracts a plugin identifier from the provided input string.
	 * <p>
	 * This method scans the input string for a substring matching the Maven coordinate
	 * format {@code group:artifact[:version]}. If found, it returns the identifier wrapped
	 * in an {@link Optional}. If no valid identifier is found, it returns
	 * {@link Optional#empty()}. This is useful for parsing plugin IDs from logs or
	 * configuration files.
	 * </p>
	 * <p>
	 * Example:
	 * <pre>
	 * Optional&lt;PluginId&gt; id = PluginId.extractFromString("Plugin org.example:my-plugin:1.0.0 installed");
	 * // id contains PluginId(org.example:my-plugin:1.0.0)
	 * </pre>
	 * </p>
	 *
	 * @param str the input string to scan
	 * @return an {@link Optional} containing the extracted {@code PluginId}, or
	 *         {@link Optional#empty()} if none is found
	 * @throws NullPointerException if the input string is null
	 */
	public static Optional<PluginId> extractFromString(String str) {
		Objects.requireNonNull(str, "Input string cannot be null");
		Pattern pattern = Pattern.compile(EXTRACT_PATTERN);
		var matcher = pattern.matcher(str);

		if (matcher.find()) {
			String group = matcher.group(1);
			String artifact = matcher.group(2);
			String versionStr = matcher.group(3);
			try {
				Version version = versionStr != null ? new Version(versionStr) : null;
				return Optional.of(new PluginId(group, artifact, version));
			} catch (InvalidVersionException e) {
				return Optional.empty();
			}
		}
		return Optional.empty();
	}

	/**
	 * Parses a plugin identifier string into a {@code PluginId} instance.
	 * <p>
	 * The input string must be in the format {@code group:artifact[:version]}. This
	 * method is used by the plugin manager to create {@code PluginId} instances from
	 * configuration or user input.
	 * </p>
	 * <p>
	 * Example:
	 * <pre>
	 * PluginId id = PluginId.parse("org.example:my-plugin:1.0.0");
	 * // id represents org.example:my-plugin:1.0.0
	 * </pre>
	 * </p>
	 *
	 * @param id the plugin identifier string
	 * @return a new {@code PluginId} instance
	 * @throws InvalidPluginIdException if the string is invalid
	 * @throws NullPointerException     if the input string is null
	 */
	public static PluginId parse(String id) {
		Objects.requireNonNull(id, "Plugin identifier string cannot be null");

		if (!id.matches(PLUGIN_ID_PATTERN)) {
			throw new InvalidPluginIdException("Invalid plugin identifier format: " + id);
		}

		String[] parts = id.split(":");
		String group = parts[0];
		String artifact = parts[1];
		Version version = parts.length > 2 ? new Version(parts[2]) : null;

		return new PluginId(group, artifact, version);
	}

	/**
	 * Creates a new {@code PluginId} with the specified group and artifact, and no version.
	 * <p>
	 * This constructor is used when the plugin version is not specified, allowing the
	 * plugin manager to resolve the latest version if needed.
	 * </p>
	 *
	 * @param group    the group identifier
	 * @param artifact the artifact identifier
	 * @throws IllegalArgumentException if group or artifact is invalid
	 * @throws NullPointerException     if group or artifact is null
	 */
	public PluginId(String group, String artifact) {
		this(group, artifact, null);
	}

	/**
	 * Creates a new {@code PluginId} with the specified group, artifact, and version.
	 * <p>
	 * This constructor is used by plugin developers when initializing a plugin via
	 * {@code BasePlugin} to specify a fully qualified identifier.
	 * </p>
	 *
	 * @param group    the group identifier
	 * @param artifact the artifact identifier
	 * @param version  the version, or null if unspecified
	 * @throws IllegalArgumentException if group or artifact is invalid
	 * @throws NullPointerException     if group or artifact is null
	 */
	public PluginId(String group, String artifact, Version version) {
		Objects.requireNonNull(group, "Group cannot be null");
		Objects.requireNonNull(artifact, "Artifact cannot be null");

		if (!group.matches("[a-zA-Z0-9][a-zA-Z0-9.-]*[a-zA-Z0-9]")) {
			throw new IllegalArgumentException("Invalid group format: " + group);
		}
		if (!artifact.matches("[a-zA-Z0-9][a-zA-Z0-9_-]*[a-zA-Z0-9]")) {
			throw new IllegalArgumentException("Invalid artifact format: " + artifact);
		}

		this.group = group;
		this.artifact = artifact;
		this.version = version;
	}

	/**
	 * Returns the group identifier.
	 *
	 * @return the group identifier
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * Returns the artifact identifier.
	 *
	 * @return the artifact identifier
	 */
	public String getArtifact() {
		return artifact;
	}

	/**
	 * Returns the version, if specified.
	 *
	 * @return the version, or null if none exists
	 */
	public Version getVersion() {
		return version;
	}

	/**
	 * Returns a string representation of the {@code PluginId} in Maven coordinate format.
	 * <p>
	 * Format: {@code group:artifact[:version]}
	 * </p>
	 *
	 * @return the formatted plugin identifier
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(group).append(':').append(artifact);
		if (version != null) {
			sb.append(':').append(version.toString());
		}
		return sb.toString();
	}

	/**
	 * Compares this {@code PluginId} with another object for equality.
	 * <p>
	 * Two {@code PluginId} instances are equal if their group, artifact, and version
	 * components are equal.
	 * </p>
	 *
	 * @param obj the object to compare with
	 * @return true if equal, false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PluginId other))
			return false;

		return Objects.equals(this.group, other.group) &&
				Objects.equals(this.artifact, other.artifact) &&
				Objects.equals(this.version, other.version);
	}

	/**
	 * Returns a hash code for this {@code PluginId}.
	 * <p>
	 * The hash code is computed from the group, artifact, and version components.
	 * </p>
	 *
	 * @return the hash code
	 */
	@Override
	public int hashCode() {
		return Objects.hash(group, artifact, version);
	}
}
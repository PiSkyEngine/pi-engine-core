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

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.piengine.core.plugin.registry.spi.PluginService;
import org.piengine.util.InvalidVersionException;
import org.piengine.util.Version;

/**
 * Utility class for filtering plugin-related objects based on Maven-style
 * plugin identifier patterns.
 * <p>
 * This class provides methods to create {@link Predicate} instances for
 * filtering {@link Plugin}, {@link PluginService}, or {@link PluginId} objects
 * using patterns in the format {@code group:artifact[:version]}. Patterns
 * support wildcards ({@code ?} for a single character, {@code *} for zero or
 * more characters) and are evaluated in the order provided. The predicates can
 * be used with streams to filter plugins managed by a
 * {@link org.piengine.core.plugin.registry.PluginRegistry} or other
 * collections.
 * </p>
 * <h2>Pattern Format</h2> Patterns must adhere to Maven coordinate conventions:
 * <ul>
 * <li>{@code group:artifact} (e.g., "org.example:*")</li>
 * <li>{@code group:artifact:version} (e.g., "org.example:my-plugin:1.0.*")</li>
 * </ul>
 * Where:
 * <ul>
 * <li><b>Group</b>: Alphanumeric with dots, hyphens, or wildcards (e.g.,
 * "org.*")</li>
 * <li><b>Artifact</b>: Alphanumeric with hyphens, underscores, or wildcards
 * (e.g., "my-*")</li>
 * <li><b>Version</b>: Optional, Semantic Versioning with wildcards (e.g.,
 * "1.?.0")</li>
 * </ul>
 * <h2>Usage Example</h2>
 * 
 * <pre>
 * List&lt;Plugin&gt; plugins = Arrays.asList(
 * 		new MyPlugin(PluginId.parse("org.example:my-plugin:1.0.0")),
 * 		new MyPlugin(PluginId.parse("com.acme:core:1.0.1")));
 * var filter = Plugins.filterPlugin("org.example:*");
 * List&lt;Plugin&gt; matched = plugins.stream()
 * 		.filter(filter)
 * 		.toList();
 * // matched contains: [MyPlugin(org.example:my-plugin:1.0.0)]
 * </pre>
 *
 * @author Mark Bednarczyk
 * @author Sly Technologies Inc.
 * @version 2.0
 * @since 1.0
 * @see PluginId
 * @see Plugin
 * @see PluginService
 */
public final class Plugins {

	/**
	 * Exception thrown when a plugin identifier pattern is invalid.
	 */
	public static class InvalidPluginPatternException extends RuntimeException {
		public InvalidPluginPatternException(String message) {
			super(message);
		}

		public InvalidPluginPatternException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	/**
	 * Creates a predicate for filtering {@link HasPluginId} instances based on
	 * their plugin identifiers.
	 * <p>
	 * The predicate tests the {@link PluginId} returned by
	 * {@link HasPluginId#pluginId()} against the provided patterns. Patterns are
	 * evaluated in order, and the predicate returns {@code true} if any pattern
	 * matches. If the patterns array is empty, the predicate returns {@code true}
	 * for all inputs.
	 * </p>
	 * <h2>Example</h2>
	 * 
	 * <pre>
	 * List&lt;PluginMetadata&gt; plugins = Arrays.asList(
	 * 		new PluginMetadata(0, PluginId.parse("org.example:my-plugin:1.0.0")),
	 * 		new PluginMetadata(0, PluginId.parse("com.acme:core:1.0.1")));
	 * var filter = Plugins.filterHasPluginId("org.example:*");
	 * List&lt;PluginMetadata&gt; matched = plugins.stream()
	 * 		.filter(filter)
	 * 		.toList();
	 * // matched contains: [MyPlugin(org.example:my-plugin:1.0.0)]
	 * </pre>
	 *
	 * @param pluginIds an array of plugin identifier patterns in Maven format
	 * @return a {@link Predicate} that tests {@link PluginMetadata} instances
	 * @throws NullPointerException          if the patterns array or any pattern is
	 *                                       null
	 * @throws InvalidPluginPatternException if any pattern is invalid
	 */
	public static Predicate<HasPluginId> filterHasPluginId(String... pluginIds) {
		return filter(HasPluginId::pluginId, pluginIds);
	}

	/**
	 * Creates a predicate for filtering {@link PluginId} instances based on plugin
	 * identifier patterns.
	 * <p>
	 * The predicate tests the {@link PluginId} directly against the provided
	 * patterns. Patterns are evaluated in order, and the predicate returns
	 * {@code true} if any pattern matches. If the patterns array is empty, the
	 * predicate returns {@code true} for all inputs.
	 * </p>
	 * <h2>Example</h2>
	 * 
	 * <pre>
	 * List&lt;PluginId&gt; ids = Arrays.asList(
	 * 		PluginId.parse("org.example:my-plugin:1.0.0"),
	 * 		PluginId.parse("com.acme:core:1.0.1"));
	 * var filter = Plugins.filterPluginId("org.example:*");
	 * List&lt;PluginId&gt; matched = ids.stream()
	 * 		.filter(filter)
	 * 		.toList();
	 * // matched contains: [org.example:my-plugin:1.0.0]
	 * </pre>
	 *
	 * @param pluginIds an array of plugin identifier patterns in Maven format
	 * @return a {@link Predicate} that tests {@link PluginId} instances
	 * @throws NullPointerException          if the patterns array or any pattern is
	 *                                       null
	 * @throws InvalidPluginPatternException if any pattern is invalid
	 */
	public static Predicate<PluginId> filterPluginId(String... pluginIds) {
		return filter(id -> id, pluginIds);
	}

	/**
	 * Creates a predicate for filtering objects by mapping them to {@link PluginId}
	 * and testing against patterns.
	 * <p>
	 * The provided {@link Function} maps the input object to a {@link PluginId},
	 * which is then tested against the provided patterns. Patterns are evaluated in
	 * order, and the predicate returns {@code true} if any pattern matches. If the
	 * patterns array is empty, the predicate returns {@code true} for all inputs.
	 * </p>
	 * <h2>Example</h2>
	 * 
	 * <pre>
	 * record PluginWrapper(PluginId id) {}
	 * 
	 * List&lt;PluginWrapper&gt; wrappers = Arrays.asList(
	 * 		new PluginWrapper(PluginId.parse("org.example:my-plugin:1.0.0")),
	 * 		new PluginWrapper(PluginId.parse("com.acme:core:1.0.1")));
	 * var filter = Plugins.filter(PluginWrapper::id, "org.example:*");
	 * List&lt;PluginWrapper&gt; matched = wrappers.stream()
	 * 		.filter(filter)
	 * 		.toList();
	 * // matched contains: [PluginWrapper(org.example:my-plugin:1.0.0)]
	 * </pre>
	 *
	 * @param <T>       the type of objects to filter
	 * @param mapper    a function that maps the input object to a {@link PluginId}
	 * @param pluginIds an array of plugin identifier patterns in Maven format
	 * @return a {@link Predicate} that tests objects of type {@code T}
	 * @throws NullPointerException          if the mapper, patterns array, or any
	 *                                       pattern is null
	 * @throws InvalidPluginPatternException if any pattern is invalid
	 */
	public static <T> Predicate<T> filter(Function<T, PluginId> mapper, String... pluginIds) {
		Objects.requireNonNull(pluginIds, "Plugin patterns array cannot be null");

		// If no patterns, return a predicate that always returns true
		if (pluginIds.length == 0) {
			return _ -> true;
		}

		// Validate and convert each pattern to a regex
		Pattern[] patterns = Arrays.stream(pluginIds)
				.map(Objects::requireNonNull)
				.map(Plugins::convertToRegex)
				.toArray(Pattern[]::new);

		// Return a predicate that tests the PluginId against each pattern in order
		return src -> {
			var pluginId = mapper.apply(src);
			Objects.requireNonNull(pluginId, "PluginId cannot be null");
			String idString = pluginId.toString();
			for (Pattern pattern : patterns) {
				if (pattern.matcher(idString).matches()) {
					return true;
				}
			}
			return false;
		};
	}

	/**
	 * Converts a plugin identifier pattern to a regular expression pattern.
	 * <p>
	 * The input pattern is split into group, artifact, and optional version
	 * components. Each component is validated and converted to a regex, with
	 * wildcards ({@code ?} to {@code .}, {@code *} to {@code .*}). The resulting
	 * regex matches the full Maven coordinate string, allowing an optional version
	 * if none is specified in the pattern.
	 * </p>
	 * <h2>Example</h2>
	 * 
	 * <pre>
	 * // Internal use, not typically called directly
	 * Pattern pattern = Plugins.convertToRegex("org.example:*");
	 * boolean matches = pattern.matcher("org.example:my-plugin:1.0.0").matches(); // true
	 * </pre>
	 *
	 * @param pattern the plugin identifier pattern
	 * @return a compiled {@link Pattern} for matching
	 * @throws InvalidPluginPatternException if the pattern is invalid
	 */
	private static Pattern convertToRegex(String pattern) {
		if (!pattern.matches("^([a-zA-Z0-9.\\-?\\*]*):([a-zA-Z0-9\\?\\*][a-zA-Z0-9_\\-\\?\\*]*)(?::(.+))?$")) {
			throw new InvalidPluginPatternException("Invalid plugin pattern format: " + pattern);
		}

		String[] parts = pattern.split(":");
		String group = parts[0];
		String artifact = parts[1];
		String version = parts.length > 2 ? parts[2] : null;

		// Check for consecutive dots in group
		if (group.contains("..")) {
			throw new InvalidPluginPatternException("Invalid group pattern: consecutive dots not allowed: " + group);
		}
		if (!group.matches("[a-zA-Z0-9\\?\\*][a-zA-Z0-9.\\-\\?\\*]*[a-zA-Z0-9\\?\\*]?|[\\*\\?]")) {
			throw new InvalidPluginPatternException("Invalid group pattern: " + group);
		}
		if (!artifact.matches("[a-zA-Z\\?\\*][a-zA-Z0-9_\\-\\?\\*]*[a-zA-Z0-9\\?\\*]?")) {
			throw new InvalidPluginPatternException("Invalid artifact pattern: " + artifact);
		}
		if (version != null) {
			try {
				// Attempt to parse as a version to ensure it's a valid SemVer pattern
				new Version(version.replace("*", "0").replace("?", "0"));
			} catch (InvalidVersionException e) {
				throw new InvalidPluginPatternException("Invalid version pattern: " + version, e);
			}
		}

		// Convert wildcards to regex
		String groupRegex = regexify(group);
		String artifactRegex = regexify(artifact);
		String versionRegex = version != null ? regexify(version) : null;

		// Build the full regex
		StringBuilder regex = new StringBuilder();
		regex.append("^").append(groupRegex).append(":").append(artifactRegex);
		if (versionRegex != null) {
			regex.append(":").append(versionRegex);
		} else {
			// Allow optional version in the input PluginId
			regex.append("(?:$|:.+$)");
		}
		regex.append("$");

		return Pattern.compile(regex.toString());
	}

	/**
	 * Converts a pattern component (group, artifact, or version) to a regex string.
	 * <p>
	 * Escapes special regex characters and converts wildcards: {@code ?} to
	 * {@code .}, {@code *} to {@code .*}.
	 * </p>
	 * <h2>Example</h2>
	 * 
	 * <pre>
	 * // Internal use, not typically called directly
	 * String regex = Plugins.regexify("org.ex*"); // Returns "org\\.ex.*"
	 * </pre>
	 *
	 * @param component the pattern component
	 * @return the regex equivalent
	 */
	private static String regexify(String component) {
		StringBuilder regex = new StringBuilder();
		for (char c : component.toCharArray()) {
			if (c == '?') {
				regex.append(".");
			} else if (c == '*') {
				regex.append(".*");
			} else if (".[]{}()^$\\".indexOf(c) != -1) {
				regex.append("\\").append(c);
			} else {
				regex.append(c);
			}
		}
		return regex.toString();
	}

	/**
	 * Private constructor to prevent instantiation of this utility class.
	 */
	private Plugins() {}
}
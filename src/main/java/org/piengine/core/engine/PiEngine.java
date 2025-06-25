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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.piengine.core.Updatable;
import org.piengine.core.engine.dispatcher.DispatchableWorker;
import org.piengine.core.engine.dispatcher.impl.EngineDispatcher;
import org.piengine.core.engine.impl.EngineContextBuilder;
import org.piengine.core.engine.impl.EngineStateSupport;
import org.piengine.core.logging.PiEngineLogging;
import org.piengine.core.plugin.Plugin;
import org.piengine.core.plugin.PluginId;
import org.piengine.core.plugin.app.AppPlugin;
import org.piengine.core.plugin.registry.PluginNotFound;
import org.piengine.core.plugin.registry.PluginRegistry;
import org.piengine.core.plugin.registry.PluginSignature;
import org.piengine.core.plugin.registry.PluginVerificationFailure;
import org.piengine.util.config.ConfigNotFound;

/**
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 */
public class PiEngine implements AutoCloseable {

	private static final Logger logger = PiEngineLogging.getLogger(PiEngine.class.getName());

	/**
	 * The Class EngigeCycle.
	 */
	private class EngigeCycle implements EngineLifecycle {

		@Override
		public void close() throws IllegalStateException, InterruptedException {
			if (engineState.current() == EngineState.RUNNING)
				stop();

			PiEngine.this.shutdown();
		}

		@Override
		public void initialize() {
			engineState.validate(EngineState.SHUTDOWN);

			// User initialize
			PiEngine.this.initialize();
			PiEngine.this.initializeEngine();
			PiEngine.this.initializePlugins();

			engineState.validateAndSet(EngineState.STOPPED);
		}

		private void run() {
			engineState.validate(EngineState.RUNNING);

			PiEngine.this.loop();
		}

		@Override
		public void shutdown() throws IllegalStateException {
			engineState.validateAndSet(EngineState.SHUTDOWN);

			PiEngine.this.shutdownPlugins();
			PiEngine.this.shutdownEngine();
			PiEngine.this.context = null;

			// User shutdown
			PiEngine.this.shutdown();
		}

		@Override
		public void start() throws InterruptedException, IllegalStateException {
			if (engineState.current() == EngineState.SHUTDOWN)
				initialize(); // SHUTDOWN -> STOPPED

			engineState.validate(EngineState.STOPPED);

			PiEngine.this.startPlugins();

			// STOPPED -> RUNNING
			engineState.validateAndSet(EngineState.RUNNING);

			run();

		}

		@Override
		public void stop() throws InterruptedException, IllegalStateException {

			PiEngine.this.stopPlugins();

			engineState.validateAndSet(EngineState.STOPPED);

			shutdown();
		}

	}

	private static final String INTERNAL_DEFAULT_CONFIG = "/org/piengine/core/engine.yaml";;

	/**
	 * PiEngine lifecycle controller
	 */
	private final EngineLifecycle cycle = new EngigeCycle();
	private final EngineConfig config;
	private EngineContext context;
	private Updatable[] updatableArray;

	private final EngineStateSupport engineState = new EngineStateSupport();

	private final List<EngineWorker> engineWorkers = new ArrayList<>();
	private final List<EngineDispatcher> engineDispatchers = new ArrayList<>();
	private final List<Updatable> updatableWorkers = new ArrayList<>();
	private final List<DispatchableWorker> dispatchableWorkers = new ArrayList<>();

	/* All the plugins that have been used */
	private final Set<Plugin> pluginsUsed = new HashSet<>();

	@SuppressWarnings("preview")
	public PiEngine(AppPlugin... apps) {
		EngineConfig config = null;
		try {
			config = EngineConfig.loadFromClasspath(INTERNAL_DEFAULT_CONFIG, EngineConfig::new);
		} catch (ConfigNotFound | IOException e) {
			throw new IllegalStateException("missing internal engine configuration [%s]".formatted(
					INTERNAL_DEFAULT_CONFIG), e);
		}
		this(config, apps);
	}

	public PiEngine(EngineConfig config, AppPlugin... apps) {
		this.config = config;
	}

	@Override
	public final void close() throws IllegalStateException, InterruptedException {
		cycle.close();
	}

	/**
	 * @return
	 */
	public EngineConfig config() {
		return config;
	}

	protected void initialize() {
		assert engineState.current() == EngineState.SHUTDOWN;

		logger.fine("Debug: PiEngine state = " + engineState.current());
	}

	private void loadPlugins() {
		var registry = PluginRegistry.newInstance();

		var pluginNameList = config.get("engine.plugins").asList(String.class);

		logger.finest("Loading plugins: " + pluginNameList);

		for (var name : pluginNameList) {
			try {
				var plugin = registry.loadPlugin(Plugin.class, name);

				loadSinglePlugin(plugin);

			} catch (PluginNotFound | PluginVerificationFailure e) {
				logger.log(Level.WARNING, "unable to load plugin [%s]".formatted(name), e);
			}
		}

		this.updatableArray = updatableWorkers.toArray(Updatable[]::new);

		logUnusedPlugins();
	}

	private void logUnusedPlugins() {
		var unusedPlugins = new HashSet<Plugin>(loadedPlugins);
		unusedPlugins.removeAll(pluginsUsed);

		if (!unusedPlugins.isEmpty()) {
			String nameList = unusedPlugins.stream()
					.map(Plugin::pluginId)
					.map(PluginId::toString)
					.collect(Collectors.joining(", "));

			logger.warning("Some plugins were not used: " + nameList);
		}
	}

	private void verifyPlugin(Plugin plugin) throws PluginVerificationFailure {
		var signature = plugin.getMetadata().signature();

		if (signature.isEmpty()) {
			logger.finer("Plugin signature verification skipped, no signature: " + plugin.pluginId());
			return;
		}

		logger.finest("Verifying plugin signature: " + plugin.pluginId());
		verifyPluginSignature(plugin, signature.get());
		logger.finer("Plugin signature verified: " + plugin.pluginId());
	}

	private void verifyPluginSignature(Plugin plugin, PluginSignature signature) throws PluginVerificationFailure {
		throw new UnsupportedOperationException("not implemented yet");
	}

	private final List<Plugin> loadedPlugins = new ArrayList<>();

	private void verifyUsedOnlyOnce(Plugin plugin) {
		if (pluginsUsed.contains(plugin)) {
			var e = new IllegalStateException("plugin [%s] already used"
					.formatted(plugin.pluginId()));

			logger.log(Level.SEVERE,
					"Plugin used more than once: " + plugin.pluginId(),
					e);

			throw e;
		}

		pluginsUsed.add(plugin);
	}

	private void loadSinglePlugin(Plugin plugin) throws PluginVerificationFailure {

		verifyPlugin(plugin);

		loadedPlugins.add(plugin);

		if (plugin instanceof EngineWorker worker) {
			verifyUsedOnlyOnce(plugin);

			engineWorkers.add(worker);

			if (worker instanceof EngineDispatcher dispatcher)
				engineDispatchers.add(dispatcher);

			if (worker instanceof Updatable updatable)
				updatableWorkers.add(updatable);
		}

		if (plugin instanceof DispatchableWorker dispatchable) {
			verifyUsedOnlyOnce(plugin);

			dispatchableWorkers.add(dispatchable);
		}
	}

	protected void initializePlugins() {
		assert engineState.current() == EngineState.SHUTDOWN;

		for (var dispatcher : engineDispatchers)
			dispatcher.registerWorkers(dispatchableWorkers);

		for (var worker : engineWorkers)
			worker.initialize(context);
	}

	protected void initializeEngine() {
		assert engineState.current() == EngineState.SHUTDOWN;

		this.context = new EngineContextBuilder()
				.usingConfig(config);

		loadPlugins();
	}

	private static final float NANOS_PER_SEC = 1_000_000_000f;

	private void loop() {
		assert updatableArray != null;
		assert engineState.current() == EngineState.RUNNING;
		long lastNanos = nanos();
		final int count = updatableArray.length;

		// Main loop
		while (engineState.current() == EngineState.RUNNING) {
			// time-per-frame
			float tpf = (nanos() - lastNanos) / NANOS_PER_SEC;

			for (int i = 0; i < count; i++) {
				var updater = updatableArray[i];

				// In sub-thread or single thread
				updater.updateFrame(tpf);
			}
		}

		assert engineState.current() != EngineState.RUNNING;
	}

	private long nanos() {
		return System.nanoTime();
	}

	public void shutdown() throws IllegalStateException {
		cycle.shutdown();
	}

	protected void shutdownPlugins() {
		assert engineState.current() != EngineState.SHUTDOWN;

		for (var worker : engineWorkers)
			worker.shutdown();
	}

	protected void shutdownEngine() {
		assert engineState.current() != EngineState.SHUTDOWN;

		engineState.validateAndSet(EngineState.SHUTDOWN);
		context = null;
	}

	public final void start() throws InterruptedException, IllegalStateException {
		cycle.start();
	}

	protected void startPlugins() {
		assert engineState.current() == EngineState.STOPPED;

		for (var worker : engineWorkers)
			worker.start();
	}

	public final void stop() throws InterruptedException, IllegalStateException {
		cycle.start();
	}

	protected void stopPlugins() {
		assert engineState.current() == EngineState.RUNNING;

		for (var worker : engineWorkers)
			try {
				worker.stop();
			} catch (IllegalStateException e) {
				logger.log(Level.WARNING, "unable to stop plugin [%s]".formatted(worker.toString()), e);
			} catch (InterruptedException e) {
				logger.log(Level.FINE, "plugin [%s] stop interrupted".formatted(worker.toString()), e);
			}
	}

}

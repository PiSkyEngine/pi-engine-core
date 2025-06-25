package org.piengine.core.logging;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Enhanced PiEngine logging configuration that can load from properties
 */
public class PiEngineLogging {
    
    public static final String ROOT_LOGGER_NAME = "org.piengine";
    
    // Sub-component loggers
    public static final String ENGINE_CORE_LOGGER = "org.piengine.core";
    public static final String RENDERER_LOGGER = "org.piengine.renderer";
    public static final String ASSET_LOGGER = "org.piengine.asset";
    public static final String AUDIO_LOGGER = "org.piengine.audio";
    public static final String INPUT_LOGGER = "org.piengine.input";
    public static final String PHYSICS_LOGGER = "org.piengine.physics";
    
    private static final Level DEFAULT_ROOT_LEVEL = Level.INFO;
    private static final Level DEFAULT_ENGINE_LEVEL = Level.FINEST;
    
    private static boolean initialized = false;
	static {

		// Initialize logging first thing
		PiEngineLogging.initialize();

		getLogger(PiEngineLogging.class).fine("PiEngine initializing...");
	}
    /**
     * Initialize PiEngine logging with defaults, then try to load properties
     */
	public static synchronized void initialize() {
	    if (initialized) {
	        return;
	    }
	    // Set sensible defaults first
	    setDefaults();
	    // Add ConsoleHandler
	    ConsoleHandler handler = new ConsoleHandler();
	    handler.setLevel(Level.FINEST);
	    Logger.getLogger(ROOT_LOGGER_NAME).addHandler(handler);
	    Logger.getLogger(PiEngineLogging.class.getName()).setLevel(Level.OFF);
	    // Try to load user configuration
//	    loadPropertiesConfiguration();
	    initialized = true;
	    Logger.getLogger(ENGINE_CORE_LOGGER).fine("PiEngine logging initialized");
	}
    
    private static void setDefaults() {
        Logger.getLogger(ROOT_LOGGER_NAME).setLevel(DEFAULT_ROOT_LEVEL);
        Logger.getLogger(ENGINE_CORE_LOGGER + ".engine").setLevel(DEFAULT_ENGINE_LEVEL);
    }
    
    /**
     * Load PiEngine-specific logging configuration from properties.
     * This method ONLY affects PiEngine loggers, never global logging.
     */
    private static void loadPropertiesConfiguration() {
        // Try multiple locations in order of preference
        String[] configPaths = {
            "logging.properties",             // Standard location
            "logging-defaults.properties"  // Library defaults
        };
        
        Properties props = null;
        String loadedFrom = null;
        
        for (String path : configPaths) {
            props = loadPropertiesFromClasspath(path);
            if (props != null) {
                loadedFrom = path;
                break;
            }
        }
        
        if (props != null) {
            applyPiEngineProperties(props);
            Logger.getLogger(ENGINE_CORE_LOGGER).info("Loaded PiEngine logging config from: " + loadedFrom);
        } else {
            Logger.getLogger(ENGINE_CORE_LOGGER).info("No PiEngine logging configuration found, using defaults");
        }
    }
    
    private static Properties loadPropertiesFromClasspath(String path) {
        try (InputStream is = PiEngineLogging.class.getClassLoader().getResourceAsStream(path)) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                return props;
            }
        } catch (IOException e) {
            Logger.getLogger(ENGINE_CORE_LOGGER).fine("Could not load " + path + ": " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Apply ONLY PiEngine-related properties from the loaded configuration.
     * This ensures we don't interfere with user's global logging setup.
     */
    private static void applyPiEngineProperties(Properties props) {
        // Apply levels for PiEngine loggers only
        for (String key : props.stringPropertyNames()) {
            if (key.startsWith("org.piengine") && key.endsWith(".level")) {
                String loggerName = key.substring(0, key.length() - ".level".length());
                String levelStr = props.getProperty(key);
                
                try {
                    Level level = Level.parse(levelStr.trim().toUpperCase());
                    Logger.getLogger(loggerName).setLevel(level);
                    Logger.getLogger(ENGINE_CORE_LOGGER).finest("Set " + loggerName + " level to " + level);
                } catch (IllegalArgumentException e) {
                    Logger.getLogger(ENGINE_CORE_LOGGER).warning(
                        "Invalid log level '" + levelStr + "' for " + loggerName);
                }
            }
        }
    }
    
    // Rest of the API methods...
    public static void setLevel(Level level) {
        Logger.getLogger(ROOT_LOGGER_NAME).setLevel(level);
    }
    
    public static void setSubsystemLevel(String subsystem, Level level) {
        Logger.getLogger(ROOT_LOGGER_NAME + "." + subsystem).setLevel(level);
    }
    
    public static void enableDebugLogging() {
        setLevel(Level.FINE);
    }
    
    public static void enableVerboseLogging() {
        setLevel(Level.FINEST);
    }
    
    public static void enableQuietMode() {
        setLevel(Level.WARNING);
    }
    
    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(clazz.getName());
    }
    
    public static Logger getLogger(String name) {
        if (!name.startsWith(ROOT_LOGGER_NAME)) {
            name = ROOT_LOGGER_NAME + "." + name;
        }
        return Logger.getLogger(name);
    }
}
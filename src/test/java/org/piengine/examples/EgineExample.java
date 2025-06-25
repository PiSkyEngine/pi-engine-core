package org.piengine.examples;

import java.io.IOException;

import org.piengine.core.engine.PiEngine;
import org.piengine.util.config.ConfigNotFound;

public class EgineExample {
	public static void main(String[] args) throws ConfigNotFound, IOException, IllegalStateException, InterruptedException {
		PiEngine engine = new PiEngine();
		var config = engine.config();
		
//		System.out.println(config);
		
		engine.start();
	}
}
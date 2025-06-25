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

package org.piengine.core.engine.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.piengine.core.engine.EngineState;
import org.piengine.core.logging.PiEngineLogging;

public final class EngineStateSupport {
	private static final Logger logger = PiEngineLogging.getLogger(EngineState.class);
	private EngineState current = EngineState.SHUTDOWN;

	public EngineState current() {
		return current;
	}

	private IllegalStateException invalidState(EngineState oldState, EngineState newState) {
		var e = new IllegalStateException("invalid engine state [%s -> %s]"
				.formatted(current, newState));

		logger.log(Level.SEVERE, "Invalid engine state", e);

		return e;
	}

	public void validate(EngineState state) throws IllegalStateException {
		if (current != state)
			throw invalidState(current, state);
	}

	public EngineState validateAndSet(EngineState newState) throws IllegalStateException {
		logger.finer("Engine state changed: %s -> %s".formatted(current, newState));

		return current = switch (newState) {
		case EngineState.SHUTDOWN -> {
			if (current != EngineState.STOPPED)
				throw invalidState(current, newState);

			yield newState;
		}
		case EngineState.RUNNING -> {
			if (current != EngineState.STOPPED)
				throw invalidState(current, newState);

			yield newState;
		}
		case EngineState.STOPPED -> {
			if (current != EngineState.RUNNING && current != EngineState.SHUTDOWN)
				throw invalidState(current, newState);

			yield newState;
		}
		};
	}

	@Override
	public String toString() {
		return current.name();
	}
}
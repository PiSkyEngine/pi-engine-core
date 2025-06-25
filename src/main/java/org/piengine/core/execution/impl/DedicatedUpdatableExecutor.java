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

package org.piengine.core.execution.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Phaser;
import java.util.concurrent.StructuredTaskScope;
import java.util.stream.Collectors;

import org.piengine.core.Updatable;
import org.piengine.core.engine.EngineContext;

/**
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 */
public class DedicatedUpdatableExecutor implements Updatable {

	class DedicatedCallable implements Callable<Void> {

		private final Updatable dst;
		private final Phaser phaser;
		private final Object instance;

		public DedicatedCallable(Object instance, Updatable dst, Phaser phaser) {
			this.instance = instance;
			this.dst = dst;
			this.phaser = phaser;
		}

		/**
		 * @see java.util.concurrent.Callable#call()
		 */
		@Override
		public Void call() throws Exception {

			phaser.register();

			// Start barrier
			int phase = phaser.arriveAndAwaitAdvance();

			try {

				while (true) {

					// Per frame barrier
					phaser.arrive();
					phase = phaser.awaitAdvanceInterruptibly(phase);
					float tpf = DedicatedUpdatableExecutor.this.tpf;

					dst.updateFrame(tpf);
				}

			} catch (InterruptedException e) {} finally {

				// Stop barrier
				phaser.arriveAndDeregister();
			}

			return null;
		}

		public Object instance() {
			return instance;
		}
	}

	private volatile float tpf;
	private StructuredTaskScope<Void> scope;
	private final Phaser phaser = new Phaser();
	private Map<Object, List<DedicatedCallable>> groups;

	public void initialize(EngineContext engineContext, List<UpdatableTask> updateTaskList) {
		this.groups = updateTaskList.stream()
				.map(task -> new DedicatedCallable(task.taskContainer(), task, phaser))
				.collect(Collectors.groupingBy(DedicatedCallable::instance));

	}

	public void shutdown() {
		stop();

		assert phaser.getRegisteredParties() == 0;

		groups = null;
	}

	public void start() {
		if (phaser.getRegisteredParties() > 0)
			return; // Still running

		this.scope = new StructuredTaskScope<>();
		phaser.register();

		for (final var list : groups.values()) {
			assert list.size() != 0;

			if (list.size() == 1) {
				var call = list.get(0);

				scope.fork(call);
			} else {
				scope.fork(() -> {

					try (var group = new StructuredTaskScope<>()) {
						for (var call : list)
							group.fork(call);

						group.join();
					}

					return null;
				});
			}

		}

		// Start barrier
		phaser.arriveAndAwaitAdvance();
	}

	public void stop() {
		if (scope == null)
			return;

		scope.close();
		scope = null;

		// Stop barrier
		phaser.arriveAndAwaitAdvance();
		phaser.arriveAndDeregister();

		assert phaser.getRegisteredParties() == 0;
	}

	/**
	 * @see org.piengine.core.Updatable#updateFrame(float)
	 */
	@Override
	public void updateFrame(float tpf) {
		this.tpf = tpf;

		phaser.arriveAndAwaitAdvance();
	}
}

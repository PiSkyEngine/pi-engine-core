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

import org.piengine.core.engine.impl.EngineContextBuilder;
import org.piengine.core.plugin.impl.BaseTransitionStatePolicy;
import org.piengine.core.plugin.impl.TransitionPolicyJumpTo;
import org.piengine.core.plugin.impl.TransitionPolicyOneStep;
import org.piengine.core.plugin.impl.TransitionPolicyStepThrough;

/**
 * Enumerates the possible states of a plugin in the PIEngine framework.
 * <p>
 * This enum defines the lifecycle states that a plugin can be in, as managed by
 * the {@link PluginLifecycle} interface. Each state represents a distinct phase
 * in the plugin's operation, from initialization to shutdown. The states are
 * used to enforce valid transitions and ensure that lifecycle methods are
 * called in the correct context.
 * </p>
 * <p>
 * The defined states are:
 * <ul>
 * <li><b>SHUTDOWN</b>: The plugin is fully terminated and unloaded, with all
 * resources and dependencies released.</li>
 * <li><b>RUNNING</b>: The plugin is active, providing services and consuming
 * resources.</li>
 * <li><b>PAUSED</b>: The plugin is temporarily suspended, with some resources
 * released but state preserved for quick resumption.</li>
 * <li><b>STOPPED</b>: The plugin is stopped, with services unavailable but
 * capable of being restarted without reinitialization.</li>
 * </ul>
 * </p>
 * <p>
 * Example usage:
 * 
 * <pre>
 * public class MyPlugin implements PluginLifecycle {
 * 	private PluginState currentState = PluginState.SHUTDOWN;
 * 
 * 	public void start() throws IllegalStateException {
 * 		if (currentState != PluginState.STOPPED) {
 * 			throw new IllegalStateException("Plugin must be in STOPPED state");
 * 		}
 * 		currentState = PluginState.RUNNING;
 * 		System.out.println("Plugin is now " + currentState);
 * 	}
 * 
 * 	// Implement other lifecycle methods...
 * }
 * </pre>
 * </p>
 *
 * @author Mark Bednarczyk [mark@slytechs.com]
 * @author Sly Technologies Inc.
 * @see PluginLifecycle
 * @since 1.0
 */
public enum PluginState {
	/**
	 * The plugin is fully shut down and unloaded.
	 * <p>
	 * In this state, all resources and dependencies have been released, and the
	 * plugin cannot be restarted without reinitialization via
	 * {@link PluginLifecycle#initialize(EngineContextBuilder)}. This is the initial state
	 * of a plugin before initialization and the final state after
	 * {@link PluginLifecycle#shutdown()}.
	 * </p>
	 */
	SHUTDOWN,

	/**
	 * The plugin is stopped, with services unavailable.
	 * <p>
	 * In this state, the plugin has been stopped via
	 * {@link PluginLifecycle#stop()}, with worker threads terminated and resources
	 * released. The plugin can be restarted via {@link PluginLifecycle#start()}
	 * without reinitialization.
	 * </p>
	 */
	STOPPED,

	/**
	 * The plugin is actively running and providing services.
	 * <p>
	 * In this state, the plugin has started via {@link PluginLifecycle#start()} and
	 * is fully operational, with worker threads, resources, and services active.
	 * The plugin can transition to {@link #PAUSED} or {@link #STOPPED} from this
	 * state.
	 * </p>
	 */
	RUNNING,

	/**
	 * The plugin is temporarily paused.
	 * <p>
	 * In this state, the plugin has been paused via
	 * {@link PluginLifecycle#pause()}, suspending non-critical operations and
	 * releasing some resources while preserving state for quick resumption. The
	 * plugin can transition back to {@link #RUNNING} via
	 * {@link PluginLifecycle#unpause()}.
	 * </p>
	 */
	PAUSED

	;

	/**
	 * Defines policies for managing state transitions of plugins in the PIEngine
	 * framework.
	 * <p>
	 * The {@code Policy} interface specifies how a plugin, implementing
	 * {@link PluginLifecycle}, can transition between states defined in
	 * {@link PluginState}. Each policy enforces specific rules for valid state
	 * transitions, ensuring that lifecycle methods are invoked in a controlled and
	 * predictable manner. The framework provides three predefined policies:
	 * </p>
	 * <ul>
	 * <li><b>"one-step"</b>: Allows only direct, single-step transitions
	 * between adjacent states (e.g., {@code STOPPED} to {@code RUNNING}).</li>
	 * <li><b>"jump-to"</b>: Permits direct transitions from any state to
	 * any other state without intermediate steps.</li>
	 * <li><b>"step-through"</b>: Allows transitions to any state but
	 * requires passing through all intermediate states in sequence.</li>
	 * </ul>
	 * <p>
	 * These policies are used by the plugin framework to validate and execute state
	 * changes, ensuring that plugins operate within the constraints of their
	 * defined lifecycle. Implementations of this interface must provide logic for
	 * checking, validating, and performing transitions.
	 * </p>
	 * <p>
	 * Example usage:
	 * 
	 * <pre>
	 * Policy policy = Policy.oneStepTransitions();
	 * PluginLifecycle plugin = new MyPlugin();
	 * PluginContext context = new PluginContext();
	 * 
	 * // Check if transition is allowed
	 * if (policy.checkPolicy(plugin.state(), PluginState.RUNNING)) {
	 * 	policy.transition(plugin.state(), PluginState.RUNNING, plugin, context);
	 * }
	 * </pre>
	 * </p>
	 *
	 * @author Mark Bednarczyk [mark@slytechs.com]
	 * @author Sly Technologies Inc.
	 * @see PluginState
	 * @see PluginLifecycle
	 * @see PluginContext
	 * @since 1.0
	 */
	public interface Policy {

		/**
		 * Returns a policy allowing direct transitions from any state to any state.
		 * <p>
		 * This policy permits jumping directly to the desired state without requiring
		 * intermediate transitions. For example, a plugin can transition from
		 * {@link PluginState#SHUTDOWN} to {@link PluginState#PAUSED} in a single step.
		 * </p>
		 *
		 * @return the jump-to transition policy
		 */
		static Policy jumpToTransition() {
			return BaseTransitionStatePolicy.JUMP_TO_TRANSITION;
		}

		/**
		 * Retrieves a policy by its name.
		 * <p>
		 * This method returns one of the predefined policies based on the provided
		 * name. Valid policy names are defined in the implementing classes, such as
		 * {@code "one-step"}, {@code "jump-to"}, or {@code "step-through"}.
		 * </p>
		 *
		 * @param name the name of the policy
		 * @return the corresponding plugin state policy
		 * @throws IllegalArgumentException if the specified policy name is unknown
		 */
		static Policy of(String name) throws IllegalArgumentException {
			return switch (name) {
			case TransitionPolicyOneStep.NAME -> BaseTransitionStatePolicy.ONE_STOP_TRANSITION;
			case TransitionPolicyJumpTo.NAME -> BaseTransitionStatePolicy.JUMP_TO_TRANSITION;
			case TransitionPolicyStepThrough.NAME -> BaseTransitionStatePolicy.STEP_THROUGH_TRANSITION;
			default -> throw new IllegalArgumentException("Unknown plugin state policy name: " + name);
			};
		}

		/**
		 * Returns a policy allowing only single-step state transitions.
		 * <p>
		 * This strict policy enforces transitions between adjacent states in the plugin
		 * lifecycle, such as:
		 * <ul>
		 * <li>{@link PluginState#SHUTDOWN} &rarr; {@link PluginState#STOPPED}</li>
		 * <li>{@link PluginState#STOPPED} &rarr; {@link PluginState#RUNNING}</li>
		 * <li>{@link PluginState#RUNNING} &rarr; {@link PluginState#PAUSED}</li>
		 * <li>{@link PluginState#PAUSED} &rarr; {@link PluginState#RUNNING}</li>
		 * <li>{@link PluginState#RUNNING} &rarr; {@link PluginState#STOPPED}</li>
		 * <li>{@link PluginState#STOPPED} &rarr; {@link PluginState#SHUTDOWN}</li>
		 * </ul>
		 * </p>
		 *
		 * @return the one-step transition policy
		 */
		static Policy oneStepTransitions() {
			return BaseTransitionStatePolicy.ONE_STOP_TRANSITION;
		}

		/**
		 * Returns a policy allowing transitions through all intermediate states.
		 * <p>
		 * This policy permits transitions from any state to any other state but
		 * requires the plugin to pass through all intermediate states in the correct
		 * sequence. For example, transitioning from {@link PluginState#SHUTDOWN} to
		 * {@link PluginState#PAUSED} would follow the sequence: SHUTDOWN &rarr; STOPPED
		 * &rarr; RUNNING &rarr; PAUSED.
		 * </p>
		 *
		 * @return the step-through transition policy
		 */
		static Policy stepThroughTransition() {
			return BaseTransitionStatePolicy.STEP_THROUGH_TRANSITION;
		}

		/**
		 * Checks if the policy allows a transition from the current state to the
		 * desired state.
		 * <p>
		 * This method evaluates whether the specified state change is permitted under
		 * the policy's rules without performing the transition. It is typically used to
		 * validate a transition before attempting it.
		 * </p>
		 *
		 * @param current the current state of the plugin
		 * @param desired the desired state to transition to
		 * @return {@code true} if the transition is allowed, {@code false} otherwise
		 */
		boolean checkPolicy(PluginState current, PluginState desired);

		/**
		 * Returns the name of the policy.
		 * <p>
		 * The name is a unique identifier for the policy, used in methods like
		 * {@link #of(String)} to retrieve a specific policy instance.
		 * </p>
		 *
		 * @return the policy name
		 */
		String name();

		/**
		 * Transitions the plugin from the current state to the desired end state.
		 * <p>
		 * This method executes the state transition according to the policy's rules,
		 * invoking the appropriate lifecycle methods on the provided
		 * {@link PluginLifecycle} instance. The transition may involve intermediate
		 * states, depending on the policy. The {@link PluginContext} provides
		 * additional context for the transition, such as configuration or dependencies.
		 * </p>
		 *
		 * @param current the current state of the plugin
		 * @param end     the desired end state
		 * @param plugin  the plugin instance to transition
		 * @param context the context for the transition
		 * @throws InterruptedException  if the transition is interrupted
		 * @throws IllegalStateException if the transition is not allowed by the policy
		 */
		void transition(PluginState current, PluginState end, PluginLifecycle plugin, PluginContext context)
				throws InterruptedException, IllegalStateException;

		/**
		 * Validates whether the policy allows a transition from the current state to
		 * the desired state.
		 * <p>
		 * Unlike {@link #checkPolicy(PluginState, PluginState)}, this method throws an
		 * exception if the transition is not allowed, providing a mechanism for strict
		 * enforcement of the policy's rules. It is typically used before attempting a
		 * transition to ensure compliance with the policy.
		 * </p>
		 *
		 * @param current the current state of the plugin
		 * @param desired the desired state to transition to
		 * @throws IllegalStateException if the transition is not allowed by the policy
		 */
		void validatePolicy(PluginState current, PluginState desired) throws IllegalStateException;
	}
}
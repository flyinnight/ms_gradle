package com.dilapp.radar.ui;

/**
 * 状态设计模式 {@link ContextState.State} 内容不解释
 */
public final class ContextState {

	private State state;

	public ContextState(State state) {
		this.state = state;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public void request(Object... params) {
		if (state != null) {
			state.handle(this, params);
		}
	}

	/**
	 * 状态设计模式{@link #ContextState} 内容不解释
	 */
	public interface State {

		void handle(ContextState context, Object... params);
	}
}

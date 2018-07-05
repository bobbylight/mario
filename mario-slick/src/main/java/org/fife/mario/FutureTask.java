package org.fife.mario;

/**
 * An event that will happen in the future.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public abstract class FutureTask {

	private int delay;

	public FutureTask(int delay) {
		this.delay = delay;
	}

	public int getDelay() {
		return delay;
	}

	public boolean decreaseDelay(int amt) {
		delay -= amt;
		return delay<=0;
	}

	public abstract void run();

}

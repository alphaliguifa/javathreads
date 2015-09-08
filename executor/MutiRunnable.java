package threads.executor;

import java.util.List;

public class MutiRunnable implements Runnable {
	
	private final List<Runnable> runnables;
	
	public MutiRunnable(List<Runnable> runnables) {
		this.runnables = runnables;
	}
	
	public void run() {
		for (Runnable runnable : runnables) {
			new Thread(runnable).start();
		}
	}

}

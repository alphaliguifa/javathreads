package threads.executor;

public class TaskOne implements Runnable {
	public void run() {
		System.out.println("Executing task one");
		try {
			Thread.sleep(1000);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}

class TaskTwo implements Runnable {
	public void run() {
		System.out.println("Executing task two");
		try {
			Thread.sleep(1000);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}

class TaskThree implements Runnable {
	public void run() {
		System.out.println("Executing task three");
		try {
			Thread.sleep(1000);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}

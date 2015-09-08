package threads.join;

public class JoinExample {
	public static void main(String[] args) throws InterruptedException {
		Thread t = new Thread(new Runnable() {
			public void run() {
				System.out.println("First task started");
				System.out.println("Sleeping for 2 seconds");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("First task completed");
			}
		});
		Thread t1 = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("Second task completed");
			}
		});
		Thread t2 = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("Third task completed");
			}
		});
		t.start(); // Line 15
		//Waits for this thread to die.
		t.join(); // Line 16
		t1.start();
		t2.start();
	}
}

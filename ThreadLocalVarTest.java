package threads;

/**
 * Local variables are thread safe in Java.
 * 
 * @author 
 *
 */
public class ThreadLocalVarTest implements Runnable {
	
	public void run() {
		int counter = 0;
		while(true) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				System.out.println(e);
			}
			counter++;
			System.out.println(Thread.currentThread().getName()+" counter: " + counter);
		}
	}
	
	

	public static void main(String[] args) {
		ThreadLocalVarTest obj = new ThreadLocalVarTest();
		Thread t1 = new Thread(obj, "Thread-1");
		Thread t2 = new Thread(obj, "Thread-2");
		Thread t3 = new Thread(obj, "Thread-3");
		t1.start();
		t2.start();
		t3.start();
	}

}

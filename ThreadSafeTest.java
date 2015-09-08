package threads;

/**
 * static method getNumbers() now synchronized, class level locking(Thread-5 MyCounter instance not same as Thread-1 to Thread-4)
 * non-static method getDots() now synchronized, object level locking (Thread-1 to Thread-4 are some MyCounter instance)
 * 
 * explain result print out to console:
 * 
 * getDots(), getAngleBrackets() 
 *      case0) only call getNumbers() method in main(), it's result are synchronized for ALL threads;
 *      case1) only call getAngleBrackets() method in main(), it's result are NOT synchronized for ALL threads;
 *      case2) only call getDots() method in main(), getDots() result are synchronized for Thread-1 to Thread-4, but NOT synchronized for Thread-5;
 * 		case4) getNumbers(),getDots(), getAngleBrackets() 3 methods are called at the same time in main(), 
 * 			for getNumbers() method result, ALL threads are synchronized;
 * 			for getDots(), getAngleBrackets() method result are synchronized for Thread-1 to Thread-4, and NOT synchronized for Thread-5;
 * 
 * 
 * 
 * 
 */
public class ThreadSafeTest {
	
	public static void main(String[] args) {
		MyCounter myCounter = new MyCounter();
		Thread t1 = new Thread(myCounter, "Thread-1: ");
		Thread t2 = new Thread(myCounter, "Thread-2: ");
		Thread t3 = new Thread(myCounter, "Thread-3: ");
		Thread t4 = new Thread(myCounter, "Thread-4: ");
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		
		MyCounter myCounter2 = new MyCounter();
		Thread t5 = new Thread(myCounter2, "Thread-5: ");
		t5.start();
	}
	
}

class MyCounter implements Runnable {
	private static int counter = 0;
	private StringBuffer counter2 = new StringBuffer();;
	private StringBuffer counter3 = new StringBuffer();
 
	public static synchronized int getNumbers() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return counter++;
	}
	
	public synchronized StringBuffer getDots() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return counter2.append(".");
	}
	
	public StringBuffer getAngleBrackets() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return counter3.append(">");
	}
	
	public void run() {
		while (true) {
			int c = getNumbers();
			System.out.println(Thread.currentThread().getName() + " c1: " +   c + ", Time: " + System.currentTimeMillis());
			
			StringBuffer c2 = getDots();
			System.out.println(Thread.currentThread().getName() + " c2: " +  c2.toString() + ", Time: " + System.currentTimeMillis());
			
			StringBuffer c3 = getAngleBrackets();
			System.out.println(Thread.currentThread().getName() + " c3: " +  c3.toString() + ", Time: " + System.currentTimeMillis());
		}
	}
	
}

package threads;

public class ThreadA {
	
	public static void main(String[] args) {
		ThreadB b = new ThreadB();
		Thread t = new Thread(b, "Thread-1: ");
		Thread t1 = new Thread(b, "Thread-2: ");
		
		t.start();
		t1.start();
		
//		System.out.println("Waiting for b to complete...");
//		System.out.println("Total is: " + b.getTotal());
		
//		synchronized(b) {
//			try {
				System.out.println("Waiting for b to complete...");
//				Thread.sleep(2000);
//				b.wait();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			System.out.println("Total is: " + b.getTotal());
//		}
		
	}
	
	

}

class ThreadB implements Runnable {
	private int total;

	public void run() {
//		try {
//			Thread.sleep(10);
			synchronized(this) {
				for (int i = 0; i < 100; i++){
					total += i;
					System.out.println(Thread.currentThread().getName()+"Total---->"+(this.total));  
				}
				notify();
			}
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
	public int getTotal() {
		return total;
	}
}

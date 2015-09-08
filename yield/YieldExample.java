package threads.yield;

/**
 * A hint to the scheduler that the current thread is willing to yield its current use of a processor. The scheduler is free to ignore
 * this hint. Yield is a heuristic attempt to improve relative progression between threads that would otherwise over-utilize a CPU.
 * Its use should be combined with detailed profiling and benchmarking to ensure that it actually has the desired effect.
 */
public class YieldExample {
	public static void main(String[] args) {
		Thread producer = new Producer();
		Thread consumer = new Consumer();

		producer.setPriority(Thread.MIN_PRIORITY); // Min Priority
		consumer.setPriority(Thread.MAX_PRIORITY); // Max Priority

		producer.start();
		consumer.start();
	}
}

class Producer extends Thread {
	public void run() {
		for (int i = 0; i < 5; i++) {
			System.out.println("I am Producer : Produced Item " + i);
			Thread.yield();
		}
	}
}

class Consumer extends Thread {
	public void run() {
		for (int i = 0; i < 5; i++) {
			System.out.println("I am Consumer : Consumed Item " + i);
			Thread.yield();
		}
	}
}

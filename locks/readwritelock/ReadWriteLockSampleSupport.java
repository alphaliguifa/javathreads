package threads.locks.readwritelock;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockSampleSupport {

	public static void main(String[] args) {
		
		

	}

}

class ShareQueue  implements Runnable {
	private final List<Integer> taskQueue;
	private final int MAX_CAPACITY;
	
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock readLock = lock.readLock();
	private final Lock writeLock = lock.writeLock();
	
	private int counter = 0;
	
	public ShareQueue(List<Integer> sharedQueue, int size) {
		this.taskQueue = sharedQueue;
		this.MAX_CAPACITY = size;
	}
	
	@Override
	public void run() {
		
	}
	
	public void initQueue() {
		readLock.lock();
		while (this.taskQueue.size() < MAX_CAPACITY) {
			readLock.unlock();
			writeLock.lock();
			if (this.taskQueue.size() < MAX_CAPACITY) {
				System.out.println("Produced: " + counter++);
			}
			writeLock.unlock();
			readLock.lock();
		}
		System.out.println("empty? " + taskQueue.isEmpty());  
		readLock.unlock();
	}
	
	public void read() {
		readLock.lock();
		try{  
			if (this.taskQueue.size() == 0){
				System.out.println("Queue is empty " + Thread.currentThread().getName() + " is waiting , size: "
						+ taskQueue.size());
			}
			Thread.sleep(1000);
			int i = (Integer) taskQueue.remove(0);
			counter--;
			System.out.println("Consumed: " + i);
        } catch (InterruptedException e) {
			e.printStackTrace();
		}  
        finally{  
            readLock.unlock();  
        }  
	}
	
	public void write() {
		writeLock.lock();
		try{  
			if (this.taskQueue.size() == MAX_CAPACITY){
				System.out.println("Queue is full " + Thread.currentThread().getName() + " is waiting , size: "
						+ taskQueue.size());
			}
			Thread.sleep(1000);
			int i = (Integer) taskQueue.remove(0);
			taskQueue.add(counter++);
			System.out.println("Produced: " + i);
        } catch (InterruptedException e) {
			e.printStackTrace();
		}  
        finally{  
        	writeLock.unlock();  
        }  
	}
	
	
}


class Producer implements Runnable {
	private final List<Integer> taskQueue;
	private final int MAX_CAPACITY;
	
	public Producer(List<Integer> sharedQueue, int size) {
		this.taskQueue = sharedQueue;
		this.MAX_CAPACITY = size;
	}

	@Override
	public void run() {
		int counter = 0;
		while (true) {
			try {
				produce(counter++);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	private void produce(int i) throws InterruptedException {
		synchronized (taskQueue) {
			while (taskQueue.size() == MAX_CAPACITY) {
				System.out.println("Queue is full " + Thread.currentThread().getName() + " is waiting , size: "
						+ taskQueue.size());
				taskQueue.wait();
			}

			Thread.sleep(1000);
			taskQueue.add(i);
			System.out.println("Produced: " + i);
			taskQueue.notifyAll();
		}
	}
}

class Consumer implements Runnable {
	private final List<Integer> taskQueue;
	

	public Consumer(List<Integer> sharedQueue) {
		this.taskQueue = sharedQueue;
	}

	@Override
	public void run() {
		while (true) {
			try {
				consume();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	private void consume() throws InterruptedException {
		synchronized (taskQueue) {
			while (taskQueue.isEmpty()) {
				System.out.println("Queue is empty " + Thread.currentThread().getName() + " is waiting , size: "
						+ taskQueue.size());
				taskQueue.wait();
			}
			Thread.sleep(1000);
			int i = (Integer) taskQueue.remove(0);
			System.out.println("Consumed: " + i);
			taskQueue.notifyAll();
		}
	}
}

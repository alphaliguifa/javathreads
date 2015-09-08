package threads.pool;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduledThreadPoolExecutorExample {

	/*
	public static void main(String[] args) {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
		Task task1 = new Task("Demo Task 1");
		Task task2 = new Task("Demo Task 2");
		long start = System.currentTimeMillis();
		System.out.println("The time is: " + new Date(start));
		
		executor.schedule(task1, 5, TimeUnit.SECONDS);
		executor.schedule(task2, 10, TimeUnit.SECONDS);
		
		try {
//			executor.awaitTermination(1, TimeUnit.DAYS);
			executor.awaitTermination(20, TimeUnit.SECONDS);
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
		executor.shutdown();
		long end = System.currentTimeMillis();
		System.out.println("The time is: " + new Date(end));
		System.out.println("Thread alive: " + (end -start)/1000 + " sec.");
	}
	*/
	
	
	public static void main(String[] args) {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		Task task1 = new Task("Demo Task 1");
		
		long start = System.currentTimeMillis();
		System.out.println("The time is: " + new Date(start));
		
		long initialDelay = 2L;
		long period = 5L;
		ScheduledFuture<?> result = executor.scheduleAtFixedRate(task1, initialDelay, period, TimeUnit.SECONDS);
		try {
			TimeUnit.MILLISECONDS.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		executor.shutdown();
		long end = System.currentTimeMillis();
		System.out.println("The time is: " + new Date(end));
		System.out.println("Thread alive: " + (end -start)/1000 + " sec.");
		
		
	}

}

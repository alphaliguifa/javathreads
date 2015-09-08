package threads.pool;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/*
 * create an executor with a maximum number of threads at any time.
 * if sent more tasks than the number of threads, the remaining tasks
 *  will be blocked until there is a free thread to process them.
 */
public class FixedThreadPoolExecutorExample {

	public static void main(String[] args) {
		ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(4);
		for (int i = 0; i < 10; i++){
			Task task = new Task("Task " + i);
			System.out.println("A new task has been added:" + task.getName());
			executor.execute(task);
		}
		System.out.println("Maximum threads inside pool " + executor.getMaximumPoolSize());
		executor.shutdown();

	}

}

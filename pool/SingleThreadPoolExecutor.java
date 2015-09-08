package threads.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * extreme case of a fixed-size thread executor
 */
public class SingleThreadPoolExecutor {

	public static void main(String[] args) {
		ExecutorService executorService = (ExecutorService)Executors.newSingleThreadExecutor();
			for (int i = 0; i < 10; i++){
				Task task = new Task("Task " + i);
				System.out.println("A new task has been added:" + task.getName());
				executorService.execute(task);
			}
			//System.out.println("Maximum threads inside pool " + executorService.getMaximumPoolSize());
			executorService.shutdown();

	}

}

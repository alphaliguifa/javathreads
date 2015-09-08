package threads.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MutiTaskExecutor {

	public static void main(String[] args) {
		BlockingQueue<Runnable> worksQueue = new ArrayBlockingQueue<Runnable>(10);
		RejectedExecutionHandler rejectedHandler = new RejectedExecutionHandlerImpl();
		
		int corePoolSize = 3;
		int maximumPoolSize = 3;
		long keepAliveTime = 10L;
		ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, worksQueue, rejectedHandler);
		
		executor.prestartAllCoreThreads();
		
		List<Runnable> taskGroup = new ArrayList<Runnable>();
		taskGroup.add(new TaskOne());
		taskGroup.add(new TaskTwo());
		taskGroup.add(new TaskThree());
		
		worksQueue.add(new MutiRunnable(taskGroup));
	}

}

class RejectedExecutionHandlerImpl implements RejectedExecutionHandler {

	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		System.out.println(r.toString() + ": I've been rejected!");
		
	}
	
}


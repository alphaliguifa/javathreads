package threads.locks;

class PrintingJob implements Runnable {
	private PrinterQueue printerQueue;

	public PrintingJob(PrinterQueue printerQueue) {
		this.printerQueue = printerQueue;
	}

	public void run() {
		System.out.printf("%s: Going to print a document\n", Thread.currentThread().getName());
		printerQueue.printJob(new Object());
	}
}

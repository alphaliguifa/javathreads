package threads;


class TimePrinter implements Runnable {
	private int ticket =400;

	public int getCounter() {
		return ticket--;
	}
	
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				System.out.println(e);
			}
			if(ticket<=0)
                break;
			
			int nowTickets = getCounter();
            System.out.println(Thread.currentThread().getName()+"---sell out: " + nowTickets);
		}
	}

	static public void main(String args[]) {
		TimePrinter tp = new TimePrinter();
		Thread t1 = new Thread(tp, "Thread-1");
		t1.start();
		Thread t2 = new Thread(tp, "Thread-2");
		t2.start();
		Thread t3 = new Thread(tp, "Thread-3");
		t3.start();
	}

}

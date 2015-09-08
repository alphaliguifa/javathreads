package threads.synchronization.object_level;

/*
 * http://howtodoinjava.com/2013/03/08/thread-synchronization-object-level-locking-and-class-level-locking/
 */
public class DemoClass
{
	public synchronized void demoMethod(){}
}

//or
//
//public class DemoClass
//{
//	public void demoMethod(){
//		synchronized (this)
//		{
//			//other thread safe code
//		}
//	}
//}
//
//or
//
//public class DemoClass
//{
//	private final Object lock = new Object();
//	public void demoMethod(){
//		synchronized (lock)
//		{
//			//other thread safe code
//		}
//	}
//}

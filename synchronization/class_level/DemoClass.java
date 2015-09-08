package threads.synchronization.class_level;

/*
 * http://howtodoinjava.com/2013/03/08/thread-synchronization-object-level-locking-and-class-level-locking/
 */
public class DemoClass
{
    public synchronized static void demoMethod(){}
}
 
//or
// 
//public class DemoClass
//{
//    public void demoMethod(){
//        synchronized (DemoClass.class)
//        {
//            //other thread safe code
//        }
//    }
//}
// 
//or
// 
//public class DemoClass
//{
//    private final static Object lock = new Object();
//    
//    public void demoMethod(){
//        synchronized (lock)
//        {
//            //other thread safe code
//        }
//    }
//}

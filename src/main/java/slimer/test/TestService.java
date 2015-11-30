package slimer.test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import slimer.model.Customer;

@Service
public class TestService {
    @Cacheable(value = "usercache",keyGenerator = "KeyGenerator")  
    public Customer findCustomer(Long id,String firstName,String lastName){  
        System.out.println("无缓存的时候调用这里");  
        return new Customer(id,firstName,lastName);  
    } 
    
   public  void testCountDownLatch()throws InterruptedException {
	final CountDownLatch beginCountDownLatch=new CountDownLatch(1);
	final CountDownLatch endCountDownLatch=new CountDownLatch(10);
	final ExecutorService service=Executors.newFixedThreadPool(10);
	for (int i = 0; i < 10; i++) {
		final int temp=i+1;
		//此处可以实现一个继承runnable的类进一步的封装一些属性方法
		Runnable runnable=new Runnable() {
			public void run() {
				try {
					beginCountDownLatch.await();
					Thread.sleep((long)Math.random()*10000);
					System.out.println("no."+temp+"arrived.");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					//每次执行完一个进程，数目减一
					endCountDownLatch.countDown();
				}
			}
		};
		service.execute(runnable);
	}
	System.out.println("begin.");
	//开始等待的所有进程
	beginCountDownLatch.countDown();
	//等待所有进程执行完成
	endCountDownLatch.await();
	System.err.println("end.");
	//Executors使用完成后需要关闭
	service.shutdown();
   }
   
   public  void testCyclicBarrier() throws InterruptedException {  
       ExecutorService exec = Executors.newCachedThreadPool();
       //通过一个await阻塞所有线程，所有线程都执行barrier的await方法，所有的都执行完成后执行barrier的run方法
       final CyclicBarrier barrier = new CyclicBarrier(4, new Runnable() {   
           public void run() {  
               System.out.println("好了，大家可以去吃饭了……"  );  
           }  
       });       
         
       System.out.println("要吃饭，必须所有人都到终点，oK?");                  
       System.out.println("不放弃不抛弃！");  
         
       for (int i = 0; i < 4; i++) {  
           exec.execute(new Runnable() {  
               public void run() {  
                   System.out  
                           .println(Thread.currentThread().getName() + ":Go");  
                   try {  
                       Thread.sleep((long) (2000 * Math.random()));  
                   } catch (InterruptedException e1) {  
                       e1.printStackTrace();  
                   }  
                   System.out.println(Thread.currentThread().getName()  
                           + ":我到终点了");  
                   try {  
                       barrier.await();  
                   } catch (InterruptedException e) {  
                       // TODO Auto-generated catch block  
                       e.printStackTrace();  
                   } catch (BrokenBarrierException e) {  
                       // TODO Auto-generated catch block  
                       e.printStackTrace();  
                   }  
 
                   System.out.println(Thread.currentThread().getName()  
                           + ":终于可以吃饭啦！");  
               }  
           });  
 
       }  
       exec.shutdown();  
                 
   }  
}

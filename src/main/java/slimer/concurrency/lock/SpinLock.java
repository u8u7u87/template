package slimer.concurrency.lock;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author kelier
 * 锁作为并发共享数据，保证一致性的工具，在JAVA平台有多种实现(如 synchronized 和 ReentrantLock等等 )。
 * 这些已经写好提供的锁为我们开发提供了便利，但是锁的具体性质以及类型却很少被提及。
 * 本系列文章将分析JAVA下常见的锁名称以及特性
 * 1、自旋锁
 * 2、
 * 
 * 自旋锁，
 * 自旋锁是采用CAS原子操作，让当前线程不停地的在循环体内执行实现的，当循环的条件被其他线程改变时 才能进入临界区
 * 优点：自旋锁只是将当前线程不停地执行循环体，不进行线程状态的改变，所以响应速度更快
 * 缺点：不适合高并发，因为当线程数不停增加时，性能下降明显，因为每个线程都需要执行，占用CPU时间
 * 以下是自旋锁的非公平实现
 */
public class SpinLock {
	private AtomicReference<Thread> sign=new AtomicReference<Thread>();
	
	public void lock() {
		Thread currentThread =Thread.currentThread();
		while (!sign.compareAndSet(null, currentThread)) {
			
		}
	}
	public void unLock() {
		Thread currentThread=Thread.currentThread();
		sign.compareAndSet(currentThread, null);
	}
}

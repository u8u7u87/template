package slimer.concurrency.lock;

import java.awt.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.utils.CollectionUtils;

@Service
public class LockService {
	int length=10;
	ExecutorService executorService=Executors.newFixedThreadPool(length);
	ArrayList<ActiveThread> list= new ArrayList<ActiveThread>();
	
	public void spinLockTest() {
		for (int i = 0; i < length; i++) {
			ActiveThread thread=new ActiveThread();
			list.add(thread);
			thread.start();
		}
		
		System.out.println("end.");
	}
	public class ActiveThread extends  Thread{
		SpinLock spinLock=new SpinLock();
		@Override
		public void run() {
			System.err.println(Thread.currentThread().getId()+"begin.");
			spinLock.lock();
			System.err.println(Thread.currentThread().getId()+"end.");
			super.run();
		}
	}
	public static void main(String[] args) {
		LockService lockService=new LockService();
		lockService.spinLockTest();
		for (int i = 0; i < lockService.length; i++) {
			lockService.list.get(i).spinLock.unLock();
		}
	}
}


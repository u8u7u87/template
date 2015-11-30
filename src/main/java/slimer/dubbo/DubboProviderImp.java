package slimer.dubbo;

public class DubboProviderImp implements DubboService {

	public void sayHello() {
		System.out.println("hello.");
		System.err.println("provider called.");
	}

}

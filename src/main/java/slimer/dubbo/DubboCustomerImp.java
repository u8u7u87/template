package slimer.dubbo;

import org.springframework.beans.factory.annotation.Autowired;

public class DubboCustomerImp {
	@Autowired
	private DubboService dubboService;
	public void sayHelloCustomer(){
		dubboService.sayHello();
		System.err.println("customer called.");
	}
	public void setDubboService(DubboService dubboService) {
		this.dubboService = dubboService;
	}
}

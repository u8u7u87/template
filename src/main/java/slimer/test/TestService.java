package slimer.test;

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
}

package slimer.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
//import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import slimer.model.TestModel;
import slimer.utils.jedis.SlimerCacheManager;
/*import slimer.mapper.CustomerMapper;*/

//@RestController
@Controller
public class Test {
	private static final Logger log = LoggerFactory.getLogger(Test.class);
	private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    private static final SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	SlimerCacheManager cacheManager;
	
	@Autowired  
    TestService service; 
	
/*	@Autowired
	CustomerMapper customerMapper;*/
	/**
	 * return a ResolveView if @Control
	 * @param name
	 * @param model
	 * @return
	 */
	@RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "test";
    }
	
	/**
	 * return string only
	 * @param name
	 * @param model
	 * @return
	 */
	@RequestMapping("/greetingtest")
	@ResponseBody
    public TestModel greetingString(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        /*model.addAttribute("name", name);
        return "test";*/
		return new TestModel(counter.incrementAndGet(), String.format(template, name));
    }
	
	/**
	 * return a ResponseEntity,Without pages
	 * @param name
	 * @return
	 */
	@RequestMapping(value="/test/{name}")
	public ResponseEntity<Map<String, Object>> test(@PathVariable(value="name")String name) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("name", name);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	/**
	 * a scheduled method action for every 5 seconds
	 */
	//@Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
		log.info("The time is now " + dateFormat.format(new Date()));
    }
	
	/**
	 * get client type(nomal,mobile,tablet)
	 * @param device
	 * @return
	 */
	/*@RequestMapping(value="/device")
	public @ResponseBody Device DeviceDetect(Device device) {
		String deviceTypeString="Undetected";
		List<Object[]> splitUpNames = Arrays.asList("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long").stream()
                .map(name -> name.split(" "))
                .collect(Collectors.toList());
		return device;
	}*/
	
	/**basic jdbc connect
	 * @param model
	 * @return
	 */
/*	@RequestMapping(value="/insert")
	public String inserTestModel(Model model) {
		log.info("create tables.");
		//jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
        //jdbcTemplate.execute("CREATE TABLE customers(" +
        //        "id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");
        List<Object[]> splitNames=Arrays.asList("John Woo", "Jeff Dean").stream().map(name->name.split(" ")).collect(Collectors.toList());
        splitNames.forEach(name->log.info("firstname:{},second name:{}",name[0],name[1]));
        jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitNames);

        log.info("Querying for customer records where first_name = 'Josh':");
        jdbcTemplate.query(
                "SELECT id, first_name, last_name FROM customers WHERE first_name = ?", new Object[] { "Josh" },
                (rs, rowNum) -> new TestModel(rs.getLong("id"), rs.getString("first_name"))
        ).forEach(customer -> log.info(customer.toString()));
        model.addAttribute("name", "success");
		return "test";
	}*/
	
	/*@ResponseBody
	@RequestMapping("/testMybaits")
	public List<Customer> testMybaits() {
		CustomerExample customerExample=new CustomerExample();
		customerExample.createCriteria();
		List<Customer> customers= customerMapper.selectByExample(customerExample);
		return customers;
	}*/
    
    @ResponseBody
    @RequestMapping(value="/testJedis")
    public String testJedis() {
		String dataString=null;
		String keyString="1";
		String value="test";
		try {
			cacheManager.set(keyString, value);
			dataString=cacheManager.getbykey(keyString);
			cacheManager.del(keyString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dataString;
	}
    

    @RequestMapping("/testredis")  
    @ResponseBody  
    public String putCache(){  
    	service.findCustomer(1l,"wang","yunfei");    
        System.out.println("若下面没出现“无缓存的时候调用”字样且能打印出数据表示测试成功");  
        return "ok";  
    } 
}

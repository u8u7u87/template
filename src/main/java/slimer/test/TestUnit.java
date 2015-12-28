package slimer.test;

import javassist.expr.NewArray;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import slimer.utils.ES.ElasticsearchProxy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:*")
@WebAppConfiguration
public class TestUnit {

	@Autowired
	ElasticsearchProxy elasticsearchProxy;
	
	@Test
	public void testSignleAddDocument() {
		System.out.println(elasticsearchProxy.signleAddDocument(new Object()));
	}
}

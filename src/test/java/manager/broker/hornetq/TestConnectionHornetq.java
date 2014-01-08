package manager.broker.hornetq;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import javax.jms.JMSException;

import manager.broker.bean.Fila;
import manager.broker.hornetq.Monitor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/resources/applicationContext-hornetq.xml"})
public class TestConnectionHornetq {
	
	@Autowired
	private Monitor monitor;
	
	@Test
	public void listAllMsg() throws JMSException { 
		List<Fila> list =  monitor.listAllMsg("queueBoleto");
		System.out.println(list.size()); 
		assertNotNull(list);
	}
	
	@Test
	public void totalMessagensTest()  {
		try {
			monitor.atualizaTotalMsg();
			Map<String, Integer> map = monitor.getMapFilas();
			System.out.println(map.size()); 
		} catch (Exception e) { 
			fail(e.getMessage()); 
		}
	}

}
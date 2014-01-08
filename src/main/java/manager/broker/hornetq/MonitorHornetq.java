package manager.broker.hornetq;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueRequestor;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import manager.broker.bean.Fila;

import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.management.JMSManagementHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Monitor Provider Hornetq
 *  
 * @author Eduardo Wallace
 * @since 03/01/2014
 */
@Component
public class MonitorHornetq implements Monitor {
	
	@Autowired
	private ConnectionFactory connectionFactoryHornetQ;
	private Connection connection;
	private Session session;
	
	/** {@link Monitor#getFilas()}} **/
	private String[] filas;
	
	/** {@link Monitor#getMapFilas()} **/
	private Map<String, Integer> mapFilas = new HashMap<String, Integer>();
	
	public MonitorHornetq() { 
	}
	
	/**
	 * Prepara a conexao com o provider Hornetq, criando uma nova conexao e uma
	 * nova sessao caso nao exista.
	 * @throws JMSException em caso de erro
	 */
	private void prepareConnection() throws JMSException {
		if (connection == null) {
			connection = connectionFactoryHornetQ.createConnection();
		}
		if (session == null) {
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		}
	}

	@SuppressWarnings("rawtypes") 
	@Override
	public List<Fila> listAllMsg(String queueName) throws JMSException {
		
		prepareConnection();
		
		Queue queue = session.createQueue(queueName);
		QueueBrowser browser = session.createBrowser(queue);
		
		List<Fila> filas = new ArrayList<Fila>();
		
		connection.start();
		
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
		Fila fila;
		
		Enumeration msgs = browser.getEnumeration();
		while(msgs.hasMoreElements()) 
		{
			TextMessage message = (TextMessage) msgs.nextElement();
			fila = new Fila();
			fila.setJMSMessageID(message.getJMSMessageID());
			fila.setJMSDestination(message.getJMSDestination().toString());
			fila.setJMSTimestamp(format.format(new Date(message.getJMSTimestamp())));
			fila.setJMSPriority(String.valueOf(message.getJMSPriority())); 
			fila.setJMSReplyTo( (message.getJMSReplyTo() != null) ? message.getJMSReplyTo().toString() : "");
			fila.setTexto(message.getText()); 
			filas.add(fila);
		}	
		
		return filas;
	}

	
	public void atualizaTotalMsg() throws Exception {
		
		prepareConnection();
		
		Queue managementQueue = HornetQJMSClient.createQueue("hornetq.management");
		QueueRequestor requestor = new QueueRequestor((QueueSession) session, managementQueue); 
		Message message = session.createMessage();
		
		connection.start();
		
		for(int i = 0; i < filas.length; i++) 
		{
			JMSManagementHelper.putAttribute(message, "jms.queue." + filas[i], "messageCount");
			Message resposta = requestor.request(message);			
			int total = (Integer) JMSManagementHelper.getResult(resposta);
			mapFilas.put(filas[i], total);
		}
		
	}
	
	public void delete(Fila[] filas, String queueName) throws Exception {
		
		prepareConnection();
		
		connection.start();
		Queue managementQueue = HornetQJMSClient.createQueue("hornetq.management");
		QueueRequestor requestor = new QueueRequestor((QueueSession) session, managementQueue); 
		
		for (Fila fila : filas) {
			
			Message message = session.createMessage();
			JMSManagementHelper.putOperationInvocation(
					message,"jms.queue." + queueName,
					"removeMessage", 
					fila.getJMSMessageID());
			Message reply = requestor.request(message);
			
			boolean ok = JMSManagementHelper.hasOperationSucceeded(reply);
			//caso a operacao de delete nao seja executada com sucesso, lanca o erro
			if (!ok) throw new Exception("Nao foi possivel deletar as mensagens");
			
			System.out.println("Operacao removeMessage invocada com sucesso: " + ok);	
		
		}	
		
	}
	
	public String[] getFilas() {
		return filas;
	}

	public void setFilas(String[] filas) {
		this.filas = filas;
	}

	public Map<String, Integer> getMapFilas() {
		return mapFilas;
	}
	
}
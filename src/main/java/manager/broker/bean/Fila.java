package manager.broker.bean;

import java.io.Serializable;

/**
 * Representacao do Modelo das Mensagens JMS, a serem exibidas no datatable.
 *  
 * @author Eduardo Wallace
 * @since 03/01/2014
 */
public class Fila implements Serializable {
	private static final long serialVersionUID = 1L;

	private String JMSMessageID;
	private String JMSDestination;
	private String JMSTimestamp;
	private String JMSPriority;
	private String JMSReplyTo;
	private String texto;
	
	public Fila() {
		super();
	}

	public String getJMSMessageID() {
		return JMSMessageID;
	}

	public void setJMSMessageID(String jMSMessageID) {
		JMSMessageID = jMSMessageID;
	}

	public String getJMSDestination() {
		return JMSDestination;
	}

	public void setJMSDestination(String jMSDestination) {
		JMSDestination = jMSDestination;
	}

	public String getJMSTimestamp() {
		return JMSTimestamp;
	}

	public void setJMSTimestamp(String jMSTimestamp) {
		JMSTimestamp = jMSTimestamp;
	}

	public String getJMSPriority() {
		return JMSPriority;
	}

	public void setJMSPriority(String jMSPriority) {
		JMSPriority = jMSPriority;
	}

	public String getJMSReplyTo() {
		return JMSReplyTo;
	}

	public void setJMSReplyTo(String jMSReplyTo) {
		JMSReplyTo = jMSReplyTo;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}
	
}
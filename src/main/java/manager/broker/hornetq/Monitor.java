package manager.broker.hornetq;

import java.util.List;
import java.util.Map;

import javax.jms.JMSException;

import manager.broker.bean.Fila;

import org.springframework.stereotype.Component;

/**
 * Funcionalidades a serem implementadas para a Monitoracao do Provider Hornetq 
 * @author Eduardo Wallace
 * @since 03/01/2014
 */
@Component
public interface Monitor {
	
	/**
	 * Lista todas as Mensagens da fila passada como parametro
	 * @return todas os TextMessage da fila passada como parametro 
	 */
	List<Fila> listAllMsg(String queueName) throws JMSException;
	
	/**
	 * Atualiza o total das Mensagens de todas as filas configuradas
	 * @throws Exception
	 */
	void atualizaTotalMsg() throws Exception;
	
	/**
	 * Filas a serem injetadas previamente pelo Container Spring
	 * @return todas as filas previamente configuradas
	 */
	String[] getFilas();
	
	/**
	 * Sera implementado para armazenar o total das mensagens para cada fila do provider hornetq
	 * @return nome e total de filas
	 */
	Map<String, Integer> getMapFilas();
	
	/**
	 * Deleta uma ou mais filas do Provider Hornetq
	 */
	void delete(Fila[] filas, String queueName) throws Exception;

}
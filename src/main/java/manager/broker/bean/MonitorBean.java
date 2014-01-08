package manager.broker.bean;

import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.jms.JMSException;

import manager.broker.hornetq.Monitor;
import manager.broker.hornetq.MonitorHornetq;

import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * ManagedBean responsavel pela monitoracao do Provider Hornetq
 * @author Eduardo Wallace
 */
@ManagedBean(name = "monitorBean")
@RequestScoped
public class MonitorBean {
	
	/** Carrega as configuracoes do Spring **/
	WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
	
	/** Injection Bean - previamente configurado **/
	private Monitor monitor = (MonitorHornetq) context.getBean("monitorHornetq"); 
	
	/** Objeto responsavel por montar o grafico com as mensagens **/
	private CartesianChartModel filaChartModel;
	
	/** Filas do Provider Hornetq **/
	private List<Fila> filas;
	
	/**
	 * Sera populado implicitamente pela classe FilaDataModel, ao ser selecionado as filas no DataTable 
	 */
	private Fila[] selectedFilas;
	
	/** Object da API - Primefaces, para manipulacao do DataTable **/
	private FilaDataModel filaDataModel;
	
	/**
	 * E necessario guardarmos o nome da ultima fila selecionada no grafico.
	 * <p>
	 * Caso contrario dara erro ao tentar inicializar o metodo getRowData(String rowKey)
	 * da classe FilaDataModel, pois o framework primefaces sempre inicializa o managed-bean
	 * chamando seu construtor, para comparacao do getRowData(String rowKey).
	 */
	private static String nomeUltimaFilaSelecionada = "";

	public MonitorBean() throws Exception {
		//inicializa o carregamento do grafico
		criafilaChartModel();
		
		//Necessario popular o objeto filaDataModel para ser encontrado em sua chamada getRowData(String rowKey).
		if (!"".equals(nomeUltimaFilaSelecionada)) {			
			popularTabela(nomeUltimaFilaSelecionada);
		}
    }
    
	/**
	 * Popula o DataTable com as mensagens do Provider Hornetq  
	 * @param queueName - nome da fila previamente configurada no provider.
	 */
    public void popularTabela(String queueName) {
    	try {
			filas = monitor.listAllMsg(queueName);
			filaDataModel = new FilaDataModel(filas);
		} catch (JMSException e) {
			e.printStackTrace();
		}
    }
	
	/**
	 * Cria o Grafico o qual e responsavel por exibir as filas configuradas no provider
	 * e o total de mensagens armazenadas
	 * 
	 * @throws Exception em caso de erro
	 */
	private void criafilaChartModel() throws Exception {  
		filaChartModel = new CartesianChartModel();
		ChartSeries series = null;
		//atualiza as mensagens do hornetq
        monitor.atualizaTotalMsg();
        String[] filas = monitor.getFilas();
        Map<String, Integer> maps = monitor.getMapFilas();
        
        for(int i = 0; i < filas.length; i++) {
        	series = new ChartSeries();
        	series.setLabel(filas[i]);
        	series.set("Total Mensagens", maps.get(filas[i]));
        	filaChartModel.addSeries(series);
        }
        
    } 
    
	/**
	 * Listener iniciado apos o clique sobre o grafico das filas.
	 * <p>
	 * Neste evento atualizamos as mensagens do provider, e populamos o DataTable
	 * com as mensagens de cada fila obtida com o event acionado pelo usuario.
	 * 
	 * @param event - click do usuario
	 * @throws Exception - em caso de erro
	 */
    public void itemSelect(ItemSelectEvent event) throws Exception {  
    	
    	//atualiza o total das mensagens 
    	monitor.atualizaTotalMsg();
        
    	String[] filas = monitor.getFilas();
        Map<String, Integer> maps = monitor.getMapFilas();
        String queueName = filas[event.getSeriesIndex()];
        
        //guarda a ultima fila selecionada no evento do grafico em memoria
        nomeUltimaFilaSelecionada = queueName;
        
        //e necessario atualizar o DataTable
        popularTabela(queueName);
        
        //--Exibe Mensagem de informacao com o total das mensagens ou "transacoes" ao usuario
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, 
        				"Fila: "+ queueName,  
                        "Total Mensagens: " + maps.get(queueName));   
        
        FacesContext.getCurrentInstance().addMessage(null, msg); 
       
    }
    
    /**
     * Deleta todas as mensagens selecionadas pelo usuario no DataTable
     */
    public void deleteMsgs() {
    	
    	//deleta caso tenha mensagem selecionada no DataGrid
    	if (selectedFilas.length > 0) {
    		
    		try {
    			//deleta as mensagens
				monitor.delete(selectedFilas, nomeUltimaFilaSelecionada);
    			//Atualiza o Grafico
    			criafilaChartModel();	
    			//Atualiza a Tabela
    			popularTabela(nomeUltimaFilaSelecionada);
    			//exibe mensagem ao usuario
				FacesContext.getCurrentInstance().addMessage(null, 
						new FacesMessage(FacesMessage.SEVERITY_WARN, 
								"Delete com Sucesso!", 
								"Total Mensagens Deletadas: " + selectedFilas.length)); 
				
			} catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(null, 
						new FacesMessage(FacesMessage.SEVERITY_ERROR, 
								"Erro ao Deletar Mensagens", 
								e.getMessage()));
				//--
				e.printStackTrace();
				
			}
    		
    	}
    	
    }
    
    /** {@link #filaChartModel} **/
    public CartesianChartModel getFilaChartModel() {
		return filaChartModel;
	}
    
    /** {@link #selectedFilas} **/
	public Fila[] getSelectedFilas() {
		return selectedFilas;
	}

	public void setSelectedFilas(Fila[] selectedFilas) {
		this.selectedFilas = selectedFilas;
	}
	
	/** {@link #filaDataModel} **/
	public FilaDataModel getFilaDataModel() {
		return filaDataModel;
	}
        
}
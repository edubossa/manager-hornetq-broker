package manager.broker.bean;

import java.util.List;  

import javax.faces.model.ListDataModel;  

import org.primefaces.model.SelectableDataModel; 

/**
 * Model chamado implicitamente pelo framework primefaces, para criacao e manipulacao
 * do DataTable
 *  
 * @author Eduardo Wallace
 * @since 03/01/2014
 */
public class FilaDataModel extends ListDataModel<Fila> implements SelectableDataModel<Fila> {
	
	public FilaDataModel() {
	}
	
	public FilaDataModel(List<Fila> data) {
		super(data);
	}

	@SuppressWarnings("unchecked") 
	@Override
	public Fila getRowData(String rowKey) {
		List<Fila> filas = (List<Fila>) getWrappedData();  
        
        for(Fila fila : filas) {  
            if(fila.getJMSMessageID().equals(rowKey))  
                return fila;  
        }  
		return null;
	}

	@Override
	public Object getRowKey(Fila fila) {
		return fila.getJMSMessageID();
	}
}

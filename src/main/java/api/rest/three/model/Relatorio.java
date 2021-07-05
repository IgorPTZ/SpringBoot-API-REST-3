package api.rest.three.model;

import java.io.Serializable;

public class Relatorio implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String dataInicio;

	private String dataFim;

	public String getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(String dataInicio) {
		this.dataInicio = dataInicio;
	}

	public String getDataFim() {
		return dataFim;
	}

	public void setDataFim(String dataFim) {
		this.dataFim = dataFim;
	}
}

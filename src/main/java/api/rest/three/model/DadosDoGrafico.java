package api.rest.three.model;

import java.io.Serializable;

public class DadosDoGrafico implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String nomes;
	
	private String salarios;
	
	public DadosDoGrafico() {
		
	}
	
	public DadosDoGrafico(String nomes, String salarios) {
		
		this.nomes = nomes;
		
		this.salarios = salarios;
	}

	public String getNomes() {
		return nomes;
	}

	public void setNomes(String nomes) {
		this.nomes = nomes;
	}

	public String getSalarios() {
		return salarios;
	}

	public void setSalarios(String salarios) {
		this.salarios = salarios;
	}
}



package ontologia.entidades;

import jade.core.AID;

import java.io.Serializable;

import environment.Cidade;

public class Emergencia implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2422214654185065271L;
		
	public Emergencia(Cidade cidade, String descricao, int latitude, int longitude) {
		this.endereco =cidade.map_create(descricao, "emergencia", latitude, longitude,0,0);
		cidade.emergenciasPendentes.add(this);
	}

	public int endereco;
	
	public AID getAgente() {
		return agente;
	}
	
	public void setAgente(AID agente) {
		this.agente = agente;
	}
	
	private AID agente;
	
	@Override
	public String toString() {
		return ""+ endereco;
	}
}

package ontologia.entidades;

import java.io.Serializable;

import ontologia.status.EmergenciaStatus;
import environment.Cidade;
import environment.Endereco;
import jade.content.onto.SerializableOntology;
import jade.core.AID;

public class Emergencia implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2422214654185065271L;
	private static long geradorProtocolo = 0;

	
	public Emergencia(Cidade cidade, String descricao, int latitude, int longitude) {
		gerarProtocolo();
		this.endereco = new Endereco(descricao, "emergencia", latitude, longitude);
		this.status = EmergenciaStatus.Pendente;
		cidade.emergenciasPendentes.add(this);
		cidade.mapa.add(this.endereco);
	}

	public void gerarProtocolo() {
		synchronized (Emergencia.class) {
			this.protocolo = geradorProtocolo++;
		}
	}

	private long protocolo;

	public long getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(long protocolo) {
		this.protocolo = protocolo;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setLocal(Endereco local) {
		this.endereco = local;
	}

	private Endereco endereco;
	
	public AID getAgente() {
		return agente;
	}
	
	public void setAgente(AID agente) {
		this.agente = agente;
	}
	
	private AID agente;
	
	public EmergenciaStatus getStatus() {
		return status;
	}
	
	public void setStatus(EmergenciaStatus status) {
		this.status = status;
	}
	
	private EmergenciaStatus status;
}

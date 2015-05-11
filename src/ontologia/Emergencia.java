package ontologia;

import behaviours.StatusAtendimento;
import jade.core.AID;

public class Emergencia {
	private static long geradorProtocolo = 0;

	public Emergencia() {
	}
	
	public Emergencia(String descricao, int latitude, int longitude) {
		gerarProtocolo();
		this.descricao = descricao;
		this.local = new Local(latitude, longitude);
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

	private String descricao;

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Local getLocal() {
		return local;
	}

	public void setLocal(Local local) {
		this.local = local;
	}

	private Local local;
	
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

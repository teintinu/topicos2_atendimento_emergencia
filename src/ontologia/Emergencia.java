package ontologia;

public class Emergencia {
	private static long geradorProtocolo = 0;

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
}

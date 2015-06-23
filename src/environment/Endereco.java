package environment;

import java.io.Serializable;

public class Endereco implements Serializable{

	public String descricao;
	public String tipo;
	public int latitude;
	public int longitude;

	public Endereco(String descricao, String tipo, int latitude, int longitude) {
		this.descricao = descricao;
		this.tipo = tipo;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public void moveTo(int latitude, int longitude) {
		synchronized (this) {
			this.latitude = latitude;
			this.longitude = longitude;
		}
	}

	public void serialize(StringBuilder s){
		s.append(descricao);
		s.append(',');
		s.append(tipo);
		s.append(',');
		synchronized (this) {
			s.append(latitude);
			s.append(',');
			s.append(longitude);
		}
		s.append('\n');
	}
	
	public  int distancia(Endereco outro) {
		int a = Math.abs(this.latitude - outro.latitude);
		int b = Math.abs(this.longitude - outro.longitude);
		int c = (int) Math.sqrt(a * a + b * b);
		return c;
	}
}

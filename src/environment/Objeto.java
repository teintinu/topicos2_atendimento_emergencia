package environment;

import java.io.Serializable;

public class Objeto implements Serializable {

	public String descricao;
	public String tipo;
	public int latitude;
	public int longitude;

	public Objeto(String descricao, String tipo, int latitude, int longitude) {
		this.descricao = descricao;
		this.tipo = tipo;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	@Override
	public String toString() {
		StringBuilder s=new StringBuilder();
		serialize(s);
		return s.toString();
	}

	public void serialize(StringBuilder s) {
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

	public int distancia(Objeto outro) {
		int a = Math.abs(this.latitude - outro.latitude);
		int b = Math.abs(this.longitude - outro.longitude);
		int c = (int) Math.sqrt(a * a + b * b);
		return c;
	}

	public void moveTo(int latitude, int longitude) {
		synchronized (this) {
			this.latitude = latitude;
			this.longitude = longitude;
		}
	}

	public boolean walkTo(Objeto target) {
		int lat_delta = 0, long_delta = 0;
		synchronized (this) {
			synchronized (target) {
				if (this.latitude > target.latitude)
					lat_delta = -1;
				else if (this.latitude < target.latitude)
					lat_delta = 1;
				if (this.longitude > target.longitude)
					long_delta = -1;
				else if (this.longitude < target.longitude)
					long_delta = 1;
			}
			this.latitude += lat_delta;
			this.longitude += long_delta;
		}
		return lat_delta==0 && long_delta==0;
	}

	public void copy(Objeto target) {
		synchronized (target) {
			this.latitude=target.latitude;
			this.longitude=target.longitude;
		}		
	}

}

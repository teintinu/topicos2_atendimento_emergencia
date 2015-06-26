package environment;

import java.io.Serializable;

public class Objeto implements Serializable {

	public String descricao;
	public String tipo;
	public int latitude;
	public int longitude;
	public int[] props;

	public Objeto(String descricao, String tipo, int latitude, int longitude,
			int[] props) {
		this.descricao = descricao;
		this.tipo = tipo;
		this.latitude = latitude;
		this.longitude = longitude;
		this.props = props;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		serialize(s);
		return s.toString();
	}

	public void serialize(StringBuilder s) {
		int lat, lng;
		int[] props;
		synchronized (this) {
			lat = latitude;
			lng = longitude;
			props = this.props;
		}
		s.append(descricao);
		s.append(',');
		s.append(tipo);
		s.append(',');

		s.append(lat);
		s.append(',');
		s.append(lng);
		s.append(',');
		for (int i = 0; i < props.length; i++) {
			s.append(props[i]);
			s.append('^');
		}
		s.append('\n');
	}

	public int distancia(Objeto outro) {
		int a = Math.abs(this.latitude - outro.latitude);
		int b = Math.abs(this.longitude - outro.longitude);
		int c = (int) Math.sqrt(a * a + b * b);
		return c;
	}
}

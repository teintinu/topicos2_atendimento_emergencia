package environment;

import ontologia.Local;

public class Emergencia {
	public Emergencia fromString(String str) {
		String[] s = str.split(";");
		if (s.length != 3)
			return null;
		return new Emergencia(s[0], Integer.parseInt(s[1]),
				Integer.parseInt(s[2]));
	}

	public Emergencia(String descricao, int latitude, int longitude) {
		this.descricao = descricao;
		this.local = new Local(latitude, longitude);
	}

	public final String descricao;
	public final Local local;

	public String toString() {
		return descricao + ";" + local.latitude + ';' + local.longitude;
	}
}

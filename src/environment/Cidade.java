package environment;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import agents.CentralEmergencia;
import ontologia.entidades.Emergencia;

public class Cidade {

	public static Cidade singleton= new Cidade("Bitlandia", 600, 600);
	public static CentralEmergencia central;

	public final String nome;
	public final int tamanhoLat;
	public final int tamanhoLong;

	public CopyOnWriteArrayList<Emergencia> emergenciasPendentes = new CopyOnWriteArrayList<Emergencia>();
	private HashMap<Integer, Objeto> mapa = new HashMap<Integer, Objeto>();

	public Cidade(String nomeCidade, int tamanhoLat, int tamanhoLong) {
		nome = nomeCidade;
		this.tamanhoLat = tamanhoLat;
		this.tamanhoLong = tamanhoLong;
	}

	public void registrarEmergencia(String descricao, int latitude,
			int longitude) {
		new Emergencia(this, descricao, latitude, longitude);
	}

	public Emergencia pegarEmergenciaParaAtender() {
		if (emergenciasPendentes.size()==0)
			return null;
		return emergenciasPendentes.remove(0);
	}

	private int map_id = 0;

	public int map_create(String descricao, String tipo, int latitude,
			int longitude, int[] props) {
		synchronized (mapa) {
			map_id++;
			Objeto o = new Objeto(descricao, tipo, latitude, longitude, props);
			mapa.put(map_id, o);
			return map_id;
		}
	}

	public Objeto map_get(int objId) {
		synchronized (mapa) {
			return mapa.get(objId);
		}
	}

	public int map_getprop(int objId, int idx) {
		synchronized (mapa) {
			Objeto o = mapa.get(objId);
			synchronized (o) {
				return o.props[idx];
			}
		}
	}

	public void map_setprop(int objId, int idx, int value) {
		synchronized (mapa) {
			Objeto o = mapa.get(objId);
			synchronized (o) {
				o.props[idx]=value;
			}
		}
	}

	public void map_remove(int objId) {
		synchronized (mapa) {
			mapa.remove(objId);
		}
	}

	public void toCSV(StringBuilder ret) {
		Object[] items;
		synchronized (mapa) {
			items = mapa.values().toArray();
		}
		ret.append(nome);
		ret.append(',');
		ret.append(tamanhoLat);
		ret.append(',');
		ret.append(tamanhoLong);
		ret.append('\n');
		for (int i = 0; i < items.length; i++) {
			Objeto o = (Objeto) items[i];
			o.serialize(ret);
		}
	}
}

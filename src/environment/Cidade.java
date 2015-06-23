package environment;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import ontologia.entidades.Emergencia;

public class Cidade {

	public static Cidade singleton;

	public final String nome;
	public final int tamanhoLat;
	public final int tamanhoLong;

	public CopyOnWriteArrayList<Emergencia> emergenciasPendentes = new CopyOnWriteArrayList<Emergencia>();
	public CopyOnWriteArraySet<Endereco> mapa = new CopyOnWriteArraySet<Endereco>(); 

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
		return emergenciasPendentes.remove(0);
	}
}

package environment;

import jade.lang.acl.ACLMessage;

import java.util.ArrayList;

import agents.Ambulancia;
import agents.Emergencia190;

public class Cidade {

	private static ArrayList<Cidade> todasCidades = new ArrayList<Cidade>();

	public String nome;
	public Emergencia190 central;
	public int tamanhoLat = 600;
	public int tamanhoLong = 600;

	private ArrayList<Emergencia> emergenciasPendentes = new ArrayList<Emergencia>();
	private ArrayList<Emergencia> emergenciasSendoAtendidas = new ArrayList<Emergencia>();

	public Cidade(String nomeCidade, Emergencia190 central) {
		nome = nomeCidade;
		this.central = central;
	}

	public void registrarEmergenciaMedica(String descricao, int latitude, int longetude) {
		Emergencia emergencia = new Emergencia(descricao, latitude, longetude);
		synchronized (emergenciasPendentes) {
			emergenciasPendentes.add(emergencia);
		}
		central.novaEmergenciaMedica(emergencia);
	}

	public static Cidade registrarCentralDeEmergencia(String nomeCidade,
			Emergencia190 central) {
		synchronized (todasCidades) {
			if (todasCidades.size() > 0) {
				System.out
						.println("Por enquanto nao eh permitido mais de uma cidade");
				return null;
			}
			Cidade cidade = new Cidade(nomeCidade, central);
			todasCidades.add(cidade);
			return cidade;
		}
	}

	public static void fecharCentralDeEmergencia(Cidade cidade) {
		cidade.central = null;
		synchronized (todasCidades) {
			todasCidades.remove(cidade);
		}
	}
}

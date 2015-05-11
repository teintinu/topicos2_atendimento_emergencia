package environment;

import java.util.ArrayList;

import ontologia.Emergencia;
import ontologia.EmergenciaMedica;

public class Cidade {

	private static ArrayList<Cidade> todasCidades = new ArrayList<Cidade>();

	public String nome;
	public int tamanhoLat = 600;
	public int tamanhoLong = 600;

	private ArrayList<Emergencia> emergenciasPendentes = new ArrayList<Emergencia>();
	private ArrayList<Emergencia> emergenciasSendoAtendidas = new ArrayList<Emergencia>();

	public Cidade(String nomeCidade) {
		nome = nomeCidade;
	}

	public static Cidade registrarCentralDeEmergencia(String nomeCidade) {
		synchronized (todasCidades) {
			if (todasCidades.size() > 0) {
				System.out
						.println("Por enquanto nao eh permitido mais de uma cidade");
				return null;
			}
			Cidade cidade = new Cidade(nomeCidade);
			todasCidades.add(cidade);
			return cidade;
		}
	}

	public static void fecharCentralDeEmergencia(Cidade cidade) {
		synchronized (todasCidades) {
			todasCidades.remove(cidade);
		}
	}

	public void registrarEmergenciaMedica(String descricao, int latitude,
			int longitude) {
		EmergenciaMedica emergencia = new EmergenciaMedica(descricao, latitude,
				longitude);
		synchronized (emergenciasPendentes) {
			emergenciasPendentes.add(emergencia);
		}
	}

	public Emergencia pegarEmergenciaParaAtender(Class<EmergenciaMedica> class1) {
		synchronized (emergenciasPendentes) {
			for (Emergencia emergencia : emergenciasPendentes) {
				if (emergencia.getClass().isInstance(class1)) {
					emergenciasPendentes.remove(emergencia);
					emergenciasSendoAtendidas.add(emergencia);
					return emergencia;
				}
			}
		}
		return null;
	}
}

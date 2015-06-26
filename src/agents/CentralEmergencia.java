package agents;

import web.HttpServer;
import jade.core.Agent;
import behaviours.SimularEmergencias;
import behaviours.central.AlocarAmbulancia;
import behaviours.central.ControlaManutencao;
import environment.Cidade;

public class CentralEmergencia extends Agent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6158354184040721305L;
	private Cidade cidade;
	private Integer endereco;

	@Override
	protected void setup() {
		if (Cidade.central != null) {
			System.out
					.println("Só é suportado uma Central de emergencia por cidade");
			doDelete();
			return;
		}

		System.out.println("Registrando central de emergencia para cidade");
		cidade = Cidade.singleton;
		Cidade.central = this;

		endereco = cidade.map_create("Central", "central",
				cidade.tamanhoLat / 2, cidade.tamanhoLong / 2, new int[] {});

		addBehaviour(new SimularEmergencias(this, cidade));

		addBehaviour(new AlocarAmbulancia(cidade));
		addBehaviour(new ControlaManutencao(this));

		HttpServer.start();
	}

	@Override
	protected void takeDown() {
		System.out
				.println("Central de emergencia encerrou as atividades na cidade: "
						+ getAID().getLocalName());
		if (cidade != null) {
			Cidade.central = null;
			if (endereco != null)
				cidade.map_remove(endereco);
			Cidade.singleton = null;
		}
		cidade = null;
		// HttpServer.stop();
	}
}

package agents;

import web.HttpServer;
import jade.core.Agent;
import behaviours.SimularEmergencias;
import behaviours.central.AlocarAmbulancia;
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

		Cidade cidade = null;
		Object[] args = getArguments();

		System.out.println("Registrando central de emergencia para cidade");
		cidade = Cidade.singleton;
		cidade.central = this;

		endereco = cidade.map_create("Central", "central",
				cidade.tamanhoLat / 2, cidade.tamanhoLong / 2,0,0);

		addBehaviour(new SimularEmergencias(this, cidade));
		this.cidade = cidade;
		if (cidade == null) {
			System.out
					.println("Central de emergencia vai encerrar as atividades na cidade null: "
							+ getAID().getLocalName());
			doDelete();
		} else
			addBehaviour(new AlocarAmbulancia(cidade));

		HttpServer.start();
	}

	@Override
	protected void takeDown() {
		System.out
				.println("Central de emergencia encerrou as atividades na cidade: "
						+ getAID().getLocalName());
		if (cidade != null) {
			cidade.central = null;
			if (endereco != null)
				cidade.map_remove(endereco);
			Cidade.singleton = null;
		}
		cidade = null;
		//HttpServer.stop();
	}
}

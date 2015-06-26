package behaviours.central;

import java.util.concurrent.CopyOnWriteArrayList;

import environment.Cidade;
import ontologia.entidades.Emergencia;
import agents.Ambulancia;
import agents.CentralEmergencia;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class UsarNitroglicerina extends TickerBehaviour {

	private CentralEmergencia central;

	public UsarNitroglicerina(CentralEmergencia central) {
		super(central, 5000);
		this.central = central;
	}

	@Override
	protected void onTick() {
		central.setNitrogliceria(Cidade.singleton.emergenciasPendentes.size() > 10);
	}
}

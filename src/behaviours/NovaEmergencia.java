package behaviours;

import java.awt.image.TileObserver;
import java.util.Date;

import ontologia.Local;
import agents.Ambulancia;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class NovaEmergencia extends CyclicBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6294712118122235914L;
	private Ambulancia ambulancia;

	public NovaEmergencia(Agent agent) {
		ambulancia=(Ambulancia) agent;
	}

	private static MessageTemplate filtro = MessageTemplate
			.MatchPerformative(ACLMessage.INFORM)
			.MatchConversationId(ontologia.Servico.AtenderEmergenciaMedica);
	
	@Override
	public void action() {
		
		ACLMessage msg = ambulancia.receive(filtro);
		if (msg != null) {

		}
		else 
			block();
	}
}

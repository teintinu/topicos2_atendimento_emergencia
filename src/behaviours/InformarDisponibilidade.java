package behaviours;

import java.awt.image.TileObserver;
import java.util.Date;

import ontologia.Local;
import agents.Ambulancia;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class InformarDisponibilidade extends WakerBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6294712118122235914L;
	private Ambulancia ambulancia;

	public InformarDisponibilidade(Agent agent) {
		super(agent, 500);
		ambulancia=(Ambulancia) agent;
	}

	@Override
	protected void handleElapsedTimeout() {
		MessageTemplate filtro = MessageTemplate
				.MatchConversationId(ontologia.Acao.InformarDisponibilidade);
		ACLMessage msg = ambulancia.receive(filtro);
		if (msg != null) {
Local localDaEmergencia=new Local(msg.getContent());
		}
	}
}

package behaviours.ambulancia;

import java.io.IOException;

import environment.Cidade;
import agents.Ambulancia;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class VerificaSePrecisaManutencaoAmbulancia extends Behaviour {

	/**
	 *
	 */
	private static final long serialVersionUID = 9159985426381656992L;

	private MessageTemplate mt = MessageTemplate.and(MessageTemplate
			.MatchConversationId(ontologia.Servicos.ManutencaoVeiculo),
			MessageTemplate.MatchPerformative(ACLMessage.AGREE));

	@Override
	public void action() {
		Ambulancia amb = (Ambulancia) myAgent;
		if (amb.precisaManutencao()) {
			ACLMessage req = new ACLMessage(ACLMessage.REQUEST);
			req.setConversationId(ontologia.Servicos.ManutencaoVeiculo);
			req.addReceiver(Cidade.central.getAID());
			amb.send(req);
			block(1000);
		}
		ACLMessage agree = amb.receive(mt);
		if (agree != null) {
			if (amb.precisaManutencao()) {
				amb.setManutStatus(1);
				amb.addBehaviour(new FazerManutencaoAmbulancia(amb));
			}
			ACLMessage conf = new ACLMessage(ACLMessage.INFORM);
			conf.setConversationId(ontologia.Servicos.ManutencaoVeiculo);
			conf.addReceiver(Cidade.central.getAID());
			amb.send(conf);
		}
	}

	@Override
	public boolean done() {
		return false;
	}
}

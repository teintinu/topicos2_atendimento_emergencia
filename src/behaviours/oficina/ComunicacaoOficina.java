package behaviours.oficina;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;

import ontologia.entidades.Emergencia;
import agents.Ambulancia;
import agents.Oficina;

public class ComunicacaoOficina extends CyclicBehaviour {

	/**
	 *
	 */
	private static final long serialVersionUID = 3194845523280405159L;

	@Override
	public void action() {
		ACLMessage msg = myAgent.receive();
		if (msg != null) {
			if (msg.getPerformative() == ACLMessage.CFP
					&& ontologia.Servicos.ManutencaoVeiculo.equals(msg
							.getConversationId())) {
				Oficina oficina = (Oficina) myAgent;
				ACLMessage reply = msg.createReply();
				reply.setConversationId(ontologia.Servicos.ManutencaoVeiculo);
				try {
					reply.setContentObject(oficina.endereco);
				} catch (IOException e) {
					e.printStackTrace();
				}
				reply.setPerformative(ACLMessage.PROPOSE);
				myAgent.send(reply);
			}
		}
	}
}

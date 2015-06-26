package behaviours.ambulancia;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;

import ontologia.entidades.Emergencia;
import agents.Ambulancia;

public class ComunicacaoAmbulanciaCentral extends CyclicBehaviour {

	/**
	 *
	 */
	private static final long serialVersionUID = 3194845523280405159L;

	@Override
	public void action() {
		ACLMessage msg = myAgent.receive();
		if (msg != null) {
			if (msg.getPerformative() == ACLMessage.CFP
					&& ontologia.Servicos.TransportarPacientes.equals(msg
							.getConversationId())) {
				Ambulancia amb = (Ambulancia) myAgent;
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.REFUSE);
				if (amb.livre() && amb.getManutStatus() == 0)
					try {
						reply.setContentObject(amb.endereco);
						reply.setPerformative(ACLMessage.PROPOSE);
					} catch (IOException e) {
						e.printStackTrace();
					}
				myAgent.send(reply);
			} else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL
					&& ontologia.Servicos.TransportarPacientes.equals(msg
							.getConversationId())) {
				Ambulancia amb = (Ambulancia) myAgent;
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.REFUSE);
				if (amb.livre())
					try {
						Emergencia e = (Emergencia) msg.getContentObject();
						amb.addBehaviour(new BuscarPaciente(amb, e));
						reply.setContentObject(amb.endereco);
						reply.setPerformative(ACLMessage.INFORM);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (UnreadableException e1) {
						e1.printStackTrace();
					}
				myAgent.send(reply);
			}
		}
	}
}

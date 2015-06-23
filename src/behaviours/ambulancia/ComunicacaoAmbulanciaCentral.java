package behaviours.ambulancia;

import java.io.IOException;

import ontologia.entidades.Emergencia;
import ontologia.status.AmbulanciaStatus;
import agents.Ambulancia;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SenderBehaviour;
import jade.lang.acl.ACLMessage;

public class ComunicacaoAmbulanciaCentral extends CyclicBehaviour {

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
				if (amb.getStatus() == AmbulanciaStatus.Livre)
					try {
						reply.setContentObject(amb.getEndereco());
						reply.setPerformative(ACLMessage.PROPOSE);
					} catch (IOException e) {
						e.printStackTrace();
					}
				myAgent.send(reply);
			}
			else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL
					&& ontologia.Servicos.TransportarPacientes.equals(msg
							.getConversationId())) {
				Ambulancia amb = (Ambulancia) myAgent;
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.REFUSE);
				if (amb.getStatus() == AmbulanciaStatus.Livre)
					try {
						Emergencia e=(Emergencia) msg.getContentObject();
						amb.buscarPaciente(e);
						reply.setContentObject(amb.getEndereco());
						reply.setPerformative(ACLMessage.PROPOSE);
					} catch (IOException e) {
						e.printStackTrace();
					}
				myAgent.send(reply);
			}
		}
	}
}

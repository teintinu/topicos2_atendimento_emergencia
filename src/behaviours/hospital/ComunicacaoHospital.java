package behaviours.hospital;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;

import ontologia.entidades.Emergencia;
import agents.Ambulancia;
import agents.Hospital;

public class ComunicacaoHospital extends CyclicBehaviour {

	@Override
	public void action() {
		
		ACLMessage msg = myAgent.receive();
		if (msg != null) {
			if (msg.getPerformative() == ACLMessage.CFP
					&& ontologia.Servicos.TratarPacientes.equals(msg
							.getConversationId())) {
				Hospital hosp = (Hospital) myAgent;
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.REFUSE);
				if (hosp.leitos_disponiveis()>0)
					try {
						reply.setContentObject(hosp.endereco);
						reply.setPerformative(ACLMessage.PROPOSE);
					} catch (IOException e) {
						e.printStackTrace();
					}
				myAgent.send(reply);
			} else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL
					&& ontologia.Servicos.TratarPacientes.equals(msg
							.getConversationId())) {
				Hospital hosp = (Hospital) myAgent;
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.REFUSE);
				reply.setConversationId(ontologia.Servicos.TratarPacientes);
				if (hosp.ocuparLeito())
					try {
						System.out.println("Hospital "+hosp.getLocalName()+" reservou leito");
						Emergencia e = (Emergencia) msg.getContentObject();
						reply.setContentObject(hosp.endereco);
						reply.setPerformative(ACLMessage.INFORM);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (UnreadableException e1) {
						e1.printStackTrace();
					}
				myAgent.send(reply);
			} else if (msg.getPerformative() == ACLMessage.INFORM) {
				try {
					Emergencia e = (Emergencia) msg.getContentObject();
					Hospital hosp = (Hospital) myAgent;
					hosp.addBehaviour(new TrataPaciente(hosp, e));
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}
		}

	}
}

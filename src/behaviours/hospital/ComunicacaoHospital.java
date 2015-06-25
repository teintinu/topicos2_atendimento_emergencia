package behaviours.hospital;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;

import ontologia.entidades.Emergencia;
import ontologia.status.AmbulanciaStatus;
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
				if (hosp.leitos_em_uso < hosp.qtde_leitos)
					try {
						reply.setContentObject(hosp.endereco);
						reply.setPerformative(ACLMessage.PROPOSE);
					} catch (IOException e) {
						e.printStackTrace();
					}
				System.out.println("aaaaaaaaaaaaaaaa-reply");
				myAgent.send(reply);
			} else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL
					&& ontologia.Servicos.TratarPacientes.equals(msg
							.getConversationId())) {
				Hospital hosp = (Hospital) myAgent;
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.REFUSE);
				if (hosp.leitos_em_uso < hosp.qtde_leitos)
					try {
						hosp.leitos_em_uso++;
						Emergencia e = (Emergencia) msg.getContentObject();
						reply.setContentObject(hosp.endereco);
						reply.setPerformative(ACLMessage.PROPOSE);
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
					hosp.addBehaviour(new TrataPaciente(hosp, e, 2000));
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}
		}

	}
}

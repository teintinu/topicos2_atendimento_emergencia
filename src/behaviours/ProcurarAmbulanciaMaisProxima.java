package behaviours;

import ontologia.Emergencia;
import ontologia.Local;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ProcurarAmbulanciaMaisProxima extends Behaviour {
	
		private AID ambulanciaMaisProxima; 
		private int menorDistancia;
		private int repliesPending = 0; 
		private MessageTemplate mt; 
		private ProcurarAmbulanciaMaisProximaPasso passo = ProcurarAmbulanciaMaisProximaPasso.PerguntaLocalDaAmbulancia;
		private Emergencia emergencia;

		public ProcurarAmbulanciaMaisProxima(Emergencia emergencia) {
			this.emergencia = emergencia;
		}

		public void action() {
			switch (passo) {
			case PerguntaLocalDaAmbulancia:
				// Send the cfp to all sellers
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("book-selling");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template); 
					for (int i = 0; i < result.length; ++i) {
						cfp.addReceiver(result[i].getName());
					}
					repliesPending=result.length;
				}
				catch (FIPAException fe) {
					fe.printStackTrace();
				}
				cfp.setContent("");
				cfp.setConversationId(ontologia.Servico.InformeLocal);
				cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
				myAgent.send(cfp);
				// Prepare the template to get proposals 
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId(ontologia.Servico.InformeLocal),
						MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
				passo=ProcurarAmbulanciaMaisProximaPasso.RecebeLocalDasAmbulancias;
				break;
			case RecebeLocalDasAmbulancias:
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					if (reply.getPerformative() == ACLMessage.PROPOSE) {
						Local local_amb = Local.parse(reply.getContent());
						if (local_amb!=null){
							int distancia = local_amb.distancia(emergencia.getLocal());
						if (ambulanciaMaisProxima == null || distancia < menorDistancia) {
							ambulanciaMaisProxima=reply.getSender();
							menorDistancia=distancia;
						}
						}
					}
					repliesPending--;
					if (repliesPending<=0) 
						passo=ProcurarAmbulanciaMaisProximaPasso.EncaminhaEmergenciaParaAmbulancia;
				}
				else {
					block();
				}
				break;
			case EncaminhaEmergenciaParaAmbulancia:
				// Send the purchase order to the seller that provided the best offer
				ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				order.addReceiver(ambulanciaMaisProxima);
				order.setContent(targetBookTitle);
				order.setConversationId("book-trade");
				order.setReplyWith("order"+System.currentTimeMillis());
				myAgent.send(order);
				// Prepare the template to get the purchase order reply
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
						MessageTemplate.MatchInReplyTo(order.getReplyWith()));
				step = 3;
				break;
			case RECEBE_CONFIRMACAO_DA_AMBULANCIA:      
				// Receive the purchase order reply
				reply = myAgent.receive(mt);
				if (reply != null) {
					// Purchase order reply received
					if (reply.getPerformative() == ACLMessage.INFORM) {
						// Purchase successful. We can terminate
						System.out.println(targetBookTitle+" successfully purchased from agent "+reply.getSender().getName());
						System.out.println("Price = "+bestPrice);
						myAgent.doDelete();
					}
					else {
						System.out.println("Attempt failed: requested book already sold.");
					}

					step = ;
				}
				else {
					block();
				}
				break;
			}        
		}

		public boolean done() {
			if (step == 2 && bestSeller == null) {
				System.out.println("Attempt failed: "+targetBookTitle+" not available for sale");
			}
			return ((step == 2 && bestSeller == null) || step == 4);
		}
	}  // End of inner class RequestPerformer

}

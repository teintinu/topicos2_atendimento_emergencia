package behaviours.central;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;

import ontologia.entidades.Emergencia;
import environment.Cidade;
import environment.Objeto;

public class GerenciamentoDeTransporteDePacientes extends Behaviour {

	private AID ambulanciaMaisProxima;
	private int menorDistancia;
	private int repliesPending = 0;
	private MessageTemplate mt;
	private Passos passo = Passos.PegaEmergenciaDaFila;
	private Emergencia emergencia;
	private Cidade cidade;

	public GerenciamentoDeTransporteDePacientes(Cidade cidade) {
		this.cidade = cidade;
		this.emergencia = null;
	}

	public void action() {
		switch (passo) {
		case PegaEmergenciaDaFila:
			if (emergencia == null)
				emergencia = cidade.pegarEmergenciaParaAtender();
			if (emergencia == null)
				block();
			else
				passo = Passos.PerguntaEnderecoDasAmbulancias;
		case PerguntaEnderecoDasAmbulancias:
			System.out.println("Procurando ambulancia para emergencia");
			ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
			cfp.setConversationId(ontologia.Servicos.TransportarPacientes);
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType(ontologia.Servicos.TransportarPacientes);
			template.addServices(sd);
			try {
				DFAgentDescription[] result = DFService.search(myAgent,
						template);
				for (int i = 0; i < result.length; ++i) {
					cfp.addReceiver(result[i].getName());
				}
				repliesPending = result.length;
			} catch (FIPAException fe) {
				fe.printStackTrace();
			}
			cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique
																	// value
			myAgent.send(cfp);
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId(ontologia.Servicos.TransportarPacientes),
					MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
			
			passo = Passos.RecebeEnderecoDasAmbulancias;
			break;
		case RecebeEnderecoDasAmbulancias:
			ACLMessage reply = myAgent.receive(mt);
			if (reply != null) {
				System.out.println("Proposta de "
						+ reply.getSender().getLocalName()
						+ " para atender a emergencia");
				if (reply.getPerformative() == ACLMessage.PROPOSE) {
					try {
						Objeto endereco = cidade.map_get(  (int) reply.getContentObject());
						if (endereco != null) {
							int distancia = endereco.distancia(cidade.map_get(emergencia
									.endereco));
							if (ambulanciaMaisProxima == null
									|| distancia < menorDistancia) {
								ambulanciaMaisProxima = reply.getSender();
								menorDistancia = distancia;
							}
							System.out.println("Distância de "
									+ reply.getSender().getLocalName()
									+ " até a emergencia"+ " distancia= "
									+ distancia);
						}
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}
				repliesPending--;
				if (repliesPending <= 0)
					passo = Passos.EncaminhaEmergenciaParaAmbulancia;
			} else {
				block();
			}
			break;
		case EncaminhaEmergenciaParaAmbulancia:
			System.out.println("Aceitando a proposta de "
					+ ambulanciaMaisProxima.getLocalName()
					+ " para atender a emergencia");
			ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
			order.addReceiver(ambulanciaMaisProxima);
			try {
				order.setConversationId(ontologia.Servicos.TransportarPacientes);
				order.setContentObject(emergencia);
				myAgent.send(order);
				mt = MessageTemplate.and(
						MessageTemplate.MatchConversationId(ontologia.Servicos.TransportarPacientes),
						MessageTemplate.MatchInReplyTo(order.getReplyWith()));
				passo = Passos.TrataRespostaDaAmbulancia;
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case TrataRespostaDaAmbulancia:
			reply = myAgent.receive(mt);
			if (reply != null) {
				if (reply.getPerformative() == ACLMessage.INFORM) {
					System.out.println(ambulanciaMaisProxima.getLocalName()
							+ " informou que irá atender a emergencia");
					this.emergencia = null;
				} else {
					System.out.println(ambulanciaMaisProxima.getLocalName()
							+ " informou que nao irá pode atender a emergencia, tentando outra");
				}
				passo = Passos.PegaEmergenciaDaFila;
			} else {
				block();
			}
			break;
		}
	}

	public boolean done() {
		return false;
	}
}

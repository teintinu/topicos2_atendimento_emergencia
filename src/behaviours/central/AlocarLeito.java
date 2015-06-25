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

import behaviours.ambulancia.TransportarPaciente;
import agents.Ambulancia;
import ontologia.entidades.Emergencia;
import environment.Cidade;
import environment.Objeto;

public class AlocarLeito extends Behaviour {

	private AID hospitalMaisProximo;
	private int menorDistancia;
	private int repliesPending = 0;
	private MessageTemplate mt;
	private HospitalPassos passo = HospitalPassos.PerguntarLeitosLivresAosHospitais;
	private Emergencia emergencia;
	private Ambulancia ambulancia;

	public AlocarLeito(Ambulancia amb, Emergencia e) {
		this.emergencia = e;
		this.ambulancia = amb;
	}

	public void action() {
		switch (passo) {
		case PerguntarLeitosLivresAosHospitais:
			System.out.println("Procurando hospital");
			ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
			cfp.setConversationId(ontologia.Servicos.TratarPacientes);
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType(ontologia.Servicos.TratarPacientes);
			template.addServices(sd);
			try {
				DFAgentDescription[] result = DFService.search(myAgent,
						template);
				for (int i = 0; i < result.length; ++i) {
					System.out.println("Notificando hospital: "+result[i].getName());
					cfp.addReceiver(result[i].getName());
				}
				repliesPending = result.length;
			} catch (FIPAException fe) {
				fe.printStackTrace();
			}
			cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique
																	// value
			myAgent.send(cfp);
			mt = MessageTemplate
					.and(MessageTemplate
							.MatchConversationId(ontologia.Servicos.TransportarPacientes),
							MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));

			passo = HospitalPassos.RecebeInformacoesDosHospitais;
			break;
		case RecebeInformacoesDosHospitais:
			
			ACLMessage reply = myAgent.receive(mt);
			if (reply != null) {
				System.out.println("Proposta do hospital "
						+ reply.getSender().getLocalName()
						+ " para atender a emergencia");
				if (reply.getPerformative() == ACLMessage.PROPOSE) {
					try {
						Objeto endereco_hospital = Cidade.singleton
								.map_get((int) reply.getContentObject());
						if (endereco_hospital != null) {
							int distancia = endereco_hospital
									.distancia(Cidade.singleton
											.map_get(emergencia.endereco));
							if (hospitalMaisProximo == null
									|| distancia < menorDistancia) {
								hospitalMaisProximo = reply.getSender();
								menorDistancia = distancia;
							}
							System.out.println("Distância de "
									+ reply.getSender().getLocalName()
									+ " até a emergencia" + " distancia= "
									+ distancia);
						}
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}
				repliesPending--;
				if (repliesPending <= 0)
					passo = HospitalPassos.SelecionaHospital;
			}
			break;
		case SelecionaHospital:
			System.out.println("Aceitando a proposta do hospital "
					+ hospitalMaisProximo.getLocalName()
					+ " para atender a emergencia");
			ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
			order.addReceiver(hospitalMaisProximo);
			try {
				order.setConversationId(ontologia.Servicos.TratarPacientes);
				order.setContentObject(emergencia);
				myAgent.send(order);
				mt = MessageTemplate
						.and(MessageTemplate
								.MatchConversationId(ontologia.Servicos.TratarPacientes),
								MessageTemplate.MatchInReplyTo(order
										.getReplyWith()));
				passo = HospitalPassos.ConfirmarReservaLeito;
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case ConfirmarReservaLeito:
			reply = myAgent.receive(mt);
			if (reply != null) {
				if (reply.getPerformative() == ACLMessage.INFORM) {
					System.out.println(hospitalMaisProximo.getLocalName()
							+ " informou que irá atender a emergencia");
					this.emergencia = null;
				} else {
					System.out
							.println(hospitalMaisProximo.getLocalName()
									+ " informou que nao irá pode atender a emergencia, tentando outra");
				}
				Objeto endereco_hospital;
				try {
					endereco_hospital = Cidade.singleton.map_get((int) reply
							.getContentObject());
					ambulancia.addBehaviour(new TransportarPaciente(emergencia,
							hospitalMaisProximo, endereco_hospital));
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
				passo = HospitalPassos.TransportandoPaciente;
			} else {
				block();
			}
			break;
		}
	}

	public boolean done() {
		return passo == HospitalPassos.TransportandoPaciente;
	}
}

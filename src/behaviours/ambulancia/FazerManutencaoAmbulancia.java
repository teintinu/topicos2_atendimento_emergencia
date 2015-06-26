package behaviours.ambulancia;

import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.Date;

import agents.Ambulancia;
import environment.Cidade;
import environment.Objeto;
import environment.Walk;

public class FazerManutencaoAmbulancia extends Behaviour {

	/**
	 *
	 */
	private static final long serialVersionUID = -1417932487595129339L;
	final private int ESPERADO_ESTAR_LIVRE = 2;
	final private int PROCURANDO_OFICINA = 3;
	final private int ESCOLHENDO_OFICINA = 4;
	final private int INDO_OFICINA = 5;
	final private int EXECUTANDO_MANUTENCAO = 6;
	final private int MANUTENCAO_CONCLUIDA = 7;
	int passo = ESPERADO_ESTAR_LIVRE;
	private Ambulancia ambulancia;

	private MessageTemplate mt;
	private int repliesPending;
	private Objeto oficinaMaisProxima;
	private int menorDistancia;
	private long stampManut;
	private Walk walk;

	public FazerManutencaoAmbulancia(Ambulancia amb) {
		super(amb);
		ambulancia = amb;
	}

	@Override
	public void action() {

		switch (passo) {
		case ESPERADO_ESTAR_LIVRE:
			if (ambulancia.statusEmManutencao()) {
				passo = PROCURANDO_OFICINA;
				ambulancia.setManutStatus(passo);
			}
			break;

		case PROCURANDO_OFICINA:
			System.out.println("Procurando oficinas mecanicas");
			ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
			cfp.setConversationId(ontologia.Servicos.ManutencaoVeiculo);
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType(ontologia.Servicos.ManutencaoVeiculo);
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
			cfp.setReplyWith("cfp" + System.currentTimeMillis());
			myAgent.send(cfp);
			mt = MessageTemplate.and(MessageTemplate
					.MatchConversationId(ontologia.Servicos.ManutencaoVeiculo),
					MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));

			passo = ESCOLHENDO_OFICINA;
			ambulancia.setManutStatus(passo);
			stampManut = new Date().getTime() + 1000;
			break;
		case ESCOLHENDO_OFICINA:
			ACLMessage reply = myAgent.receive(mt);
			if (reply != null) {
				if (reply.getPerformative() == ACLMessage.PROPOSE) {
					try {
						Objeto endereco = Cidade.singleton.map_get((int) reply
								.getContentObject());
						if (endereco != null) {
							int distancia = endereco.distancia(Cidade.singleton
									.map_get(ambulancia.endereco));
							if (oficinaMaisProxima == null
									|| distancia < menorDistancia) {
								oficinaMaisProxima = endereco;
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
				if (oficinaMaisProxima == null)
					passo = PROCURANDO_OFICINA;
				else {
					walk = new Walk(
							Cidade.singleton.map_get(ambulancia.endereco),
							oficinaMaisProxima, null, ambulancia.velocidade);
					passo = INDO_OFICINA;
				}
			} else if (new Date().getTime() - stampManut > 0)
				passo = PROCURANDO_OFICINA;

			ambulancia.setManutStatus(passo);
			break;
		case INDO_OFICINA:
			walk.walk();
			if (walk.chegou) {
				ambulancia.setComecouManutencao();
				stampManut = new Date().getTime()
						+ ((int) Math.random() * 10000 + 5000);
				passo = EXECUTANDO_MANUTENCAO;
				ambulancia.setManutStatus(passo);
			} else
				ambulancia.km_rodado(walk.km_rodado());
			break;
		case EXECUTANDO_MANUTENCAO:
			if (new Date().getTime() - stampManut > 0) {
				passo = MANUTENCAO_CONCLUIDA;
				ACLMessage conf = new ACLMessage(ACLMessage.CONFIRM);
				conf.setConversationId(ontologia.Servicos.ManutencaoVeiculo);
				conf.addReceiver(Cidade.central.getAID());
				ambulancia.send(conf);
				 conf = new ACLMessage(ACLMessage.CONFIRM);
				conf.setConversationId(ontologia.Servicos.ManutencaoVeiculo);
				conf.addReceiver(Cidade.central.getAID());
				ambulancia.send(conf);
				ambulancia.setTerminouManutencao();
			}
		}
	}

	@Override
	public boolean done() {
		return passo == MANUTENCAO_CONCLUIDA;
	}

}

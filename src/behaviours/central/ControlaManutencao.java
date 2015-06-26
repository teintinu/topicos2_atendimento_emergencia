package behaviours.central;

import java.util.concurrent.CopyOnWriteArrayList;

import environment.Cidade;
import ontologia.entidades.Emergencia;
import agents.Ambulancia;
import agents.CentralEmergencia;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ControlaManutencao extends Behaviour {

	private CentralEmergencia central;

	public ControlaManutencao(CentralEmergencia central) {
		super(central);
		this.central = central;
	}

	/**
	 *
	 */
	private static final long serialVersionUID = -888346907936483337L;
	private AID emManutencao;
	private boolean pendente;
	public CopyOnWriteArrayList<AID> pedidosManutencao = new CopyOnWriteArrayList<AID>();
	private MessageTemplate mt = MessageTemplate
			.MatchConversationId(ontologia.Servicos.ManutencaoVeiculo);

	@Override
	public void action() {
		ACLMessage m = central.receive(mt);
		if (m != null) {
			if (m.getPerformative() == ACLMessage.REQUEST) {
				if (!pedidosManutencao.contains(m.getSender()))
					pedidosManutencao.add(m.getSender());
			} else if (m.getPerformative() == ACLMessage.INFORM
					&& m.getSender().equals(emManutencao)) {
				pendente = false;
			} else if (m.getPerformative() == ACLMessage.CONFIRM
					&& m.getSender().equals(emManutencao)) {
				pedidosManutencao.remove(emManutencao);
				emManutencao = null;
			}
		}

		if (emManutencao == null) {
			if (pedidosManutencao.size() > 0) {
				emManutencao = pedidosManutencao.remove(0);
				pendente = true;
				System.out
						.println("Manutencao: " + emManutencao.getLocalName());
			}
		}
		if (emManutencao != null && pendente) {
			m = new ACLMessage(ACLMessage.AGREE);
			m.setConversationId(ontologia.Servicos.ManutencaoVeiculo);
			m.addReceiver(emManutencao);
			central.send(m);
		}
	}

	@Override
	public boolean done() {
		return false;
	}
}

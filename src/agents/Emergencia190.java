package agents;

import java.util.ArrayList;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import environment.Cidade;
import environment.Emergencia;

public class Emergencia190 extends Agent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6158354184040721305L;
	private Cidade cidade;

	@Override
	protected void setup() {
		System.out.println("Central de emergencia aberta para cidade: "
				+ getAID().getLocalName());

		Cidade cidade = null;
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			String nomeCidade = (String) args[0];
			System.out.println("Registrando central de emergencia para cidade "
					+ nomeCidade);
			cidade = Cidade.registrarCentralDeEmergencia(nomeCidade, this);
		}

		this.cidade = cidade;
		if (cidade == null)
			doDelete();
	}

	@Override
	protected void takeDown() {
		System.out
				.println("Central de emergencia encerrou as atividades na cidade: "
						+ getAID().getLocalName());
		if (cidade != null)
			Cidade.fecharCentralDeEmergencia(cidade);
		cidade = null;
	}

	public void novaEmergenciaMedica(Emergencia emergencia) {

		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setConversationId(ontologia.Acao.InformarDisponibilidade);
		msg.setLanguage("English");
		msg.setOntology(ontologia.AtendimentoEmergencia.NomeOntologia);
		msg.setContent("Aconteceu um acidente");

		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(ontologia.Servico.AtenderEmergencia);
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			for (int i = 0; i < result.length; ++i) {
				msg.addReceiver(result[i].getName());
			}
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		send(msg);
	}
}

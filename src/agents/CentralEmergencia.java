package agents;


import java.util.ArrayList;

import behaviours.VerificarSeHaEmergenciasSemAgente;
import ontologia.Emergencia;
import environment.Cidade;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class CentralEmergencia extends Agent {
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
			cidade = Cidade.registrarCentralDeEmergencia(nomeCidade);
		}

		this.cidade = cidade;
		if (cidade == null)
			doDelete();
		else 
			new VerificarSeHaEmergenciasSemAgente(this, cidade);
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
		msg.setLanguage("English");
		msg.setOntology(ontologia.OntologiaEmergencia.NOME);
		msg.setConversationId(ontologia.Servico.AtenderEmergenciaMedica);
		msg.setContent("Aconteceu um acidente");

		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(ontologia.Servico.AtenderEmergenciaMedica);
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

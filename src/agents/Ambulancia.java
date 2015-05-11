package agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class Ambulancia extends Agent {
	private AmbulanciaStatus status = AmbulanciaStatus.Livre;
	/**
	 * 
	 */
	private static final long serialVersionUID = 5167273497591319321L;

	@Override
	protected void setup() {
		System.out.println("Motorista de ambulância contratado: "
				+ getAID().getLocalName()); 

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(ontologia.Servico.AtenderEmergenciaMedica);
		sd.setName("ambulancia: "+getAID().getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
			statusLivre();
		} catch (FIPAException fe) {
			fe.printStackTrace();
			doDelete();
		}
	}

	private void statusLivre() {
		System.out.println("Ambulância está livre e a postos");
		addBehaviour(new VerificaHaEmergencias());
	}

	@Override
	protected void takeDown() {
		System.out.println("Motorista de ambulância demitido: "
				+ getAID().getLocalName());
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
}

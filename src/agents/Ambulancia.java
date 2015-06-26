package agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import ontologia.entidades.Emergencia;
import behaviours.ambulancia.ComunicacaoAmbulanciaCentral;
import environment.Cidade;
import environment.Objeto;

public class Ambulancia extends Agent {
	public Integer endereco;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5167273497591319321L;

	@Override
	protected void setup() {
		System.out.println("Motorista de ambulância contratado: "
				+ getAID().getLocalName());

		endereco = Cidade.singleton.map_create(getAID().getLocalName(),
				"ambulancia", Cidade.singleton.tamanhoLat / 2,
				Cidade.singleton.tamanhoLong / 2, 0, 1);

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(ontologia.Servicos.TransportarPacientes);
		sd.setName("ambulancia: " + getAID().getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
			addBehaviour(new ComunicacaoAmbulanciaCentral());
			setStatusLivre();
		} catch (FIPAException fe) {
			fe.printStackTrace();
			doDelete();
		}
	}

	@Override
	protected void takeDown() {
		if (Cidade.singleton != null) {
			if (endereco != null)
				Cidade.singleton.map_remove(endereco);
		}
		System.out.println("Motorista de ambulância demitido: "
				+ getAID().getLocalName());
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	public void setStatusLivre() {
		Objeto o = Cidade.singleton.map_get(endereco);
		synchronized (o) {
			o.pos = 0;
		}
	}

	public void setStatusBuscarPaciente(Emergencia e) {
		Objeto o = Cidade.singleton.map_get(endereco);
		synchronized (o) {
			o.pos = 1;
			o.max = e.endereco;
		}
	}

	public void setStatusTransportarPacienteParaHospital(Emergencia e) {
		Objeto o = Cidade.singleton.map_get(endereco);
		synchronized (o) {
			o.pos = 2;
			o.max = e.endereco;
		}
	}

	public boolean livre() {
		Objeto o = Cidade.singleton.map_get(endereco);
		synchronized (o) {
			return o.pos == 0;
		}
	}
}

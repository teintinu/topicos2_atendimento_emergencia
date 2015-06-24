package agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import ontologia.entidades.Emergencia;
import ontologia.status.AmbulanciaStatus;
import behaviours.ambulancia.ComunicacaoAmbulanciaCentral;
import environment.Cidade;

public class Ambulancia extends Agent {
	private AmbulanciaStatus status = AmbulanciaStatus.Livre;
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
				Cidade.singleton.tamanhoLong / 2);

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
		if (endereco != null)
			Cidade.singleton.map_remove(endereco);
		System.out.println("Motorista de ambulância demitido: "
				+ getAID().getLocalName());
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	public AmbulanciaStatus getStatus() {
		return status;
	}

	public void setStatusLivre() {
		status = AmbulanciaStatus.Livre;
		System.out.println(getAID().getLocalName()
				+ ": Ambulância está livre e a postos");
	}

	public void setStatusBuscarPaciente() {
		status = AmbulanciaStatus.IndoAtenderBuscarPaciente;
		System.out.println(getAID().getLocalName()
				+ ": Ambulância está indo buscar um paciente");
	}
	
	public void setStatusTransportarPacienteParaHospital() {
		status = AmbulanciaStatus.TransportandoPacienteParaHospital;
		System.out.println(getAID().getLocalName()
				+ ": Ambulância está levando paciente para hospital");
	}
}

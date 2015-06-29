package behaviours.ambulancia;

import java.io.IOException;
import java.util.Date;

import agents.Ambulancia;
import ontologia.entidades.Emergencia;
import environment.Cidade;
import environment.Objeto;
import environment.Walk;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class TransportarPaciente extends Behaviour {

	public static int emergenciaSemAtendimento;

	private Ambulancia ambulancia;
	private Emergencia emergencia;
	private Objeto endereco_hospital;
	private AID hospistal;
	private Walk walk;

	public TransportarPaciente(Ambulancia amb, Emergencia e, AID hospistal,
			Objeto endereco_hospital) {
		super(amb);
		this.hospistal = hospistal;
		ambulancia = (Ambulancia) myAgent;
		emergencia = e;
		this.endereco_hospital = endereco_hospital;

		walk = new Walk(Cidade.singleton.map_get(ambulancia.endereco),
				endereco_hospital,
				Cidade.singleton.map_get(emergencia.endereco),
				ambulancia.velocidade
						* (Cidade.central.getNitrogliceria() ? 180 : 100) / 100);
		ambulancia.setStatusTransportePacienteParaHospital(e);
	}

	@Override
	public void action() {
		if (emergenciaSemAtendimento == emergencia.endereco) {
			walk = null;
			emergenciaSemAtendimento = 0;
			Cidade.singleton.emergenciasPendentes.add(emergencia);
			return;
		}
		walk.walk();
		if (walk.chegou) {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setConversationId(ontologia.Servicos.TratarPacientes);
			try {
				msg.setContentObject(emergencia);
				msg.addReceiver(hospistal);
				ambulancia.send(msg);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			ambulancia.setStatusLivre();
		} else
			ambulancia.km_rodado(walk.km_rodado());
	}

	@Override
	public boolean done() {
		return walk == null || walk.chegou;
	}
}

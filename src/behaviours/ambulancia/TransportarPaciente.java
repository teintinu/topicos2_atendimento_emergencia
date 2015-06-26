package behaviours.ambulancia;

import java.io.IOException;
import java.util.Date;

import agents.Ambulancia;
import ontologia.entidades.Emergencia;
import environment.Cidade;
import environment.Objeto;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class TransportarPaciente extends Behaviour {
	private boolean chegou;
	private Ambulancia ambulancia;
	private Emergencia emergencia;
	private Objeto endereco_hospital;
	private AID hospistal;
	private long lastTick;

	public TransportarPaciente(Ambulancia amb, Emergencia e, AID hospistal,
			Objeto endereco_hospital) {
		super(amb);
		this.hospistal = hospistal;
		ambulancia = (Ambulancia) myAgent;
		emergencia = e;
		this.endereco_hospital = endereco_hospital;
		chegou = false;
		ambulancia.setStatusTransportarPacienteParaHospital(e);
		lastTick = new Date().getTime();
	}

	@Override
	public void action() {
		long tick = new Date().getTime();
		if (tick - lastTick < 20)
			return;
		lastTick = tick;
		Objeto amb = Cidade.singleton.map_get(ambulancia.endereco);
		Objeto e = Cidade.singleton.map_get(emergencia.endereco);
		chegou = amb.walkTo(endereco_hospital);
		e.copy(amb);
		if (chegou) {

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
		}
	}

	@Override
	public boolean done() {
		return chegou;
	}


}

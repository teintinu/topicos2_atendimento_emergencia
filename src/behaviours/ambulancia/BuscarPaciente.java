package behaviours.ambulancia;

import jade.core.behaviours.Behaviour;
import ontologia.entidades.Emergencia;
import agents.Ambulancia;
import environment.Cidade;
import environment.Objeto;

public class BuscarPaciente extends Behaviour {

	private Ambulancia ambulancia;
	private Emergencia emergencia;
	private boolean chegou;

	public BuscarPaciente(Emergencia e) {
		ambulancia = (Ambulancia) myAgent;
		emergencia = e;
		chegou = false;
		ambulancia.setStatusBuscarPaciente();
	}

	@Override
	public void action() {
		Objeto amb = Cidade.singleton.map_get(ambulancia.endereco);
		Objeto e = Cidade.singleton.map_get(emergencia.endereco);
		chegou = amb.walkTo(e);
		if (chegou)
			ambulancia.addBehaviour(new TransportarPaciente(emergencia));
	}

	@Override
	public boolean done() {
		return chegou;
	}

}

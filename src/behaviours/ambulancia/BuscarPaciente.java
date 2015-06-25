package behaviours.ambulancia;

import behaviours.central.AlocarLeito;
import jade.core.behaviours.Behaviour;
import ontologia.entidades.Emergencia;
import agents.Ambulancia;
import agents.CentralEmergencia;
import environment.Cidade;
import environment.Objeto;

public class BuscarPaciente extends Behaviour {

	private Ambulancia ambulancia;
	private Emergencia emergencia;
	private boolean chegou;

	public BuscarPaciente(Ambulancia amb, Emergencia e) {
		super(amb);
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
		if (chegou) {
			System.out.println("Ambulancia chegou no local da emergencia");
			Cidade.central.addBehaviour(new AlocarLeito(ambulancia, emergencia));
		}
	}

	@Override
	public boolean done() {
		return chegou;
	}

}

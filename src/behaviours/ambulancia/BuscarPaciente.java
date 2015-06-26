package behaviours.ambulancia;

import java.util.Date;

import behaviours.central.AlocarLeito;
import jade.core.behaviours.Behaviour;
import ontologia.entidades.Emergencia;
import agents.Ambulancia;
import agents.CentralEmergencia;
import environment.Cidade;
import environment.Objeto;
import environment.Walk;

public class BuscarPaciente extends Behaviour {

	private Ambulancia ambulancia;
	private Emergencia emergencia;
	private Walk walk;

	public BuscarPaciente(Ambulancia amb, Emergencia e) {
		super(amb);
		ambulancia = (Ambulancia) myAgent;
		emergencia = e;
		ambulancia.setStatusBuscarPaciente(e);
		walk = new Walk(Cidade.singleton.map_get(ambulancia.endereco),
				Cidade.singleton.map_get(emergencia.endereco), null, ambulancia.velocidade *
				(Cidade.central.getNitrogliceria()?180:100)/100
				);
	}

	@Override
	public void action() {
		walk.walk();
		if (walk.chegou) {
			System.out.println("Ambulancia chegou no local da emergencia");
			Cidade.central
					.addBehaviour(new AlocarLeito(ambulancia, emergencia));
		} else
			ambulancia.km_rodado(walk.km_rodado());
	}

	@Override
	public boolean done() {
		return walk.chegou;
	}

}

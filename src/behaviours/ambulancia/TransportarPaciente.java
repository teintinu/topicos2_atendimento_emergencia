package behaviours.ambulancia;

import ontologia.AtendimentoStatus;
import jade.core.behaviours.Behaviour;

public class TransportarPaciente extends Behaviour {

	private AtendimentoStatus status = AtendimentoStatus.AmbulanciaIndoAoLocalDaEmergencia;

	@Override
	public void action() {
		if (status == AtendimentoStatus.AmbulanciaIndoAoLocalDaEmergencia)
			dirigir();
		else if (status == AtendimentoStatus.Atendendo)
			status = AtendimentoStatus.Atendido;
	}

	private void dirigir() {
		status = AtendimentoStatus.Atendendo;
	}

	@Override
	public boolean done() {
		return status == AtendimentoStatus.Atendido;
	}

}

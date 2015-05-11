package behaviours;

import jade.core.behaviours.Behaviour;

public class AtendenteAcidentado extends Behaviour {

	private StatusAtendimento status = StatusAtendimento.EmTransito;

	@Override
	public void action() {
		if (status == StatusAtendimento.EmTransito)
			dirigir();
		else if (status == StatusAtendimento.Atendendo)
			status = StatusAtendimento.Atendido;
	}

	private void dirigir() {
		status = StatusAtendimento.Atendendo;
	}

	@Override
	public boolean done() {
		return status == StatusAtendimento.Atendido;
	}

}

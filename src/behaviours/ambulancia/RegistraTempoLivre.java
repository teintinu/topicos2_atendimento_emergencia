package behaviours.ambulancia;

import agents.Ambulancia;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class RegistraTempoLivre extends TickerBehaviour {

	/**
	 *
	 */
	private static final long serialVersionUID = 9159985426381656992L;

	public RegistraTempoLivre(Agent a) {
		super(a, 1000);
	}

	@Override
	protected void onTick() {
		Ambulancia amb = (Ambulancia) myAgent;
		amb.registraTempo();
	}
}

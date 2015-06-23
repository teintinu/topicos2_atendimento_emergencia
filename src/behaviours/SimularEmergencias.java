package behaviours;

import ontologia.entidades.Emergencia;
import environment.Cidade;
import agents.CentralEmergencia;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class SimularEmergencias extends TickerBehaviour{

	private CentralEmergencia central;
	private Cidade cidade;

	public SimularEmergencias(CentralEmergencia central, Cidade cidade) {
		super(central, 5000);
		this.central=central;
		this.cidade = cidade;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5849101535775120863L;
	private static int id=1;

	@Override
	protected void onTick() {
		new Emergencia(cidade, "Random"+(id++), cidade.tamanhoLat/2, cidade.tamanhoLong/2);
	}
}
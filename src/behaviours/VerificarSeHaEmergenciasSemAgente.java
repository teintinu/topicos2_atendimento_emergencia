package behaviours;

import ontologia.Emergencia;
import ontologia.EmergenciaMedica;
import environment.Cidade;
import agents.CentralEmergencia;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class VerificarSeHaEmergenciasSemAgente extends TickerBehaviour{

	private CentralEmergencia central;
	private Cidade cidade;

	public VerificarSeHaEmergenciasSemAgente(CentralEmergencia central, Cidade cidade) {
		super(central, 2000);
		this.central=central;
		this.cidade = cidade;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5849101535775120863L;

	@Override
	protected void onTick() {
		Emergencia emergencia = cidade.pegarEmergenciaParaAtender(EmergenciaMedica.class);
		if (emergencia!=null)
			new ProcurarAmbulanciaMaisProxima(emergencia);
	}

}

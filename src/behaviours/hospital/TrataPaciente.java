package behaviours.hospital;

import environment.Cidade;
import agents.Hospital;
import ontologia.entidades.Emergencia;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;

public class TrataPaciente extends WakerBehaviour {

	private Emergencia emergencia;

	public TrataPaciente(Agent a, Emergencia emergencia) {
		super(a, (int) (Math.random() * 10000)+5000);
		this.emergencia = emergencia;
	}

	@Override
	protected void onWake() {
		Hospital hosp = (Hospital) myAgent;
		hosp.desocuparLeito();
		Cidade.singleton.map_remove(emergencia.endereco);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5290169586872498987L;

}

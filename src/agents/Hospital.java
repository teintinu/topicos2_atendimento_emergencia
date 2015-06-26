package agents;

import behaviours.ambulancia.ComunicacaoAmbulanciaCentral;
import behaviours.hospital.ComunicacaoHospital;
import environment.Cidade;
import environment.Objeto;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class Hospital extends Agent {

	public Integer endereco;

	@Override
	protected void setup() {
		Object[] args = getArguments();
		if (args != null && args.length == 3) {
			int lat = Integer.parseInt((String) args[0]);
			int lng = Integer.parseInt((String) args[1]);
			int qtde_leitos = Integer.parseInt((String) args[2]);
			endereco = Cidade.singleton.map_create(getAID().getLocalName(),
					"hospital", lat, lng, 0, qtde_leitos);
			System.out.println("Abrindo hospital: " + getAID().getLocalName());
		} else {
			System.out
					.println("Hospital precisa latitude, longitude e qtde_leitos. "
							+ args.length);
			doDelete();
			return;
		}

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(ontologia.Servicos.TratarPacientes);
		sd.setName("hospital: " + getAID().getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
			addBehaviour(new ComunicacaoHospital());
		} catch (FIPAException fe) {
			fe.printStackTrace();
			doDelete();
		}
	}

	@Override
	protected void takeDown() {
		System.out.println("Fechando hospital: " + getAID().getLocalName());
		if (endereco != null)
			Cidade.singleton.map_remove(endereco);
	}

	public boolean ocuparLeito() {
		Objeto o = Cidade.singleton.map_get(endereco);
		synchronized (o) {
			if (o.pos < o.max) {
				o.pos++;
				return true;
			}
		}
		return false;
	}

	public void desocuparLeito() {
		Objeto o = Cidade.singleton.map_get(endereco);
		synchronized (o) {
			o.pos--;
		}
	}

	public int leitos_disponiveis() {
		Objeto o = Cidade.singleton.map_get(endereco);
		synchronized (o) {
			return o.max-o.pos;
		}
	}
}

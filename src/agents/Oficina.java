package agents;

import behaviours.hospital.ComunicacaoHospital;
import behaviours.oficina.ComunicacaoOficina;
import environment.Cidade;
import environment.Objeto;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class Oficina extends Agent {

	public Integer endereco;

	@Override
	protected void setup() {
		Object[] args = getArguments();
		if (args != null && args.length == 2) {
			int lat = Integer.parseInt((String) args[0]);
			int lng = Integer.parseInt((String) args[1]);
			endereco = Cidade.singleton.map_create(getAID().getLocalName(),
					"oficina", lat, lng, new int[]{});
			System.out.println("Abrindo oficina: " + getAID().getLocalName());
		} else {
			System.out
					.println("Oficina precisa latitude, longitude. "
							+ args.length);
			doDelete();
			return;
		}

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(ontologia.Servicos.ManutencaoVeiculo);
		sd.setName("oficina: " + getAID().getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
			addBehaviour(new ComunicacaoOficina());
		} catch (FIPAException fe) {
			fe.printStackTrace();
			doDelete();
		}
	}

	@Override
	protected void takeDown() {
		System.out.println("Fechando oficina: " + getAID().getLocalName());
		if (endereco != null)
			Cidade.singleton.map_remove(endereco);
	}
}

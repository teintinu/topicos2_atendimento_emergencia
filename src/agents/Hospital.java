package agents;

import behaviours.ambulancia.ComunicacaoAmbulanciaCentral;
import behaviours.hospital.ComunicacaoHospital;
import environment.Cidade;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class Hospital extends Agent {

	public Integer endereco;
	private String nome;
	public int leitos_em_uso=0, qtde_leitos;

	@Override
	protected void setup() {
		Object[] args = getArguments();
		if (args != null && args.length == 4) {
			this.nome = (String) args[0];
			int lat = Integer.parseInt((String) args[1]);
			int lng = Integer.parseInt((String) args[2]);
			this.qtde_leitos = Integer.parseInt((String) args[3]);
			endereco = Cidade.singleton.map_create(nome, "hospital", lat, lng);
			System.out.println("Abrindo hospital: " + nome);
		} else {
			System.out
					.println("Hospital precisa nome, latitude, longitude e qtde_leitos");
			doDelete();
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
		if (nome != null)
			System.out.println("Fechando hospital: " + nome);
		if (endereco != null)
			Cidade.singleton.map_remove(endereco);
	}
}

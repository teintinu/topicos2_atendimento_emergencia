package agents;

import jade.core.Agent;

public class Hospital extends Agent {

		private Integer endereco;

		@Override
		protected void setup() {
			Object[] args = getArguments();
			if (args != null && args.length ==4 ) {
				nome= (String) args[0];
			}

			System.out.println("Registrando central de emergencia para cidade "
					+ nomeCidade);
			Cidade.singleton= new Cidade(nomeCidade, 600, 600);
			cidade = Cidade.singleton;

			endereco=cidade.map_create("Central", "central", cidade.tamanhoLat / 2,cidade.tamanhoLong / 2);	
			
			addBehaviour(		new SimularEmergencias(this, cidade));
			this.cidade = cidade;
			if (cidade == null)
				doDelete();
			else 
				addBehaviour(new GerenciamentoDeTransporteDePacientes(cidade));			
		}

		@Override
		protected void takeDown() {
			System.out
					.println("Central de emergencia encerrou as atividades na cidade: "
							+ getAID().getLocalName());
			if (cidade != null)
			{
				if (endereco!=null)
					cidade.map_remove(endereco);
				Cidade.singleton=null;
			}
			cidade = null;
		}
	}

}

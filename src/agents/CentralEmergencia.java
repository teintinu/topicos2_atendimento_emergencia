package agents;


import jade.core.Agent;
import behaviours.SimularEmergencias;
import behaviours.central.GerenciamentoDeTransporteDePacientes;
import environment.Cidade;

public class CentralEmergencia extends Agent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6158354184040721305L;
	private Cidade cidade;
	private Integer endereco;

	@Override
	protected void setup() {
		if (Cidade.singleton!=null){
			System.out.println("Só é suportado uma Central de emergencia por cidade");
			doDelete();
			return;
		}
		
		String nomeCidade="Bitlandia";
		Cidade cidade = null;
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			nomeCidade = (String) args[0];
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

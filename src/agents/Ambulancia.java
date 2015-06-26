package agents;

import java.util.Date;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import ontologia.entidades.Emergencia;
import behaviours.ambulancia.ComunicacaoAmbulanciaCentral;
import behaviours.ambulancia.RegistraTempoLivre;
import behaviours.ambulancia.TransportarPaciente;
import behaviours.ambulancia.VerificaSePrecisaManutencaoAmbulancia;
import environment.Cidade;
import environment.Objeto;

public class Ambulancia extends Agent {
	public int velocidade = 70;

	public Integer endereco;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5167273497591319321L;
	private static final int PROP_STATUS = 0;
	private static final int PROP_EMERGENCIA = 1;
	private static final int PROP_KM_RODADOS = 2;
	private static final int PROP_KM_MANUT = 3;
	private static final int PROP_IDLE = 4;
	private static final int PROP_LIVE = 5;
	private static final int PROP_MANUT_STATUS = 6;

	@Override
	protected void setup() {
		System.out.println("Motorista de ambulância contratado: "
				+ getAID().getLocalName());

		endereco = Cidade.singleton.map_create(getAID().getLocalName(),
				"ambulancia", Cidade.singleton.tamanhoLat / 2,
				Cidade.singleton.tamanhoLong / 2, new int[] { 0, 0, 0, 0, 0, 0,
						0 });

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(ontologia.Servicos.TransportarPacientes);
		sd.setName("ambulancia: " + getAID().getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
			addBehaviour(new ComunicacaoAmbulanciaCentral());
			addBehaviour(new RegistraTempoLivre(this));
			addBehaviour(new VerificaSePrecisaManutencaoAmbulancia());
			setStatusLivre();
		} catch (FIPAException fe) {
			fe.printStackTrace();
			doDelete();
		}
	}

	@Override
	protected void takeDown() {
		if (Cidade.singleton != null) {
			if (endereco != null) {
				Objeto o = Cidade.singleton.map_get(endereco);
				if (o.props[PROP_STATUS] == 2)
					TransportarPaciente.emergenciaSemAtendimento=o.props[PROP_EMERGENCIA];
				Cidade.singleton.map_remove(endereco);
			}
		}
		System.out.println("Motorista de ambulância demitido: "
				+ getAID().getLocalName());
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	public void setStatusLivre() {
		Objeto o = Cidade.singleton.map_get(endereco);
		stampTempoLivre = new Date().getTime();
		synchronized (o) {
			o.props[PROP_STATUS] = 0;
			o.props[PROP_EMERGENCIA] = 0;
		}
	}

	public void setStatusBuscarPaciente(Emergencia e) {
		Objeto o = Cidade.singleton.map_get(endereco);
		registraTempo();
		synchronized (o) {
			o.props[PROP_STATUS] = 1;
			o.props[PROP_EMERGENCIA] = e.endereco;
		}
	}

	public void setStatusTransportarPacienteParaHospital(Emergencia e) {
		Objeto o = Cidade.singleton.map_get(endereco);
		synchronized (o) {
			o.props[PROP_STATUS] = 2;
			o.props[PROP_EMERGENCIA] = e.endereco;
		}
	}

	public boolean livre() {
		Objeto o = Cidade.singleton.map_get(endereco);
		synchronized (o) {
			return o.props[PROP_STATUS] == 0;
		}
	}

	public void km_rodado(int rodado) {
		Objeto o = Cidade.singleton.map_get(endereco);
		synchronized (o) {
			o.props[PROP_KM_RODADOS] += rodado;
			o.props[PROP_KM_MANUT] += rodado;
		}
	}

	private long stampTempoLivre = 0;
	private long stampTempoVida = new Date().getTime();

	public void registraTempo() {
		long stamp = new Date().getTime();
		Objeto o = Cidade.singleton.map_get(endereco);
		if (stampTempoLivre > 0) {
			synchronized (o) {
				o.props[PROP_IDLE] += (stamp - stampTempoLivre);
				o.props[PROP_LIVE] = (int) (stamp - stampTempoVida);
				if (o.props[PROP_STATUS] == 0)
					stampTempoLivre = stamp;
				else
					stampTempoLivre = 0;
			}
		} else
			synchronized (o) {
				o.props[PROP_LIVE] = (int) (stamp - stampTempoVida);
			}
	}

	public boolean precisaManutencao() {
		Objeto o = Cidade.singleton.map_get(endereco);
		synchronized (o) {
			if (o.props[PROP_KM_MANUT] < 2000)
				velocidade = 90;
			else if (o.props[PROP_KM_MANUT] < 6000)
				velocidade = 70;
			else if (o.props[PROP_KM_MANUT] < 10000)
				velocidade = 60;
			else if (o.props[PROP_KM_MANUT] < 15000)
				velocidade = 50;
			else
				velocidade = 40;
			return o.props[PROP_KM_MANUT] > 10000
					&& o.props[PROP_MANUT_STATUS] == 0;
		}
	}

	public int getManutStatus() {
		Objeto o = Cidade.singleton.map_get(endereco);
		synchronized (o) {
			return o.props[PROP_MANUT_STATUS];
		}
	}

	public void setManutStatus(int value) {
		Objeto o = Cidade.singleton.map_get(endereco);
		synchronized (o) {
			o.props[PROP_MANUT_STATUS] = value;
		}
	}

	public boolean statusEmManutencao() {
		Objeto o = Cidade.singleton.map_get(endereco);
		boolean r;
		synchronized (o) {
			r = o.props[PROP_STATUS] == 0;
			if (r) {
				o.props[PROP_STATUS] = 3;
				o.props[PROP_MANUT_STATUS] = 2;
			}
		}
		return r;
	}

	public void setComecouManutencao() {
		Objeto o = Cidade.singleton.map_get(endereco);
		synchronized (o) {
			if (o.props[PROP_STATUS] == 3) {
				o.props[PROP_KM_MANUT] = 0;
			}
		}
	}

	public void setTerminouManutencao() {
		Objeto o = Cidade.singleton.map_get(endereco);

		synchronized (o) {
			if (o.props[PROP_STATUS] == 3) {
				o.props[PROP_STATUS] = 0;
				o.props[PROP_MANUT_STATUS] = 0;
			}
		}

	}
}

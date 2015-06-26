package environment;

import java.util.Date;

public class Walk {

	private Objeto source;
	private Objeto target;
	private Objeto transporting;
	private long stamp;
	private int timeout;

	public Walk(Objeto source, Objeto target, Objeto transporting, int speed) {
		this.source = source;
		this.target = target;
		this.transporting = transporting;
		this.stamp = new Date().getTime();
		this.timeout = 1000 / speed;
	}

	public int acum_rodado = 0;
	public boolean chegou = false;

	public void walk() {
		if (!chegou) {

			long st = new Date().getTime();
			if (st - stamp < timeout)
				return;

			int lat_delta = 0, long_delta = 0;
			synchronized (this) {
				synchronized (target) {
					if (source.latitude > target.latitude)
						lat_delta = -1;
					else if (source.latitude < target.latitude)
						lat_delta = 1;
					if (source.longitude > target.longitude)
						long_delta = -1;
					else if (source.longitude < target.longitude)
						long_delta = 1;
				}
				source.latitude += lat_delta;
				source.longitude += long_delta;
				if (transporting != null)
					synchronized (transporting) {
						transporting.latitude = source.latitude;
						transporting.longitude = source.longitude;
					}
			}
			int rodado = Math.abs(lat_delta) + Math.abs(long_delta);
			acum_rodado += rodado;
			chegou = rodado == 0;
			this.stamp = new Date().getTime();
		}

	}

	public int km_rodado(){
		int r=acum_rodado;
		acum_rodado=0;
		return r;
	}
}

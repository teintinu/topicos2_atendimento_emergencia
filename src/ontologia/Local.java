package ontologia;

public class Local {

	public int latitude;
	public int longitude;

	public Local(int latitude, int longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public int distancia(Local outro) {
		int a=Math.abs(this.latitude-outro.latitude);
		int b=Math.abs(this.longitude-outro.longitude);
		int c=(int) Math.sqrt(a*a+b*b);
		return c;
	}

}
 
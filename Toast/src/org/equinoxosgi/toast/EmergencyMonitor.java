package org.equinoxosgi.toast;

public class EmergencyMonitor implements IAirbagListener {
	
	private Airbag airbag;
	private Gps gps;
	
	public void deployed() {
		System.out.println("Emergency occurred at lat="+gps.getLatitude()
				+ " lon=" + gps.getLongitude()
				+ " heading=" + gps.getHeading()
				+ " speed=" + gps.getSpeed());
	}
	
	public void setAirbag(Airbag value) {
		airbag = value;
	}
	public void setGps(Gps value) {
		gps = value;
	}
	
	public void shutdown() {
		airbag.removeListener(this);
	}
	
	public void startup() {
		airbag.addListener(this);
	}
}

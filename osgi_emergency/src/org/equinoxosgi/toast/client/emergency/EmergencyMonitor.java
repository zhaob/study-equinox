package org.equinoxosgi.toast.client.emergency;

import org.equinoxosgi.toast.dev.airbag.Airbag;
import org.equinoxosgi.toast.dev.airbag.IAirbagListener;
import org.equinoxosgi.toast.dev.gps.Gps;

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

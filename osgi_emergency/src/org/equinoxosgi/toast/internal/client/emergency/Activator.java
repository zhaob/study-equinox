package org.equinoxosgi.toast.internal.client.emergency;

import org.equinoxosgi.toast.dev.airbag.IAirbag;
import org.equinoxosgi.toast.dev.gps.IGps;
import org.equinoxosgi.toast.internal.client.emergency.bundle.EmergencyMonitor;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class Activator implements BundleActivator {
	private IAirbag airbag;
	private ServiceReference<IAirbag> airbagRef;
	private IGps gps;
	private ServiceReference<IGps> gpsRef;
	private EmergencyMonitor monitor;
	
	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println("Launching");
		monitor = new EmergencyMonitor();
		gpsRef = (ServiceReference<IGps>) context.getServiceReference(IGps.class.getName());
		airbagRef = (ServiceReference<IAirbag>) context.getServiceReference(IAirbag.class.getName());
		if (gpsRef == null || airbagRef == null) {
			System.err.println("Unable to acquire GPS or airbag!");
			return;
		}
		gps = context.getService(gpsRef);
		airbag = context.getService(airbagRef);
		if (gps == null || airbag == null) {
			System.err.println("Unable to acquire GPS or airbag!");
			return;
		}
		monitor.setGps(gps);
		monitor.setAirbag(airbag);
		monitor.startup();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		monitor.shutdown();
		if (gpsRef != null)
			context.ungetService(gpsRef);
		if (airbagRef != null)
			context.ungetService(airbagRef);
		System.out.println("Terminating");
	}

}

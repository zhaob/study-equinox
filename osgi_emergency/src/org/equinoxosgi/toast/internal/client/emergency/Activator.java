package org.equinoxosgi.toast.internal.client.emergency;

import org.equinoxosgi.toast.dev.airbag.IAirbag;
import org.equinoxosgi.toast.dev.gps.IGps;
import org.equinoxosgi.toast.internal.client.emergency.bundle.EmergencyMonitor;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class Activator implements BundleActivator {
	private IAirbag airbag;
//	private ServiceReference<IAirbag> airbagRef;
	private ServiceTracker airbagTracker;
	private BundleContext context;
	private IGps gps;
//	private ServiceReference<IGps> gpsRef;
	private ServiceTracker gpsTracker;
	private EmergencyMonitor monitor;
	
	private void bind() {
		if (gps == null) {
			gps = (IGps) gpsTracker.getService();
			if (gps == null)
				return;
		}
		if (airbag == null) {
			airbag = (IAirbag) airbagTracker.getService();
			if (airbag == null)
				return;
		}
		monitor.setGps(gps);
		monitor.setAirbag(airbag);
		monitor.startup();
	}
	
	private ServiceTrackerCustomizer<IAirbag, IAirbag> createAirbagCustomizer() {
		return new ServiceTrackerCustomizer<IAirbag,IAirbag>() {
			public IAirbag addingService(ServiceReference<IAirbag> reference) {
				IAirbag service = context.getService(reference);
				synchronized (Activator.this) {
					if (Activator.this.airbag == null) {
						Activator.this.airbag =  service;
						Activator.this.bind();
					}
				}
				return service;
			}

			@Override
			public void modifiedService(ServiceReference<IAirbag> reference,
					IAirbag service) {
			}

			@Override
			public void removedService(ServiceReference<IAirbag> reference,
					IAirbag service) {
				synchronized (Activator.this) {
					if (service != Activator.this.airbag)
						return;
					Activator.this.unbind();
					Activator.this.bind();
				}
			}
		};
	}
	
	private ServiceTrackerCustomizer<IGps, IGps> createGpsCustomizer() {
		return new ServiceTrackerCustomizer<IGps, IGps>() {

			@Override
			public IGps addingService(ServiceReference<IGps> reference) {
				IGps service = context.getService(reference);
				synchronized (Activator.this) {
					if (Activator.this.gps == null) {
						Activator.this.gps = service;
						Activator.this.bind();
					}
				}
				return service;
			}

			@Override
			public void modifiedService(ServiceReference<IGps> reference,
					IGps service) {
			}

			@Override
			public void removedService(ServiceReference<IGps> reference,
					IGps service) {
				synchronized (Activator.this) {
					if (service != Activator.this.gps)
						return;
					Activator.this.unbind();
					Activator.this.bind();
				}
			}
			
		};
	}
	@Override
	public void start(BundleContext context) throws Exception {
		this.context = context;
		monitor = new EmergencyMonitor();
		ServiceTrackerCustomizer<IGps, IGps> gpsCustomizer = createGpsCustomizer();
		gpsTracker = new ServiceTracker<IGps, IGps>(context, IGps.class.getName(), gpsCustomizer);
		ServiceTrackerCustomizer<IAirbag, IAirbag> airbagCustomizer = createAirbagCustomizer();
		airbagTracker = new ServiceTracker<IAirbag, IAirbag>(context, IAirbag.class.getName(), airbagCustomizer);
		gpsTracker.open();
		airbagTracker.open();
//		System.out.println("Launching");
//		monitor = new EmergencyMonitor();
//		gpsRef = (ServiceReference<IGps>) context.getServiceReference(IGps.class.getName());
//		airbagRef = (ServiceReference<IAirbag>) context.getServiceReference(IAirbag.class.getName());
//		if (gpsRef == null || airbagRef == null) {
//			System.err.println("Unable to acquire GPS or airbag!");
//			return;
//		}
//		gps = context.getService(gpsRef);
//		airbag = context.getService(airbagRef);
//		if (gps == null || airbag == null) {
//			System.err.println("Unable to acquire GPS or airbag!");
//			return;
//		}
//		monitor.setGps(gps);
//		monitor.setAirbag(airbag);
//		monitor.startup();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		airbagTracker.close();
		gpsTracker.close();
//		monitor.shutdown();
//		if (gpsRef != null)
//			context.ungetService(gpsRef);
//		if (airbagRef != null)
//			context.ungetService(airbagRef);
//		System.out.println("Terminating");
	}
	
	private void unbind() {
		if (gps == null || airbag == null)
			return;
		monitor.shutdown();
		gps = null;
		airbag = null;
	}
}

import io.littlehorse.sdk.worker.LHTaskMethod;

public class Workers {

	@LHTaskMethod("assign-truck")
	public String assignTruck(String loadType) {
		String truckId = "mock-truck-001";
		System.out.printf("Assigned %s to load type %s.%n", truckId, loadType);
		return truckId;
	}

	@LHTaskMethod("load-truck")
	public void loadTruck(String truckId, String loadId) {
		System.out.printf("Loaded load %s onto truck %s.%n", loadId, truckId);
	}

	@LHTaskMethod("send-delay-email")
	public void sendDelayEmail(String customerEmail) {
		System.out.printf("Sent delay email to %s.%n", customerEmail);
	}

	@LHTaskMethod("assign-driver")
	public void assignDriver(String truckId) {
		System.out.printf("Assigned a mock driver to truck %s.%n", truckId);
	}

	@LHTaskMethod("out-for-delivery-notification")
	public void sendOutForDeliveryNotification(String customerEmail) {
		System.out.printf("Sent out-for-delivery notification to %s.%n", customerEmail);
	}
}

package foundation.notification;

public interface NotificationListener {

	class Notification {
		public String key;
		public Object object;
		public Object object1;
		public int arg;
		public int arg1;
		public int arg2;
	}

	boolean onNotification(Notification notification);
}

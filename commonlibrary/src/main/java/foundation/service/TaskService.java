package foundation.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import foundation.callback.ICallback;
import foundation.callback.ICallback1;


public class TaskService extends Service {

	private TaskBinder binder = new TaskBinder();

	public class TaskBinder extends Binder {
		
		public TaskService getService() {
			return TaskService.this;
		}
	}
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	private TaskQueue taskQueue = new TaskQueue();
	
	public void addTask(TaskRunnable runnable) {
		taskQueue.add(runnable);
	}

	public void cancel(ITask task, ICallback1<ITask> waitUntilFinishCallback) {
		taskQueue.cancel(task, waitUntilFinishCallback);
	}

	public void cancelAllTasks(ICallback waitUntilFinishCallback) {
		taskQueue.cancelAllTasks(waitUntilFinishCallback);
	}
}

package foundation.service;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import foundation.callback.ICallback;
import foundation.callback.ICallback1;


public class TaskQueue {

	private ExecutorService executorService = null;
	private ArrayList<TaskRunnable> runnables = new ArrayList<TaskRunnable>();

	private ExecutorService getExecutorService() {
		if (executorService == null) {
			executorService = Executors.newFixedThreadPool(1);
		}
		return executorService;
	}
	
	public void add(TaskRunnable runnable) {
		runnable.taskQueue = new WeakReference<TaskQueue>(this);
		getExecutorService().execute(runnable);
		runnables.add(runnable);
	}

	public void cancel(ITask task, ICallback1<ITask> waitUntilFinishCallback) {
		
		for (int i=0;i<runnables.size();i++) {
			TaskRunnable runnable = runnables.get(i);
			if (runnable.getTask().getId().compareTo(task.getId()) == 0) {
				runnable.cancel();
				
				if (waitUntilFinishCallback != null) {
					runnable.waitUntilFinished();
					waitUntilFinishCallback.callback(runnable.getTask());
				}
				
				break;
			}
		}
	}

	public void cancelAllTasks(ICallback waitUntilFinishCallback) {
		for (int i=0;i<runnables.size();i++) {
			TaskRunnable runnable = runnables.get(i);
			runnable.cancel();
		}
		
		if (waitUntilFinishCallback != null) {
			this.waitUntilAllTaskFinished();
		}
	}
	
	public void waitUntilAllTaskFinished() {

		boolean allFinished = false;
		
		while (!allFinished) {
			
			allFinished = true;
			
			for (int i=0;i<runnables.size();i++) {
				TaskRunnable runnable = runnables.get(i);
				if (!runnable.finished()) {
					allFinished = false;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
					break;
				}
			}
		}
	}

	public void close() {
		if (executorService != null) {
			executorService.shutdown();
		}
	}
	
	public void remove(TaskRunnable runnable) {
		runnables.remove(runnable);
	}
}

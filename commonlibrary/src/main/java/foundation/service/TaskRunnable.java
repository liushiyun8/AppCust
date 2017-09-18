package foundation.service;

import java.lang.ref.WeakReference;

public abstract class TaskRunnable implements Runnable {

	protected boolean stop = false;
	private boolean _running = false;
	
	public WeakReference<TaskQueue> taskQueue;
	
	@Override
	public void run() {
		
		_running = true;

		if (!stop) {
			main();
		}
		
		_running = false;
		
		// TaskRunnable完成
		if (taskQueue.get() != null) {
			taskQueue.get().remove(this);
		}
	}
	
	public void cancel() {
		stop = true;
	}
	
	public void waitUntilFinished() {
		
		while(_running) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
		}
	}
	
	public boolean finished() {
		return _running;
	}

	protected abstract void main();
	
	protected abstract ITask getTask();
}

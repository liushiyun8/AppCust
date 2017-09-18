package foundation.task;

import android.os.AsyncTask;

class TaskResult<Result> {
	public Exception exception;
	public Result result;
}

public abstract class MyAsyncTask<Result> extends
		AsyncTask<Void, Void, TaskResult<Result>> {

	abstract protected Result onExecute() throws Exception;

	abstract protected void onPostExecute(Result result, Exception exception);

	@Override
	protected TaskResult<Result> doInBackground(Void... params) {

		TaskResult<Result> result = new TaskResult<Result>();
		try {
			result.result = this.onExecute();
		} catch (Exception e) {
			result.exception = e;
		}

		return result;
	}

	protected void onPostExecute(TaskResult<Result> result) {
		this.onPostExecute(result.result, result.exception);
	}
}

package mapreduce.worker;

import mapreduce.Context;
import mapreduce.Job;

import org.apache.log4j.Logger;

import threadpool.Queue;

public class GenericReducer extends Thread {
	private Logger logger = Logger.getLogger(getClass());
	private Queue<ReduceInput> jobQueue;
	private Job job;
	private Context context;
	private boolean shouldRun = true;

	public boolean isShouldRun() {
		return shouldRun;
	}

	public void setShouldRun(boolean shouldRun) {
		this.shouldRun = shouldRun;
	}

	public Queue<ReduceInput> getJobQueue() {
		return jobQueue;
	}

	public void setJobQueue(Queue<ReduceInput> jobQueue) {
		this.jobQueue = jobQueue;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public void run() {
		logger.info("GenericReducer thread " + getName() + " started");
		while (shouldRun) {
			ReduceInput input = null;
			synchronized (jobQueue) {
				if (jobQueue.getSize() <= 0) {
					try {
						jobQueue.wait();
					} catch (InterruptedException e) {
						logger.error("GenericReducer " + getName()
								+ " interrupted");
						// e.printStackTrace();
					}
				}
				input = jobQueue.dequeue();
				logger.info("Thread" + getName() + " got input " + input);
				String key = input.getKey();
				String[] values = input.getValues();
				job.reduce(key, values, context);
			}
		}
	}

}

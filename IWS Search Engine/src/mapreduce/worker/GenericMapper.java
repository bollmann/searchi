package mapreduce.worker;

import mapreduce.Context;
import mapreduce.Job;

import org.apache.log4j.Logger;

import threadpool.Queue;

public class GenericMapper extends Thread {
	private Logger logger = Logger.getLogger(getClass());
	private Queue<String> jobQueue;
	private Job job;
	private Context context;
	private boolean shouldRun = true;

	public boolean isShouldRun() {
		return shouldRun;
	}

	public void setShouldRun(boolean shouldRun) {
		this.shouldRun = shouldRun;
	}

	public Queue<String> getJobQueue() {
		return jobQueue;
	}

	public void setJobQueue(Queue<String> jobQueue) {
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
		logger.info("Starting mapper");
		while (shouldRun) {
			String line = null;
			synchronized (jobQueue) {
				logger.info("Queue now " + jobQueue);
				if (jobQueue.getSize() <= 0) {
					try {
						jobQueue.wait();
					} catch (InterruptedException e) {
						logger.error("GenericMapper " + getName()
								+ " interrupted");
						// e.printStackTrace();
					}
				}
				line = jobQueue.dequeue();
				logger.info("Mapper " + getName() + " got line:" + line);
			}
			
			String key = line.split("\t")[0];
			String value = line.split("\t")[1];
			synchronized (context) {
				job.map(key, value, context);
			}
		}
		logger.info("Thread exiting as should run set to false");
	}

}

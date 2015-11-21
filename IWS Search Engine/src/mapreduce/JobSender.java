package mapreduce;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

import org.apache.log4j.Logger;

import parsers.Parser;
import requests.Http10Request;
import threadpool.Queue;
import clients.HttpClient;

public class JobSender extends Thread {

	private Logger logger = Logger.getLogger(getClass());
	private Queue<JobStatus> jobQueue;
	private Map<String, WorkerStatus> workerStatusMap;

	private boolean shouldRun = true;

	public boolean isShouldRun() {
		return shouldRun;
	}

	public void setShouldRun(boolean shouldRun) {
		this.shouldRun = shouldRun;
	}

	public JobSender(Queue<JobStatus> jobQueue,
			Map<String, WorkerStatus> workerStatusMap) {
		this.jobQueue = jobQueue;
		this.workerStatusMap = workerStatusMap;
	}

	@Override
	public void run() {
		while (shouldRun) {
			JobStatus job = null;
			synchronized (jobQueue) {
				if (jobQueue.getSize() <= 0) {
					try {
						jobQueue.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				job = jobQueue.dequeue();
			}

			processJob(job);

		}
	}

	public void processJob(JobStatus job) {
		logger.info(job);
		Http10Request request = convertJobToRequest(job);
		request.setHeader("Content-Type", Parser.formEncoding);
		for (WorkerStatus status : job.getWorkers()) {

			String ipPort = status.getIpAddress() + ":" + status.getPort();
			String jobUrl = getJobUrl(job.getJobType(), ipPort);
			logger.info("Processing job " + job.getClass()
					+ ". Sending to worker: " + ipPort + " job url:" + jobUrl);

			try {
				HttpClient.post(jobUrl, request);
			} catch (IOException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Http10Request convertJobToRequest(JobStatus job) {
		Http10Request request = new Http10Request();

		if (job.getJobType().equals("map")) {

			String encodedWorkers = getEncodedWorkers(job);
			request.setBody(job.toMapParameters(encodedWorkers, job
					.getWorkers().size()));

		} else if (job.getJobType().equals("reduce")) {
			request.setBody(job.toReduceParameters());
		}

		return request;
	}

	public String getJobUrl(String jobType, String ipPort) {
		String jobUrl = null;
		if (jobType.equals("map")) {
			jobUrl = "http://" + ipPort + "/worker/runmap";
		} else if (jobType.equals("reduce")) {
			jobUrl = "http://" + ipPort + "/worker/runreduce";
		}
		return jobUrl;
	}

	public String getEncodedWorkers(JobStatus job) {
		logger.info("JobSender encoding workers of job:" + job.getJob()
				+ " type " + job.getJobType() + " with "
				+ job.getWorkers().size() + " workers");
		StringBuilder workerEncoding = new StringBuilder();
		// synchronized (workerStatusMap) {
		for (WorkerStatus status : job.getWorkers()) {
			logger.info("WorkerStatus " + status.getStatus() + " "
					+ status.isActive());
			workerEncoding.append(status.encodeForMap() + ",");

		}
		// }
		logger.info("Encoding now " + workerEncoding.toString());
		return workerEncoding.substring(0, workerEncoding.length() - 1);
	}
}

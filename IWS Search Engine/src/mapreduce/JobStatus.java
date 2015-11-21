package mapreduce;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class JobStatus {
	private final Logger logger = Logger.getLogger(getClass());

	private String jobType;
	private String status;
	private String inputDirectory;
	private String outputDirectory;
	private int mapThreads;
	private int reduceThreads;
	private String job;
	private List<WorkerStatus> workers;
	private boolean onePass = false;

	public boolean isOnePass() {
		return onePass;
	}

	public void setOnePass(boolean onePass) {
		this.onePass = onePass;
	}

	public JobStatus() {
		workers = new ArrayList<WorkerStatus>();
	}

	public List<WorkerStatus> getWorkers() {
		return workers;
	}

	public void addWorker(WorkerStatus worker) {
		this.workers.add(worker);
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String state) {
		this.status = state;
	}

	public String getInputDirectory() {
		return inputDirectory;
	}

	public void setInputDirectory(String inputDirectory) {
		this.inputDirectory = inputDirectory;
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public int getMapThreads() {
		return mapThreads;
	}

	public void setMapThreads(int mapThreads) {
		this.mapThreads = mapThreads;
	}

	public int getReduceThreads() {
		return reduceThreads;
	}

	public void setReduceThreads(int reduceThreads) {
		this.reduceThreads = reduceThreads;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String toMapParameters(String encodedWorkers, int numWorkers) {
		StringBuilder sb = new StringBuilder();
		sb.append("job=" + getJob() + "&");
		sb.append("input=" + getInputDirectory() + "&");
		sb.append("numThreads=" + getMapThreads() + "&");
		sb.append("numWorkers=" + numWorkers + "&");
		sb.append(encodedWorkers);
		return sb.toString();
	}

	public String toReduceParameters() {
		StringBuilder sb = new StringBuilder();
		sb.append("job=" + getJob() + "&");
		sb.append("output=" + getOutputDirectory() + "&");
		sb.append("numThreads=" + getReduceThreads());
		return sb.toString();
	}

	public void updateWorkerStatus(WorkerStatus status) {
		String allWorkerStatus = null;
		int workersInStatus = 0;
		logger.debug("Looking atjob  workers:" + workers);
		for (WorkerStatus jobWorkerStatus : workers) {
			if (status.getIpAddress().equals(jobWorkerStatus.getIpAddress())
					&& status.getPort() == jobWorkerStatus.getPort()) {
				logger.info("Updating status for worker:"
						+ jobWorkerStatus.toString() + " to "
						+ status.toString());
				jobWorkerStatus.setJobClass(status.getJobClass());
				jobWorkerStatus.setKeysRead(status.getKeysRead());
				jobWorkerStatus.setKeysWritten(status.getKeysWritten());
				jobWorkerStatus.setStatus(status.getStatus());
				jobWorkerStatus.setLastStatusReceived(status
						.getLastStatusReceived());
			}
			if (workersInStatus == 0) {
				allWorkerStatus = jobWorkerStatus.getStatus();
				workersInStatus++;
				logger.debug("All worker status of " + jobWorkerStatus.getPort()
						+ "  initialized to " + allWorkerStatus);
			} else {
				if (jobWorkerStatus.getStatus().equals(allWorkerStatus)) {
					workersInStatus++;
					logger.debug("Found " + workersInStatus
							+ " in all worker status " + allWorkerStatus
							+ " of " + jobWorkerStatus.getPort());
				}
			}

		}
		if (workersInStatus == workers.size()) {
			logger.info("Updating worker status to " + allWorkerStatus + " as "
					+ workersInStatus + " = " + workers.size());
			this.status = allWorkerStatus;
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Worker => job:" + job + " type:" + jobType + " status:"
				+ status + " inputDir:" + inputDirectory + " outputDir:"
				+ outputDirectory + " numMapThreads:" + mapThreads
				+ " numReduceThreads:" + reduceThreads + " workers:" + workers);

		return sb.toString();
	}
}

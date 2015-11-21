package mapreduce.master;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mapreduce.JobSender;
import mapreduce.JobStatus;
import mapreduce.WorkerStatus;

import org.apache.log4j.Logger;

import policies.FilePolicy;
import threadpool.Queue;

public class MasterServlet extends HttpServlet {
	private final Logger logger = Logger.getLogger(getClass());
	public static final String WORKER_STATUSES_KEY = "workerStatuses";
	public static final String JOB_STATUS_KEY = "jobStatus";

	private Map<String, WorkerStatus> workerStatusMap;
	private Queue<JobStatus> jobQueue;
	private JobStatus currentJob = null;
	private JobSender jobSender;

	static final long serialVersionUID = 455555001;

	@Override
	public void init() {
		workerStatusMap = new HashMap<String, WorkerStatus>();
		jobQueue = new Queue<JobStatus>();
		jobSender = new JobSender(jobQueue, workerStatusMap);
		jobSender.start();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws java.io.IOException {
		if (request.getPathInfo().equals("/status")) {
			String content = null;
			try {
				content = FilePolicy
						.readFile("resources/master_status_page.html");
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			StringBuilder sb = new StringBuilder();
			synchronized (workerStatusMap) {
				for (Entry<String, WorkerStatus> entry : workerStatusMap
						.entrySet()) {
					WorkerStatus status = entry.getValue();
					if (status.isActive()) {
						sb.append("<tr>");
						sb.append("<td>" + status.getName() + "</td>" + "<td>"
								+ status.getIpAddress() + ":"
								+ status.getPort() + "</td>" + "<td>"
								+ status.getStatus() + "</td>" + "<td>"
								+ status.getJobClass() + "</td>" + "<td>"
								+ status.getKeysRead() + "</td>" + "<td>"
								+ status.getKeysRead() + "</td>");
						sb.append("</tr>");
					}
				}
			}

			content = content.replace("<$workers$>", sb.toString());
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println(content);
			return;
		} else if (request.getPathInfo().equals("/workerstatus")) {

			boolean created;
			String ipAddress = request.getServerName();

			int port = -1;
			if (request.getParameter("port") != null) {
				port = Integer.parseInt(request.getParameter("port"));
			} else {
				response.sendError(400, "No port given!");
				return;
			}
			String workerKey = ipAddress + ":" + port;
			WorkerStatus status = null;

			synchronized (workerStatusMap) {
				if (workerStatusMap.containsKey(workerKey)) {
					logger.info("Found existing entry for " + workerKey
							+ " in worker status map");
					status = workerStatusMap.get(workerKey);
					created = false;
				} else {
					logger.info("Creating new entry for " + workerKey
							+ " in worker status map");
					status = new WorkerStatus();
					status.setName("worker" + (workerStatusMap.size() + 1));
					status.setIpAddress(ipAddress);
					status.setPort(port);
					created = true;
				}
			}

			status.setIpAddress(ipAddress);

			if (request.getParameter("status") != null) {
				status.setStatus(request.getParameter("status"));
			} else {
				response.sendError(400, "No status given!");
				return;
			}
			if (request.getParameter("job") != null) {
				status.setJobClass(request.getParameter("job"));
			} else {
				response.sendError(400, "No job given!");
				return;
			}
			if (request.getParameter("keysRead") != null) {
				status.setKeysRead(Integer.parseInt(request
						.getParameter("keysRead")));
			} else {
				response.sendError(400, "No keysRead given!");
				return;
			}
			if (request.getParameter("keysWritten") != null) {
				status.setKeysWritten(Integer.parseInt(request
						.getParameter("keysWritten")));
			} else {
				response.sendError(400, "No keysWritten given!");
				return;
			}

			status.setLastStatusReceived(Calendar.getInstance().getTime());

			logger.info("Accessing current job " + currentJob);
			if (currentJob != null) {
				synchronized (currentJob) {
					currentJob.updateWorkerStatus(status);
				}
				logger.info("Updated job:" + currentJob + " to "
						+ currentJob.getStatus() + " with worker:" + status);
			}

			synchronized (workerStatusMap) {
				workerStatusMap.put(
						status.getIpAddress() + ":" + status.getPort(), status);
			}

			if (currentJob != null) {
				//
				if (currentJob.getStatus().equals("waiting")
						&& !currentJob.isOnePass()) {
					logger.info("Master starting reduce jobs on workers");
					JobSender sender = new JobSender(null, null);
					synchronized (currentJob) {
						currentJob.setJobType("reduce");
						currentJob.setOnePass(true);
					}
					sender.processJob(currentJob);

				} else if (currentJob.getStatus().equals("idle")
						&& currentJob.isOnePass()) {
					logger.info("Master forgetting job");
					currentJob = null;
				}

			}

			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println("<html><body>Successfully "
					+ (created ? "created" : "updated")
					+ " status for worker at:" + status.getIpAddress() + ":"
					+ status.getPort() + " </body></html>");
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		JobStatus job = new JobStatus();
		job.setJobType("map");
		job.setStatus("started");
		if (request.getParameter("job") != null) {
			job.setJob(request.getParameter("job"));
		} else {
			response.sendError(400, "No job given!");
			return;
		}
		if (request.getParameter("ip") != null) {
			job.setInputDirectory(request.getParameter("ip"));
		} else {
			response.sendError(400, "No input directory given!");
			return;
		}
		if (request.getParameter("op") != null) {
			job.setOutputDirectory(request.getParameter("op"));
		} else {
			response.sendError(400, "No output directory given!");
			return;
		}
		if (request.getParameter("numMapThreads") != null) {
			job.setMapThreads(Integer.parseInt(request
					.getParameter("numMapThreads")));
		} else {
			response.sendError(400, "No num map threads given!");
			return;
		}
		if (request.getParameter("numReduceThreads") != null) {
			job.setReduceThreads(Integer.parseInt(request
					.getParameter("numReduceThreads")));
		} else {
			response.sendError(400, "No num reduce threads given!");
			return;
		}

		for (Entry<String, WorkerStatus> workerStatus : workerStatusMap
				.entrySet()) {
			WorkerStatus worker = workerStatus.getValue();
			if (worker.isActive() && worker.getStatus().equals("idle")) {
				logger.info("Adding worker:" + worker.getName() + " for job:"
						+ job.getClass());
				job.addWorker(worker);
			}
		}

		currentJob = job;
		JobSender sender = new JobSender(null, null);
		logger.info("Master processing job request");
		sender.processJob(job);
		logger.info("Master processed job request");
		// synchronized (currentJob) {
		// if (currentJob == null) {
		// currentJob = job;
		// }
		// }
		//
		// try {
		// synchronized (jobQueue) {
		// jobQueue.enqueue(job);
		// jobQueue.notify();
		// }
		// } catch (QueueFullException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// response.sendError(500,
		// "Cannot accept more jobs. Queue size is full!");
		// return;
		// }

		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		out.println("<html><body>Job successfully posted!</body></html>");
	}

	public Map<String, WorkerStatus> getWorkerStatusMap() {
		return workerStatusMap;
	}

	public JobStatus getCurrentJob() {
		return this.currentJob;
	}

	public void setCurrentJob(JobStatus job) {
		this.currentJob = job;
	}
}

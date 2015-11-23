package mapreduce.worker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mapreduce.Context;
import mapreduce.Job;
import mapreduce.MapContext;
import mapreduce.ReduceContext;
import mapreduce.WorkerStatus;

import org.apache.log4j.Logger;

import policies.FilePolicy;
import requests.Http10Request;
import threadpool.Queue;
import clients.HttpClient;
import errors.QueueFullException;

public class WorkerServlet extends HttpServlet {
	private final Logger logger = Logger.getLogger(getClass());
	public static final String WORKER_STATUS_KEY = "workerStatus";

	private String storageDirectory;
	private String spoolInDirectory;
	private String spoolOutDirectory;
	private WorkerStatus workerStatus;
	private String masterIPPort;

	Map<String, WorkerStatus> workerStatuses;
	
	public void setWorkerStatus(WorkerStatus status) {
		this.workerStatus = status;
	}

	static final long serialVersionUID = 455555002;

	public void initializeWorkerStatus() {
		workerStatus = new WorkerStatus();
		workerStatus.setStatus("idle");
		String ip = (String) getServletContext().getInitParameter("worker-ip");
		int port = Integer.parseInt((String) getServletContext()
				.getInitParameter("worker-port"));
		workerStatus.setPort(port);
		workerStatus.setIpAddress(ip);
	}

	@Override
	public void init() {
		workerStatuses = new HashMap<String, WorkerStatus>();
		initializeWorkerStatus();
		masterIPPort = (String) getServletContext().getInitParameter("master");
		HeartBeat heartBeat = new HeartBeat("http://" + masterIPPort
				+ "/master/workerstatus");
//		heartBeat.setWorkerStatus(workerStatus);
		heartBeat.start();

		String storageDir = (String) getServletContext().getInitParameter(
				"storagedir");
		storageDirectory = storageDir;
		File storageDirFile = new File(storageDir);
		if (!storageDirFile.exists()) {
			storageDirFile.mkdirs();
		}
		spoolInDirectory = storageDir + "/spool-in";
		cleanAndRemakeDirectory(spoolInDirectory);
		spoolOutDirectory = storageDir + "/spool-out";
		cleanAndRemakeDirectory(spoolOutDirectory);
	}

	public void cleanAndRemakeDirectory(String dirPath) {
		File dirPathFile = new File(dirPath);
		if (dirPathFile.exists()) {
			logger.info("Worker got storage directory from config:"
					+ dirPathFile.getAbsolutePath());
			for (File c : dirPathFile.listFiles())
				c.delete();
			if (!dirPathFile.delete()) {
				System.out.println("Couldn't delete "
						+ dirPathFile.getAbsolutePath());
			}
		}
		if (dirPathFile.mkdirs()) {
		} else {
			System.out.println("Couldn't create "
					+ dirPathFile.getAbsolutePath());
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws java.io.IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<html><head><title>Worker</title></head>");
		out.println("<body>Hi, I am the worker!</body></html>");
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		if (request.getPathInfo().equals("/runmap")
				&& workerStatus.getStatus().equals("idle")) {
			synchronized (workerStatus) {
				workerStatus.setKeysWritten(0);
				workerStatus.setKeysRead(0);
				workerStatus.setStatus("mapping");
			}
			String job = null, input = null;
			int numThreads = 0, numWorkers = 0;
			if (request.getParameter("job") != null) {
				job = request.getParameter("job");
			} else {
				response.sendError(400, "No job class sent!");
				return;
			}
			if (request.getParameter("input") != null) {
				input = request.getParameter("input");
			} else {
				response.sendError(400, "No input directory sent!");
				return;
			}
			if (request.getParameter("numThreads") != null) {
				numThreads = Integer.parseInt(request
						.getParameter("numThreads"));
			} else {
				response.sendError(400, "No numThreads sent!");
				return;
			}
			if (request.getParameter("numWorkers") != null) {
				numWorkers = Integer.parseInt(request
						.getParameter("numWorkers"));
			} else {
				response.sendError(400, "No numWorkers class sent!");
				return;
			}
			BufferedReader br = request.getReader();
			String body = HttpClient.extractBody(Integer.MAX_VALUE, br);
			String[] workers = body.split("&")[4].split(",");
			logger.info("Worker got job:" + job + " input:" + input
					+ " numThreads:" + numThreads + " numWorkers:" + numWorkers
					+ " workers:" + Arrays.toString(workers));
			synchronized (workerStatus) {
				workerStatus.setJobClass(job);
			}

			workerStatuses = new HashMap<String, WorkerStatus>(numWorkers);
			for (String worker : workers) {
				String workerName = worker.split("=")[0];
				String address = worker.split("=")[1];
				String ip = address.split(":")[0];
				int port = Integer.parseInt(address.split(":")[1]);
				WorkerStatus workerStatus = new WorkerStatus();
				workerStatus.setName(workerName);
				workerStatus.setIpAddress(ip);
				workerStatus.setPort(port);
				workerStatus.setJobClass(job);
				workerStatuses.put(workerName, workerStatus);
			}
			Class jobClass = null;
			try {
				jobClass = Class.forName(job);
			} catch (ClassNotFoundException e) {
				response.sendError(400, "Job class not found.");
				return;
			}

			Job jobInstance;
			try {
				jobInstance = (Job) jobClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				response.sendError(400, "Couldn't instantaite job class.");
				return;
			}

			Context context = new MapContext(spoolOutDirectory, workerStatuses,
					workerStatus);
			Queue<String> jobQueue = new Queue<String>();
			List<GenericMapper> mapperThreads = new ArrayList<GenericMapper>();
			for (int i = 0; i < numThreads; i++) {
				GenericMapper mapper = new GenericMapper();
				mapper.setJob(jobInstance);
				mapper.setJobQueue(jobQueue);
				mapper.setContext(context);
				mapperThreads.add(mapper);
				mapper.start();
			}

			try {
				processMapFiles(storageDirectory + "/" + input, jobQueue);
			} catch (QueueFullException e) {
				response.sendError(500, "Couldn't handle data. Queue overflow!");
				return;
			}

			try {
				while (jobQueue.getSize() > 0) {
					Thread.sleep(100);
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			for (GenericMapper mapper : mapperThreads) {
				mapper.setShouldRun(false);
				mapper.interrupt();
			}
			// send pushdata
			try {
				pushData(spoolOutDirectory);
			} catch (ParseException e) {
				logger.error("Couldn't send pushdata to other workers!");
				e.printStackTrace();
				response.sendError(500,
						"Couldn't send pushdata to other workers!");
				return;
			}

			// set status to waiting should send heartbeat
			synchronized (workerStatus) {
				workerStatus.setStatus("waiting");

			}

			String heartBeatUrl = "http://" + masterIPPort
					+ "/master/workerstatus";
			HeartBeat heartBeat = new HeartBeat(heartBeatUrl);
//			heartBeat.setWorkerStatus(workerStatus);
			heartBeat.sendHeartBeat(heartBeatUrl);
			logger.info("Worker processed job request");
			return;
		} else if (request.getPathInfo().equals("/runreduce")
				&& workerStatus.getStatus().equals("waiting")) {
			synchronized (workerStatus) {
				workerStatus.setKeysWritten(0);
				workerStatus.setKeysRead(0);
				workerStatus.setStatus("reducing");
			}
			String job = null, output = null;
			int numThreads = 0;
			if (request.getParameter("job") != null) {
				job = request.getParameter("job");
			} else {
				response.sendError(400, "No job class sent!");
				return;
			}
			if (request.getParameter("output") != null) {
				output = request.getParameter("output");
			} else {
				response.sendError(400, "No input directory sent!");
				return;
			}
			if (request.getParameter("numThreads") != null) {
				numThreads = Integer.parseInt(request
						.getParameter("numThreads"));
			} else {
				response.sendError(400, "No numThreads sent!");
				return;
			}

			Class jobClass = null;
			try {
				jobClass = Class.forName(job);
			} catch (ClassNotFoundException e) {
				response.sendError(400, "Job class not found.");
				return;
			}

			File outputDir = new File(storageDirectory + "/" + output);
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}

			Job jobInstance;
			try {
				jobInstance = (Job) jobClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				response.sendError(400, "Couldn't instantaite job class.");
				return;
			}

			File outputFile = new File(storageDirectory + "/" + output
					+ "/output");
			outputFile.delete();
			Context context = new ReduceContext(
					storageDirectory + "/" + output, workerStatus);
			Queue<ReduceInput> jobQueue = new Queue<ReduceInput>();
			List<GenericReducer> reducerThreads = new ArrayList<GenericReducer>();
			for (int i = 0; i < numThreads; i++) {
				GenericReducer reducer = new GenericReducer();
				reducer.setJob(jobInstance);
				reducer.setJobQueue(jobQueue);
				reducer.setContext(context);
				reducerThreads.add(reducer);
				reducer.start();
			}

			try {
				processReduceFiles(spoolInDirectory, jobQueue);
			} catch (QueueFullException | InterruptedException e) {
				response.sendError(500, "Couldn't reduce files!");
				e.printStackTrace();
				return;
			}

			for (GenericReducer reducer : reducerThreads) {
				reducer.setShouldRun(false);
				reducer.interrupt();
			}
			// set status to waiting should send heartbeat
			synchronized (workerStatus) {
				workerStatus.setStatus("idle");
			}
			String heartBeatUrl = "http://" + masterIPPort
					+ "/master/workerstatus";
			HeartBeat heartBeat = new HeartBeat(heartBeatUrl);
//			heartBeat.setWorkerStatus(workerStatus);
			heartBeat.sendHeartBeat(heartBeatUrl);
			logger.info("Worker processed reduce request");
			return;
		} else if (request.getPathInfo().equals("/pushdata")) {

			String iPPort = request.getRemoteHost() + ":"
					+ request.getRemotePort();
			logger.info("Getting pushdata from " + request.getRemoteHost()
					+ ":" + request.getRemotePort());
			BufferedReader br = request.getReader();
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}

			File file = new File(spoolInDirectory + "/" + iPPort);
			logger.info("Saving pushdata to " + file.getAbsolutePath());
			FileWriter fos = new FileWriter(file.getAbsoluteFile());
			fos.write(sb.toString());
			fos.close();
			logger.info("Worker processed pushdata request");
			return;
		} else {
			response.sendError(404);
			return;
		}
	}

	public void processMapFiles(String inputDirectory, Queue<String> jobQueue)
			throws IOException, QueueFullException {
		File file = new File(inputDirectory);
		for (File ipFile : file.listFiles()) {
			logger.info("Reading " + ipFile.getAbsolutePath());
			BufferedReader br = new BufferedReader(new FileReader(ipFile));
			String line = null;
			synchronized (jobQueue) {
				while ((line = br.readLine()) != null) {
					logger.info("Sending line:" + line + " to jobQueue");
					jobQueue.enqueue(line);
					jobQueue.notify();
					synchronized (workerStatus) {
						workerStatus
								.setKeysRead(workerStatus.getKeysRead() + 1);
					}
				}
			}
			br.close();
		}
		synchronized (jobQueue) {
			logger.info("After processing, queue now" + jobQueue.toString());
		}
	}

	public void processReduceFiles(String inputDirectory,
			Queue<ReduceInput> jobQueue) throws IOException,
			QueueFullException, InterruptedException {
		String tempSortedFile = "sorted.file";
		StringBuilder sFiles = new StringBuilder();
		File inputDirFile = new File(inputDirectory);

		File sortedFile = new File(inputDirectory + "/" + tempSortedFile);
		if (sortedFile.exists()) {
			logger.info("Deleting " + sortedFile.getAbsolutePath());
			sortedFile.delete();
		}

		if (inputDirFile.listFiles().length > 0) {
			for (File tbsorted : inputDirFile.listFiles()) {
				sFiles.append(tbsorted.getAbsolutePath() + " ");
			}

			String command = "/usr/bin/sort -o " + sortedFile.getAbsolutePath()
					+ " " + sFiles.toString();
			logger.info("Reducer sorting with command:" + command);

			Process p = Runtime.getRuntime().exec(command);
			p.waitFor();

			logger.info("Finished running command");

			BufferedReader br = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			BufferedReader bre = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;

			sb = new StringBuilder();
			line = null;
			try {
				while ((line = bre.readLine()) != null) {
					sb.append(line);

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			bre.close();
			br.close();

			logger.error("error output:" + sb.toString());
			// File file = new File(inputDirectory);
			String currentKey = null;
			List<String> currentKeyValues = new ArrayList<String>();
			// for (File ipFile : file.listFiles()) {

			sortedFile = new File(inputDirectory + "/" + tempSortedFile);
			logger.info("Reading " + sortedFile.getAbsolutePath());
			BufferedReader sf = new BufferedReader(new FileReader(sortedFile));
			line = null;
			while ((line = sf.readLine()) != null) {
				String key = line.split("\t")[0];
				String value = line.split("\t")[1];
				synchronized (workerStatus) {
					workerStatus.setKeysRead(workerStatus.getKeysRead() + 1);
				}
				if (currentKey == null) {
					currentKey = key;
					currentKeyValues.add(value);
				} else if (!currentKey.equals(key)) {
					// write out with current accumulation
					String[] outputValues = currentKeyValues
							.toArray(new String[0]);
					ReduceInput ri = new ReduceInput(currentKey, outputValues);
					logger.info("Pushing " + ri + " to job queue");
					synchronized (jobQueue) {
						jobQueue.enqueue(ri);
						jobQueue.notify();
					}
					currentKeyValues.clear();
					currentKey = key;
					currentKeyValues.add(value);
				} else {
					currentKeyValues.add(value);
				}

			}
			sf.close();

			// write out last values
			String[] outputValues = currentKeyValues.toArray(new String[0]);
			ReduceInput ri = new ReduceInput(currentKey, outputValues);
			logger.info("Final pushing " + ri + " to job queue");
			synchronized (jobQueue) {
				jobQueue.enqueue(ri);
				jobQueue.notify();
			}
		}
	}

	public void pushData(String spoolOutDirectory) throws IOException,
			ParseException {
		File spoolOut = new File(spoolOutDirectory);
		for (File file : spoolOut.listFiles()) {
			String workerName = file.getName();
			WorkerStatus fileStatus = workerStatuses.get(workerName);
			String pushDataUrl = "http://" + fileStatus.getIpAddress() + ":"
					+ fileStatus.getPort() + "/worker/pushdata";
			String content = FilePolicy.readFile(file.getAbsolutePath());
			Http10Request request = new Http10Request();
			request.setBody(content);
			HttpClient.post(pushDataUrl, request);
		}
	}
}

package mapreduce;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

public class ReduceContext implements Context {
	private final Logger logger = Logger.getLogger(getClass());
	private String outputDirectory;
	private WorkerStatus workerStatus;
	
	public ReduceContext(String outputDirectory, WorkerStatus workerStatus) {
		this.outputDirectory = outputDirectory;
		this.workerStatus = workerStatus;
	}
	
	@Override
	public void write(String key, String value) {
		String outputFile = outputDirectory + "/output";
		FileWriter writer = null;
		try {
			writer = new FileWriter(outputFile, true);
			writer.append(key + "\t" + value + "\n");
			synchronized(workerStatus) {
				workerStatus.setKeysWritten(workerStatus.getKeysWritten() + 1);
			}
			logger.info("ReduceCOntext writing " + key + " " + value + " to " + outputFile);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

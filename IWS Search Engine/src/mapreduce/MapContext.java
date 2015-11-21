package mapreduce;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Map;

import org.apache.log4j.Logger;

public class MapContext implements Context {
	private Logger logger = Logger.getLogger(getClass());
	private String outputDirectory;
	private Map<String, WorkerStatus> workerStatuses;
	private int numWorkers;
	private BigInteger hashMax, bucketLimit;
	private WorkerStatus workerStatus;

	public MapContext(String outputDirectory,
			Map<String, WorkerStatus> workerStatuses, WorkerStatus workerStatus) {
		this.outputDirectory = outputDirectory;
		this.workerStatuses = workerStatuses;
		this.numWorkers = workerStatuses.size();
		hashMax = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16);
		bucketLimit = hashMax.divide(new BigInteger(String.valueOf(numWorkers)));
		this.workerStatus = workerStatus;
	}

	public String formatHexByteArray(byte[] byteArray) {
		Formatter formatter = new Formatter();
		for (byte b : byteArray) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString().toUpperCase();
		formatter.close();
		return result;
	}

	public int getBucketForKey(String key) throws UnsupportedEncodingException,
			NoSuchAlgorithmException {
		MessageDigest cript = MessageDigest.getInstance("SHA-1");
		cript.reset();
		cript.update(key.getBytes("utf8"));
		byte[] encoding = cript.digest();
		logger.info("Encoding:" + formatHexByteArray(encoding));
		BigInteger toBucket = new BigInteger(formatHexByteArray(encoding), 16);
		BigInteger bucket = toBucket.divide(bucketLimit).add(new BigInteger("1", 16));
		logger.info("Got toBucket:" + toBucket.toString() + " assigned to bucket:" + bucket.toString());
		return bucket.intValue();
	}

	@Override
	public void write(String key, String value) {
		// hash key
		// bucket it
		int worker = 1;
		try {
			worker = getBucketForKey(key);
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String workerName = "worker" + worker;
		// name the file to write
		String outputFile = outputDirectory + "/" + workerName;
		logger.info("Mapper writing " + key + "\t" + value + " to file:" + outputFile);
		File outputFileF = new File(outputDirectory);
		
		try {
			if(!outputFileF.exists()) {
				outputFileF.mkdirs();
			}
			outputFileF = new File(outputFile);
			if(!outputFileF.exists()) {
				outputFileF.createNewFile();
			}
			FileWriter writer = new FileWriter(outputFile, true);
			writer.append(key + "\t" + value + "\n");
			synchronized(workerStatus) {
				workerStatus.setKeysWritten(workerStatus.getKeysWritten() + 1);
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

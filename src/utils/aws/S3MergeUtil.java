package utils.aws;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.log4j.Logger;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public final class S3MergeUtil {
	private static final String DEFAULT_CLIENT = "default";
	private static final Logger logger = Logger.getLogger(S3MergeUtil.class);
	
	private AmazonS3Client s3client;
	private static S3MergeUtil utilIns = null;
	
	private S3MergeUtil(String client) {
		s3client = new AmazonS3Client(
				new ProfileCredentialsProvider(client));
		s3client.setRegion(Region.getRegion(Regions.US_EAST_1));
	}
	
	public static S3MergeUtil getInstance(String client) {
		if (utilIns  == null) {
			utilIns = new S3MergeUtil(client);
		}
		return utilIns;
	}
	
	/**
	 * Merges file objects in specified bucket and puts in output
	 * bucket
	 * 
	 * @param inpBucket
	 * @param outBucket
	 * @param mergeFactor
	 */
	public void merge(String inpBucket, String outBucket, int mergeFactor,
		String prefix, String outPrefix) throws IOException {
		
		ListObjectsRequest listObjectsRequest = 
			new ListObjectsRequest().withBucketName(inpBucket).withPrefix(prefix);
		
		ObjectListing objectListing;

		int counter = 0;
		int batch = 0;
		String keyPrefix = "file";
		
		StringBuffer contents = new StringBuffer();
		
		do {
			objectListing = s3client.listObjects(listObjectsRequest);
			for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
				//System.out.println( " - " + objectSummary.getKey() + "  " 
				//	+ "(size = " + objectSummary.getSize() + ")");
				S3Object obj = s3client.getObject(inpBucket, objectSummary.getKey());
				
				if (counter != 0) {
					contents.append("\n");
				}
				contents.append(getContentAsString(obj.getObjectContent()));
				counter++;
				
				if (counter == mergeFactor) {
					InputStream mergedContentIStream = new ByteArrayInputStream(
						contents.toString().getBytes(StandardCharsets.UTF_8));
					ObjectMetadata metadata = new ObjectMetadata();
					metadata.setContentLength(contents.toString().getBytes(
						StandardCharsets.UTF_8).length);
					
					s3client.putObject(new PutObjectRequest(
						outBucket,
						outPrefix + "/"+ keyPrefix + "_" + batch,
						mergedContentIStream,
						metadata));

					batch++;
					counter = 0;
					contents = new StringBuffer();
				}
			}
			listObjectsRequest.setMarker(objectListing.getNextMarker());
		} while (objectListing.isTruncated());

		InputStream mergedContentIStream = new ByteArrayInputStream(
			contents.toString().getBytes(StandardCharsets.UTF_8));
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(contents.toString().getBytes(
			StandardCharsets.UTF_8).length);
			
		s3client.putObject(new PutObjectRequest(
			outBucket,
			outPrefix + "/" + keyPrefix + "_" + batch,
			mergedContentIStream,
			metadata));
		
	}
	
	

	public static void main(String [] args) throws NumberFormatException, IOException {
		S3MergeUtil utility = S3MergeUtil.getInstance(DEFAULT_CLIENT);
		
		if (args.length < 3 || args.length > 5) {
			logger.error("Wrong number of arguments. Exiting");
			usage();
			System.exit(-1);
		}
		
		String inpPrefix = "";
		if (args.length > 3) {
			inpPrefix = args[3].trim();
		}
		
		String outPrefix = "";
		if (args.length > 4) {
			outPrefix = args[4].trim();
		}
		try {
			utility.merge(args[0].trim(), args[1].trim(), Integer.parseInt(args[2].trim()),
				inpPrefix, outPrefix);
		} catch (Exception e) {
			logger.error("Exception while merging " ,e);
			System.exit(-1);
		}
	}
	

	private static void usage() {
		logger.info("USAGE\n");
		logger.info("java S3MergeUtil <S3InpBucket> <S3OutBucket> <MergeFactor>");
	}

	private String getContentAsString(S3ObjectInputStream is) 
			throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		
		String res = "";
		String line = "";
		while((line = br.readLine()) != null) {
			res += line;			
		}
		return res;
	}

}

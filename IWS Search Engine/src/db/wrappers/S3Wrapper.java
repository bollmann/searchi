package db.wrappers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class S3Wrapper {
	private final Logger logger = Logger.getLogger(getClass());
	private static S3Wrapper instance;
	private AmazonS3 s3client;
	private String endPoint;

	private S3Wrapper() {
		this.endPoint = endPoint;
		s3client = new AmazonS3Client(new ProfileCredentialsProvider("shreejit"));
		s3client.setRegion(Region.getRegion(Regions.US_EAST_1));
	}

	/**
	 * Gives a singleton instance of the wrapper with the endpoint
	 * 
	 * @param endPoint
	 *            should ideally always be s3-<region>.amazonaws.com. In our
	 *            case s3-us-east-1.amazonaws.com
	 * @return
	 */
	public static S3Wrapper getInstance() {
		if (instance == null) {
			instance = new S3Wrapper();
		}
		return instance;
	}

	public void deleteBucket(String bucketName) {
		try {
			s3client.deleteBucket(bucketName);
		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which "
					+ "means your request made it "
					+ "to Amazon S3, but was rejected with an error response"
					+ " for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException during delete, which "
					+ "means the client encountered "
					+ "an internal error while trying to "
					+ "communicate with S3, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}

	}

	public void createBucket(String bucketName) {
		try {
			if (!(s3client.doesBucketExist(bucketName))) {
				// Note that CreateBucketRequest does not specify region. So
				// bucket is
				// created in the region specified in the client.
				s3client.createBucket(new CreateBucketRequest(bucketName));
			}
			// Get location.
			String bucketLocation = s3client
					.getBucketLocation(new GetBucketLocationRequest(bucketName));
			System.out.println("bucket location = " + bucketLocation);

		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException during create, which "
					+ "means your request made it "
					+ "to Amazon S3, but was rejected with an error response"
					+ " for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which "
					+ "means the client encountered "
					+ "an internal error while trying to "
					+ "communicate with S3, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}

	public void putItem(String bucketName, String key, String content) {
		ByteArrayInputStream bais = null;
		try {
			bais = new ByteArrayInputStream(content.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s3client.putObject(bucketName, key, bais, null);
	}

	public String getItem(String bucketName, String key) {
		S3Object object = s3client.getObject(new GetObjectRequest(bucketName,
				key));
		InputStream objectData = object.getObjectContent();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(objectData));
		
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Process the objectData stream.
		try {
			objectData.close();
			object.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

}

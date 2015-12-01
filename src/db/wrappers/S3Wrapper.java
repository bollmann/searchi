package db.wrappers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.gson.Gson;

import crawler.dao.URLContent;

public class S3Wrapper {
	private final Logger logger = Logger.getLogger(getClass());
	private static S3Wrapper instance;
	private AmazonS3 s3client;
	public final String URL_BUCKET = "cis455-url-content-new"; // cannot contain
															// uppercase
															// letters. should
															// be unique
	public final String URL_QUEUE_BUCKET = "cis455-url-queue"; // cannot contain
																// uppercase
																// letters.
																// should be
																// unique

	private S3Wrapper() {
		s3client = new AmazonS3Client(
				new ProfileCredentialsProvider("shreejit"));
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
		logger.info("Deleting elements from s3 bucket " + bucketName);
		
		try {
			Integer deletedItems = 0;
			ObjectListing objectListing = s3client.listObjects(bucketName);
			while (true) {
				for (Iterator<?> iterator = objectListing.getObjectSummaries()
						.iterator(); iterator.hasNext();) {
					S3ObjectSummary objectSummary = (S3ObjectSummary) iterator
							.next();
					s3client.deleteObject(bucketName, objectSummary.getKey());
					deletedItems += 1;
					if(deletedItems % 500 == 0) {
						logger.info("Deleted " + deletedItems + " items");
					}
				}
				
				if (objectListing.isTruncated()) {
					objectListing = s3client
							.listNextBatchOfObjects(objectListing);
				} else {
					break;
				}
			}
			;
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
			System.out
					.println("Caught an AmazonClientException during delete, which "
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
			System.out
					.println("Caught an AmazonServiceException during create, which "
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

	public void putItem(String key, String content) {
		putItem(URL_BUCKET, key, content);
	}


//	public void putBatchItem(String key, String content) {
//		synchronized (currentBatchId) {
//			if (batchKeyValueMap.size() < BATCH_SIZE) {
//				batchKeyValueMap.put(key, content);
//			} else {
//				// write all content out to s3
//				StringBuilder sb = new StringBuilder();
//				List<String> contentList = new ArrayList<String>(
//						batchKeyValueMap.keySet());
//				sb.append(new Gson().toJson(contentList));
//				// for (Entry<String, String> entry :
//				// batchKeyValueMap.entrySet()) {
//				// sb.append(entry.getValue() + "\n");
//				// }
//				putItem(currentBatchId, sb.toString());
//				logger.info("Wrote a batch " + currentBatchId + " of "
//						+ batchKeyValueMap.size() + " items to s3 ");
//				batchKeyValueMap.clear();
//				currentBatchId = null;
//			}
//		}
//
//	}


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

	public String getItem(String key) {
		return getItem(URL_BUCKET, key);
	}

	public String getItem(String bucketName, String key) {
		S3Object object = s3client.getObject(new GetObjectRequest(bucketName,
				key));
		InputStream objectData = object.getObjectContent();

		BufferedReader br = new BufferedReader(
				new InputStreamReader(objectData));

		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
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

	public void deleteItem(String bucketName, String key) {
		s3client.deleteObject(bucketName, key);
	}

	public Integer getNumberOfItemsInBucket(String bucketName) {
		Integer itemCount = 0;
		try {
//			System.out.println("Listing objects");
			logger.info("Checking number of items in bucket " + bucketName);
			ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
					.withBucketName(bucketName);
			ObjectListing objectListing;
			do {
				objectListing = s3client.listObjects(listObjectsRequest);
				for (S3ObjectSummary objectSummary : objectListing
						.getObjectSummaries()) {
					if(itemCount % 500 == 0) {
						logger.info("Looked at " + itemCount + " items");
					}
//					System.out.println(" - " + objectSummary.getKey() + "  "
//							+ "(size = " + objectSummary.getSize() + ")");
					itemCount += 1;
				}
				listObjectsRequest.setMarker(objectListing.getNextMarker());
			} while (objectListing.isTruncated());
		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, "
					+ "which means your request made it "
					+ "to Amazon S3, but was rejected with an error response "
					+ "for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, "
					+ "which means the client encountered "
					+ "an internal error while trying to communicate"
					+ " with S3, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
		
		return itemCount;
	}

	public Integer getDuplicateUrlsInBucket(String bucketName) {
		Set<String> urlsSeen = new HashSet<String>(300000);
		Integer duplicateCount = 0, totalCount = 0;
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
				.withBucketName(bucketName);
		logger.info("Checking duplicates in " + bucketName);
		ObjectListing objectListing;
		do {
			objectListing = s3client.listObjects(listObjectsRequest);
			for (S3ObjectSummary objectSummary : objectListing
					.getObjectSummaries()) {
				totalCount++;
				// System.out.println(" - " + objectSummary.getKey() + " "
				// + "(size = " + objectSummary.getSize() + ")");
				if (totalCount % 500 == 0) {
					logger.info("Went through " + totalCount + " items in s3. Found " + duplicateCount + " duplicates.");
				}
				String content = getItem(URL_BUCKET, objectSummary.getKey());
				URLContent urlContent = new Gson().fromJson(content,
						URLContent.class);
				if (urlsSeen.contains(urlContent.getUrl())) {
					duplicateCount++;
				} else {
					urlsSeen.add(urlContent.getUrl());
				}
			}
			listObjectsRequest.setMarker(objectListing.getNextMarker());
		} while (objectListing.isTruncated());
		return duplicateCount;
	}
}

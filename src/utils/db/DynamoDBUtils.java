package utils.db;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class DynamoDBUtils {
	public static final String CREDENTIALS_PROFILE = "default";

	public static DynamoDBMapper connectDB() {
		AWSCredentials credentials = new ProfileCredentialsProvider(CREDENTIALS_PROFILE).getCredentials();

		AmazonDynamoDBClient dbClient = new AmazonDynamoDBClient(credentials);
		dbClient.setRegion(Region.getRegion(Regions.US_EAST_1));

		return new DynamoDBMapper(dbClient);
	}
}

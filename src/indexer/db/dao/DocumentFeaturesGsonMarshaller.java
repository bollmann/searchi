package indexer.db.dao;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DocumentFeaturesGsonMarshaller implements
		DynamoDBMarshaller<List<DocumentFeatures>> {

	@Override
	public String marshall(List<DocumentFeatures> features) {
		return new Gson().toJson(features);
	}

	@Override
	public List<DocumentFeatures> unmarshall(
			Class<List<DocumentFeatures>> clazz, String rawFeatures) {
		return new Gson().fromJson(rawFeatures, new TypeToken<List<DocumentFeatures>>(){}.getType());
	}
}

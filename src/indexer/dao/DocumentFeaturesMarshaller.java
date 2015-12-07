package indexer.dao;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;
import com.google.gson.Gson;

public class DocumentFeaturesMarshaller implements
		DynamoDBMarshaller<List<DocumentFeatures>> {
	
	@Override
	public String marshall(List<DocumentFeatures> features) {
		Gson gson = new Gson();
		return gson.toJson(features);
	}

	@Override
	public List<DocumentFeatures> unmarshall(
			Class<List<DocumentFeatures>> clazz, String jsonFeatures) {
		Gson gson = new Gson();
		return gson.fromJson(jsonFeatures, clazz);
	}
}

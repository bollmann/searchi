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
		
//	@Override
//	public String marshall(DocumentFeatures features) {
//		return String.format("%s\t%f\t%f\t%d\t%d\t%d\t%d", 
//				features.getUrl(),
//				features.getMaximumTermFrequency(),
//				features.getEuclideanTermFrequency(), 
//				features.getTotalCount(),
//				features.getLinkCount(), 
//				features.getMetaTagCount(),
//				features.getHeaderCount());
//	}
//
//	@Override
//	public DocumentFeatures unmarshall(Class<DocumentFeatures> clazz,
//			String input) {
//		DocumentFeatures features = new DocumentFeatures();
//		String parts[] = input.split("\t");
//		features.setUrl(parts[0]);
//		features.setMaximumTermFrequency(Double.parseDouble(parts[1]));
//		features.setEuclideanTermFrequency(Double.parseDouble(parts[2]));
//		features.setTotalCount(Integer.parseInt(parts[3]));
//		features.setLinkCount(Integer.parseInt(parts[4]));
//		features.setMetaTagCount(Integer.parseInt(parts[5]));
//		features.setHeaderCount(Integer.parseInt(parts[6]));
//		
//		return features;
//	}
}

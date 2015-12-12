package indexer.db.dao;


import indexer.db.dao.exceptions.DocumentFeaturesMarshallerException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DocumentFeaturesMarshaller implements
		DynamoDBMarshaller<List<DocumentFeatures>> {
	
	public String marshallEntry(DocumentFeatures feature) {
		StringBuffer sb = new StringBuffer();
		sb.append(feature.getDocId() + ",");
		sb.append(feature.getMaximumTermFrequency() + ",");
		sb.append(feature.getEuclideanTermFrequency() + ",");
		sb.append(feature.getTfidf() + ",");
		sb.append(feature.getTotalCount() + ",");
		sb.append(feature.getHeaderCount() + ",");
		sb.append(feature.getLinkCount() + ",");
		sb.append(feature.getMetaTagCount() + ",");
		
		for(Integer position: feature.getPositions())
			sb.append(position + ",");
				
		return sb.toString();
	}
	
	@Override
	public String marshall(List<DocumentFeatures> features) {
		StringBuffer sb = new StringBuffer();
		for(DocumentFeatures feature: features)
			sb.append(marshallEntry(feature) + ";");
		
		return sb.toString();
//		return new Gson().toJson(features);
	}

	public DocumentFeatures unmarshallEntry(String rawFeatures) {
		String[] features = rawFeatures.split(",");
		try {
			int docId = Integer.parseInt(features[0]);
			float maxtf = Float.parseFloat(features[1]);
			float euclidtf = Float.parseFloat(features[2]);
			float tfidf = Float.parseFloat(features[3]);
			int totalCount = Integer.parseInt(features[4]);
			int headerCount = Integer.parseInt(features[5]);
			int linkCount = Integer.parseInt(features[6]);
			int metaTagCount = Integer.parseInt(features[7]);
			
			Set<Integer> positions = new HashSet<>();
			for(int i = 8; i < features.length; ++i)
				positions.add(Integer.parseInt(features[i]));
		
			DocumentFeatures docFeatures = new DocumentFeatures();
			docFeatures.setDocId(docId);
			docFeatures.setMaximumTermFrequency(maxtf);
			docFeatures.setEuclideanTermFrequency(euclidtf);
			docFeatures.setTfidf(tfidf);
			docFeatures.setTotalCount(totalCount);
			docFeatures.setHeaderCount(headerCount);
			docFeatures.setLinkCount(linkCount);
			docFeatures.setMetaTagCount(metaTagCount);
			docFeatures.setPositions(positions);
			
			return docFeatures;
		} catch(ArrayIndexOutOfBoundsException | NumberFormatException e) {
			throw new DocumentFeaturesMarshallerException(rawFeatures, e);
		}
	}

	@Override
	public List<DocumentFeatures> unmarshall(Class<List<DocumentFeatures>> clazz, String rawFeatures) {
		List<DocumentFeatures> result = new ArrayList<>();
		
		String[] entries = rawFeatures.split(";");
		for(String entry: entries)
			result.add(unmarshallEntry(entry));
		
		return result;
//		return new Gson().fromJson(rawFeatures, new TypeToken<List<DocumentFeatures>>(){}.getType());
	}
}
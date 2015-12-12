package searchengine.ranking;

import java.util.ArrayList;
import java.util.List;

public abstract class Ranker extends Thread {
	
	public abstract void computeRank();
	public abstract List<Double> getRanks();
	public abstract void setWeight(double weight);
	public abstract double getWeight();
	public abstract void doNormalization(boolean bool);
	public abstract String getRankingName();
	
	@Override
	public void run() {
		computeRank();
	}
	
	protected List<Double> normalize(List<Double> values, double min, double max) {
		
		if (values == null || values.isEmpty()) {
			return values;
		}
		
		if (Double.compare(max,min) == 0) {
			if (Double.compare(max,0.0) == 0) {
				return values;
			}
			min = 0;
		}
		
		List<Double> normalValues = new ArrayList<>();
		for (Double val : values)  {
			normalValues.add((val - min) / (max - min));			
		}
		return normalValues;
		
	}

}

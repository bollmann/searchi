package searchengine.query;

public class QueryWord {
	private String word;
	private Integer nGramSize;
	private Double weight;
	
	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Integer getnGramSize() {
		return nGramSize;
	}

	public void setnGramSize(Integer nGramSize) {
		this.nGramSize = nGramSize;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public QueryWord(String word) {
		this.word = word;
	}

	public String getWord() {
		return word;
	}
	
	@Override
	public String toString() {
		return "word:" + word + " " + nGramSize + "-gram weight:" + weight;
	}

	public double getWeight() {
		// TODO Auto-generated method stub
		return 0;
	}

}

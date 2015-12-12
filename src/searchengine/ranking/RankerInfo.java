package searchengine.ranking;

public final class RankerInfo {
	
	public static enum RankerType {
		RANKER_HEADER,	
		RANKER_LINKS,
		RANKER_META,
		RANKER_POSITION,
		RANKER_QUERYMATCH,
		RANKER_TFIDF,
		RANKER_TOTALCOUNT,
		RANKER_URLCOUNT
	}

}

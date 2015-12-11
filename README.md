# searchi -- our awesome IWS search engine!

1) Authors:
-----------

Dominik Bollmann (bollmann@)
Gouthaman Kumarappan (goku@)
Ishan Srivastava (ishan@)
Shreejit Ganghadaran (shreejit@)

2) Implemented Features:
------------------------

The entire Assignment.

3) Extra Credits:
-----------------

Indexer: Image Search
PageRank: Domain rank
SearchEngine: Ajax dynamic snippets, 3rd party search, image search

4) Source Files:
----------------

The entire src/ folder, namely the following files.

.
├── crawler
│   ├── clients
│   │   └── HttpClient.java
│   ├── dao
│   │   └── URLContent.java
│   ├── errors
│   │   ├── MalformedHeaderException.java
│   │   ├── NoDomainConfigException.java
│   │   └── QueueFullException.java
│   ├── extractors
│   │   ├── ContentExtractor.java
│   │   └── HtmlContentExtractor.java
│   ├── handlers
│   │   ├── MainHandler.java
│   │   ├── RequestHandler.java
│   │   ├── ServletHandler.java
│   │   └── StaticRequestHandler.java
│   ├── info
│   │   ├── RobotsTxtInfo.java
│   │   └── URLInfo.java
│   ├── parsers
│   │   ├── Parser.java
│   │   └── PdfUtils.java
│   ├── policies
│   ├── requests
│   │   ├── Http10Request.java
│   │   ├── Http11Request.java
│   │   └── HttpRequest.java
│   ├── responses
│   │   ├── Http10Response.java
│   │   ├── Http11Response.java
│   │   ├── HttpResponse.java
│   │   └── Response.java
│   ├── servlet
│   │   ├── background
│   │   │   └── UrlProcessorThread.java
│   │   ├── multinodal
│   │   │   └── status
│   │   │       └── WorkerStatus.java
│   │   ├── SingleNodeCrawler.java
│   │   └── url
│   │       └── UrlProcessor.java
│   ├── servlets
│   │   ├── HttpSessionImpl.java
│   │   ├── ServletConfigImpl.java
│   │   ├── ServletContextImpl.java
│   │   ├── ServletMap.java
│   │   └── SessionMap.java
│   ├── shutdownHook
│   │   └── ShutdownHook.java
│   ├── threadpool
│   │   ├── DiskBackedQueue.java
│   │   ├── MercatorNode.java
│   │   ├── MercatorQueue.java
│   │   ├── Queue.java
│   │   ├── ThreadPool2.java
│   │   └── ThreadPool.java
│   └── webserver
│       ├── HttpServer.java
│       └── RunnerDaemon.java
├── db
│   ├── dbo
│   │   ├── DomainInfo.java
│   │   ├── QueueInfo.java
│   │   └── URLMetaInfo.java
│   ├── samples
│   │   ├── AmazonDynamoDBSample.java
│   │   ├── AwsConsoleApp.java
│   │   ├── MoviesCreateTable.java
│   │   ├── MoviesLoadData.java
│   │   └── S3Sample.java
│   └── wrappers
│       ├── DynamoDBWrapper.java
│       └── S3Wrapper.java
├── examples
│   └── WordCount.java
├── indexer
│   ├── api
│   │   └── DocumentIDs.java
│   ├── clients
│   │   └── InvertedIndexClient.java
│   ├── db
│   │   ├── dao
│   │   │   ├── DocumentFeatures.java
│   │   │   ├── DocumentFeaturesMarshaller.java
│   │   │   ├── DocumentIDRow.java
│   │   │   ├── ImageIndex.java
│   │   │   └── InvertedIndexRow.java
│   │   └── imports
│   │       ├── adapters
│   │       │   ├── DocumentIDsAdapter.java
│   │       │   ├── FileToDatabaseAdapter.java
│   │       │   ├── ImageIndexAdapter.java
│   │       │   └── InvertedIndexAdapter.java
│   │       └── DynamoImporter.java
│   ├── DocumentScore.java
│   ├── DocumentVector.java
│   ├── InvertedIndexFetcher.java
│   ├── InvertedIndex.java
│   ├── offline
│   │   ├── ImageJob.java
│   │   ├── InvertedIndexJob.java
│   │   └── Tokenizer.java
│   └── WordCounts.java
├── log4j.properties
├── mapreduce
│   └── lib
│       ├── URLInputFormat.java
│       └── URLsFromJsonReader.java
├── pagerank
│   ├── api
│   │   └── PageRankAPI.java
│   ├── db
│   │   ├── dao
│   │   │   ├── DRDao.java
│   │   │   └── PRDao.java
│   │   └── ddl
│   │       ├── PRCreateTable.java
│   │       └── PRLoadData.java
│   ├── phase1
│   │   ├── DRInitMapper.java
│   │   ├── PRInitJob.java
│   │   ├── PRInitMapper2.java
│   │   ├── PRInitMapper.java
│   │   └── PRInitReducer.java
│   └── phase2
│       ├── PRComputeJob.java
│       ├── PRComputeMapper.java
│       ├── PRComputeReducer.java
│       ├── PRFinalAggJob.java
│       ├── PRFinalAggMapper.java
│       ├── PRFinalAggReducer.java
│       └── test.java
├── searchengine
│   ├── rank
│   │   ├── combinators
│   │   │   └── DocumentFeatureCombinators.java
│   │   └── comparators
│   │       └── DocumentScoreComparators.java
│   ├── ranking
│   │   └── Ranker.java
│   └── servlets
│       ├── IndexerClientServlet.java
│       ├── SearchEngineInterface.java
│       └── SearchResult.java
├── test
│   ├── crawler
│   │   ├── clients
│   │   │   └── TestHttpClient.java
│   │   ├── parsers
│   │   │   ├── TestParser.java
│   │   │   └── TestPdfUtils.java
│   │   ├── servlet
│   │   │   ├── TestSingleNodeCrawler.java
│   │   │   └── url
│   │   │       └── TestUrlProcessor.java
│   │   └── threadpool
│   │       ├── TestDiskBackedQueue.java
│   │       ├── TestMercatorNode.java
│   │       ├── TestMercatorQueue.java
│   │       └── TestQueue.java
│   ├── db
│   │   ├── dbo
│   │   │   └── TestURLMetaInfo.java
│   │   └── wrappers
│   │       ├── TestDynamoDBWrapper.java
│   │       └── TestS3Wrapper.java
│   ├── indexer
│   │   ├── TestDocumentIDs.java
│   │   ├── TestInvertedIndexClient.java
│   │   ├── TestInvertedIndexJob.java
│   │   ├── TestWordCounts.java
│   │   └── TestWordCountsPerformance.java
│   ├── pagerank
│   │   ├── api
│   │   │   └── TestPageRankAPI.java
│   │   └── offline
│   │       └── TestPageRankCompute.java
│   ├── searchengine
│   │   └── ranking
│   │       └── TestRanker.java
│   └── utils
│       ├── nlp
│       │   ├── TestDictionary.java
│       │   ├── TestLanguageDetector.java
│       │   └── TestPornDetector.java
│       ├── searchengine
│       │   └── TestSearchEngineUtils.java
│       └── string
│           └── TestStringUtils.java
└── utils
    ├── aws
    │   ├── dynamo
    │   │   └── DynamoDBUtils.java
    │   └── s3
    │       └── S3MergeUtil.java
    ├── file
    │   ├── ConcatFiles.java
    │   ├── FileUtils.class
    │   ├── FileUtils.java
    │   └── FindDuplicates.java
    ├── LRUCache.java
    ├── nlp
    │   ├── Dictionary.java
    │   ├── LanguageDetector.java
    │   └── PornDetector.java
    ├── searchengine
    │   └── SearchEngineUtils.java
    ├── string
    │   └── StringUtils.java
    └── Tuple.java

76 directories, 135 files

5) Install Instructions
-----------------------

Compile:
To compile all source files, run ant compile-all

Crawler: 
To run crawler, first in the conf directory, in master-web.xml, there is a disk back queue id in the domain servlet config
mapping. That needs to be set to an id. Then the csv file for allowed domains needs to be provided in the following format
line number, domain (of the format google.com. This represents a domain, not a web page root).

Then the crawler can be run by sh scripts/run-master.sh from the project root.

Searchengine: 
To run the searchengine front end, node app.js when in the project root.

For page rank and indexer, it requires multiple steps along with Amazon EMR integration and that cannot be done 
programmatically right now.
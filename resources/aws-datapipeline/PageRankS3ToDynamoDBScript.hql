CREATE EXTERNAL TABLE s3_import_pagerank(page string, pagescore double)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
LOCATION 's3://pagerank.sample/';

CREATE EXTERNAL TABLE s3PageRankHive (pagecol string, pagescore double)
STORED BY 'org.apache.hadoop.hive.dynamodb.DynamoDBStorageHandler' 
TBLPROPERTIES ("dynamodb.table.name" = "PageRank", 
"dynamodb.column.mapping" = "pagecol:Page,pagescore:PageScore");  
                    
INSERT OVERWRITE TABLE s3PageRankHive SELECT * FROM s3_import_pagerank;

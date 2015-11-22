CREATE EXTERNAL TABLE s3_import_inv_index(word string, url string, tf string, wordcount string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
LOCATION 's3://inverted-index-raw-table/';

CREATE EXTERNAL TABLE s3InvIndexHive (wordcol string, urlcol string, tfcol string, wordcountcol string)
STORED BY 'org.apache.hadoop.hive.dynamodb.DynamoDBStorageHandler' 
TBLPROPERTIES ("dynamodb.table.name" = "invertedIndex", 
"dynamodb.column.mapping" = "wordcol:word,urlcol:url,tfcol:tf,wordcountcol:wordcount");  
                    
INSERT OVERWRITE TABLE 's3InvIndexHive' SELECT * FROM s3_import_inv_index;

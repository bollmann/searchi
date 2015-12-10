CREATE EXTERNAL TABLE s3_import_domainrank(domain string, domainscore double)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
LOCATION 's3://crawled-data-batch1/input/';

CREATE EXTERNAL TABLE s3DomainRankHive (domaincol string, domainscore double)
STORED BY 'org.apache.hadoop.hive.dynamodb.DynamoDBStorageHandler' 
TBLPROPERTIES ("dynamodb.table.name" = "DomainRank", 
"dynamodb.column.mapping" = "domaincol:Domain,domainscore:DomainScore");  
                    
INSERT OVERWRITE TABLE s3DomainRankHive SELECT * FROM s3_import_domainrank;

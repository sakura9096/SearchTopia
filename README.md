# SearchTopia
=================

### A description of all features implemented
----------------------------------------------
1. Distributed multi-threaded crawler: 
The multi-threaded distributed web crawler crawled over 1,000,000 web pages. The crawler was Mercator-style and each crawling peer was responsible for a subset of domains and each thread kept its own url frontier. The extracted urls from crawled pages were periodically shuffled and pushed to the nodes responsible for them via REST-style messages. The crawler also checked to prevent crawling duplicated pages and non-English pages.
2. Indexer:
Created inverted indexes for all the crawled documents. Filtered out non-ASCII words. Generated separate indexes for “fancy hit” (words in urls, titles, descriptions, anchors) and “normal hit”(words in body of the documents) and calculated their tf-idf scores. Also extracted the links for every crawled pages and created the anchor file. Then the data was run in Hadoop MapReduce framework to produce the TF-IDF score for each word and url pair. The data was then uploaded in dynamoDB separately in fancy barrel and normal barrel.
3.PageRank:
Removed all the dangling links in the anchor file during preprocess and calculated PageRank through multiple iterations of MapReduce on EMR. For sink nodes, their PageRank score was distributed to all the links in the graph. 
4. Search Engine and Web UI:
Ranked the search result (document/url) using the following factors:
* fancy hit 
* the TF-IDF score of the words 
* the PageRank of corresponding url 
The ranking was calculated by 2 * tfidf * pageRank / (tfidf + pageRank) 
* normal hit 
* the TF-IDF score of the words
* the PageRank of corresponding url
* display results

### Extra Features
------------------
* Integrate search result from web services, including Wiki, yelp API.  
* Autocomplete feature. 
* Spell check feature.
* SEO defenses. 

### detailed instructions on how to install and run the project
----------------------------------
1. Crawler:
* (1) Compile the Crawler project into a JAR file
* (2) Create an input list of all the other crawlers with their ids, ip addresses, port numbers
* (3) Create another input file with its ids, ip addresses, port numbers
* (4) Create a file of seed urls
* (5) Run the JAR, with 9 arguments: (maxSize, numThread, selfFileDir, workersFileDir, output, countMax, dbDir, seedsFileDir, profileDir)
* maxSize: the max length of a file 
* numThread: the number of thread of each crawler 
* selfFileDir: the configuration info of itself
* workersFileDir: the configuration info of themselves 
* output: the output directory 
* countMax: the maximum count of crawled page
* dbDir: the local berkeley database directory 
* seedsFileDir: the seed urls file directory 
* profileDir: profile dictionary file directory (for the third-party language detection)

2. Indexer:
* The input of the FileManage is a folder that contains a lot of files, whose format would be a url as the first line and content as the body. 

* Compile the FileManage project into a JAR file. (make sure that all the libraries in FileManage/lib/* are included in the build path). The argument would be (inputDirectory, hitListOutputDirectory, anchorFileOutputDirectory).
Compile the MapReduce project into a JAR file. Run EMR using the output of FileManage using the hit list output from the FileManage by providing the S3 input and output directory.

* Compile the DynamoDBDataUpload project to a jar file and copy it to EC2. The input is the directory that stored the map reduce result. 

3. PageRank:
* The input file format: each line is an encoded record (using Codec in FileManage package) containing the source url, and the outbound urls. 
* Compile the PageRank project into a JAR file. (make sure that all the libraries in PageRank/lib/* are included in the build path).
To run on EMR, upload the JAR file into S3, take notes of the input directory (the S3 directory containing all the anchor files), create an output directory. Then add the customer JAR file as a new step, provide the following command line arguments:
PageRank s3://<bucket_name>/input s3://<bucket_name>/output s3://<bucket_name>/intermediate
* Once finished, the results can be found in the s3://<bucket_name>/output folder. 

4. Search Engine:
Generate the war file for the project SearchInterfaceV2. Upload jetty-distribution-9.3.8.v20160314 to EC2 instance and copy the .war file inside jetty-distribution-9.3.8.v20160314/webapps/. To run the servlet, cd to /jetty-distribution-9.3.8.v20160314/ and enter: java -jar start.jar -Djetty.port=8080. When the servlet is running, open a browser and type in the EC2’s public DNS in the following format: <domain>:8080/SearchInterfaceV2/welcome. You should now see the homepage of our search engine.

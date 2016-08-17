# SearchTopia (mini-Google Search Engine)

### A description of our architecture

<img src="https://github.com/sakura9096/SearchTopia/blob/master/Picture1.png" width="250px">

1. <strong>Distributed multi-threaded crawler: </strong>
<p>The multi-threaded distributed web crawler crawled over 1,000,000 web pages. The crawler was Mercator-style and each crawling peer was responsible for a subset of domains and each thread kept its own url frontier. The extracted urls from crawled pages were periodically shuffled and pushed to the nodes responsible for them via REST-style messages. The crawler also checked to prevent crawling duplicated pages and non-English pages.</p>
2. <strong>Indexer:</strong>
<p>Created inverted indexes for all the crawled documents. Filtered out non-ASCII words. Generated separate indexes for “fancy hit” (words in urls, titles, descriptions, anchors) and “normal hit”(words in body of the documents) and calculated their tf-idf scores. Also extracted the links for every crawled pages and created the anchor file. Then the data was run in Hadoop MapReduce framework to produce the TF-IDF score for each word and url pair. The data was then uploaded in dynamoDB separately in fancy barrel and normal barrel.</p>
3. <strong>PageRank:</strong>
<p>Removed all the dangling links in the anchor file during preprocess and calculated PageRank through multiple iterations of MapReduce on EMR. For sink nodes, their PageRank score was distributed to all the links in the graph. </p>
4. <strong>Search Engine and Web UI:</strong>
<p>Ranked the search result (document/url) using the following factors:<p>
<p>fancy hit -> the TF-IDF score of the words -> the PageRank of corresponding url -> The ranking was calculated by 2 * tfidf * pageRank / (tfidf + pageRank) -> normal hit -> the TF-IDF score of the words -> the PageRank of corresponding url -> display results</p>

### Extra Features
<ul>
  <li>Integrate search result from web services, including Wiki, yelp API. </li>
  <li>Autocomplete feature. </li>
  <li>Spell check feature.</li>
  <li>SEO defenses. </li>
</ul>

### Screenshots
<strong>Front Page</strong><br>
<img src="https://github.com/sakura9096/SearchTopia/blob/master/G02_0_FrontPage.png" width="250px">


<strong>Search Page</strong><br>
<img src="https://github.com/sakura9096/SearchTopia/blob/master/G02_2.png" width="250px">

### Detailed instructions on how to install and run the project

1. Crawler:
<p>(1) Compile the Crawler project into a JAR file<br>
  (2) Create an input list of all the other crawlers with their ids, ip addresses, port numbers<br>
  (3) Create another input file with its ids, ip addresses, port numbers<br>
  (4) Create a file of seed urls<br>
  (5) Run the JAR, with 9 arguments: (maxSize, numThread, selfFileDir, workersFileDir, output, countMax, dbDir, seedsFileDir, profileDir)<br>
  <ul>
    <li>maxSize: the max length of a file </li>
    <li>numThread: the number of thread of each crawler </li>
    <li>selfFileDir: the configuration info of itself</li>
    <li>workersFileDir: the configuration info of themselves </li>
    <li>output: the output directory </li>
    <li>countMax: the maximum count of crawled page</li>
    <li>dbDir: the local berkeley database directory </li>
    <li>seedsFileDir: the seed urls file directory </li>
    <li>profileDir: profile dictionary file directory (for the third-party language detection)</li>
  </ul>
</p>
2. Indexer:
<p>The input of the FileManage is a folder that contains a lot of files, whose format would be a url as the first line and content as the body. </p>
<p> Compile the FileManage project into a JAR file. (make sure that all the libraries in FileManage/lib/* are included in the build path). The argument would be (inputDirectory, hitListOutputDirectory, anchorFileOutputDirectory).
Compile the MapReduce project into a JAR file. Run EMR using the output of FileManage using the hit list output from the FileManage by providing the S3 input and output directory. </p>
<p>Compile the DynamoDBDataUpload project to a jar file and copy it to EC2. The input is the directory that stored the map reduce result.</p> 

3. PageRank:
<p> The input file format: each line is an encoded record (using Codec in FileManage package) containing the source url, and the outbound urls. </p>
<p>Compile the PageRank project into a JAR file. (make sure that all the libraries in PageRank/lib/* are included in the build path).</p>
<p>To run on EMR, upload the JAR file into S3, take notes of the input directory (the S3 directory containing all the anchor files), create an output directory. Then add the customer JAR file as a new step, provide the following command line arguments:</p>
<p>PageRank s3://<bucket_name>/input s3://<bucket_name>/output s3://<bucket_name>/intermediate</p>
<p>Once finished, the results can be found in the s3://<bucket_name>/output folder. </p>

4. Search Engine:
<p> Generate the war file for the project SearchInterfaceV2. Upload jetty-distribution-9.3.8.v20160314 to EC2 instance and copy the .war file inside jetty-distribution-9.3.8.v20160314/webapps/. To run the servlet, cd to /jetty-distribution-9.3.8.v20160314/ and enter: java -jar start.jar -Djetty.port=8080. When the servlet is running, open a browser and type in the EC2’s public DNS in the following format: <domain>:8080/SearchInterfaceV2/welcome. You should now see the homepage of our search engine.</p>

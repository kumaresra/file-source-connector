#File-source-connector
improved File connector for enterprise

# Credits

This connector uses the  [kafka-connect-spooldir](https://github.com/jcustenborder/kafka-connect-spooldir) 

# Overview

This POC Kafka Connect connector provides the capability to 

* Watch a directory for files and read the data as new files are
written to the input directory. 
* Each of the records (line) in the input file will be converted and send to specified kafka topic. 
* Send an End of file event to a specified topic when the file read is completed.

# Configuration

The task class `FCSourceTask.java` should be modified to parse FC files and write internal connect schema, that is automatically translated to AVRO by confluent avro converter. 
The main method to adapt is `FCSourceTask.process()`.


This POC parses the file line by splitting it by "|" character:
* split[0] - key
* split[1] - value field `field1`
* split[2] - value field `field2`

# Building Locally

```
git clone git@github.com:kumaresra/file-source-connector.git
cd file-source-connector
mvn clean package
```

# Installing

The FC POC connector can be installed as any standard kafka connector.

For example:

**Start confluent platform**
* Go to confluent installation directory
* `./bin/confluent start`  - start the full confluent platform
* `./bin/confluent status` - verify it is working
* `./bin/confluent stop connect && ./bin/confluent start connect` - restart connect (after placing new connector for ex)
* `./bin/confluent log connect` - kafka connect logs
 
**Install FC connector**
* By default kafka connect scans for connectors in `./share/java`  
* For simplicity you can create a symlinc in this directory pointing to your connector build: 
   `ln -s <path to cloned directory>/file-source-connector/target/file-source-connector ./share/java/file-source-connector`
* Alternatively you can just copy directory `<path to cloned directory>/file-source-connector/target/file-source-connector` to `./share/java`
* Restart kafka connect: `./bin/confluent stop connect && ./bin/confluent start connect`
* Verify that connector is detected in kafka-connect-ui:
![Create New Connector](./docs/MMD1.png)

**Create new connector instance**
* First of all create 3 dirs (`source-file` for files to be integrated, `finished-file` - for finished files, `error-file` - for rejected files)
```
mkdir /tmp/source-file
mkdir /tmp/finished-file
mkdir /tmp/error-file
```
* in kafka-connect-ui
* click `New` -> `FCSourceConnector`
* Fill in the following properties:
```
name=FCSourceConnector
connector.class=io.huzzle.kafka.connect.file.FCSourceConnector
input.file.pattern=^file.*.csv$
finished.path=/tmp/finished-file
timestamp.mode=FILE_TIME
tasks.max=1
topic=fc-file-poc
halt.on.error=false
error.path=/tmp/error-file
input.path=/tmp/source-file
schema.generation.enabled=true
```  
* Click `Create`
![Create New Connector](docs/MMD2.png)
* Your new connector is now up and running

* It is possible to do the same with APIs:
```
curl -X POST \
  http://localhost:8083/connectors/ \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{
  "name": "MmdAccountSourceConnector",
  "config": {
    "connector.class": "io.huzzle.kafka.connect.file.FCSourceConnector",
    "input.file.pattern": "^file.*.csv$",
    "finished.path": "/tmp/finished-file",
    "timestamp.mode": "FILE_TIME",
    "tasks.max": "1",
    "topic": "fc-file-poc",
    "halt.on.error": "false",
    "error.path": "/tmp/error-file",
    "input.path": "/tmp/source-file",
    "schema.generation.enabled": "true"
  }
}'
```

# Test it
* Generate some test files
```
for i in {0..10}; do echo "id-$i|field1 $i|field2 $i"; done >> file1.mmd
for i in {0..10}; do echo "id-$i|field1 $i|field2 $i"; done >> file2.mmd
for i in {0..10}; do echo "id-$i|field1 $i|field2 $i"; done >> file3.mmd
```
* Move them to /tmp/source-file: `mv file*.mmd /tmp/source-file`
* Verify that the /tmp/source-file is empty (file has beed integrated and placed in /tmp/finished-file)

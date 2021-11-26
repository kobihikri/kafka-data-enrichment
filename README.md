# kafka-streams-enrichment-using-kafka-connect

<!-- Prerquisites -->
<!-- --------------- -->
<!-- Maven installed -->

cd .\demo\data-producers\enrichment-data-redis-producer\
mvn clean package
cd ..\raw-data-kafka-producer\
mvn clean package

cd ..\..\direct-data-enrichment\
cd ..\..

cd .\demo\redis-encrichment-fetching-kafka-sink-connector
mvn clean package
cd ..\..\

cd .\demo\redis-enrichment-source-connector
mvn clean package
cd ..\..\

docker build -t kafka-connect .\apache-kafka\kafka-connect\

cd .\demo\kafka-streams-based-enrichment
mvn clean package
cd ..\..\

docker-compose build
docker-compose up
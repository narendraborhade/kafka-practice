Running a program through intellij - StreamStarterApp
# Steps to run program in intellij

# open zookeeper
bin\windows\zookeeper-server-start.bat config\zookeeper.properties

# open server
bin\windows\kafka-server-start.bat config\server.properties

# create word count topic
bin\windows\kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 2 --topic word-count-input

# create word count topic
bin\windows\kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic word-count-output

# open consumer
bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 \
--topic word-count-output \
--from-beginning \
--formatter kafka.tools.DefaultMessageFormatter \
--property print.key=true \
--property print.value=true \
--property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
--property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer

# add data in topic
bin\windows\kafka-console-producer.bat --broker-list localhost:9092 --topic word-count-input

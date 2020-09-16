Sample kafka
==================================================

##Kafka Theory Overview

* Topics: a particular stream of data
    - Similiar to a table in a database (without all the constraints)
    - You can have as many topics as you want
    - A topic is identified by its name
    
* Topics are split in partitions
    - Each partitions is ordered
    - Each message within a partition gets a incremental id, called offset
    
* Offset
    - Offset only have a meaning for a specific partition
        - E.g offset 3 in partition 0 doesn`t represent the same data as offset 3 in partition 1
    - Order is guaranteed only within a partition (not across partitions)
    - Data is kept only for a limited time (default is one week)
    - Once the data is written to a partition, it can`t be changed (immutability)
    - Data is assigned randomly to a partition unless a key is provided
    
* Brokers
    - A kafka cluster is composed of multiple brokers (servers)
    - Each broker is identified with its ID (integer)
    - Each broker contains certain topic partitions
    - After connecting to any broker (called a bootstrap broker), you will be connected to the entire cluster
   
* Topic replication factor
    - Topics should have a replication factor > 1 (usually between 2 and 3(gold standard))
    - This way if a broker is down, another broker can serve the data

* Concept of Leader for a Partition
    - At any time only ONE broker can be a leader for a given partition
    - Only that leader can receive and serve data for a partition
    - The other brokers will synchronize the data
    - Therefore each partition has one leader and multiple ISR (in-sync replica)
    - The leader and the ISR is determined by Zookeeper

* Producers
    - Producers write data to topics (which is made of partitions)
        - The load is balanced to many brokers thanks to the number of partitions
    - Producers automatically know to which broker and partition to write to
    - In case of Broken failures, Producers will automatically recover

* Producers can choose to receive acknowledgment of data writes
    - acks=0: Producer won`t wait for acknowledgment (possible data loss)
    - acks=1: Producer will wait for leader acknowledgment (limited data loss)
    - acks=all: Leader + replicas acknowledgment (no data loss)
    
 * Producers: Message keys
    - Producers can choose to send a key with the message (string, number, etc...)
    - If key=null, data is sent round robin
    - If a key is sent, then all messages for that key will always go to the same partition
    - A key is basically sent if you need message ordering for a specific field

* Consumers
    - Consumers read data from a topic (identified by name)
    - Consumers know which broker to read from 
    - In case of broker failures, consumers know how to recover
    - Data is read in order within each partitions
    
* Consumer Groups
    - Consumers read data in consumer groups
    - Each consumer within a group reads from exclusive partitions
    - If you have more consumers than partitions, some consumers will be inactive
    - Consumers will automatically use a GroupCoordinator and a ConsumerCoordinator to assign a consumers to partition.
    
* Consumer Offsets
    - Kafka stores the offsets at which a consumer group has been reading
    - The offsets committed live in Kafka topic named __consumer__offsets
    - When a consumer in a group has processed data received from Kafka, it should be committing the offsets
    - if a consumer dies, it will be able to read back from where it left off thanks to the committed consumer offsets.
    
* Delivery semantics for consumers

    Consumers choose when to commit offsets.
    There are 3 delivery semantics:
    
    - At most once: 
        * offsets are committed as soon as the message is received
        * If the processing goes wrong, the message will be lost (it won`t be read again)
    
    - At least once (usually preferred)
        * offsets are committed after the message is processed
        * if the processing goes wrong, the message will be read again
        * This can result in duplicate processing of messages. Make sure your processing is idempotent (i.e processing again won`t impact your systems)
        
    - Exactly once:
        * Can be achieved for Kafka => Kafka workflows using Kafka Streams API
        * For Kafka => External System workflows, use an idempotent consumer.

Bottom line: 
- For most applications you should use “At least once” processing and ensure your transformations / processing are idempotent.

   
* Kafka Broker Discovery
    - Every Kafka broker is also called a "bootstrap server"
    - That means that you only need to connect to one broker, and you will be connected to the entire cluster.
    - Each broker knows about all brokers, topics and partitions (metadata)

* Zookeeper
    - Zookeeper manages brokers (keeps a list of them)
    - Zookeeper helps in performing leader election for partitions
    - Zookeeper sends notifications to Kafka in case of changes (e.g. new topic, broker dies, broker comes up, delete topics, etc...)
    - Kafka can`t work without Zookeeper
    - Zookeeper by design operates with an odd number of server (3,5,7)
    - Zookeeper has a leader (handle writes) the rest of the servers are followers (handle reads)
    - Zookeeper does not store consumer offsets with Kafka > v0.10

* Kafka Guarantees
    - Messages are appended to a topic-partition in the order they are sent
    - Consumers read messages in the order stored in a topic-partition
    - With a replication factor of N, producers and consumers can tolerate up to N-1 brokers being down
    - This is why a replication factor of 3 is a good idea:
        - Allows for one broker to be taken down for maintenance
        - Allows for another broker to be taken down unexpectedly 
    - As long as the number of partitions remains constant for a topic (no new partitions), the same key will always go to the same partitions
 
##Starting Kafka

* Install Kafka
    - https://kafka.apache.org/quickstart
    - Edit Zookeeper & Kafka configs using a text editor
        - zookeeper.properties => dataDir=/your/path/to/data/zookeeper
        - server.properties => log.dirs=/your/path/to/data/kafka
* Start
    - zookeeper-server-start.sh config/zookeeper.properties
    - kafka-server-start.sh config/server.properties

* Command Line Interface

kafka-topics.sh

    - kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --create --partitions 3 --replication-factor 1
    - kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --describe
    - kafka-topics.sh --zookeeper 127.0.0.1:2181 --list
    - kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --delete

Notes:
- You cannot create the topic with a replication factor greater than the number of brokers you have

kafka-console-producer.sh

    - kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic first_topic
    - kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic first_topic --producer-property acks=all    
    
Notes:
- If I produce to a topic that does not exist, by default I will see a WARNING and Kafka will auto create the topic.
- It is good practice to create and configure the topic before, but it is possible to create through the producer, however, it is not indicated.
- When a topic is created automatically, the number of partitions and the replication factor are defined through the settings num.partitions and default.replication.factor in the file config/server.properties.

kafka-console-consumer.sh

    - kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic
    - kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic --from-beginning
    - kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic --group my-first-application
    - kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic --group my-second-first-application --from-beginning (2x)    
    (the offsets have been committed in Kafka, so it says, “okay, my-second-application has read all the messages until message number XX. 
    So now you only will gonna read the new message from message XX.”

kafka-consumer-groups.sh

    - kafka-consumer-groups.sh --bootstrap-server 127.0.0.1:9092 --list
    - kafka-consumer-groups.sh --bootstrap-server 127.0.0.1:9092 --describe --group my-first-application
    
Notes:
- The order of the messages in this consumer is not “total”, the order is per partition. Because “first_topic” was created with 3 partitions, we saw that the order is only guaranteed at the partition level. If you try with a topic with 1 partition, you will see total ordering.
- Run more than one consumer in the same group and check the receipt of messages according to the functioning of the consumer group.     
- kafka-console-consumer use a random group id. Try running kafka-consumer-groups --list to see!
- I should override the group.id for kafka-console-consumer using -—group.
- Lag represents how far the Consumer Group application is behind the producers.
- Kafka generic id: topic + partition offset() => going to be unique, but you probably have a more semantic id 

Resetting Offsets:

    - kafka-consumer-groups.sh --bootstrap-server 127.0.0.1:9092 --group my-first-application --reset-offsets --to-earliest --execute --topic first_topic
    - kafka-consumer-groups.sh --bootstrap-server 127.0.0.1:9092 --describe --group my-first-application	
    - kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic --group my-first-application
    - kafka-consumer-groups.sh --bootstrap-server 127.0.0.1:9092 --describe --group my-first-application	
    - kafka-consumer-groups.sh --bootstrap-server 127.0.0.1:9092 --group my-first-application --reset-offsets --shift-by 2 --execute --topic first_topic

CLI Options that are good to know
    
Producer with keys

    - kafka-console-producer --broker-list 127.0.0.1:9092 --topic first_topic --property parse.key=true --property key.separator=,
        > key,value
        >another key,another value

Consumer with keys

    - kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic first_topic --from-beginning --property print.key=true --property key.separator=,

See later: 
    https://www.conduktor.io/
    https://medium.com/@coderunner/debugging-with-kafkacat-df7851d21968
    


    
    
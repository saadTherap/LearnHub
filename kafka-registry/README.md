### Build jar
```java
gradle clean jar publishToMavenLocal
```

### Add Dependencies
```gradle
implementation 'net.therap:kafka-registry:0.0.1-SNAPSHOT'
```

or 

```gradle
implementation project(':kafka-registry')
```

### Add This To application.propertiesq
```java
spring.kafka.client-id=topic-registrar-test
spring.kafka.bootstrap-servers=broker-1:9092,broker-2:9094,broker-3:9096
```

### Add Host in /etc/hosts
```java
172.16.2.114      broker-1
172.16.2.114      broker-2
172.16.2.114      broker-3
```

### Scan Component
```java
@SpringBootApplication(scanBasePackages = {
    "net.therap.kafkaregistrytest",
    "net.therap.kafkaregistry.service"  // add this
})
```


### To Send a Message
```java
@Autowired
private ProducerConsumerTask producerConsumerTask;

producerConsumerTask.send("your-topic-name", <your_object>);

```

### Receive a message
```java
@KafkaListener( 
    topics = "your-topic-name",
    groupId = "your-group-name"
)
public void listen(String json) {
    EnrollmentNotification enrollmentNotification = producerConsumerTask.deserialize(json, EnrollmentNotification.class);
    System.out.println("Enrollment notification: " + enrollmentNotification);
}
```

### Creating a Topic

```java
@Autowired
    private KafkaTopicRegistrar  kafkaTopicRegistrar;

@Override
public void run(String... args) throws Exception {
    kafkaTopicRegistrar.registerTopic("my-topic", 10, (short) 3);
    ...
}
```
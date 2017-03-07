# convoy

## Questions
* How will it be tested? (Please include any tests you have written)
The Dropwizard framework supports unit test and integration test. And I would also add stress test on top of those.
* How will it handle real world input?
Current inputs schemas are clearly dummy but since serialization and deserialization and some sanitation is already handled by the Jackson package included in the Dropwizard framework, to handle real world input we just need to do a little more checks. 
* How does it behave under concurrent load?
Currently everything is within db transactions so there will be no error but will be slow when load is heavy. However, the even driven pattern is really clear and all we need is an event broker like Kafka or AWS Kenisis and this project can scale really large.
* Can the code be easily extended to become v1 of the convoy API?
Yes. Because of the event driven pattern, the API can be very easily extended. You can even just specify new event types and use existing API and write specific handlers for those events.

## Additional Questions
Please write a paragraph or two for each of the following questions.

* What persistence solution did you choose and why?
I used MySQL. The event tables need to have incremental id for the events and also in future optimistic locking. The materialized tables are actually better to be saved in MongoDB but I'm saving it also in MySQL now for simplicity.
* What are some other ways you might score a driver?
Distance to pick up location (from current location or last drop off location) should be a better score for a driver. One more reason to save the materialized view in MongoDB which supports geo location distance calculation.
* What do you think are the best features to implement? When and why?
The best feature should be the one with the highest cost/benefit ratio. The cost are human resources to gather requirements, design, implement and later maintain, plus infrastructure cost. The benefit may be direct contract and financial benefit for company or better experience for users.
* What would you add/change for a real-world v1 of this system?
The event broker

## Dependency
A mysql database server at localhost with a database named convoy, username convoy, password convoy

## How to start the convoy application
---

1. Run `mvn clean install` to build your application
1. Create databases with `java -jar target/convoy-1.0-SNAPSHOT.jar db migrate config.yml`
1. Start application with `java -jar target/convoy-1.0-SNAPSHOT.jar server config.yml`
1. To check that your application is running enter url `http://localhost:8080`

Alternatively you can open it in IntelliJ and have these 2 run configurations
![db](https://cloud.githubusercontent.com/assets/1072766/23655400/b28cca30-02e9-11e7-9ace-d5900680ee8e.jpg)
![service](https://cloud.githubusercontent.com/assets/1072766/23655402/b29f6866-02e9-11e7-995f-ca7e9d571af6.jpg)



Health Check
---

To see your applications health enter url `http://localhost:8081/healthcheck`

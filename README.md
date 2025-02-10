
# What does this do
The tool creates X accounts, and generates Y random transactions against these accounts. See `application.yaml` for config details.


A transaction is 

```json
{"transactionId":"32399c82-41c5-4edc-b81a-1758e849e014","transactionType":"DEBIT","iban":"DE123456785068416","amount":1032.14,"currency":"JPY","remittenceInfo":"raspberry raspberry kiwi"}
```

It uses Spring Integration to achieve this. 

Input channel is subscribed by 3 consumers
* console logger
* file logger
* gcp pubsub sink

See `spring-integration-config.xml` to toggle subscribers on an off.

# How to test without PubSub

`mvn spring-boot:run` will run without GCP publishing.

# How to run with PubSub

## Create PubSub topic
```bash
gcloud services enable batch.googleapis.com pubsub.googleapis.com artifactregistry.googleapis.com
gcloud pubsub topics create transactions
```

## Get the IAM service account access token

Create a Service Account in Google Cloud Console


Google Cloud IAM & Admin.
Select Your Project (or create a new one if needed).
Click "Create Service Account".
Enter a Name & Description (e.g., pubsub-service-account).
Click "Create and Continue".


Assign Pub/Sub Permissions.
Under Grant access, choose:
Role: Pub/Sub Publisher (to allow publishing messages).
(Optional) Add Pub/Sub Subscriber if you also need to receive messages.
Click "Continue".
Click "Done".


Generate a JSON Key.
Find the new service account in the Service Accounts list.
Click on it and go to the "Keys" tab.
Click "Add Key" → "Create new key".
Select "JSON", then click "Create".
The JSON key file will be downloaded to your computer (save it as ./service-account-key.json).

## Activate the GcpPublisher

Uncomment

```java
// @Configuration
public class GcpPublisher {
```

Edit `application.yaml`
```yaml
spring:
...
  cloud:
    gcp:
      pubsub:
        enabled: true
        project-id: YOURPROJECT
        credentials:
          location: file:service-account-key.json
```

`mvn spring-boot:run` will now run WITH GCP publishing.

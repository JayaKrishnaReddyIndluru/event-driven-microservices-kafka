Event-Driven Microservices with Kafka

A real-world event-driven microservices system built using Spring Boot, Apache Kafka (KRaft mode), MySQL, and Spring Security.

This project demonstrates:

Asynchronous communication

Loose coupling between services

Event choreography using Kafka

Architecture Overview
UserService
   │
   └──▶ Kafka (user_created)
             ├──▶ WalletService
             ├──▶ NotificationService
             └──▶ TransactionService


TransactionService
   │
   └──▶ Kafka (transaction_initiated)
             ├──▶ WalletService
             └──▶ NotificationService


WalletService
   │
   └──▶ Kafka (transaction_updated)
             └──▶ TransactionService

Tech Stack

Java 21

Spring Boot

Spring Security (Basic Authentication)

Apache Kafka (KRaft Mode – No Zookeeper)

Spring Kafka

MySQL

Gradle

Postman

Microservices
UserService

Role: Producer

Responsibilities

Create users

Persist users in MySQL

Publish user_created events to Kafka

Handle authentication (phone-number-based login)

Endpoints

POST /user
GET  /user?phoneNo=XXXXXXXXXX

TransactionService

Role: Producer + Consumer

As Producer

Create transactions

Publish transaction_initiated events

As Consumer

Consume transaction_updated events

Update transaction status asynchronously

Endpoints

POST /transaction
GET  /transaction/all?pageNo=0&limit=10

WalletService

Role: Kafka Consumer

Consumes

user_created

transaction_initiated

Responsibilities

Initialize wallet for new users

Debit / credit wallet during transactions

Publish transaction_updated event after processing

NotificationService

Role: Kafka Consumer

Consumes

user_created

transaction_initiated

Responsibilities

Send notifications (log / email / SMS – extendable)

Uses a separate consumer group to receive all events independently

Kafka Topics
Topic Name	Produced By	Consumed By
user_created	UserService	WalletService, NotificationService
transaction_initiated	TransactionService	WalletService, NotificationService
transaction_updated	WalletService	TransactionService
Security & Authentication

Spring Security with Basic Authentication

Username = Phone Number

Password = Original plain password

Passwords stored using BCrypt

Authorization enforced via authorities

Example

Username: 9123456789
Password: Password@456

Database

MySQL

Hibernate ORM

Constraints

Email → Unique

Phone Number → Unique

Recommendation

Use separate schemas per microservice

Kafka Setup (Windows – KRaft Mode)
Format Kafka Storage (One-time)
bin\windows\kafka-storage.bat format --standalone --cluster-id <CLUSTER_ID> --config config\controller.properties

Start Kafka Controller
bin\windows\kafka-server-start.bat config\controller.properties

Start Kafka Broker (New Terminal)
bin\windows\kafka-server-start.bat config\broker.properties


Kafka runs on

localhost:9092

How to Run the System (IMPORTANT ORDER)
Infrastructure

Kafka (Controller + Broker)

MySQL

Producer / Hybrid Services

UserService

TransactionService

Consumer Services

WalletService

NotificationService

⚠️ Every Kafka consumer must be started explicitly.
Kafka does NOT auto-run consumers.

API Testing (Postman)
Create User

POST /user

{
  "name": "Rahul Verma",
  "email": "rahul.verma01@test.com",
  "phoneNo": "9123456789",
  "password": "Password@456",
  "userIdentificationType": "AADHAAR",
  "userIdentificationValue": "567856785678"
}

Initiate Transaction

POST /transaction

{
  "receiverPhoneNo": "9876543210",
  "amount": 500.00,
  "message": "Dinner payment"
}


🔐 Requires Basic Auth

Get Transactions (Paginated)

GET /transaction/all

?pageNo=0&limit=10


🔐 Requires Basic Auth

Kafka Consumer Verification

Successful startup logs

wallet-group: partitions assigned: [transaction_initiated-0]
notification-group: partitions assigned: [user_created-0]
transaction-service: partitions assigned: [transaction_updated-0]


This confirms

Kafka is healthy

Consumer groups are active

Event flow is working

# Systemarchitecture
This repository is a collection of different Systemarchitectures lectured at the FHV in summer semester 2022. It consists of:
* Lab Exercise **[Command Query Responsibility Segregation](#CQRS) + [Event Sourcing](#ES) for a [hotel management software](#Lab1)**
* Lab Exercise **Actors for an automated home**
* Lab Exercise **Pipes & Filters Utah Teapot**
* Lab Exercise Game **2D Pool with Java Physics Engine and JavaFX** (Maybe a game engine too)

<a name="CQRS"/>

## Command Query Responsibility Segregation (CQRS) 
### Why CQRS is relevant
#### Recap
We already learned the hexagonal architecture with its four layers, being:
* Presentation Layer
* Application Layer
* Domain Layer
* Infrastructure Layer
While this separation of layers is very helpful for coupling, cohesion and defining your core domain, it has some drawbacks. 

#### Good Use 
Create Example: Think about a 4-layer hotel reservation system. Whenever you want to create a booking, you have to walk through all of the layers. 
- You send a request to the presentation layer 
- -> the presentation layer calls the use case on application layer 
- -> the application layer calls action on domain entities 
- -> they enforce business rules 
- -> then they get persistet by the infrastructure layer. 
While an Create, Update or Delete use case makes sense, it doesn't fit the need for Read use cases, which will occur more frequently

#### Problematic Use
Read example: You want to know the available rooms in a specific time span 
- You send a request to the presentation layer  
- -> the presentation layer calls the application layer use call 
- -> the application layer gets the required domain objects from the infrastructure layer 
- -> either by a complicated - underperforming query (due to many joins or similar) or by enforcing many domain rules 
- -> once the application layer got all the necessary data, it must convert it to DTOs to hand it over to the presentation layer. 
This call for available rooms will occur many times. In a large application, this could cause performance issues and in some way even violate the DRY (Don't Repeat Yourself) principle.

#### Conclusion
Therefore we separate our _write_ and our _read_ actions. To do so, we want to apply these bulletpoints:
* On _write_ operations (Create, Update, Delete) 
  * We want to ensure our 4-layer-architecture. 
  * We want our domain-based database
* On _read_ operations 
  * We don't want to go through many layers to get our data
  * We want less database joins and data that fits our read-request (DTO-like structure)

<img src="https://user-images.githubusercontent.com/86053522/160587167-445f1ce4-a27e-461d-9337-ee6d999c9104.png" width="400" height="400" />

> Illustration of a CQRS Architecture. (Remote Facade is equal to Presentation Layer here)

- - - -
<a name="ES"/>

## Event-Sourcing (ES)
### Event-Sourcing relevance with CQRS
With the rise of the agile manifesto, we also want to have a flexible way of persisting our data. In combination with CQRS it can be used to _Project_ our Domain (_Write_) Database to our Denormalized (_Read_) Database. This can be done, by publishing Events to a **Service Bus**. This Bus acts as a Event Storage (And Log) and the subscribers in the _Projection_ Layer get triggered to modify the Denormalized Database. In the sense of DDD, this ensures **Eventual Consistency** and decouples Aggregates and Bounded Contexts.

<img src="https://user-images.githubusercontent.com/86053522/160591339-f383a5eb-b910-4c63-b06e-43372fb22dc6.png" width="400" height="400" />

> A system using CQRS and ES architecture example

- - - -

<a name="Lab1"/>

## Hotel Management with CQRS + ES

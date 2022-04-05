# Systemarchitecture
This repository is a collection of different Systemarchitectures lectured at the FHV in summer semester 2022. It consists of:
* Lab Exercise **[Command Query Responsibility Segregation](#CQRS) + [Event Sourcing](#ES) for a [Hotel Management Software](#Lab1)**
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
With the rise of the agile manifesto, we also want to have a flexible way of persisting our data. In combination with CQRS it can be used to _project_ our Domain (_Write_) Database to our Denormalized (_Read_) Database. This can be done, by publishing Events to a **Service Bus**. This Bus acts as a Event Storage (And Log) and the subscribers in the _Projection_ Layer get triggered to modify the Denormalized Database. In the sense of DDD, this ensures **Eventual Consistency** and decouples Aggregates and Bounded Contexts.

<img src="https://user-images.githubusercontent.com/86053522/160591339-f383a5eb-b910-4c63-b06e-43372fb22dc6.png" width="400" height="400" />

> A system using CQRS and ES architecture example
 
- - - -

<a name="Lab1"/>

## Hotel Management with CQRS + ES (Lab1Template)
### Project structure and communication

To start off, we divide our CQRS project into 3 subprojects (we will do this by dividing into different `packages` in Java).
This is demonstrated in the image below:

* The red section is our `Write` side. This section will contain our core Domain and is implemented in the typical DDD style with its 4 layers.
* The green section is our `Event` side. This section is used as a Publish-Subscribe Service Bus, to transfer events from the `Write` side to our `Read`side.
* The blue section is our `Read` side. This section holds the data in a structure that fits the style of the query (think of it like DTOs). It is also responsible for changing the `Read` data in the `projection` layer to adapt to changes that occured in the `Write` layer.

<img src="https://user-images.githubusercontent.com/86053522/161307173-8f24dc8d-6e8e-4ab5-8dc9-e4cff0c5d0a1.png" width="400" height="400" />

> CQRS divided into subprojects (Write - Event - Read)

The 3 subprojects communicate with each other by REST interfaces. each of them have a `RestController` which will use Spring Annotations to map incoming HTTP (GET, POST) Requests. The `WriteRestController` only has POST mappings that are used to modify domain objects via use cases (create a booking, cancel a booking) by sending `Commands`. `Commands` are like DTOs, but they get sent **from** the presentation layer **to** the application layer. They contain the information needed to complete an use case (e.g. create a booking). At the end of a successful use case, an `Event` is triggered, which will send an `Event` Object to the `EventRestController` (**Publish**)

The `EventRestController` only has POST mappings to receive the different kinds of `Event`. After the `Event` got processed, all the **Subscribers** of the specified `Event` are notified by sending the relevant `Event` to the `Read` side.

The `ReadRestController` has POST mappings to receive the `Event` and process it in the projection layer. It also has GET mappings for the end user to get their desired information (free rooms in a time span, bookings in a time span).

### Where to start
To start off, we start with an independent side. This could be anything but the `projection` layer, because this needs to have the finished `Event`s and also the finished `ReadRepository` to modify it. In this project, we started with the `Write` site and build our way Bottom-Up. We started with modelling our core domain. The domain consists of Bookings, Rooms and Customers, while a Booking must have one customer and at least one room assigned to it. To store our domain objects, we used Sets, Lists and Maps. No database was required in this project.

<img src="https://user-images.githubusercontent.com/86053522/161337899-4f29ac9c-22de-4a13-addb-9dc76ff3f532.png" width="600" height="400" />

> Class model of our core domain

After implementing the `Write` side, we recommend to implement the `Read` side. This makes you think about the Structure of the data that you want to offer to your end user. Therefore, we design our Repository to fit the customer needs. The idea is, that we have as little joins as possible. Ideally, we structure our data in the way we would structure our DTOs to send to the customer. So if we want to give the available rooms in a time span, we would create a `FreeRoom` DTO and safe them in a list to easily iterate over it, instead of doing complex queries to find free rooms.

<img src="https://user-images.githubusercontent.com/86053522/161347051-dc1e81fe-be67-423e-835a-2e9fa9e2af55.png" width="600" height="400" />

> Class model of read objects

To implement the `Event` side, you have to think about what data your `Write` side has to send to your `Read` side to ensure that the read domain can modify its data to fit the new state of the core domain. Once you designed your `Event`s and their content, you just need a controller to receive `Event`s, a list of `Event`s to store all the occurings in the application and send them to the subscribers controller (in this project we statically added them to a subscriber list). It is also advised to have a parent class of `Event` to have a unified way of storing, receiving and sending them, if you wish to do so.

<img src="https://user-images.githubusercontent.com/86053522/161353348-55cf354d-9e85-4674-8d00-9d74beeee886.png" width="600" height="400" />

> Class model of events

### Write side sequence (creating a booking)

The `Write` side is responsible for modifying the core domain. On successful use case, an `Event` is triggered and published to our `Event`side. While the implementation of this side usually follows the DDD rules, we did not focus on this section, therefore the classes are not implemented in Good Practices. To explain the process of the `Write` side, take a look at these steps for creating a booking: 

1. The end user fires off a HTTP POST request to the `WriteRestController` with the Port `8081`
2. The `WriteRestController` takes the body parameters and creates a `CreateBookingCommand`
3. The `WriteRestController` calls the application layer method `book(CreateBookingCommand command)` with the `CreateBookingCommand`
4. The `BookingService` then performs a data check, creates the domain objects and calls the `BookingRepository` with the `createBooking(Booking booking)` to persist the booking
5. After the domain actions for the create booking use case have been carried out, the application layer creates a `BookingCreatedEvent` and sends it to the `EventPublisher`
6. Every `Event` in the `EventPublisher` is sent to the `EventRestController` with the Port `8083`
 
### Event side sequence (booking is created)

The `Event` side is used to log events. This has several advantages. First off, it can be used for eventual consistancy and to send messages between aggregates. It is also useful for logging what happened in our application. And last but not least, if we apply all events on an inital set of data on our core domain, we will end up with a valid state. So therefore, the logic in the `Event` side is not that complex:

1. The `Write` side `EventPublisher` sends a HTTP POST request, containing the `BookingCreatedEvent` to the `EventRestController` with the Port `8083`
2. The `EventRestController` calls the `EventRepository` method `process(Event event)` 
3. The `EventRepository` adds the `BookingCreatedEvent` to a list of `Event`s and informs the subscribers of the `BookingCreatedEvent` about the changes by sending a HTTP POST request, containing the `BookingCreatedEvent` to the `ReadRestController` with the Port `8082`

### Read side sequence (projection of core domain to read domain)

The `Read` sides job is to structure data in a way that can easily be queried to give the user the desired data. Therefore we need a logic that transforms our core domain to our read domain. This is where our `projection` layer comes into use. Its job is to transform the `Event` that origined from the core domain use case and modify the read domain data in a way, that it takes the core domain changes into consideration, but also still give the user the desired feedback. For example, if we want to offer the free rooms in a time span, we would offer `FreeRoom` read objects (DTOs) that will be sent to the end user. If we create a booking, we have to adapt our `FreeRoom`s in a way, that the newly created booking is considered. E.g. delete a `FreeRoom` and create `FreeRoom`s before the bookings arrival date and after the bookings departure date. This process does depend on the DTOs offered in your read domain and the `Event`s that you create and send. Thus, I will not explain the logic in our `Projection` layer in detail. The process for the `Read` side looks like this:

1. The `EventRepository` sends a HTTP POST requests, containing the `BookingCreatedEvent` to the `ReadRestController` with the Port `8082`
2. The `ReadRestController` calls the `Projection` method `processIncomingBookingCreatedEvent(BookingCreatedEvent event)` to start the projection
3. The method modifies the data in the `FreeRoomRepository` so that the data takes the new booking into consideration
4. The end user sends a HTTP GET request to the `ReadRestController` to find the free rooms in a time span
5. The `ReadRestController` calls the `FreeRoomRepository` method `getAvailableRooms(Localdate from, Localdate to)` directly, to get the desired data (because we designed our domain as DTOs, we can respond with the domain objects immediately)

### Starting our project

To start the project, make sure to start all 3 `Main` classes (`WriteSide`, `EventSide` and `ReadSide`) in the `Lab1Template/src/main/java/at/fhv/lab1reference/` folder. You can see and use all the REST interfaces in the Swagger-UI `http://localhost:808x/swagger-ui/index.html`.

- - - -

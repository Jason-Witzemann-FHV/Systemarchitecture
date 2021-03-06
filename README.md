# Systemarchitecture
This repository is a collection of different Systemarchitectures lectured at the FHV in summer semester 2022. It consists of:
* Lab Exercise **[Command Query Responsibility Segregation](#CQRS) + [Event Sourcing](#ES) for a [Hotel Management Software](#Lab1)**
* Lab Exercise **[Actors](#Actor) for an [automated home](#Lab2)**
* Lab Exercise **[Pipes & Filters](#Paf) [Utah Teapot](#Lab3)**
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

After implementing the `Write` side, we recommend implementing the `Read` side. This makes you think about the Structure of the data that you want to offer to your end user. Therefore, we design our Repository to fit the customer needs. The idea is, that we have as little joins as possible. Ideally, we structure our data in the way we would structure our DTOs to send to the customer. So if we want to give the available rooms in a time span, we would create a `FreeRoom` DTO and safe them in a list to easily iterate over it, instead of doing complex queries to find free rooms.

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

To start the project, make sure to start all 3 `Main` classes (`WriteSide`, `EventSide` and `ReadSide`) in the `Lab1Template/src/main/java/at/fhv/lab1reference/` folder. You can see and use all the REST interfaces in the Swagger-UI `http://localhost:808x/swagger-ui/index.html` or if you have Node.js installed, you can go into the `Lab1Template/src/main/js` folder and type `npm run dev` (after using `npm install` once) to start a SPA on `http://localhost:8080` that can be accessed via your browser. 

<a name="Actor"/>

## Actor model

The actor model originates from the functional programming paradigm. It is designed to fit the needs for multi-core programming. The way we used to program has come to a bottleneck in performance due to us not utilizing the multi-core CPUs. With the actor model and the *Reactive Principles*, we can make use of this technology and build more scalable applications, but also increase the complexity by a lot.

The actor model is based on the *Reactive Principles*

* Message Driven - the messages we send between actors are immutable. This ensures no-shared data and prevents racing conditions from happening.
* Resilient - a actor system is very complex in the way of handling messages and their respsones. It offers a lot more room for resilient errors due to the network component that is added.
* Elastic - actors that have nothing to do will not occupy any CPU performance. Only active actors use performance.
* Responsive - a responsive system must respond in a fast and consistent way. Thus, because actors are originaly based off the functional programming paradigm, we can ensure the same behavior under same conditions.

### What is an actor

| Java Class | Actor |
|-|-|
| Java Classes have a constructor to create an instance of the object. | Actors get created via a Builder, which creates a **Behavior** of the desired actor |
| Java Classes have an methods, which are called directly. | Actors also have methods, but we call them via **Messages**. We therefore create a methode (Receiver) that maps the **Message** to the corresponding method and calls it. |
| Java Classes have an internal state (Attributes). An instance of a class will only change its attributes, when domain methods (or in the simplest way Setters) are called | An actor has internal state (Attributes). During the lifecycle of an actor, it can change its Behavior, which can lead to a change in Attributes. Attributes can also be changed via methods, like Java Classes. |

### Problems of the actor model

The actor model is designed to work in a concurrent, distributed environment. This also brings the typical problems of such systems with it:

* How can I ensure that my message was received?
* How can I differ between a connection loss and long computing time?
* How much eventual consistency should I allow?
* ...

- - - -


<a name="Lab2" />

## Automated home 
### Structure and messages

To start off, we create a model on how we want to divide our actors (select guardians, sub-actors...) and how they interact with each other. We divided our actors into "devices", that act inside the house and "environment" that acts in itself outside the house. We also decided to do a separate hierarchy for the fridge as well. 
This is our hierarchy model with all the actors and there potential attributes:

<img src="https://user-images.githubusercontent.com/86053522/166518398-f9be4966-67c1-42bb-9a15-2bc85a9c0d01.png"/>

> Guardian hierarchy of home automation 

We also modeled the messages between the actors, and the corresponding interaction patterns. Here are a few examples: (A full file of all the messages can be found in the folder `Lab2Template/Actors_in_smart_home.drawio` and opened online via Draw.io)

<img src="https://user-images.githubusercontent.com/86053522/166518954-6bdc303e-06cc-43de-99b6-688620e543f4.png"/>

> Examples of different interaction patterns


### Where to start

We advise starting with actors that are not dependent on other actors. This can be the environmental actors (weather and temperature) or the fridge, which in itself is a closed place with own actors. We decided to start off with the environmental actors, so that each of us can implement an environmental actor and the opposite sensor. This helped in understanding the working of the akka framework and actors in general. After the initial actors, you can continue with actors that depend on your current implementation and make them communicate with each other.

Vanilla Java is not made for an actor model. This model origins from the functional programming paradigm, therefore the Java implementation of actors might seem very overloaded. Anyway, to implement an actor in Java (with the akka framework) we can follow these steps:

(example of Temperature Sensor `Lab2Template/src/main/java/at/fhv/sysarch/lab2/homeautomation/devices/TemperatureSensor.java`)
1. Create your class / actor `TemperatureSensor`, and an interface (`TemperatureSensorCommand`) inside your class. This interface is used for every `Message`, `Behavior` and `ActorRef` regarding your class.
2. Make the `TemperatureSensor` extend the `AbstractBehavior` with your interface (`AbstractBehavior<TemperatureSensor.TemperatureSensorCommand>`) 
3. Define the messages that your class will *receive*. Make it implement the interface of `step 1` and think about the data you need in the message. Remember, that messages should be immutable! We need a self-scheduled message, that triggers a polling-request to the `TemperatureEnvironment` and a  response message. Implement the self-scheduling and response message in this class!
4. Think about the attributes of your actor and create a constructor for it. Your constructor needs the actors attributes, a context and maybe other data like scheduled timers.
5. With the actor model, we work with `Behaviors`, therefore we can't just have a constructor. We also need a behavior factory (`create` method), that creates the behavior. This will later be used to `spawn` our actors, giving it the `context` in which it will be used.
6. We now need to implement the `createReceive` method. This method is inherited by the `AbstractBehavior` and is used to call the correct method, that should be invoked on a specified, incoming message. We add an `onMessage(DoCreateTemperatureRequest)` for the self-scheduled message and an `onMessage(ReceiveTemperatureResponse)` for the response.
7. Lastly, we implement the methods that will be called on a message receive. The method, that gets called by the self-scheduled message, will call the Actor of our `TemperatureEnvironment` by `tell`ing them a `Request` message. The other method will receive the current Temperature of our `TemperatureEnvironment` and send it to the `AirCondition` actor. Remember to always return a `Behavior`, so that ur actor stays alive.
8. After implementing the actor, we need to spawn it in the correct context via the `HomeAutomationController`. 

A full list of the messages and actors implemented in this project can be found in the `/Lab2Template/src/main/resources/model.drawio` file.

### Commands to use

Start the application via the main function of the `/Lab2Template/src/main/java/at/fhv/sysarch/lab2/HomeAutomationSystem.java` class

To get more infos about the commands, just type `temp`, `media` or `fridge`. Here are the different commands:

* Temperature:
    * temp set [value]
    * temp auto

* Mediastation:
    * media on [name of movie]
    * media off

* Fridge:
    * fridge content
    * fridge history
    * fridge order [name_of_order]
    * fridge consume [name_of_order]

* Orders:
    * apple
    * cheese
    * sausage
    * steak
    * carrots
    * beer

- - - -


<a name="Paf" />

<a name="Lab3" />

## Utah Teapot
### Where to start

We have to implement a *Push pipeline* and a *Pull pipeline*, therefore choose one of them to begin with. In this example, we started with the Pull pipeline. The following section will demonstrate how to implement a Pull pipeline, the Push pipeline will be covered later in the read.me.
Now think about what your pipeline has to do. In a Pull pipeline, each pipe and filter has to **pull** the data from a **source**. Therefore, we decided to create an interface and an abstract class.

| | What we implemented | Why we did it |
|-|-|-|
| Interface | A method `public T pull()`. T is the generic type of what the element (Filter or Pipe) *outputs*. We also have a `public boolean hasNext()` method. | The `pull()` method will be called by the successor to get the output of the element. Sometimes (e.g. in DepthSorting) we need to have all previous Faces to sort them. This is why we also implemented the `hasNext()` method. |
| Abstract class | Implements the interface with generic `O` and also has a `source` with generic `I`. The standard implementation for `hasNext()` is `source.hasNext()` | Each pipeline element has to pull the predecessor's element (Face, Pair). Therefore, each pipeline element has to have at least a source, that is given in the constructor. The generic `I` stands for the input into the element, which is the output of its predecessor. And also each element has to output another element, which is displayed with the generic `O`. | 

By now our abstract class has all the common information it needs to start implementing our Pipes and Filters. 
Because this project doesn't need any pipe logic, we created a general, generic pipe class with an implemented `pull()` logic of `source.pull()`. Because it is a pipeline, where data will not be transformed, our input and output elements are of the same type. 

After our interface, abstract class and pipe, we can start with our filter logic.

1. We start off with our data source, as this is not a common filter, we have to change our `source` property to `null` and change our pull logic, as it is not pulling from its predecessor (because there is none).
2. We then continue with the sink (aka Renderer), as this is the end of our pipeline, which will draw our Utah teapot. We have to implement a render logic and overwrite our `pull()` method.
3. Now we gradually implement our filters, connect them with a pipe and view the progress by starting our application. With each filter, our Utah teapot comes a step closer to completion

We will not cover the required filters or rendering logic behind the pipeline, but we provide a class diagram of our pipeline elements and how they relate to each other.

<img src="https://user-images.githubusercontent.com/86053522/170132940-8765eefa-5234-4628-bf61-90e49f9bdb6b.png"/>

> pull pipeline classes

We then connect the filters with a pipe to match the instructions workflow like this:

<img src="https://user-images.githubusercontent.com/86053522/170127256-c2296b82-983d-4234-91dd-65b3bdd45bef.png"/>

> Lab exercise instructions

By now, you should have a grasp of how the pipeline works. Implementing a second pipeline is rather simple, with the same approach. We have a *Push pipeline* which *pushes* elements to its *successor*. We therefore, again, implemented an interface and an abstract class

| | What we implemented | Why we did it |
|-|-|-|
| Interface | A method `public void push(E element)`. `E` is the generic type of what the element that is pushed into the class is based on (aka the *Input*) | We don't need a `hasNext()` logic in this pipeline, because we actively push our elements to the successor. |
| Abstract class | Implements the interface with generic `T` and also has a `source` with generic `N`. `T` stands for the type of element that is used in **T**his class, `N` is the type of element that is used in the **N**ext part of the pipeline. | Each pipeline element has to push to the successor. Therefore, each pipeline element has to have at least a successor, that is given in the constructor. | 

By now our abstract class has all the common information it needs to start implementing our Pipes and Filters. 
Because this project doesn't need any pipe logic, we, again, created a general, generic pipe class with an implemented `push(E element)` logic of `successor.push(element)`. Because it is a pipeline, where data will not be transformed, our input and output elements are of the same type. 


After our interface, abstract class and pipe, we can start with our filter logic.

0. We have to indicate some filters that there are no more elements being pushed (e.g. for DepthSorting). We could do this by a `hasNext()` logic, but we decided it would be easier to add a custom Face with specific values, that acts as a Flag-Face. We add this specific Face to the source class
1. The source class is the root of our data. Other than the specific value that is added in `0.`, we don't have a specific logic here
2. The sink is interesting in the push pipeline, as it has no successor. Therefore we construct it with `successor = null`. The `push()` methods does not work here, as this is the place where we render our faces. 
3. Now we gradually implement our filters, connect them with a pipe and view the progress by starting our application. With each filter, our Utah teapot comes a step closer to completion

The push pipeline can be a bit more challenging (at least if you follow our guide). You can see in the code, that our pipeline is somewhat upside-down. This is, because we have to create the sink so that we can pass it to its predecessor as a successor.

<img src="https://user-images.githubusercontent.com/86053522/170132785-40d3d60b-6ca5-4632-9da9-84620d9dbe08.png"/>

> push pipeline classes

An overview of what filters you need in this exercises is displayed in the description of the pull pipeline.

### How to start the project

You can start this project by either running `gradle run` in the `Lab3Template` folder or by starting the `main` method in the `Lab3Template/src/main/java/at/fhv/sysarch/lab3/StartProject.java`class.
If the application does not fit your screen size, you can adjust it by adjusting the values of `VIEW_WIDTH` and / or `VIEW_HEIGHT` field in the `Lab3Template/src/main/java/at/fhv/sysarch/lab3/Main.java` class.
You can swap between the **Push* and **Pull** pipeline by setting the `USE_PUSH_PIPELINE` flag `true` or `false` 

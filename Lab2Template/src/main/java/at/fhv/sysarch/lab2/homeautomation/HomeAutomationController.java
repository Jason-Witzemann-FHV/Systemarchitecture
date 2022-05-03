package at.fhv.sysarch.lab2.homeautomation;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.devices.TemperatureSensor;
import at.fhv.sysarch.lab2.homeautomation.outside.TemperatureEnvironment;
import at.fhv.sysarch.lab2.homeautomation.outside.WeatherEnvironment;
import at.fhv.sysarch.lab2.homeautomation.shared.Temperature;

public class HomeAutomationController extends AbstractBehavior<Void>{

    public static Behavior<Void> create() {
        return Behaviors.setup(HomeAutomationController::new);
    }

    private  HomeAutomationController(ActorContext<Void> context) {
        super(context);

        // environment
        ActorSystem<WeatherEnvironment.WeatherEnvironmentCommand> weatherEnv = ActorSystem.create(WeatherEnvironment.create(), "WetterGott");
        ActorSystem<TemperatureEnvironment.TemperatureEnvironmentCommand> temperatureEnvironment = ActorSystem.create(TemperatureEnvironment.create(new Temperature("Celcius", 22)), "TemperatureEnvironment");

        // sensors
        ActorSystem<TemperatureSensor.TemperatureSensorCommand> temperatureSensor = ActorSystem.create(TemperatureSensor.create(temperatureEnvironment), "TemperatureSensor");

        // devices

        getContext().getLog().info("HomeAutomation Application started");
    }

    @Override
    public Receive<Void> createReceive() {
        return newReceiveBuilder().onSignal(PostStop.class, signal -> onPostStop()).build();
    }

    private HomeAutomationController onPostStop() {
        getContext().getLog().info("HomeAutomation Application stopped");
        return this;
    }
}

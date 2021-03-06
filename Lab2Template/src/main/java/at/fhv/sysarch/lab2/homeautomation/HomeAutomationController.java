package at.fhv.sysarch.lab2.homeautomation;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.devices.*;
import at.fhv.sysarch.lab2.homeautomation.fridge.Fridge;
import at.fhv.sysarch.lab2.homeautomation.outside.TemperatureEnvironment;
import at.fhv.sysarch.lab2.homeautomation.outside.WeatherEnvironment;
import at.fhv.sysarch.lab2.homeautomation.shared.Temperature;
import at.fhv.sysarch.lab2.homeautomation.ui.UI;
import at.fhv.sysarch.lab2.homeautomation.shared.Weather;


public class HomeAutomationController extends AbstractBehavior<Void>{

    public static Behavior<Void> create() {
        return Behaviors.setup(HomeAutomationController::new);
    }

    private  HomeAutomationController(ActorContext<Void> context) {
        super(context);


        // environment
        ActorRef<WeatherEnvironment.WeatherEnvironmentCommand> weatherEnvironment = getContext().spawn(WeatherEnvironment.create(Weather.SUNNY), "WetterGott");
        ActorRef<TemperatureEnvironment.TemperatureEnvironmentCommand> temperatureEnvironment = getContext().spawn(TemperatureEnvironment.create(new Temperature("Celcius", 21)), "TemperatureEnvironment");
        // devices
        ActorRef<Blinds.BlindsCommand> blinds = getContext().spawn(Blinds.create(), "Blinds");
        ActorRef<MediaStation.MediaStationCommand> mediaStation = getContext().spawn(MediaStation.create(blinds), "MediaStation");
        ActorRef<AirCondition.AirConditionCommand> airCondition = getContext().spawn(AirCondition.create(), "AirCondition");
        ActorRef<TemperatureSensor.TemperatureSensorCommand> temperatureSensor = getContext().spawn(TemperatureSensor.create(temperatureEnvironment, airCondition), "TemperatureSensor");
        ActorRef<WeatherSensor.WeatherSensorCommand> weatherSensor = getContext().spawn(WeatherSensor.create(weatherEnvironment, blinds), "WeatherSensor");

        // fridge
        ActorRef<Fridge.FridgeCommand> fridge = getContext().spawn(Fridge.create(), "Fridge");

        // UI
        ActorRef<Void> ui = getContext().spawn(UI.create(mediaStation, fridge, temperatureEnvironment), "UI");

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

package at.fhv.sysarch.lab2.homeautomation.devices;

import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.*;
import akka.actor.typed.Behavior;
import at.fhv.sysarch.lab2.homeautomation.outside.WeatherEnvironment;
import at.fhv.sysarch.lab2.homeautomation.shared.Weather;

import java.time.Duration;

public class WeatherSensor extends AbstractBehavior<WeatherSensor.WeatherSensorCommand> {

    // messages
    public interface WeatherSensorCommand { }

    public static final class ReceiveWeatherResponse implements WeatherSensorCommand {
        Weather currentWeather;

        public ReceiveWeatherResponse(Weather currentWeather) {
            this.currentWeather = currentWeather;
        }
    }

    // self-scheduling message to send Weather Request
    public static final class ScheduleWeatherRequest implements WeatherSensorCommand { }

    // factory
    public static Behavior<WeatherSensorCommand> create(ActorRef<WeatherEnvironment.WeatherEnvironmentCommand> environment, ActorRef<Blinds.BlindsCommand> blinds) {
        return Behaviors.setup(context -> Behaviors.withTimers(timer -> new WeatherSensor(context, environment, blinds, timer)));
    }

    // attributes of class
    private ActorRef<WeatherEnvironment.WeatherEnvironmentCommand> environment;
    private ActorRef<Blinds.BlindsCommand> blinds;

    private Weather previousWeather = Weather.RAINY;

    // constructor
    public WeatherSensor(
            ActorContext<WeatherSensorCommand> context,
            ActorRef<WeatherEnvironment.WeatherEnvironmentCommand> environment,
            ActorRef<Blinds.BlindsCommand> blinds,
            TimerScheduler<WeatherSensorCommand> timer
    ) {
        super(context);
        this.environment = environment;
        this.blinds = blinds;
        timer.startTimerAtFixedRate(new ScheduleWeatherRequest(), Duration.ofSeconds(15));
    }

    @Override
    public Receive<WeatherSensorCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(ScheduleWeatherRequest.class, this::sendReadWeather)
                .onMessage(ReceiveWeatherResponse.class, this::receiveWeather)
                .build();
    }

    private Behavior<WeatherSensorCommand> sendReadWeather(ScheduleWeatherRequest scheduled) {
        environment.tell(new WeatherEnvironment.WeatherRequest(getContext().getSelf()));
        return this;
    }

    private Behavior<WeatherSensorCommand> receiveWeather(ReceiveWeatherResponse response) {
        var newWeather = response.currentWeather;
        if (newWeather != previousWeather)  {
            previousWeather = newWeather;
            blinds.tell(new Blinds.WeatherChangedCommand(newWeather == Weather.RAINY));
        }

        getContext().getLog().info("[SENSOR] measured Weather: " + response.currentWeather);
        return this;
    }
}

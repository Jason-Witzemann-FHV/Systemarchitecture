package at.fhv.sysarch.lab2.homeautomation.outside;


import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import at.fhv.sysarch.lab2.homeautomation.devices.WeatherSensor;
import at.fhv.sysarch.lab2.homeautomation.shared.Weather;

import java.time.Duration;

public class WeatherEnvironment extends AbstractBehavior<WeatherEnvironment.WeatherEnvironmentCommand> {

    // -- messages --
    public interface WeatherEnvironmentCommand { }

    public static final class WeatherUpdate implements WeatherEnvironmentCommand { }

    public static final class WeatherRequest implements WeatherEnvironmentCommand {
        ActorRef<WeatherSensor.WeatherSensorCommand> sender;

        public WeatherRequest(ActorRef<WeatherSensor.WeatherSensorCommand> sender) {
            this.sender = sender;
        }
    }

    // -- static behavior factory --
    public static Behavior<WeatherEnvironmentCommand> create(Weather weather) {
        return Behaviors.setup(context -> Behaviors.withTimers(timers -> new WeatherEnvironment(context, weather, timers)));
    }

    private Weather currentWeather;

    // -- actor
    public WeatherEnvironment(ActorContext<WeatherEnvironmentCommand> context, Weather weather, TimerScheduler<WeatherEnvironmentCommand> scheduler) {
        super(context);
        this.currentWeather = weather;

        getContext().getLog().info("And god said, there will be weather!");
        getContext().getLog().info("And he saw that it was good.");
        scheduler.startTimerAtFixedRate(new WeatherUpdate(), Duration.ofSeconds(30));
    }

    @Override
    public Receive<WeatherEnvironmentCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(WeatherUpdate.class, this::doWeatherChange)
                .onMessage(WeatherRequest.class, this::sendWeather)
                .build();
    }

    private Behavior<WeatherEnvironmentCommand> doWeatherChange(WeatherUpdate weatherUpdate) {
        currentWeather = Weather.random();
        getContext().getLog().info("[ENVIRONMENT] Weather set to " + currentWeather);
        return Behaviors.same();
    }

    private Behavior<WeatherEnvironmentCommand> sendWeather(WeatherRequest request) {
        request.sender.tell(new WeatherSensor.ReceiveWeatherResponse(currentWeather));
        return this;
    }
}

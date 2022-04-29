package at.fhv.sysarch.lab2.homeautomation.outside;


import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.time.Duration;

public class WeatherEnvironment extends AbstractBehavior<WeatherEnvironment.WeatherEnvironmentCommand> {

    // -- messages --
    public interface WeatherEnvironmentCommand { }

    public static final class WeatherUpdate implements WeatherEnvironmentCommand { }


    // -- static behavior factory --
    public static Behavior<WeatherEnvironmentCommand> create() {
        return Behaviors.setup(context -> Behaviors.withTimers(timers -> new WeatherEnvironment(context, timers)));
    }

    private Weather currentWeather;

    // -- actor
    public WeatherEnvironment(ActorContext<WeatherEnvironmentCommand> context, TimerScheduler<WeatherEnvironmentCommand> scheduler) {
        super(context);
        getContext().getLog().info("And god said, there will be weather!");
        getContext().getLog().info("And he saw that it was good.");
        scheduler.startTimerAtFixedRate(new WeatherUpdate(), Duration.ofSeconds(30));
    }

    @Override
    public Receive<WeatherEnvironmentCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(WeatherUpdate.class, this::doWeatherChange)
                .build();
    }

    private Behavior<WeatherEnvironmentCommand> doWeatherChange(WeatherUpdate weatherUpdate) {
        currentWeather = Weather.random();
        getContext().getLog().info("[ENVIRONMENT] Weather set to " + currentWeather);
        return Behaviors.same();
    }


}

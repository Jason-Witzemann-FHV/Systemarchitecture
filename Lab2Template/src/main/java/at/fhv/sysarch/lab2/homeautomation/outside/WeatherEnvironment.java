package at.fhv.sysarch.lab2.homeautomation.outside;


import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

/**
 * This class shows ONE way to switch behaviors in object-oriented style. Another approach is the use of static
 * methods for each behavior.
 *
 * The switching of behaviors is not strictly necessary for this example, but is rather used for demonstration
 * purpose only.
 *
 * For an example with functional-style please refer to: {@link https://doc.akka.io/docs/akka/current/typed/style-guide.html#functional-versus-object-oriented-style}
 *
 */
import java.time.Duration;

public class WeatherEnvironment extends AbstractBehavior<WeatherEnvironment.WeatherEnvironmentCommand> {

    // -- messages --
    public interface WeatherEnvironmentCommand { }

    public static final class WeatherUpdate implements WeatherEnvironmentCommand { }


    // -- static behavior factory --
    public static Behavior<WeatherEnvironmentCommand> create() {
        return Behaviors.setup(context -> Behaviors.withTimers(timers -> new WeatherEnvironment(context, timers)));
    }


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
        getContext().getLog().info("[ENVIRONMENT] Weather set to " + Weather.random());
        return Behaviors.same();
    }


}

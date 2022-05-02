package at.fhv.sysarch.lab2.homeautomation.devices;

import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.AbstractBehavior;
import at.fhv.sysarch.lab2.homeautomation.shared.Weather;

public class WeatherSensor extends AbstractBehavior<WeatherSensor.WeatherSensorCommand> {

    public interface WeatherSensorCommand {}

    public static final class WeatherRequest implements WeatherSensorCommand {
        ActorRef<WeatherSensorCommand> requester;

        public WeatherRequest(ActorRef<WeatherSensorCommand> requester) {
            this.requester = requester;
        }
    }

    public static final class WeatherResponse implements WeatherSensorCommand {
        Weather currentWeather;

        public WeatherResponse(Weather currentWeather) {
            this.currentWeather = currentWeather;
        }
    }
}

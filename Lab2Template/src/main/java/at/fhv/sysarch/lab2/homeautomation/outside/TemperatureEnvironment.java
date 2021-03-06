package at.fhv.sysarch.lab2.homeautomation.outside;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import at.fhv.sysarch.lab2.homeautomation.devices.TemperatureSensor;
import at.fhv.sysarch.lab2.homeautomation.shared.Temperature;

import java.time.Duration;
import java.util.Random;

public class TemperatureEnvironment extends AbstractBehavior<TemperatureEnvironment.TemperatureEnvironmentCommand> {

    public interface TemperatureEnvironmentCommand {}

    public static final class TemperatureAuto implements TemperatureEnvironmentCommand {

    }

    public static final class TemperatureManual implements TemperatureEnvironmentCommand {
        Temperature temperature;

        public TemperatureManual(Temperature temperature) {
            this.temperature = temperature;
        }
    }

    public static final class TemperatureUpdate implements TemperatureEnvironmentCommand {
        Temperature currentTemp;

        public TemperatureUpdate(Temperature currentTemp) {
            this.currentTemp = currentTemp;
        }
    }

    public static final class ReceiveTemperatureRequest implements TemperatureEnvironmentCommand {
        ActorRef<TemperatureSensor.TemperatureSensorCommand> sensor;

        public ReceiveTemperatureRequest(ActorRef<TemperatureSensor.TemperatureSensorCommand> sensor) {
            this.sensor = sensor;
        }
    }


    private Random random = new Random();
    private Temperature currentTemperature;
    private boolean isAuto = true;
    private final TimerScheduler<TemperatureEnvironmentCommand> temperatureTimeSchedule;

    public static Behavior<TemperatureEnvironmentCommand> create(Temperature initTemp)  {
        return Behaviors.setup(context -> Behaviors.withTimers(timer -> new TemperatureEnvironment(context, initTemp, timer)));
    }

    public TemperatureEnvironment(ActorContext<TemperatureEnvironmentCommand> context, Temperature initTemp, TimerScheduler<TemperatureEnvironmentCommand> temperatureTimeSchedule) {
        super(context);
        this.currentTemperature = initTemp;
        this.temperatureTimeSchedule = temperatureTimeSchedule;
        this.temperatureTimeSchedule.startTimerAtFixedRate(new TemperatureUpdate(new Temperature(currentTemperature.unit(), currentTemperature.value())), Duration.ofSeconds(10));
    }


    @Override
    public Receive<TemperatureEnvironmentCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(TemperatureUpdate.class, this::onTemperatureUpdate)
                .onMessage(ReceiveTemperatureRequest.class, this::onTemperatureRequest)
                .onMessage(TemperatureManual.class, this::manualTemperature)
                .onMessage(TemperatureAuto.class, this::autoTemperature)
                .build();
    }

    private Behavior<TemperatureEnvironmentCommand> manualTemperature(TemperatureManual t) {
        getContext().getLog().info("[ENVIRONMENT] Temperature Environment set to manual mode with " + t.temperature);
        isAuto = false;
        currentTemperature = new Temperature(t.temperature.unit(), t.temperature.value());
        return this;
    }

    private Behavior<TemperatureEnvironmentCommand> autoTemperature(TemperatureAuto t) {
        getContext().getLog().info("[ENVIRONMENT] Temperature Environment set to auto mode");
        isAuto = true;
        return this;
    }

    private Behavior<TemperatureEnvironmentCommand> onTemperatureUpdate(TemperatureUpdate t) {
        if (isAuto) {
            //calculate temperature difference
            //temperature pending -3/+3 of init state
            double temperatureChange = (random.nextInt(62) - 31) / (double) 10;

            //set new Temperature
            currentTemperature = new Temperature(t.currentTemp.unit(), t.currentTemp.value() - temperatureChange);

            getContext().getLog().info("[ENVIRONMENT] New temperature is: " + currentTemperature);
        }
        return this;
    }

    private Behavior<TemperatureEnvironmentCommand> onTemperatureRequest(ReceiveTemperatureRequest req) {
        req.sensor.tell(new TemperatureSensor.ReceiveTemperatureResponse(new Temperature(currentTemperature.unit(), currentTemperature.value())));
        return this;
    }
}

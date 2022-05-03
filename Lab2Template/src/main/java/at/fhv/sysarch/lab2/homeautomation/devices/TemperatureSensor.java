package at.fhv.sysarch.lab2.homeautomation.devices;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import at.fhv.sysarch.lab2.homeautomation.outside.TemperatureEnvironment;
import at.fhv.sysarch.lab2.homeautomation.shared.Temperature;

import java.time.Duration;

public class TemperatureSensor extends AbstractBehavior<TemperatureSensor.TemperatureSensorCommand> {

    // -- messages --
    public interface TemperatureSensorCommand { }

    public static final class ReceiveTemperatureResponse implements TemperatureSensorCommand {
        Temperature currentTemp;

        public ReceiveTemperatureResponse(Temperature currentTemp) {
            this.currentTemp = currentTemp;
        }
    }

    public static final class DoCreateTemperatureRequest implements TemperatureSensorCommand { }

    // -- behavior factory --
    public static Behavior<TemperatureSensorCommand> create(ActorRef<TemperatureEnvironment.TemperatureEnvironmentCommand> environment, ActorRef<AirCondition.AirConditionCommand> airCondition) {
        return Behaviors.setup(context -> Behaviors.withTimers(timer -> new TemperatureSensor(context, environment, airCondition, timer)));
    }

    private ActorRef<TemperatureEnvironment.TemperatureEnvironmentCommand> environment;
    private ActorRef<AirCondition.AirConditionCommand> airCondition;

    public TemperatureSensor(
            ActorContext<TemperatureSensorCommand> context,
            ActorRef<TemperatureEnvironment.TemperatureEnvironmentCommand> environment,
            ActorRef<AirCondition.AirConditionCommand> airCondition,
            TimerScheduler<TemperatureSensorCommand> timer) {
        super(context);
        this.environment = environment;
        this.airCondition = airCondition;

        timer.startTimerAtFixedRate(new DoCreateTemperatureRequest(), Duration.ofSeconds(5));
    }

    @Override
    public Receive<TemperatureSensorCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(DoCreateTemperatureRequest.class, this::onReadTemperature)
                .onMessage(ReceiveTemperatureResponse.class, this::onReceiveTemperature)
                .build();
    }

    private Behavior<TemperatureSensorCommand> onReadTemperature(DoCreateTemperatureRequest readTemperature) {
        environment.tell(new TemperatureEnvironment.ReceiveTemperatureRequest(getContext().getSelf()));
        return this;
    }

    private Behavior<TemperatureSensorCommand> onReceiveTemperature(ReceiveTemperatureResponse readTemperature) {
        getContext().getLog().info("[SENSOR] measured " + readTemperature.currentTemp);
        airCondition.tell(new AirCondition.ReceiveTemperature(new Temperature(readTemperature.currentTemp.unit(), readTemperature.currentTemp.value())));
        return this;
    }
}

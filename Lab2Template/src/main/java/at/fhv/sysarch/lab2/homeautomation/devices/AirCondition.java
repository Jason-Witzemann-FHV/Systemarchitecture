package at.fhv.sysarch.lab2.homeautomation.devices;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

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
import java.util.Optional;

public class AirCondition extends AbstractBehavior<AirCondition.AirConditionCommand> {
    public interface AirConditionCommand {}

    public static final class PowerAirCondition implements AirConditionCommand {
        final Optional<Boolean> value;

        public PowerAirCondition(Optional<Boolean> value) {
            this.value = value;
        }
    }

    public static final class EnrichedTemperature implements AirConditionCommand {
        Optional<Double> value;
        Optional<String> unit;

        public EnrichedTemperature(Optional<Double> value, Optional<String> unit) {
            this.value = value;
            this.unit = unit;
        }
    }

    private final String groupId;
    private final String deviceId;
    private boolean active = false;
    private boolean poweredOn = true;

    public AirCondition(ActorContext<AirConditionCommand> context, String groupId, String deviceId) {
        super(context);
        this.groupId = groupId;
        this.deviceId = deviceId;
        getContext().getLog().info("AirCondition started");
    }

    public static Behavior<AirConditionCommand> create(String groupId, String deviceId) {
        return Behaviors.setup(context -> new AirCondition(context, groupId, deviceId));
    }

    @Override
    public Receive<AirConditionCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(EnrichedTemperature.class, this::onReadTemperature)
                .onMessage(PowerAirCondition.class, this::onPowerAirConditionOff)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<AirConditionCommand> onReadTemperature(EnrichedTemperature r) {
        getContext().getLog().info("Aircondition reading {}", r.value.get());
        // TODO: process temperature
        if(r.value.get() >= 15) {
            getContext().getLog().info("Aircondition actived");
            this.active = true;
        }
        else {
            getContext().getLog().info("Aircondition deactived");
            this.active =  false;
        }

        return Behaviors.same();
    }

    private Behavior<AirConditionCommand> onPowerAirConditionOff(PowerAirCondition r) {
        getContext().getLog().info("Turning Aircondition to {}", r.value);

        if(r.value.get() == false) {
            return this.powerOff();
        }
        return this;
    }

    private Behavior<AirConditionCommand> onPowerAirConditionOn(PowerAirCondition r) {
        getContext().getLog().info("Turning Aircondition to {}", r.value);

        if(r.value.get() == true) {
            return Behaviors.receive(AirConditionCommand.class)
                    .onMessage(EnrichedTemperature.class, this::onReadTemperature)
                    .onMessage(PowerAirCondition.class, this::onPowerAirConditionOff)
                    .onSignal(PostStop.class, signal -> onPostStop())
                    .build();
        }
        return this;
    }

    private Behavior<AirConditionCommand> powerOff() {
        this.poweredOn = false;
        return Behaviors.receive(AirConditionCommand.class)
                .onMessage(PowerAirCondition.class, this::onPowerAirConditionOn)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private AirCondition onPostStop() {
        getContext().getLog().info("TemperatureSensor actor {}-{} stopped", groupId, deviceId);
        return this;
    }
}

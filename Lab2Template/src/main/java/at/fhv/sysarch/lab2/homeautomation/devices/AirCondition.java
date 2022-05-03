package at.fhv.sysarch.lab2.homeautomation.devices;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.shared.Temperature;

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

    public static final class ReceiveTemperature implements AirConditionCommand {
        final Temperature currentTemperature;

        public ReceiveTemperature(Temperature currentTemperature) {
            this.currentTemperature = currentTemperature;
        }
    }

    private boolean active = false;

    public AirCondition(ActorContext<AirConditionCommand> context) {
        super(context);
        getContext().getLog().info("AirCondition started");
    }

    public static Behavior<AirConditionCommand> create() {
        return Behaviors.setup(AirCondition::new);
    }

    @Override
    public Receive<AirConditionCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(ReceiveTemperature.class, this::onReadTemperature)
                .build();
    }

    private Behavior<AirConditionCommand> onReadTemperature(ReceiveTemperature temperature) {
        getContext().getLog().info("Aircondition reading {}", temperature.currentTemperature);

        if(temperature.currentTemperature.value() >= 20 && !active) {
            getContext().getLog().info("Aircondition actived");
            active = true;
        }
        else if (temperature.currentTemperature.value() < 20 && active){
            getContext().getLog().info("Aircondition deactived");
            active =  false;
        } //else nothing changes

        return this;
    }
}

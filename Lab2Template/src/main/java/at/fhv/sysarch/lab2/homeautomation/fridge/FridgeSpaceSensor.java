package at.fhv.sysarch.lab2.homeautomation.fridge;


import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


public class FridgeSpaceSensor extends AbstractBehavior<FridgeSpaceSensor.FridgeSpaceCommand> {

    public interface FridgeSpaceCommand { }

    public static final class AvailableSpaceCommand implements FridgeSpaceCommand {
        final ActorRef<PrepareOrder.PrepareOrderCommand> prepareOrder;
        public AvailableSpaceCommand(ActorRef<PrepareOrder.PrepareOrderCommand> prepareOrder) {
            this.prepareOrder = prepareOrder;
        }
    }

    public static final class AddSpaceCommand implements FridgeSpaceCommand { }

    public static final class RemoveSpaceCommand implements FridgeSpaceCommand { }

    public static Behavior<FridgeSpaceCommand> create() {
        return Behaviors.setup(FridgeSpaceSensor::new);
    }

    private final int maxSpace = 20;
    private int space = 0;

    public FridgeSpaceSensor(ActorContext<FridgeSpaceCommand> context) {
        super(context);
    }

    @Override
    public Receive<FridgeSpaceCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(AvailableSpaceCommand.class, this::onAvailableSpace)
                .onMessage(AddSpaceCommand.class, this::onItemAdd)
                .onMessage(RemoveSpaceCommand.class, this::onItemRemove)
                .build();
    }

    private Behavior<FridgeSpaceCommand> onAvailableSpace(AvailableSpaceCommand command) {
        command.prepareOrder.tell(new PrepareOrder.SpaceFeedbackCommand(maxSpace - space));
        return this;
    }

    private Behavior<FridgeSpaceCommand> onItemAdd(AddSpaceCommand command) {
        space += 1;
        getContext().getLog().info("[FRIDGE] An Item was added to the fridge, there are " + space + " items currently in the fridge (max " + maxSpace + " items) ");       return this;
    }

    private Behavior<FridgeSpaceCommand> onItemRemove(RemoveSpaceCommand command) {
        space -= 1;
        getContext().getLog().info("[FRIDGE] An Item was removed from the fridge, there are " + space + " items currently in the fridge (max " + maxSpace + " items) ");       return this;
    }

}

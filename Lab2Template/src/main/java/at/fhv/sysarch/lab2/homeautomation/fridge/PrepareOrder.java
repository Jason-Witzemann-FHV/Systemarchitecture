package at.fhv.sysarch.lab2.homeautomation.fridge;


import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.OptionalInt;

public class PrepareOrder extends AbstractBehavior<PrepareOrder.PrepareOrderCommand> {

    public interface PrepareOrderCommand { }

    public static final class WeightFeedbackCommand implements PrepareOrderCommand{
        final int weight;
        public WeightFeedbackCommand(int weight) {
            this.weight = weight;
        }
    }

    public static final class SpaceFeedbackCommand implements PrepareOrderCommand{
        final int space;
        public SpaceFeedbackCommand(int space) {
            this.space = space;
        }
    }

    public static Behavior<PrepareOrderCommand> create(
            Order order,
            ActorRef<Fridge.FridgeCommand> fridge,
            ActorRef<FridgeWeightSensor.FridgeWeightSensorCommand> weightSensor,
            ActorRef<FridgeSpaceSensor.FridgeSpaceCommand> spaceSensor) {
        return Behaviors.setup(context -> new PrepareOrder(context, order, fridge, weightSensor, spaceSensor));
    }


    private final Order order;
    private final ActorRef<Fridge.FridgeCommand> fridge;

    private OptionalInt availableWeight = OptionalInt.empty();
    private OptionalInt availableSpace = OptionalInt.empty();

    public PrepareOrder(
            ActorContext<PrepareOrderCommand> context,
            Order order,
            ActorRef<Fridge.FridgeCommand> fridge,
            ActorRef<FridgeWeightSensor.FridgeWeightSensorCommand> weightSensor,
            ActorRef<FridgeSpaceSensor.FridgeSpaceCommand> spaceSensor) {
        super(context);
        this.order = order;
        this.fridge = fridge;
        weightSensor.tell(new FridgeWeightSensor.AvaliableWeightCommand(getContext().getSelf()));
        spaceSensor.tell(new FridgeSpaceSensor.AvailableSpaceCommand(getContext().getSelf()));
    }


    @Override
    public Receive<PrepareOrderCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(WeightFeedbackCommand.class, this::onWeight)
                .onMessage(SpaceFeedbackCommand.class, this::onSpace)
                .build();
    }

    private Behavior<PrepareOrderCommand> onWeight(WeightFeedbackCommand command) {
        this.availableWeight = OptionalInt.of(command.weight);
        completeOrContinue();
        return this;
    }

    private Behavior<PrepareOrderCommand> onSpace(SpaceFeedbackCommand command) {
        this.availableSpace = OptionalInt.of(command.space);
        completeOrContinue();
        return this;
    }

    private Behavior<PrepareOrderCommand> completeOrContinue() {
        if (availableWeight.isPresent() && availableSpace.isPresent()) {

            if (availableWeight.getAsInt() >= order.weight() && availableSpace.getAsInt() > 0) {
                fridge.tell(new Fridge.FridgeDoOrderCommand(order));
            } else {
                fridge.tell(new Fridge.FridgeDisplayCommand(
                        "Error whilst preparing Order! Fridge does not have enough available space or weight vor requested order! " +
                                "(free space: " + availableSpace.getAsInt() + "items " +
                                "/ available weight: " + availableWeight.getAsInt() + "g " +
                                "/ weight of order: " + order.weight() + "g)"));
            }

            return Behaviors.stopped();
        }
        return this;
    }

}

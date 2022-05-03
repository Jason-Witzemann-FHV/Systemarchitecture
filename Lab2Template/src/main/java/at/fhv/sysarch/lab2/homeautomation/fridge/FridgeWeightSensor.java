package at.fhv.sysarch.lab2.homeautomation.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class FridgeWeightSensor extends AbstractBehavior<FridgeWeightSensor.FridgeWeightSensorCommand> {

    public interface FridgeWeightSensorCommand { }

    public static final class AvaliableWeightCommand implements FridgeWeightSensorCommand {
        final ActorRef<PrepareOrder.PrepareOrderCommand> prepareOrder;
        public AvaliableWeightCommand(ActorRef<PrepareOrder.PrepareOrderCommand> prepareOrder) {
            this.prepareOrder = prepareOrder;
        }
    }

    public static final class AddWeightCommand implements FridgeWeightSensorCommand {
        final int weightToAdd;
        public AddWeightCommand(int weightToAdd) {
            this.weightToAdd = weightToAdd;
        }
    }

    public static final class RemoveWeightCommand implements FridgeWeightSensorCommand {
        final int weightToRemove;
        public RemoveWeightCommand(int weightToAdd) {
            this.weightToRemove = weightToAdd;
        }
    }

    public static Behavior<FridgeWeightSensorCommand> create() {
        return Behaviors.setup(FridgeWeightSensor::new);
    }

    private final int maxWeight = 10000;
    private int currentWeight = 0;


    public FridgeWeightSensor(ActorContext<FridgeWeightSensorCommand> context) {
        super(context);
    }

    @Override
    public Receive<FridgeWeightSensorCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(AvaliableWeightCommand.class, this::onAvailableWeight)
                .onMessage(AddWeightCommand.class, this::onWeightAdd)
                .onMessage(RemoveWeightCommand.class, this::onWeightRemove)
                .build();
    }

    private Behavior<FridgeWeightSensorCommand> onAvailableWeight(AvaliableWeightCommand command){
        command.prepareOrder.tell(new PrepareOrder.WeightFeedbackCommand(maxWeight - currentWeight));
        return this;
    }

    private Behavior<FridgeWeightSensorCommand> onWeightAdd(AddWeightCommand command){
        currentWeight += command.weightToAdd;
        getContext().getLog().info("[FRIDGE] An Item with the weight " + command.weightToAdd + "g was added to the fridge, the total weight currently in the fridge is now " + currentWeight + " (max " + maxWeight + "g)");
        return this;
    }

    private Behavior<FridgeWeightSensorCommand> onWeightRemove(RemoveWeightCommand command){
        currentWeight -= command.weightToRemove;
        getContext().getLog().info("[FRIDGE] An Item with the weight " + command.weightToRemove + "g was was removed from fridge, the total weight currently in the fridge is now " + currentWeight + " (max " + maxWeight + "g)");
        return this;
    }



}

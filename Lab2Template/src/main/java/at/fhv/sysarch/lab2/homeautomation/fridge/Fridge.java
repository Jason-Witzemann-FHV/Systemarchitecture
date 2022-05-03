package at.fhv.sysarch.lab2.homeautomation.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class Fridge extends AbstractBehavior<Fridge.FridgeCommand> {



    public interface FridgeCommand { }

    public static final class FridgeRequestOrderCommand implements FridgeCommand {
        final Order order;
        public FridgeRequestOrderCommand(Order order) {
            this.order = order;
        }
    }

    public static final class FridgeDoOrderCommand implements FridgeCommand {
        final Order order;
        public FridgeDoOrderCommand(Order order) {
            this.order = order;
        }
    }

    public static final class FridgeDisplayCommand implements FridgeCommand {
        final String messageToDisplay;
        public FridgeDisplayCommand(String messageToDisplay) {
            this.messageToDisplay = messageToDisplay;
        }
    }

    public static final class FridgeListContentCommand implements FridgeCommand { }

    public static final class FridgeOrderHistoryCommand implements FridgeCommand { }

    public static final class FridgeConsumeCommand implements FridgeCommand {
        final Order order;
        public FridgeConsumeCommand(Order order) {
            this.order = order;
        }

    }

    public static Behavior<FridgeCommand> create() {
        return Behaviors.setup(Fridge::new);
    }

    private final ActorRef<FridgeWeightSensor.FridgeWeightSensorCommand> weightSensor;
    private final ActorRef<FridgeSpaceSensor.FridgeSpaceCommand> spaceSensor;

    private final Map<LocalDateTime, Order> orderHistory = new TreeMap<>();
    private final Map<Order, Integer> itemsInFridge = new HashMap<>();


    public Fridge(ActorContext<FridgeCommand> context) {
        super(context);
        this.weightSensor = getContext().spawn(FridgeWeightSensor.create(), "FridgeWeightSensor");
        this.spaceSensor = getContext().spawn(FridgeSpaceSensor.create(), "FridgeSpaceSensor");
    }

    @Override
    public Receive<FridgeCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(FridgeRequestOrderCommand.class, this::onRequestOrder)
                .onMessage(FridgeDoOrderCommand.class, this::onDoOrder)
                .onMessage(FridgeDisplayCommand.class, this::onDisplay)
                .onMessage(FridgeListContentCommand.class, this::onListContent)
                .onMessage(FridgeOrderHistoryCommand.class, this::onOrderHistory)
                .onMessage(FridgeConsumeCommand.class, this::onConsume)
                .build();
    }

    public Behavior<FridgeCommand> onRequestOrder(FridgeRequestOrderCommand command) {
        getContext().getLog().info("[FRIDGE] requested preparation order of " + command.order.name());
        getContext().spawn(PrepareOrder.create(command.order, getContext().getSelf(), weightSensor, spaceSensor), "PrepareOrder_" + UUID.randomUUID());
        return this;
    }

    public Behavior<FridgeCommand> onListContent(FridgeListContentCommand command) {
        var builder = new StringBuilder("Content of Fridge: \n");
        for(var entry : itemsInFridge.entrySet()) {
            builder.append(" - " + entry.getValue() + "x " + entry.getKey().name() + " [" + entry.getKey().weight() + "g / " + entry.getKey().price() + "€] \n");
        }
        getContext().getSelf().tell(new FridgeDisplayCommand(builder.toString()));
        return this;
    }

    public Behavior<FridgeCommand> onOrderHistory(FridgeOrderHistoryCommand command) {
        var builder = new StringBuilder("Order History: \n");
        for(var entry : orderHistory.entrySet()) {
            builder.append(" - " + entry.getValue().name() + " [" + entry.getValue().weight() + "g / " + entry.getValue().price() + "€] ordered on " + entry.getKey() + "\n");
        }
        getContext().getSelf().tell(new FridgeDisplayCommand(builder.toString()));
        return this;
    }

    public Behavior<FridgeCommand> onConsume(FridgeConsumeCommand command) {

        var entry = itemsInFridge.get(command.order);

        if (entry == null) {
            getContext().getLog().info("[FRIDGE] cannot consume " + command.order.name() + " since it is not stored in the fridge!");
            return this;
        }

        itemsInFridge.put(command.order, entry - 1);
        getContext().getLog().info("[FRIDGE] consumed " + command.order.name() + "!");
        weightSensor.tell(new FridgeWeightSensor.RemoveWeightCommand(command.order.weight()));
        spaceSensor.tell(new FridgeSpaceSensor.RemoveSpaceCommand());

        if (entry - 1 <= 0) {
            getContext().getLog().info("[FRIDGE] no " + command.order.name() + " is left in fridge => automatically requesting item again!");
            getContext().getSelf().tell(new FridgeRequestOrderCommand(command.order));
        }
        return this;
    }

    public Behavior<FridgeCommand> onDoOrder(FridgeDoOrderCommand command) {
        getContext().getLog().info("[FRIDGE] ordered " + command.order.name());
        orderHistory.put(LocalDateTime.now(), command.order);
        weightSensor.tell(new FridgeWeightSensor.AddWeightCommand(command.order.weight()));
        spaceSensor.tell(new FridgeSpaceSensor.AddSpaceCommand());
        int amount = itemsInFridge.getOrDefault(command.order, 0) + 1;
        itemsInFridge.put(command.order, amount);
        return this;
    }

    public Behavior<FridgeCommand> onDisplay(FridgeDisplayCommand command) {
        getContext().getLog().info("[FRIDGE] " + command.messageToDisplay);
        return this;
    }



}

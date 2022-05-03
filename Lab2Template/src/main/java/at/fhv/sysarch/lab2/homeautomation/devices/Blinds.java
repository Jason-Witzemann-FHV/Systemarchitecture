package at.fhv.sysarch.lab2.homeautomation.devices;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.shared.BlindsPosition;

public class Blinds extends AbstractBehavior<Blinds.BlindsCommand> {

    public interface BlindsCommand { }

    public static final class MediaStationStatusChangedCommand implements BlindsCommand {
        boolean isMoviePlaying;
        public MediaStationStatusChangedCommand(boolean isMoviePlaying) {
            this.isMoviePlaying = isMoviePlaying;
        }
    }

    public static final class WeatherChangedCommand implements BlindsCommand {
        boolean isRaining;
        public WeatherChangedCommand(boolean isRaining) {
            this.isRaining = isRaining;
        }
    }

    public static Behavior<Blinds.BlindsCommand> create() {
        return Behaviors.setup(Blinds::new);
    }


    private boolean isMoviePlaying = false;
    private boolean isRainy = true;

    private BlindsPosition blindsPosition = BlindsPosition.UP;

    public Blinds(ActorContext<BlindsCommand> context) {
        super(context);
    }


    @Override
    public Receive<BlindsCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(MediaStationStatusChangedCommand.class, this::applyMediaStationChanges)
                .onMessage(WeatherChangedCommand.class, this::applyWeatherChanges)
                .build();
    }

    private Behavior<BlindsCommand> applyMediaStationChanges(MediaStationStatusChangedCommand command) {
        isMoviePlaying = command.isMoviePlaying;
        applyChanges();
        return this;
    }

    private Behavior<BlindsCommand> applyWeatherChanges(WeatherChangedCommand command) {
        isRainy = command.isRaining;
        applyChanges();
        return this;
    }

    private void applyChanges() {
        var newPosition = isMoviePlaying || !isRainy ? BlindsPosition.DOWN : BlindsPosition.UP;
        if (newPosition != blindsPosition) {
            getContext().getLog().info("[DEVICE] Blinds changed to " + newPosition);
            blindsPosition = newPosition;
        }
    }

}

package at.fhv.sysarch.lab2.homeautomation.devices;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.shared.Movie;

public class MediaStation extends AbstractBehavior<MediaStation.MediaStationCommand> {


    public interface MediaStationCommand { }

    public static final class TurnMediaStationOnCommand implements MediaStationCommand {
        Movie movie;

        public TurnMediaStationOnCommand(Movie movie) {
            this.movie = movie;
        }
    }

    public static final class TurnMediaStationOffCommand implements MediaStationCommand {

    }

    public static Behavior<MediaStationCommand> create(ActorRef<Blinds.BlindsCommand> blinds) {
        return Behaviors.setup(context -> new MediaStation(context, blinds));
    }

    private ActorRef<Blinds.BlindsCommand> blinds;
    private Movie currentMovie;

    public MediaStation(ActorContext<MediaStationCommand> context, ActorRef<Blinds.BlindsCommand> blinds) {
        super(context);
        this.blinds = blinds;
    }


    @Override
    public Receive<MediaStationCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(TurnMediaStationOnCommand.class, this::onTurnOn)
                .onMessage(TurnMediaStationOffCommand.class, this::onTurnOff)
                .build();
    }


    private Behavior<MediaStationCommand> onTurnOn(TurnMediaStationOnCommand command) {

        if (currentMovie == null) {
            currentMovie = command.movie;
            getContext().getLog().info("[DEVICE] MediaStation is now playing " + currentMovie.title());

            blinds.tell(new Blinds.MediaStationStatusChangedCommand(true));
        } else {
            getContext().getLog().info("[DEVICE] MediaStation is currently playing " + currentMovie.title() + " therefore requested movie " + command.movie.title() + "cannot be played");
        }

        return this;
    }

    private Behavior<MediaStationCommand> onTurnOff(TurnMediaStationOffCommand command) {

        currentMovie = null;
        getContext().getLog().info("[DEVICE] Media Station now turned off");
        blinds.tell(new Blinds.MediaStationStatusChangedCommand(false));

        return this;
    }

}

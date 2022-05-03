package at.fhv.sysarch.lab2.homeautomation.devices;

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

    public static Behavior<MediaStationCommand> create() {
        return Behaviors.setup(MediaStation::new);
    }

    private Movie currentMovie;

    public MediaStation(ActorContext<MediaStationCommand> context) {
        super(context);
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

            // todo inform blinds
        } else {
            getContext().getLog().info("[DEVICE] MediaStation is currently playing " + currentMovie.title() + " therefore requested movie " + command.movie.title() + "cannot be played");
        }

        return this;
    }

    private Behavior<MediaStationCommand> onTurnOff(TurnMediaStationOffCommand command) {

        currentMovie = null;
        getContext().getLog().info("[DEVICE] Media Station now turned off");
        // todo inform blinds

        return this;
    }

}

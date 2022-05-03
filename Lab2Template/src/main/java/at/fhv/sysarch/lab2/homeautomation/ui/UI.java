package at.fhv.sysarch.lab2.homeautomation.ui;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.devices.MediaStation;
import at.fhv.sysarch.lab2.homeautomation.shared.Movie;

import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

public class UI extends AbstractBehavior<Void> {

    private ActorRef<MediaStation.MediaStationCommand> mediaStation;

    public static Behavior<Void> create(ActorRef<MediaStation.MediaStationCommand> mediaStation) {
        return Behaviors.setup(context -> new UI(context, mediaStation));
    }

    private UI(ActorContext<Void> context, ActorRef<MediaStation.MediaStationCommand> mediaStation) {
        super(context);
        this.mediaStation = mediaStation;
        new Thread(() -> { this.runCommandLine(); }).start();

        getContext().getLog().info("UI started");
    }

    @Override
    public Receive<Void> createReceive() {
        return newReceiveBuilder().onSignal(PostStop.class, signal -> onPostStop()).build();
    }

    private UI onPostStop() {
        getContext().getLog().info("UI stopped");
        return this;
    }

    public void runCommandLine() {
        Scanner scanner = new Scanner(System.in);
        String reader = "";


        while (!reader.equalsIgnoreCase("quit") && scanner.hasNextLine()) {
            reader = scanner.nextLine();
            String[] splits = reader.split(" ");
            String command = splits[0];
            String[] args = Arrays.stream(splits).toList().subList(1, splits.length).toArray(new String[splits.length - 1]);

            switch (command) {
                case "media":
                    mediaStationCommand(args);
                    break;

                default:
                    System.out.println("unknown command");
                    break;
            }


        }
        getContext().getLog().info("UI done");
    }

    private void mediaStationCommand(String[] args) {

        if (args.length == 0) {

            System.out.println("[CMD] Mediacommand - Usage:");
            System.out.println("[CMD] - media on [name of movie]");
            System.out.println("[CMD] - media off");

        } else if (args[0].equalsIgnoreCase("on") && args.length > 1 ) {

            var nameOfMovie = Arrays.stream(args).toList().subList(1, args.length).stream().collect(Collectors.joining(" "));
            System.out.println("[CMD] Requested MediaStation to play " + nameOfMovie);
            mediaStation.tell(new MediaStation.TurnMediaStationOnCommand(Movie.withTitle(nameOfMovie)));

        } else if (args[0].equalsIgnoreCase("off")) {

            System.out.println("[CMD] Requested to turn off MediaStation");
            mediaStation.tell(new MediaStation.TurnMediaStationOffCommand());
        }


    }
}

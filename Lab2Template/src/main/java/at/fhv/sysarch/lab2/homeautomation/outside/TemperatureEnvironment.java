package at.fhv.sysarch.lab2.homeautomation.outside;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import at.fhv.sysarch.lab2.homeautomation.shared.Temperature;

import java.time.Duration;
import java.util.Random;

public class TemperatureEnvironment extends AbstractBehavior<TemperatureEnvironment.TemperatureUpdateCommand> {

    public interface TemperatureUpdateCommand{}

    public static final class TemperatureUpdate implements TemperatureUpdateCommand{
        Temperature currentTemp;

        public TemperatureUpdate(Temperature currentTemp) {
            this.currentTemp = currentTemp;
        }
    }

    private Temperature currentTemperature;
    private final TimerScheduler<TemperatureUpdateCommand> temperatureTimeSchedule;

    public static Behavior<TemperatureUpdateCommand> create(Temperature initTemp)  {
        return Behaviors.setup(context -> Behaviors.withTimers(timer -> new TemperatureEnvironment(context, initTemp, timer)));
    }

    public TemperatureEnvironment(ActorContext<TemperatureUpdateCommand> context, Temperature initTemp, TimerScheduler<TemperatureUpdateCommand> temperatureTimeSchedule) {
        super(context);
        this.currentTemperature = initTemp;
        this.temperatureTimeSchedule = temperatureTimeSchedule;
        this.temperatureTimeSchedule.startTimerAtFixedRate(new TemperatureUpdate(currentTemperature), Duration.ofSeconds(5));
    }


    @Override
    public Receive<TemperatureUpdateCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(TemperatureUpdate.class, this::onTemperatureUpdate)
                .build();
    }

    private Behavior<TemperatureUpdateCommand> onTemperatureUpdate(TemperatureUpdate t) {
        getContext().getLog().info("Current temperature is: " + t.currentTemp);

        //calculate temperature difference
        Random random = new Random();
        double temperatureChange = (random.nextInt(62) - 31) / (double) 10;
        getContext().getLog().info("Temperature changed by: " + temperatureChange + t.currentTemp.unit());

        //calc new Temperature
        Temperature newTemperature = null;
        newTemperature = new Temperature(t.currentTemp.unit(), t.currentTemp.value() + temperatureChange);
        if (t.currentTemp.value() + temperatureChange > 25 || t.currentTemp.value() + temperatureChange < 15) {
            newTemperature = new Temperature(t.currentTemp.unit(), t.currentTemp.value() + temperatureChange);
        } else {
            newTemperature = new Temperature(t.currentTemp.unit(), t.currentTemp.value() - temperatureChange);
        }

        //set new Temperature
        getContext().getLog().info("New temperature is: " + newTemperature);
        currentTemperature = newTemperature;
        return this;
    }
}

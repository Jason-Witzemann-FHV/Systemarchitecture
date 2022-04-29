package at.fhv.sysarch.lab2;

import akka.actor.typed.ActorSystem;
import at.fhv.sysarch.lab2.homeautomation.HomeAutomationController;
import at.fhv.sysarch.lab2.homeautomation.outside.TemperatureEnvironment;
import at.fhv.sysarch.lab2.homeautomation.shared.Temperature;

public class HomeAutomationSystem {

    public static void main(String[] args) {
        ActorSystem<Void> home = ActorSystem.create(HomeAutomationController.create(), "HomeAutomation");
        ActorSystem<TemperatureEnvironment.TemperatureUpdateCommand> temperatureEnvironment = ActorSystem.create(TemperatureEnvironment.create(new Temperature("Celcius", 22)), "TemperatureEnvironment");
    }


}

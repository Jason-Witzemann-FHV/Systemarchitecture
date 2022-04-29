package at.fhv.sysarch.lab2;

import akka.actor.typed.ActorSystem;
import at.fhv.sysarch.lab2.homeautomation.HomeAutomationController;
import at.fhv.sysarch.lab2.homeautomation.outside.WeatherEnvironment;

public class HomeAutomationSystem {

    public static void main(String[] args) {
        ActorSystem<Void> home = ActorSystem.create(HomeAutomationController.create(), "HomeAutomation");

        ActorSystem<WeatherEnvironment.WeatherEnvironmentCommand> weatherEnv = ActorSystem.create(WeatherEnvironment.create(), "WetterGott");
    }


}

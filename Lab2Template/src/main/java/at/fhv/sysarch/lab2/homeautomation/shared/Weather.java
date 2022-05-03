package at.fhv.sysarch.lab2.homeautomation.shared;

import java.util.Random;

public enum Weather {

    SUNNY,
    RAINY;

    private static Random random = new Random();

    public static Weather random() {
        return random.nextBoolean() ? SUNNY : RAINY;
    }
}

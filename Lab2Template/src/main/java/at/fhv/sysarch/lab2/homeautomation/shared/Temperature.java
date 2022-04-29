package at.fhv.sysarch.lab2.homeautomation.shared;

public final class Temperature {
    String unit;
    double value;

    public Temperature(String unit, double value) {
        this.unit = unit;
        this.value = value;
    }

    public double value() {
        return value;
    }

    public String unit() {
        return unit;
    }

    @Override
    public String toString() {
        return "Temperature: " + value + " " + unit;
    }
}

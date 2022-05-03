package at.fhv.sysarch.lab2.homeautomation.shared;

public record Movie(String title) {

    public static Movie withTitle(String title) {
        return new Movie(title);
    }

}

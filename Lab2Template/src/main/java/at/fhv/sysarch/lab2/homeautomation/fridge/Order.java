package at.fhv.sysarch.lab2.homeautomation.fridge;

import java.util.Optional;

public record Order(
        String name,
        int weight,
        double price
) {

    public static Optional<Order> withName(String name) {

        var order = switch(name) {
            case "apple" -> new Order(name,250, 0.99);
            case "cheese" -> new Order(name,1000, 4.99);
            case "sausage" -> new Order(name,300, 4.99);
            case "steak" -> new Order(name,500, 14.99);
            case "carrots" -> new Order(name,3000, 6.99);
            case "beer" -> new Order(name, 700, 1.99);
            default -> null;
        };
        return Optional.ofNullable(order);

    }

}

package readside.dto;

import java.time.LocalDate;

public class FreeRoom {
    private String roomnumber;
    private int amountOfPeople;
    private LocalDate from;
    private LocalDate to;

    public FreeRoom(String roomnumber, int amountOfPeople, LocalDate from, LocalDate to) {
        this.roomnumber = roomnumber;
        this.amountOfPeople = amountOfPeople;
        this.from = from;
        this.to = to;
    }

    public String getRoomnumber() {
        return roomnumber;
    }

    public int getAmountOfPeople() {
        return amountOfPeople;
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }
}

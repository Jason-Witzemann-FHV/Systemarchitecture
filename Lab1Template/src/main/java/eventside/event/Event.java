package eventside.event;

public abstract class Event {

    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Event{" +
                "timestamp=" + timestamp +
                '}';
    }
}

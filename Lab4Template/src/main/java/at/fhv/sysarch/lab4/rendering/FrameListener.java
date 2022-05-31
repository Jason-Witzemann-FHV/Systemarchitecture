package at.fhv.sysarch.lab4.rendering;

@FunctionalInterface
public interface FrameListener {
    public void onFrame(double dt);
}
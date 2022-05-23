package at.fhv.sysarch.lab3.pipeline.push.filter;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.push.IPush;
import at.fhv.sysarch.lab3.pipeline.push.Push;
import javafx.scene.paint.Color;

public class PushModelColor extends Push<Face, Pair<Face, Color>> {

    private final Color color;

    public PushModelColor(IPush<Pair<Face, Color>> successor, Color color) {
        super(successor);
        this.color = color;
    }

    @Override
    public void push(Face element) {
        successor.push(new Pair<>(element, color));
    }
}

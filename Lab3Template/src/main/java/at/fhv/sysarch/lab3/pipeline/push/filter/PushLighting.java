package at.fhv.sysarch.lab3.pipeline.push.filter;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.push.IPush;
import at.fhv.sysarch.lab3.pipeline.push.Push;
import com.hackoeur.jglm.Vec3;
import javafx.scene.paint.Color;

public class PushLighting extends Push<Pair<Face, Color>, Pair<Face, Color>> {

    private final Vec3 unitVector;

    public PushLighting(IPush<Pair<Face, Color>> successor, Vec3 unitVector) {
        super(successor);
        this.unitVector = unitVector;
    }

    @Override
    public void push(Pair<Face, Color> element) {
        successor.push(applyLighting(element));
    }

    private Pair<Face, Color> applyLighting(Pair<Face, Color> pair) {
        Face f = pair.fst();
        Color c = pair.snd();
        float shading = f.getN1().toVec3().dot(unitVector);
        return new Pair<>(f, c.deriveColor(0, 1, shading, 1));
    }
}

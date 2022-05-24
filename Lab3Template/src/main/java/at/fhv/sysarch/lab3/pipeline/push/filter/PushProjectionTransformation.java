package at.fhv.sysarch.lab3.pipeline.push.filter;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.push.IPush;
import at.fhv.sysarch.lab3.pipeline.push.Push;
import com.hackoeur.jglm.Mat4;
import javafx.scene.paint.Color;

public class PushProjectionTransformation extends Push<Pair<Face, Color>, Pair<Face, Color>> {

    private final Mat4 transform;

    public PushProjectionTransformation(IPush<Pair<Face, Color>> successor, Mat4 transform) {
        super(successor);
        this.transform = transform;
    }

    @Override
    public void push(Pair<Face, Color> element) {
        successor.push(transform(element));
    }

    private Pair<Face, Color> transform(Pair<Face, Color> pair) {
        Face f = pair.fst();
        Color c = pair.snd();
        Face transformed = new Face(transform.multiply(f.getV1()), transform.multiply(f.getV2()), transform.multiply(f.getV3()), f);
        return new Pair<>(transformed, c);
    }
}

package at.fhv.sysarch.lab3.pipeline.push.filter;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.push.IPush;
import at.fhv.sysarch.lab3.pipeline.push.Push;
import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Vec4;
import javafx.scene.paint.Color;

public class PushScreenSpaceTransformation extends Push<Pair<Face, Color>, Pair<Face, Color>> {

    private final Mat4 transform;

    public PushScreenSpaceTransformation(IPush<Pair<Face, Color>> successor, Mat4 transform) {
        super(successor);
        this.transform = transform;
    }

    @Override
    public void push(Pair<Face, Color> element) {
        successor.push(transform(element));
    }

    private Pair<Face, Color> transform(Pair<Face, Color> pair) {
        Face f = pair.fst();
        Face transformed = new Face(apply(f.getV1()), apply(f.getV2()), apply(f.getV3()), f);
        return new Pair<>(transformed, pair.snd());
    }

    private Vec4 apply(Vec4 vec) {
        return transform.multiply(vec.multiply(1f / vec.getW()));
    }
}

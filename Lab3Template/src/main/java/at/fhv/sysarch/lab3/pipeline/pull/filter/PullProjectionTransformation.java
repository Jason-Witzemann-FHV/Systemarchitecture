package at.fhv.sysarch.lab3.pipeline.pull.filter;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.PipelineData;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.pull.IPull;
import at.fhv.sysarch.lab3.pipeline.pull.Pull;
import com.hackoeur.jglm.Mat4;
import javafx.scene.paint.Color;

/**
 * does apply the projection transformation
 */
public class PullProjectionTransformation<T extends Pair<Face, Color>> extends Pull<T, Pair<Face, Color>> {

    private final Mat4 transform;

    public PullProjectionTransformation(PipelineData pd, IPull<T> source) {
        super(source);
        this.transform = pd.getProjTransform();
    }

    @Override
    public Pair<Face, Color> pull() {
        return transform(source.pull());
    }

    private Pair<Face, Color> transform(Pair<Face, Color> pair) {
        Face f = pair.fst();
        Color c = pair.snd();
        Face transformed = new Face(transform.multiply(f.getV1()), transform.multiply(f.getV2()), transform.multiply(f.getV3()), f);
        return new Pair<>(transformed, c);
    }
}

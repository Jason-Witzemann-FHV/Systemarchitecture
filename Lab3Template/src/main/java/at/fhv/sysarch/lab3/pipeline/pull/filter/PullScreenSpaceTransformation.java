package at.fhv.sysarch.lab3.pipeline.pull.filter;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.PipelineData;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.pull.IPull;
import at.fhv.sysarch.lab3.pipeline.pull.Pull;
import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Vec4;
import javafx.scene.paint.Color;

/**
 * does apply the projection transformation
 */
public class PullScreenSpaceTransformation<T extends Pair<Face, Color>> extends Pull<T, Pair<Face, Color>> {

    private final Mat4 transform;

    public PullScreenSpaceTransformation(PipelineData pd, IPull<T> source) {
        super(source);
        this.transform = pd.getViewportTransform();
    }

    @Override
    public Pair<Face, Color> pull() {
        return transform(source.pull());
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

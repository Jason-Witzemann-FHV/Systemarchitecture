package at.fhv.sysarch.lab3.pipeline.pull.filter;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.PipelineData;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.pull.IPull;
import at.fhv.sysarch.lab3.pipeline.pull.Pull;
import javafx.scene.paint.Color;

/**
 * maps face to itself and its corresponding color
 */
public class PullLighting<T extends Pair<Face, Color>> extends Pull<T, Pair<Face, Color>> {

    private final PipelineData pd;

    public PullLighting(PipelineData pd, IPull<T> source) {
        super(source);
        this.pd = pd;
    }

    @Override
    public Pair<Face, Color> pull() {
        return applyLighting(source.pull());
    }

    private Pair<Face, Color> applyLighting(Pair<Face, Color> pair) {
        Face f = pair.fst();
        Color c = pair.snd();
        float shading = f.getN1().toVec3().dot(pd.getLightPos().getUnitVector());
        return new Pair<>(f, c.deriveColor(0, 1, shading, 1));
    }
}

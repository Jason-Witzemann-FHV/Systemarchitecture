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
public class PullModelColor<T extends Face> extends Pull<T, Pair<Face, Color>> {

    private PipelineData pd;

    public PullModelColor(PipelineData pd, IPull<T> source) {
        super(source);
        this.pd = pd;
    }

    @Override
    public Pair<Face, Color> pull() {
        Face f = source.pull();
        return new Pair<>(f, pd.getModelColor());
    }
}

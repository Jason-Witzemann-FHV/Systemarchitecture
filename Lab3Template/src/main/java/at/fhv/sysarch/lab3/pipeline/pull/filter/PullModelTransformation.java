package at.fhv.sysarch.lab3.pipeline.pull.filter;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.pull.IPull;
import at.fhv.sysarch.lab3.pipeline.pull.Pull;

// This class turns teapot upside down and scales it up
public class PullModelTransformation<T extends Face> extends Pull<T, Face> {
    public PullModelTransformation(IPull<T> source) {
        super(source);
    }

    @Override
    public Face pull() {
        Face f = source.pull();
        return transform(f);
    }

    private Face transform(Face f) {
        int factor = -100;
        return new Face(
                f.getV1().multiply(factor),
                f.getV2().multiply(factor),
                f.getV3().multiply(factor),
                f
        );
    }
}

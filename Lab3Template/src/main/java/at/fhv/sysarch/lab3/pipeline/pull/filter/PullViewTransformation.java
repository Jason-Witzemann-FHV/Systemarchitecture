package at.fhv.sysarch.lab3.pipeline.pull.filter;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.pull.IPull;
import at.fhv.sysarch.lab3.pipeline.pull.Pull;
import com.hackoeur.jglm.Vec4;

// idk, moving i guess
public class PullViewTransformation <T extends Face> extends Pull<T, Face> {
    public PullViewTransformation(IPull<T> source) {
        super(source);
    }

    @Override
    public Face pull() {
        Face f = source.pull();
        return transform(f);
    }

    private Face transform(Face f) {
        Vec4 factor = new Vec4(
                400,
                400,
                0,
                0
        );
        return new Face(
                f.getV1().add(factor),
                f.getV2().add(factor),
                f.getV3().add(factor),
                f
        );
    }
}

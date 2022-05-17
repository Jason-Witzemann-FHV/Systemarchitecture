package at.fhv.sysarch.lab3.pipeline.pull.filter;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.pull.IPull;
import at.fhv.sysarch.lab3.pipeline.pull.Pull;

/**
 * This class removes all dots in the back which are not seen by the camera. results in performance improvements
 */
public class PullBackfaceCulling<T extends Face> extends Pull<T, Face> {

    private Face nextFace = null;

    public PullBackfaceCulling(IPull<T> source) {
        super(source);
    }

    @Override
    public Face pull() {
        prepareNext();
        var temp = nextFace;
        nextFace = null;
        return temp;
    }

    @Override
    public boolean hasNext() {
        prepareNext();
        return nextFace != null;
    }

    private void prepareNext() {
        while(source.hasNext() && nextFace == null) {
            Face f = source.pull();
            if (f.getV1().dot(f.getN1()) > 0) {
                nextFace = f;
            }
        }
    }



}

package at.fhv.sysarch.lab3.pipeline.pull.filter;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.PipelineData;
import at.fhv.sysarch.lab3.pipeline.pull.IPull;
import at.fhv.sysarch.lab3.pipeline.pull.Pull;
import com.hackoeur.jglm.Mat4;

/**
 * This class applies the rotation of the model in the y axis
 */
public class PullModelRotation<T extends Face> extends Pull<T, Face> {

    private PipelineData pd;
    private Mat4 rotationMatrix;


    public PullModelRotation(PipelineData pd, IPull<T> source) {
        super(source);
        this.pd = pd;
    }

    @Override
    public Face pull() {
        Face f = source.pull();
        return rotate(f);
    }

    private Face rotate(Face f) {

        if(rotationMatrix == null) {
            throw new IllegalStateException("a rotation matrix must be set before using this filter");
        }

        return new Face(
                rotationMatrix.multiply(f.getV1()),
                rotationMatrix.multiply(f.getV2()),
                rotationMatrix.multiply(f.getV3()),
                rotationMatrix.multiply(f.getN1()),
                rotationMatrix.multiply(f.getN2()),
                rotationMatrix.multiply(f.getN3())
        );
    }

    public void updateRotationMatrix(Mat4 newRotation) {
        rotationMatrix = pd.getViewTransform().multiply(pd.getModelTranslation()).multiply(newRotation);

    }


}

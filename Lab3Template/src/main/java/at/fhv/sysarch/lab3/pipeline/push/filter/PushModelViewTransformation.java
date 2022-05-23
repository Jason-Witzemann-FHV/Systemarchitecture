package at.fhv.sysarch.lab3.pipeline.push.filter;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.push.IPush;
import at.fhv.sysarch.lab3.pipeline.push.Push;
import com.hackoeur.jglm.Mat4;

public class PushModelViewTransformation extends Push<Face, Face> {

    private final Mat4 viewTransform;
    private final Mat4 modelTranslation;
    private Mat4 rotationMatrix;

    public PushModelViewTransformation(IPush<Face> successor, Mat4 viewTransform, Mat4 modelTranslation) {
        super(successor);
        this.modelTranslation = modelTranslation;
        this.viewTransform = viewTransform;
    }

    @Override
    public void push(Face element) {
        successor.push(rotate(element));
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
        rotationMatrix = viewTransform.multiply(modelTranslation).multiply(newRotation);
    }
}

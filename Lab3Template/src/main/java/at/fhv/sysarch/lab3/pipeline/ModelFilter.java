package at.fhv.sysarch.lab3.pipeline;

import at.fhv.sysarch.lab3.obj.Face;

public class ModelFilter<I extends Face> implements IFilter<I> {

    private Pipe pipeSuccessor;

    public void setPipeSuccessor(Pipe pipe) {
        this.pipeSuccessor = pipe;
    }

    public void write(I face) {
        Face newFace = new Face(face.getV1().multiply(100), face.getV2().multiply(100), face.getV3().multiply(100), face);
        this.pipeSuccessor.write(newFace);
    }
}

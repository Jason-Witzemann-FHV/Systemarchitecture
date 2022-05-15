package at.fhv.sysarch.lab3.pipeline;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.obj.Model;

public class ModelSource<I extends Model> implements IFilter<I> {

    private Pipe pipeSuccessor;

    public void setPipeSuccessor(Pipe pipe) {
        this.pipeSuccessor = pipe;
    }

    public void write(I model) {
        for(Face face : model.getFaces()){
            // TODO: write face to next filter
            pipeSuccessor.write(face);
        }
    }
}

package at.fhv.sysarch.lab3.pipeline;

import at.fhv.sysarch.lab3.obj.Face;

// TODO: how can pipes be used for different data types?
public class Pipe {

    private IFilter successor;

    public void setSuccessor(IFilter sink) {
        this.successor = sink;
    }

    public void write(Face face) {
        this.successor.write(face);
    }
}

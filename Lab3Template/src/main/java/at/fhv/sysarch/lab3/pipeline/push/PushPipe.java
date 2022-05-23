package at.fhv.sysarch.lab3.pipeline.push;

public class PushPipe<E> extends Push<E, E> {

    public PushPipe(IPush<E> successor){
        super(successor);
    }

    @Override
    public void push(E e) {
        successor.push(e);
    }
}

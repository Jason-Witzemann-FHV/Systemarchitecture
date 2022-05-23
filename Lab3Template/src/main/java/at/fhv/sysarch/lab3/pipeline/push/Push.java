package at.fhv.sysarch.lab3.pipeline.push;
// T            ... Type of Elements in this class
// N            ... Type of Elements in next class
// successor    ... Next Element
public abstract class Push<T, N> implements IPush<T>{

    protected final IPush<N> successor;

    protected Push(IPush<N> successor) {
        this.successor = successor;
    }
}

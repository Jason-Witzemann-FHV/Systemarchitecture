package at.fhv.sysarch.lab3.pipeline.pull;
// T        ... Input of Element
// R        ... Output of Element
// source   ... Previous Element
public abstract class Pull<I, O> implements IPull<O> {

    protected final IPull<I> source;

    protected Pull(IPull<I> source) {
        this.source = source;
    }

    @Override
    public boolean hasNext() {
        return source.hasNext();
    }
}

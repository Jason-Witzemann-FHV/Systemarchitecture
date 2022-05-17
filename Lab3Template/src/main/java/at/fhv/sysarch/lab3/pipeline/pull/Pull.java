package at.fhv.sysarch.lab3.pipeline.pull;
// T        ... Input of Element
// R        ... Output of Element
// source   ... Previous Element
public abstract class Pull<T, R> implements IPull<R> {

    protected final IPull<T> source;

    protected Pull(IPull<T> source) {
        this.source = source;
    }

    @Override
    public boolean hasNext() {
        return source.hasNext();
    }
}

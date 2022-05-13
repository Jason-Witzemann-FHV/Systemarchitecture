package at.fhv.sysarch.lab3.pipeline.pull;

public abstract class Pull<T, R> implements IPull<R> {

    protected final IPull<T> source;

    protected Pull(IPull<T> source) {
        this.source = source;
    }
}

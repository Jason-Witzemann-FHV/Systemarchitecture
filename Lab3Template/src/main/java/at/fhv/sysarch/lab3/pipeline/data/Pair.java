package at.fhv.sysarch.lab3.pipeline.data;

public class Pair<T1, T2> {
    private T1 fst;
    private T2 snd;

    public Pair(T1 f, T2 s) {
        this.fst = f;
        this.snd = s;
    }

    public T1 fst() {
        return this.fst;
    }

    public T2 snd() {
        return this.snd;
    }
}
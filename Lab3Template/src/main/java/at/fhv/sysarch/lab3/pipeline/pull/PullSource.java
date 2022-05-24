package at.fhv.sysarch.lab3.pipeline.pull;

import at.fhv.sysarch.lab3.obj.Face;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class PullSource extends Pull<Face, Face>{
    private final Queue<Face> sourceData = new ArrayDeque<>();

    public PullSource() {
        super(null);
    }

    public void setSourceData(List<Face> sourceData) {
        this.sourceData.addAll(sourceData);
    }

    @Override
    public Face pull() {
        return sourceData.poll();
    }

    @Override
    public boolean hasNext() {
        return !sourceData.isEmpty();
    }
}

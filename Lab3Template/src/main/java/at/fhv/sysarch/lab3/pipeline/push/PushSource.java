package at.fhv.sysarch.lab3.pipeline.push;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import com.hackoeur.jglm.Vec4;
import javafx.scene.paint.Color;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class PushSource extends Push<Face, Face> {
    private Queue<Face> sourceData = new ArrayDeque<>();

    public PushSource(IPush<Face> successor) {
        super(successor);
    }

    public void setSourceData(List<Face> sourceData) {
        this.sourceData.addAll(sourceData);
        Vec4 dummyData = new Vec4(69,73,420,-100);
        this.sourceData.add(new Face(
                dummyData,
                dummyData,
                dummyData,
                dummyData,
                dummyData,
                dummyData));
        while (!this.sourceData.isEmpty()) {
            successor.push(this.sourceData.poll());
        }
    }

    @Override
    public void push(Face element) {
        throw new IllegalCallerException("PushSource is a source class of the push structure, therefore it cannot accept a push element. Use start() instead");
    }
}

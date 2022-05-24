package at.fhv.sysarch.lab3.pipeline.push.filter;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.push.IPush;
import at.fhv.sysarch.lab3.pipeline.push.Push;

import java.util.Comparator;
import java.util.LinkedList;

public class PushDepthSorting extends Push<Face, Face> {

    private final LinkedList<Face> sortedFaces = new LinkedList<>(); // use a linkedlist instead of list for #removeFirst() resulting in better performance

    public PushDepthSorting(IPush<Face> successor) {
        super(successor);
    }

    @Override
    public void push(Face element) {
        if (element.getV1().getW() == -100) {
            continuePipeline();
        } else {
            sortedFaces.add(element);
        }
    }

    private void continuePipeline() {
        sortedFaces.sort(Comparator.comparing(face -> face.getV1().getZ() + face.getV2().getZ() + face.getV3().getZ()));
        while (!sortedFaces.isEmpty()) {
            successor.push(sortedFaces.removeFirst());
        }
    }
}

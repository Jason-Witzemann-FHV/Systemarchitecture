package at.fhv.sysarch.lab3.pipeline.pull.filter;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.pull.IPull;
import at.fhv.sysarch.lab3.pipeline.pull.Pull;

import java.util.*;

/**
 * This class sorts all face by their z values and therefore assures that the front blocks the back from being visible
 */
public class PullDepthSorting<T extends Face> extends Pull<T, Face> {

    private LinkedList<Face> sortedFaces = new LinkedList<>(); // use a linkedlist instead of list for #getFirst() / #removeFirst() resulting in better performance

    public PullDepthSorting(IPull<T> source) {
        super(source);
    }

    @Override
    public Face pull() {
        var face = sortedFaces.getFirst();
        sortedFaces.removeFirst();
        return face;
    }

    @Override
    public boolean hasNext() {
        if (source.hasNext() && sortedFaces.isEmpty()) {
            sortNewFaces();
        }
        return !sortedFaces.isEmpty();
    }

    private void sortNewFaces() {
        while(source.hasNext()) {
            sortedFaces.add(source.pull());
        }
        Collections.sort(sortedFaces, Comparator.comparing(face -> face.getV1().getZ() + face.getV2().getZ() + face.getV3().getZ()));
    }



}

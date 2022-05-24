package at.fhv.sysarch.lab3.pipeline.push.filter;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.push.IPush;
import at.fhv.sysarch.lab3.pipeline.push.Push;

public class PushBackfaceCulling extends Push<Face, Face>{

    public PushBackfaceCulling(IPush<Face> successor) {
        super(successor);
    }

    @Override
    public void push(Face element) {
        if (element.getV1().getW() != -100) {
            if (element.getV1().dot(element.getN1()) < 0) {
                successor.push(element);
            }
        } else {
            successor.push(element);
        }
    }
}

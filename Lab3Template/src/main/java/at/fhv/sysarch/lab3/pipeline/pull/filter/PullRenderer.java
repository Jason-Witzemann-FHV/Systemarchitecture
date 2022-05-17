package at.fhv.sysarch.lab3.pipeline.pull.filter;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.PipelineData;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.pull.IPull;
import at.fhv.sysarch.lab3.pipeline.pull.Pull;
import javafx.scene.paint.Color;

/**
 * maps face to itself and its corresponding color
 */
public class PullRenderer<T extends Pair<Face, Color>> extends Pull<T, Pair<Face, Color>> {

    private final PipelineData pd;

    public PullRenderer(PipelineData pd, IPull<T> source) {
        super(source);
        this.pd = pd;
    }

    @Override
    public Pair<Face, Color> pull() {
        throw new IllegalCallerException("PullRenderer is a sink class of the pull structure, therefore it cannot be pulled");
    }

    @Override
    public boolean hasNext() {
        throw new IllegalCallerException("PullRenderer is a sink class of the pull structure, therefore it cannot operate has next");
    }

    public void doRender() {
        while(source.hasNext()) {
            Pair<Face, Color> pair = source.pull();
            Color color = pair.snd();
            pd.getGraphicsContext().setStroke(color);
            pd.getGraphicsContext().setFill(color);

            Face f = pair.fst();
            var cordX = new double[]{ f.getV1().getX(), f.getV2().getX(), f.getV3().getX() };
            var cordY = new double[]{ f.getV1().getY(), f.getV2().getY(), f.getV3().getY() };

            var ctx = pd.getGraphicsContext();
            switch ((pd.getRenderingMode())) {
                case POINT -> ctx.fillOval(cordX[0], cordY[0], 2, 2);
                case WIREFRAME -> ctx.strokePolygon(cordX, cordY, 3);
                case FILLED -> {
                    ctx.fillPolygon(cordX, cordY, 3);
                    ctx.strokePolygon(cordX, cordY, 3);
                }
            }
        }
    }
}

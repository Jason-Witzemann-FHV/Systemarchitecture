package at.fhv.sysarch.lab3.pipeline;

import at.fhv.sysarch.lab3.animation.AnimationRenderer;
import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.obj.Model;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.pull.*;
import at.fhv.sysarch.lab3.pipeline.pull.filter.*;
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec4;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;

public class PullPipelineFactory {
    public static AnimationTimer createPipeline(PipelineData pd) {

        // TODO: pull from the source (model)
        PullSource source = new PullSource();
        PullPipe<Face> toModelRotation = new PullPipe<>(source);

        // TODO 1. perform model-view transformation from model to VIEW SPACE coordinates
        PullModelRotation pullModelRotationFilter = new PullModelRotation(pd, toModelRotation);
        PullPipe<Face> toModelTransformPipe = new PullPipe<>(pullModelRotationFilter);

        PullModelTransformation pullModelTransformation = new PullModelTransformation(toModelTransformPipe);
        PullPipe<Face> toViewTransformation = new PullPipe<>(pullModelTransformation);

        PullViewTransformation pullViewTransformation = new PullViewTransformation(toViewTransformation);
        PullPipe<Face> afterViewTransformationPipe = new PullPipe<>(pullViewTransformation);

        // TODO 2. perform backface culling in VIEW SPACE
        PullBackfaceCulling pullBackfaceCulling = new PullBackfaceCulling(afterViewTransformationPipe);
        PullPipe<Face> toDepthSortingPipeline = new PullPipe<>(pullBackfaceCulling);

        // TODO 3. perform depth sorting in VIEW SPACE
        PullDepthSorting pullDepthSorting = new PullDepthSorting(toDepthSortingPipeline);
        PullPipe<Face> toColorPipeline = new PullPipe<>(pullDepthSorting);

        // TODO 4. add coloring (space unimportant)
        PullModelColor pullModelColor = new PullModelColor(pd, toColorPipeline);


        // lighting can be switched on/off
        if (pd.isPerformLighting()) {
            // 4a. TODO perform lighting in VIEW SPACE
            
            // 5. TODO perform projection transformation on VIEW SPACE coordinates
        } else {
            // 5. TODO perform projection transformation
        }

        // TODO 6. perform perspective division to screen coordinates

        // TODO 7. feed into the sink (renderer)

        // returning an animation renderer which handles clearing of the
        // viewport and computation of the praction
        return new AnimationRenderer(pd) {

            // TODO - done - rotation variable goes in here
            float totalRotation = 0;

            /** This method is called for every frame from the JavaFX Animation
             * system (using an AnimationTimer, see AnimationRenderer). 
             * @param fraction the time which has passed since the last render call in a fraction of a second
             * @param model    the model to render 
             */
            @Override
            protected void render(float fraction, Model model) {

                // TODO - done -  compute rotation in radians
                totalRotation += fraction;
                double rad = totalRotation % (2 * Math.PI);

                // TODO - done - create new model rotation matrix using pd.getModelRotAxis and Matrices.rotate
                var rotationMatix = Matrices.rotate((float) rad, pd.getModelRotAxis());

                // TODO - done - compute updated model-view tranformation
                pullModelRotationFilter.updateRotationMatrix(rotationMatix);

                // TODO update model-view filter
                // i guess it works not like that in our project?

                // TODO - done - trigger rendering of the pipeline
                source.setSourceData(model.getFaces());

                // yup that needs to be in a filter, we know
                while(pullModelColor.hasNext()) {

                    Pair<Face, Color> pair = pullModelColor.pull();
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
        };
    }
}
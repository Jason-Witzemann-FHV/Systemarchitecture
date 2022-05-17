package at.fhv.sysarch.lab3.pipeline;

import at.fhv.sysarch.lab3.animation.AnimationRenderer;
import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.obj.Model;
import at.fhv.sysarch.lab3.pipeline.pull.*;
import at.fhv.sysarch.lab3.pipeline.pull.filter.PullModelTransformation;
import at.fhv.sysarch.lab3.pipeline.pull.filter.PullViewTransformation;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;

public class PullPipelineFactory {
    public static AnimationTimer createPipeline(PipelineData pd) {

        // TODO: pull from the source (model)
        PullSource source = new PullSource();
        PullPipe<Face> toModelTransformation = new PullPipe<>(source);

        // TODO 1. perform model-view transformation from model to VIEW SPACE coordinates
        PullModelTransformation pullModelTransformation = new PullModelTransformation(toModelTransformation);
        PullPipe<Face> toViewTransformation = new PullPipe<>(pullModelTransformation);

        PullViewTransformation pullViewTransformation = new PullViewTransformation(toViewTransformation);

        // TODO 2. perform backface culling in VIEW SPACE



        // TODO 3. perform depth sorting in VIEW SPACE

        // TODO 4. add coloring (space unimportant)

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
            // TODO rotation variable goes in here

            /** This method is called for every frame from the JavaFX Animation
             * system (using an AnimationTimer, see AnimationRenderer). 
             * @param fraction the time which has passed since the last render call in a fraction of a second
             * @param model    the model to render 
             */
            @Override
            protected void render(float fraction, Model model) {

                source.setSourceData(model.getFaces());
                pd.getGraphicsContext().setStroke(Color.YELLOW);
                while(pullViewTransformation.hasNext()) {
                    Face f = pullViewTransformation.pull();
                    pd.getGraphicsContext().strokeLine(f.getV1().getX(), f.getV1().getY(), f.getV1().getX(), f.getV1().getY());
                    pd.getGraphicsContext().strokeLine(f.getV2().getX(), f.getV2().getY(), f.getV2().getX(), f.getV2().getY());
                    pd.getGraphicsContext().strokeLine(f.getV3().getX(), f.getV3().getY(), f.getV3().getX(), f.getV3().getY());
                }

                // TODO compute rotation in radians

                // TODO create new model rotation matrix using pd.getModelRotAxis and Matrices.rotate

                // TODO compute updated model-view tranformation

                // TODO update model-view filter

                // TODO trigger rendering of the pipeline
            }
        };
    }
}
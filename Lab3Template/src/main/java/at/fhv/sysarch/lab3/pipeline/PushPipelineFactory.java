package at.fhv.sysarch.lab3.pipeline;

import at.fhv.sysarch.lab3.animation.AnimationRenderer;
import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.obj.Model;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.push.PushPipe;
import at.fhv.sysarch.lab3.pipeline.push.filter.PushModelViewTransformation;
import at.fhv.sysarch.lab3.pipeline.push.filter.PushRenderer;
import at.fhv.sysarch.lab3.pipeline.push.PushSource;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;

public class PushPipelineFactory {
    public static AnimationTimer createPipeline(PipelineData pd) {
        PushRenderer sink = new PushRenderer(pd.getGraphicsContext(), pd.getRenderingMode());
        PushPipe<Pair<Face, Color>> toDebug = new PushPipe<>(sink);

        PushPipe<Face> toBackFaceCulling = new PushPipe<>();
        PushModelViewTransformation pushModelViewTransformation = new PushModelViewTransformation(toDebug,pd.getViewTransform(),pd.getModelTranslation())
        PushSource source = new PushSource(toDebug);

        // TODO: push from the source (model)

        // TODO: the connection of filters and pipes requires a lot of boilerplate code. Think about options how this can be minimized

        // TODO 1. perform model-view transformation from model to VIEW SPACE coordinates

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
            private int pos = 0;

            /** This method is called for every frame from the JavaFX Animation
             * system (using an AnimationTimer, see AnimationRenderer).
             * @param fraction the time which has passed since the last render call in a fraction of a second
             * @param model    the model to render
             */
            @Override
            protected void render(float fraction, Model model) {
                source.setSourceData(model.getFaces());
                // TODO compute rotation in radians

                // TODO create new model rotation matrix using pd.modelRotAxis

                // TODO compute updated model-view tranformation

                // TODO update model-view filter

                // TODO trigger rendering of the pipeline

            }
        };
    }
}
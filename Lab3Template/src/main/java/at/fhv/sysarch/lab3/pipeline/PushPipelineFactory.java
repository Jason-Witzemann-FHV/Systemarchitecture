package at.fhv.sysarch.lab3.pipeline;

import at.fhv.sysarch.lab3.animation.AnimationRenderer;
import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.obj.Model;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.push.PushPipe;
import at.fhv.sysarch.lab3.pipeline.push.filter.*;
import at.fhv.sysarch.lab3.pipeline.push.PushSource;
import com.hackoeur.jglm.Matrices;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;

public class PushPipelineFactory {
    public static AnimationTimer createPipeline(PipelineData pd) {
        // The pipeline here is inverse, because we need to successor of each element in its own Constructor
        // All the Elements (except for the PushPipe) have their Generic statically implemented, because they serve only one purpose

        PushRenderer renderer = new PushRenderer(pd.getGraphicsContext(), pd.getRenderingMode());

        // 7. feed into the sink (renderer)
        PushPipe<Pair<Face, Color>> toRenderer = new PushPipe<>(renderer);
        PushScreenSpaceTransformation pushScreenSpaceTransformation = new PushScreenSpaceTransformation(toRenderer, pd.getViewportTransform());

        // 6. perform perspective division to screen coordinates
        PushPipe<Pair<Face, Color>> toScreenSpaceTransformation = new PushPipe<>(pushScreenSpaceTransformation);
        PushProjectionTransformation pushProjectionTransformation = new PushProjectionTransformation(toScreenSpaceTransformation, pd.getProjTransform());

        // 5. perform projection transformation
        PushPipe<Pair<Face, Color>> toProjectionOrLighting;

        // lighting can be switched on/off
        if(pd.isPerformLighting()) {
            // 4a. perform lighting in VIEW SPACE
            PushPipe<Pair<Face, Color>> toProjectionTransformation = new PushPipe<>(pushProjectionTransformation);
            PushLighting pushLighting = new PushLighting(toProjectionTransformation ,pd.getLightPos().getUnitVector());

            // 5. perform projection transformation on VIEW SPACE coordinates
            toProjectionOrLighting = new PushPipe<>(pushLighting);
        } else {

            // 5. perform projection transformation
            toProjectionOrLighting = new PushPipe<>(pushProjectionTransformation);
        }

        // 4. add coloring (space unimportant)
        PushModelColor pushModelColor = new PushModelColor(toProjectionOrLighting, pd.getModelColor());

        // 3. perform depth sorting in VIEW SPACE
        PushPipe<Face> toModelColor = new PushPipe<>(pushModelColor);
        PushDepthSorting pushDepthSorting = new PushDepthSorting(toModelColor);

        // 2. perform backface culling in VIEW SPACE
        PushPipe<Face> toDepthSorting = new PushPipe<>(pushDepthSorting);
        PushBackfaceCulling pushBackfaceCulling = new PushBackfaceCulling(toDepthSorting);

        // 1. perform model-view transformation from model to VIEW SPACE coordinates
        PushPipe<Face> toBackfaceCulling = new PushPipe<>(pushBackfaceCulling);
        PushModelViewTransformation pushModelViewTransformation = new PushModelViewTransformation(toBackfaceCulling,pd.getViewTransform(),pd.getModelTranslation());

        // push from the source (model)
        PushPipe<Face> toModelViewTransformation = new PushPipe<>(pushModelViewTransformation);
        PushSource source = new PushSource(toModelViewTransformation);

        // returning an animation renderer which handles clearing of the
        // viewport and computation of the praction
        return new AnimationRenderer(pd) {

            // rotation variable goes in here
            float totalRotation = 0;

            /** This method is called for every frame from the JavaFX Animation
             * system (using an AnimationTimer, see AnimationRenderer).
             * @param fraction the time which has passed since the last render call in a fraction of a second
             * @param model    the model to render
             */
            @Override
            protected void render(float fraction, Model model) {
                // compute rotation in radians
                totalRotation += fraction;
                double rad = totalRotation % (2 * Math.PI);

                // create new model rotation matrix using pd.getModelRotAxis and Matrices.rotate
                var rotationMatrix = Matrices.rotate((float) rad, pd.getModelRotAxis());

                // compute updated model-view tranformation
                pushModelViewTransformation.updateRotationMatrix(rotationMatrix);

                //set data and start pipelineframe
                source.setSourceData(model.getFaces());
            }
        };
    }
}
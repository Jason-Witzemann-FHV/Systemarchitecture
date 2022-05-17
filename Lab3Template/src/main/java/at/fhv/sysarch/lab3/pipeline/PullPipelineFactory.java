package at.fhv.sysarch.lab3.pipeline;

import at.fhv.sysarch.lab3.animation.AnimationRenderer;
import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.obj.Model;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.pull.*;
import at.fhv.sysarch.lab3.pipeline.pull.filter.*;
import com.hackoeur.jglm.Matrices;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;

public class PullPipelineFactory {
    public static AnimationTimer createPipeline(PipelineData pd) {

        // pull from the source (model)
        PullSource source = new PullSource();
        PullPipe<Face> toModelTransformation = new PullPipe<>(source);

        // 1. perform model-view transformation from model to VIEW SPACE coordinates
        PullModelViewTransformation<Face> pullModelViewTransformationFilter = new PullModelViewTransformation<>(pd, toModelTransformation);
        PullPipe<Face> toModelTransformPipe = new PullPipe<>(pullModelViewTransformationFilter);

        // 2. perform backface culling in VIEW SPACE
        PullBackfaceCulling<Face> pullBackfaceCulling = new PullBackfaceCulling<>(toModelTransformPipe);
        PullPipe<Face> toDepthSortingPipeline = new PullPipe<>(pullBackfaceCulling);

        // 3. perform depth sorting in VIEW SPACE
        PullDepthSorting<Face> pullDepthSorting = new PullDepthSorting<>(toDepthSortingPipeline);
        PullPipe<Face> toColorPipeline = new PullPipe<>(pullDepthSorting);

        // 4. add coloring (space unimportant)
        PullModelColor<Face> pullModelColor = new PullModelColor<>(pd, toColorPipeline);

        // lighting can be switched on/off
        PullPipe<Pair<Face, Color>> afterLightingOrColoring;
        if (pd.isPerformLighting()) {
            // 4a. perform lighting in VIEW SPACE
            PullPipe<Pair<Face, Color>> toLighting = new PullPipe<>(pullModelColor);
            PullLighting<Pair<Face, Color>> pullLighting = new PullLighting<>(pd, toLighting);
            afterLightingOrColoring = new PullPipe<>(pullLighting);
        } else {
            afterLightingOrColoring = new PullPipe<>(pullModelColor);
        }

        // 5. perform projection transformation
        PullProjectionTransformation<Pair<Face, Color>> pullProjectionTransformation = new PullProjectionTransformation<>(pd, afterLightingOrColoring);
        PullPipe<Pair<Face, Color>> afterProjectTransformationPipe = new PullPipe<>(pullProjectionTransformation);

        // 6. perform perspective division to screen coordinates
        PullScreenSpaceTransformation<Pair<Face, Color>> pullScreenSpaceTransformation = new PullScreenSpaceTransformation<>(pd, afterProjectTransformationPipe);
        PullPipe<Pair<Face, Color>> afterScreenSpaceTransformation = new PullPipe<>(pullScreenSpaceTransformation);

        // 7. feed into the sink (renderer)
        PullRenderer<Pair<Face, Color>> pullRenderer = new PullRenderer<>(pd, afterScreenSpaceTransformation);

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
                pullModelViewTransformationFilter.updateRotationMatrix(rotationMatrix);

                // update model-view filter
                source.setSourceData(model.getFaces());

                // trigger rendering of the pipeline
                pullRenderer.doRender();

            }
        };
    }
}
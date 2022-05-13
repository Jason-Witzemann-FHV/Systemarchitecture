package at.fhv.sysarch.lab3.animation;

import at.fhv.sysarch.lab3.obj.Model;
import at.fhv.sysarch.lab3.pipeline.PipelineData;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;

public abstract class AnimationRenderer extends AnimationTimer {
    private long lastUpdate;

    private PipelineData pd;

    public AnimationRenderer(PipelineData pd) {
        this.pd = pd;
    }

    /** This method is called for every frame from the JavaFX Animation
     * system. The extending class can update any model transformations,
     * e.g. model rotations, based on the fraction so it is independent of 
     * framerate. The extending class is then expected to perform the 
     * actual rendering as a side effect. 
     * 
     * @param fraction the time which has passed since the last render call in a fraction of a second
     * @param model    the model to render 
     */
    protected abstract void render(float fraction, Model m);

    // is called by the JavaFX animation system for every frame. This class
    // simply computes the fraction, clears the viewport and calls the 
    // render method in the extending class to perform transformation updates
    // and rendering the model.
    @Override
    public void handle(long now) {
        if (lastUpdate > 0) {
            float fraction = (now - lastUpdate) / 1000_000_000.0f;
            float fps =  1 / fraction;

            this.pd.getGraphicsContext().clearRect(0, 0, pd.getViewWidth(), pd.getViewHeight());
            this.pd.getGraphicsContext().setFill(Color.WHITE);
            this.pd.getGraphicsContext().fillText(String.format("%,.2f", fps) + " FPS", 10, 10);
            
            this.render(fraction, pd.getModel());
        }

        this.lastUpdate = now;
    }
}
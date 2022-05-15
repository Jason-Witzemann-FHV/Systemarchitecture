package at.fhv.sysarch.lab3.pipeline;

import java.util.Objects;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;

import at.fhv.sysarch.lab3.obj.Model;
import at.fhv.sysarch.lab3.rendering.RenderingMode;
import at.fhv.sysarch.lab3.utils.MatrixUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PipelineData {
    public static class Builder {
        private Canvas c;
        private Model m;
        private int viewWidth;
        private int viewHeight;

        // optional
        private RenderingMode rm;
        private boolean performLighting;

        private Vec3 lightPos;

        private Vec3 modelPos;
        private Vec3 modelRotAxis;
        private Color modelColor;

        private Vec3 viewingCenter;
        private Vec3 viewingUp;
        private Vec3 viewingEye;

        public Builder(Canvas c, Model m, int width, int height) {
            Objects.requireNonNull(c);
            Objects.requireNonNull(m);

            if (width <= 0) {
                throw new IllegalArgumentException("width has to be > 0");
            }

            if (height <= 0) {
                throw new IllegalArgumentException("height has to be > 0");
            }

            // these are mandatory
            this.c = c;
            this.m = m;
            this.viewWidth = width;
            this.viewHeight = height;

            // default values
            this.modelColor = Color.WHITE;
            this.rm = RenderingMode.POINT; 
            this.performLighting = false;

            this.lightPos     = new Vec3(10, 10, 10);

            this.modelPos     = new Vec3(0, -2, 0);
            this.modelRotAxis = new Vec3(0, 1, 0);

            this.viewingCenter = new Vec3(0, 0, -5);
            this.viewingUp     = new Vec3(0, 1, 0);
            this.viewingEye    = new Vec3(0, 0, 5);

        }

        public Builder setRenderingMode(RenderingMode rm) {
            this.rm = rm;
            return this;
        }

        public Builder setPerformLighting(boolean flag) {
            this.performLighting = flag;
            return this;
        }

        public Builder setModelColor(Color c) {
            this.modelColor = c;
            return this;
        }

        public PipelineData build() {
            return new PipelineData(this);
        }
    }

    // the GraphicsContext where to render
    private GraphicsContext graphicsContext;
    // the mode which to render
    private Model model;

    // lightpos in VIEW SPACE!
    private Vec3 lightPos;
    // indicates if the pipline should perform lighting
    private boolean performLighting;

    // the model position in WORLD space (for this example its 0/0/0 in all cases)
    private Vec3 modelPos;
    // the model rotation axis (for this example its 0/1/0 in call cases)
    private Vec3 modelRotAxis;
    // the model color
    private Color modelColor;

    // the point at which the camera is looking at
    private Vec3 viewingCenter;
    // the up vector of the camera
    private Vec3 viewingUp;
    // the position of the camera in WORLD space
    private Vec3 viewingEye;

    // the viewport width 
    private int viewWidth;
    // the viewport height
    private int viewHeight;

    // the initial model translation to the modelPos (see above)
    private Mat4 modelTranslation;
    // the viewing transformation
    private Mat4 viewTransform;
    // the projection transformation
    private Mat4 projTransform;
    // the viewport transformation
    private Mat4 viewportTransform;

    // the rendering mode
    private RenderingMode renderingMode;

    private PipelineData(Builder builder) {

      this.graphicsContext = builder.c.getGraphicsContext2D();
      this.model = builder.m;

      this.renderingMode = builder.rm;
      this.performLighting = builder.performLighting;
      
      // lightpos in VIEW SPACE!
      this.lightPos = builder.lightPos;

      this.modelPos     = builder.modelPos;
      this.modelRotAxis = builder.modelRotAxis;
      this.modelColor   = builder.modelColor;

      this.viewingCenter = builder.viewingCenter;
      this.viewingUp     = builder.viewingUp;
      this.viewingEye    = builder.viewingEye;

      this.viewWidth  = builder.viewWidth;
      this.viewHeight = builder.viewHeight;

      float ratio     = viewWidth / viewHeight; // ratio
      float fov       = 90.0f; // field of view in angles
      float nearPlane = 0.1f; // near clipping plane, > 0
      float farPlane  = 100.0f; // far clipping plane

      this.modelTranslation = MatrixUtils.translationMatrix(builder.modelPos);
      this.projTransform = Matrices.perspective(fov, ratio, nearPlane, farPlane);
      this.viewportTransform = MatrixUtils.viewportMatrix(builder.viewWidth, builder.viewHeight);
      this.viewTransform = Matrices.lookAt(builder.viewingEye, builder.viewingCenter, builder.viewingUp);
    }

    public GraphicsContext getGraphicsContext() {
        return graphicsContext;
    }

    public Model getModel() {
        return model;
    }

    public Vec3 getLightPos() {
        return lightPos;
    }

    public boolean isPerformLighting() {
        return performLighting;
    }

    public Vec3 getModelPos() {
        return modelPos;
    }

    public Vec3 getModelRotAxis() {
        return modelRotAxis;
    }

    public Color getModelColor() {
        return modelColor;
    }

    public Vec3 getViewingCenter() {
        return viewingCenter;
    }

    public Vec3 getViewingUp() {
        return viewingUp;
    }

    public Vec3 getViewingEye() {
        return viewingEye;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public Mat4 getModelTranslation() {
        return modelTranslation;
    }

    public Mat4 getViewTransform() {
        return viewTransform;
    }

    public Mat4 getProjTransform() {
        return projTransform;
    }

    public Mat4 getViewportTransform() {
        return viewportTransform;
    }

    public RenderingMode getRenderingMode() {
        return renderingMode;
    }
}
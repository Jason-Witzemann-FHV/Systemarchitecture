package at.fhv.sysarch.lab3;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import at.fhv.sysarch.lab3.obj.Model;
import at.fhv.sysarch.lab3.obj.ObjLoader;
import at.fhv.sysarch.lab3.pipeline.PipelineData;
import at.fhv.sysarch.lab3.pipeline.PullPipelineFactory;
import at.fhv.sysarch.lab3.pipeline.PushPipelineFactory;
import at.fhv.sysarch.lab3.rendering.RenderingMode;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
    private final static int VIEW_WIDTH  = 860;
    private final static int VIEW_HEIGHT = 540;

    private final static int SCENE_WIDTH  = VIEW_WIDTH * 2;
    private final static int SCENE_HEIGHT = VIEW_HEIGHT * 2;

    private final static boolean USE_PUSH_PIPELINE = false;
    
    @Override
    public void start(Stage stage) throws IOException {
        File f = new File("resources/teapot.obj");
        Optional<Model> om = ObjLoader.loadModel(f);

        Group root = new Group();
        Scene s = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT, Color.BLACK);
        
        final Canvas c1 = new Canvas(VIEW_WIDTH, VIEW_HEIGHT);
        final Canvas c2 = new Canvas(VIEW_WIDTH, VIEW_HEIGHT);
        final Canvas c3 = new Canvas(VIEW_WIDTH, VIEW_HEIGHT);
        final Canvas c4 = new Canvas(VIEW_WIDTH, VIEW_HEIGHT);

        GridPane grid = new GridPane();
        grid.add(c1, 0, 0);
        grid.add(c2, 1, 0);
        grid.add(c3, 0, 1);
        grid.add(c4, 1, 1);

        root.getChildren().add(grid);
        stage.setScene(s);
        stage.setTitle("Simple CG Pipeline");
        stage.show();

        om.ifPresent(m -> {
            PipelineData pd1 = new PipelineData.Builder(c1, m, VIEW_WIDTH, VIEW_HEIGHT)
                                    .setModelColor(Color.ORANGE)
                                    .build();

            PipelineData pd2 = new PipelineData.Builder(c2, m, VIEW_WIDTH, VIEW_HEIGHT)
                                    .setModelColor(Color.DARKGREEN)
                                    .setRenderingMode(RenderingMode.WIREFRAME)
                                    .build();

            PipelineData pd3 = new PipelineData.Builder(c3, m, VIEW_WIDTH, VIEW_HEIGHT)
                                    .setModelColor(Color.RED)
                                    .setRenderingMode(RenderingMode.FILLED)
                                    .build();

            PipelineData pd4 = new PipelineData.Builder(c4, m, VIEW_WIDTH, VIEW_HEIGHT)
                                    .setModelColor(Color.BLUE)
                                    .setRenderingMode(RenderingMode.FILLED)
                                    .setPerformLighting(true)
                                    .build();

            AnimationTimer anim1;
            AnimationTimer anim2;
            AnimationTimer anim3;
            AnimationTimer anim4;

            if (USE_PUSH_PIPELINE) {
                anim1 = PushPipelineFactory.createPipeline(pd1);
                anim2 = PushPipelineFactory.createPipeline(pd2);
                anim3 = PushPipelineFactory.createPipeline(pd3);
                anim4 = PushPipelineFactory.createPipeline(pd4);

            } else {
                anim1 = PullPipelineFactory.createPipeline(pd1);
                anim2 = PullPipelineFactory.createPipeline(pd2);
                anim3 = PullPipelineFactory.createPipeline(pd3);
                anim4 = PullPipelineFactory.createPipeline(pd4);
            }

            anim1.start();
            anim2.start();
            anim3.start();
            anim4.start();
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
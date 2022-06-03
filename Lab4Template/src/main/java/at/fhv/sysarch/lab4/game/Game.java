package at.fhv.sysarch.lab4.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.fhv.sysarch.lab4.physics.BallPocketedListener;
import at.fhv.sysarch.lab4.physics.Physics;
import at.fhv.sysarch.lab4.rendering.Renderer;
import javafx.scene.input.MouseEvent;
import org.dyn4j.dynamics.RaycastResult;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;

public class Game implements BallPocketedListener {
    private final Renderer renderer;
    private final Physics physics;

    private double cueStartX;
    private double cueStartY;

    public Game(Renderer renderer, Physics physics) {
        this.renderer = renderer;
        this.physics = physics;
        physics.setPocketedListener(this);
        this.initWorld();
    }

    public void onMousePressed(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        cueStartX = this.renderer.screenToPhysicsX(x);
        cueStartY = this.renderer.screenToPhysicsY(y);

        this.renderer.setCueStart(x, y);
    }

    public void onMouseReleased(MouseEvent e) {
        this.renderer.releaseCue();

        double x = e.getX();
        double y = e.getY();

        double cueEndX = this.renderer.screenToPhysicsX(x);
        double cueEndY = this.renderer.screenToPhysicsY(y);

        Ray ray = new Ray(new Vector2(cueStartX, cueStartY), new Vector2(cueStartX - cueEndX, cueStartY- cueEndY));
        ArrayList<RaycastResult> results = new ArrayList<>();
        this.physics.getWorld().raycast(ray, 0.3, false, true, results);

        var hitWhite = results.stream().filter(o -> o.getBody().getUserData() instanceof Ball).findFirst();
        if(hitWhite.isPresent()) {
            var white = hitWhite.get().getBody();
            System.out.println("We hit something");
            double dx = cueStartX - cueEndX;
            double dy = cueStartY - cueEndY;
            double strength = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
            white.applyForce(new Vector2(dx, dy).multiply(Math.min(strength * 1000, 700)));
        }
    }

    public void setOnMouseDragged(MouseEvent e) {

        double x = e.getX();
        double y = e.getY();

        double pX = renderer.screenToPhysicsX(x);
        double pY = renderer.screenToPhysicsY(y);

        this.renderer.setCueEnd(x, y);
    }

    private void placeBalls(List<Ball> balls) {
        Collections.shuffle(balls);

        // positioning the billard balls IN WORLD COORDINATES: meters
        int row = 0;
        int col = 0;
        int colSize = 5;

        double y0 = -2*Ball.Constants.RADIUS*2;
        double x0 = -Table.Constants.WIDTH * 0.25 - Ball.Constants.RADIUS;

        for (Ball b : balls) {
            double y = y0 + (2 * Ball.Constants.RADIUS * row) + (col * Ball.Constants.RADIUS);
            double x = x0 + (2 * Ball.Constants.RADIUS * col);

            b.setPosition(x, y);
            b.getBody().setLinearVelocity(0, 0);
            renderer.addBall(b);

            row++;

            if (row == colSize) {
                row = 0;
                col++;
                colSize--;
            }
        }
    }

    private void initWorld() {
        List<Ball> balls = new ArrayList<>();
        
        for (Ball b : Ball.values()) {
            if (b == Ball.WHITE)
                continue;
            physics.getWorld().addBody(b.getBody());
            balls.add(b);
        }
        this.placeBalls(balls);

        Ball.WHITE.setPosition(Table.Constants.WIDTH * 0.25, 0);
        physics.getWorld().addBody(Ball.WHITE.getBody());
        renderer.addBall(Ball.WHITE);
        
        Table table = new Table();
        physics.getWorld().addBody(table.getBody());
        renderer.setTable(table);
    }

    @Override
    public boolean onBallPocketed(Ball b) {
        System.out.println("Pocketed");
        renderer.removeBall(b);
        return false;
    }
}
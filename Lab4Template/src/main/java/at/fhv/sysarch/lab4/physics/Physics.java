package at.fhv.sysarch.lab4.physics;

import at.fhv.sysarch.lab4.game.Ball;
import javafx.scene.paint.Color;
import org.dyn4j.dynamics.*;
import org.dyn4j.dynamics.contact.ContactListener;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.contact.PersistedContactPoint;
import org.dyn4j.dynamics.contact.SolvedContactPoint;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Physics implements ContactListener, StepListener {

    private World world;

    private BallPocketedListener pocketedListener;

    private BallsRestListener ballsRestListener;

    private BallsCollisionListener ballsCollisionListener;

    public Physics() {
        this.world = new World();
        this.world.setGravity(World.ZERO_GRAVITY);
        this.world.addListener(this);
    }

    public World getWorld() {
        return world;
    }

    public void setPocketedListener(BallPocketedListener pocketedListener) {
        this.pocketedListener = pocketedListener;
    }

    public void setBallsRestListener(BallsRestListener ballsRestListener) {
        this.ballsRestListener = ballsRestListener;
    }

    public void setBallsCollisionListener(BallsCollisionListener ballsCollisionListener) {
        this.ballsCollisionListener = ballsCollisionListener;
    }

    @Override
    public void begin(Step step, World world) {
    }

    @Override
    public void updatePerformed(Step step, World world) {

    }

    @Override
    public void postSolve(Step step, World world) {

    }

    @Override
    public void end(Step step, World world) {
        AtomicInteger activeBalls = new AtomicInteger();
        world.getBodies().forEach(b -> {
                    if (b.getLinearVelocity().equals(new Vector2(0,0)))
                        activeBalls.getAndIncrement();
                }
        );
        if (activeBalls.get() == world.getBodyCount()) {
            ballsRestListener.objectsAreResting();

            if (world.getBodyCount() == 2) {
                pocketedListener.allBallsPocketed();
            }
        }
    }

    @Override
    public void sensed(ContactPoint point) {

    }

    @Override
    public boolean begin(ContactPoint point) {
        var body1 = point.getBody1().getUserData();
        var body2 = point.getBody2().getUserData();

        if(body1 instanceof Ball && body2 instanceof Ball) {
            ballsCollisionListener.ballsTouched((Ball) body1, (Ball) body2);
        }
        return true;
    }

    @Override
    public void end(ContactPoint point) {

    }

    @Override
    public boolean persist(PersistedContactPoint point) {
        if(point.isSensor()) {
            var body1 = point.getBody1();
            var body2 = point.getBody2();

            Body ball;
            Body pocket;

            if (body1.getUserData() instanceof Ball) {
                ball = body1;
                pocket = body2;
            } else {
                ball = body2;
                pocket = body1;
            }

            if (pocket.contains(ball.getWorldCenter())) {
                pocketedListener.onBallPocketed((Ball) ball.getUserData());
            }
        }
        return true;
    }

    @Override
    public boolean preSolve(ContactPoint point) {
        return true;
    }

    @Override
    public void postSolve(SolvedContactPoint point) {

    }

    public List<RaycastResult> strike(double cueStartX, double cueStartY, double cueEndX, double cueEndY) {
        Vector2 startPoint = new Vector2(cueStartX, cueStartY);
        Vector2 direction = new Vector2(cueStartX - cueEndX, cueStartY- cueEndY);

        ArrayList<RaycastResult> results = new ArrayList<>();
        // Ray must have a direction
        if (!direction.equals(new Vector2(0,0))) {

            // cast Ray
            Ray ray = new Ray(startPoint, direction);
            world.raycast(ray, 0.2, false, true, results);
        }
        return results;
    }

    public void hitBall(Body ball, double cueStartX, double cueStartY, double cueEndX, double cueEndY) {
        double dx = cueStartX - cueEndX;
        double dy = cueStartY - cueEndY;
        double strength = Math.min(Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)) * 1000, 400);
        ball.applyForce(new Vector2(dx, dy).multiply(strength));
    }
}

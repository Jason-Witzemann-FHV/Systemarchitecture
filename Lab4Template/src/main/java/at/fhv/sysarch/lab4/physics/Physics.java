package at.fhv.sysarch.lab4.physics;

import at.fhv.sysarch.lab4.game.Ball;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.Step;
import org.dyn4j.dynamics.StepListener;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.contact.ContactListener;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.contact.PersistedContactPoint;
import org.dyn4j.dynamics.contact.SolvedContactPoint;
import org.dyn4j.geometry.Vector2;

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
}

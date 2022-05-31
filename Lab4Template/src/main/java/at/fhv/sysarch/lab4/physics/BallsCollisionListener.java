package at.fhv.sysarch.lab4.physics;

import at.fhv.sysarch.lab4.game.Ball;

public interface BallsCollisionListener {
    public void onBallsCollide(Ball b1, Ball b2);
}
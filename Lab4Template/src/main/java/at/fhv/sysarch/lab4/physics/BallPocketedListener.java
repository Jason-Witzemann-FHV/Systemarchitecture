package at.fhv.sysarch.lab4.physics;

import at.fhv.sysarch.lab4.game.Ball;

public interface BallPocketedListener {
    boolean onBallPocketed(Ball b);

    boolean allBallsPocketed();
}
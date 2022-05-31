package at.fhv.sysarch.lab4.game;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;

import javafx.scene.paint.Color;

public enum Ball {
    ONE(Color.YELLOW, true),
    TWO(Color.BLUE, true),
    THREE(Color.RED, true),
    FOUR(Color.VIOLET, true),
    FIVE(Color.ORANGE, true),
    SIX(Color.GREEN, true),
    SEVEN(Color.MAROON, true),
    EIGHT(Color.BLACK, true),
    NINE(Color.YELLOW, false),
    TEN(Color.BLUE, false),
    ELEVEN(Color.ORANGE, false),
    TWELVE(Color.RED, false),
    THIRTEEN(Color.VIOLET, false),
    FOURTEEN(Color.GREEN, false),
    FIFTEEN(Color.MAROON, false),
    WHITE(Color.WHITE, true);

    private Color c;
    private boolean solid;

    private Body body;

    public Color getColor() { 
        return this.c; 
    }

    public boolean isSolid() {
        return this.solid; 
    }

    public void setPosition(double x, double y) {
        this.body.translateToOrigin();
        this.body.translate(x, y);
    }

    public Circle getShape() {
        return (Circle) this.getBody().getFixture(0).getShape();
    }

    public Body getBody() {
        return this.body;
    }

    public boolean isWhite() {
        return this == WHITE;
    }

    private Ball(Color c, boolean solid) {
        this.c = c;
        this.solid = solid;

        this.body = new Body();
        this.body.addFixture(
                Geometry.createCircle(Constants.RADIUS),
                Constants.DENSITY,
                Constants.FRICTION, 
                Constants.RESTITUTION);
        this.body.translate(0, 0);
        // disable rotation, looks strange
        this.body.setMass(MassType.FIXED_ANGULAR_VELOCITY);
        this.body.setLinearDamping(Constants.LINEAR_DAMPING);
        this.body.setAngularDamping(Constants.ANGULAR_DAMPING);

        this.body.setUserData(this);
    }

    public static class Constants {
        // international pool balls have a radius of 0.05715 meter
        public final static double RADIUS = 0.05715;
        // pool balls have a mass of 0.17 kg
        private final static double MASS = 0.17;
        private final static double VOLUME = (4.0/3.0) * Math.PI * Math.pow(RADIUS, 3.0);
        private final static double DENSITY = MASS / VOLUME; // density in kg/m^3
    
        // linear damping is used to slow down the balls over time due to
        // friction with the underlying table surface
        // NOTE: this is a purely experimental value, which is obtained by
        // trial and error, until the ball movement looked real enough
        private final static double LINEAR_DAMPING = 0.95;
        // same as linear damping but for rotation
        private final static double ANGULAR_DAMPING = 0.3;
    
        // https://billiards.colostate.edu/bd_articles/2005/april05.pdf
        // "Most reported values for pool balls are close to 0.06, meaning that 
        // the friction force can be only about 6% as large as the perpendicular 
        // impact force between the balls."
        private final static double FRICTION = 0.06;
        // "Most experimental numbers I have seen for the coefficient of restitution
        // for pool balls have been in the range 0.90 to 0.96."
        private final static double RESTITUTION = 0.93;
    }
}
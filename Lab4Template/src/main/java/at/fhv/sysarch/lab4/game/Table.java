package at.fhv.sysarch.lab4.game;

import java.util.ArrayList;
import java.util.List;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Vector2;

public class Table {
    private final Body tableBody;

    public enum TablePart {
        POCKET,
        CUSHION
    }

    public Table() {
        // the tables center is the center of the game world: 0/0
        this.tableBody = new Body();
        this.tableBody.translate(0, 0);
        this.tableBody.setMass(MassType.INFINITE);

        this.createCushions();
        this.createPockets();
    }

    public Body getBody() {
        return this.tableBody;
    }

    private void createCushions() {
        List<BodyFixture> cushions = new ArrayList<>();

        // the right / left cushion height is reduced by 1 corner pocket radius
        // because both corner pockets are "half" in the table
        double fullCushionHeight = Constants.HEIGHT - Constants.POCKET_DIAMETER;

        Polygon rightCushionGeom = Geometry.createPolygon(
            new Vector2(-Constants.CUSHION_SIZE * 0.5, fullCushionHeight * 0.5 - Constants.POCKET_RADIUS ),
            new Vector2(-Constants.CUSHION_SIZE * 0.5, -fullCushionHeight * 0.5 + Constants.POCKET_RADIUS),
            new Vector2(Constants.CUSHION_SIZE * 0.5, -fullCushionHeight * 0.5),
            new Vector2(Constants.CUSHION_SIZE * 0.5, fullCushionHeight * 0.5));

        Polygon leftCushionGeom = Geometry.createPolygon(
                new Vector2(-Constants.CUSHION_SIZE * 0.5, fullCushionHeight * 0.5),
                new Vector2(-Constants.CUSHION_SIZE * 0.5, -fullCushionHeight * 0.5),
                new Vector2(Constants.CUSHION_SIZE * 0.5, -fullCushionHeight * 0.5 + Constants.POCKET_RADIUS),
                new Vector2(Constants.CUSHION_SIZE * 0.5, fullCushionHeight * 0.5 - Constants.POCKET_RADIUS));

        // assuming 0/0 the center of the table
        rightCushionGeom.translate(Constants.WIDTH * 0.5, 0);
        leftCushionGeom.translate(-Constants.WIDTH * 0.5, 0);

        // the half cushions width is reduced by half of the corner pocket size
        // and half center pocket size
        double halfCushionWidth = Constants.WIDTH  * 0.5 - Constants.POCKET_DIAMETER;
        double halfCushionX = halfCushionWidth * 0.5 + Constants.POCKET_RADIUS;

        Polygon topLeftCushionGeom = Geometry.createPolygon(
            new Vector2(-halfCushionWidth * 0.5, -Constants.CUSHION_SIZE * 0.5),
            new Vector2(halfCushionWidth * 0.5, -Constants.CUSHION_SIZE * 0.5),
            new Vector2(halfCushionWidth * 0.5 - Constants.POCKET_RADIUS * 0.1, Constants.CUSHION_SIZE * 0.5),
            new Vector2(-halfCushionWidth * 0.5 + Constants.POCKET_RADIUS, Constants.CUSHION_SIZE * 0.5));

        Polygon topRightCushionGeom = Geometry.createPolygon(
            new Vector2(-halfCushionWidth * 0.5, -Constants.CUSHION_SIZE * 0.5),
            new Vector2(halfCushionWidth * 0.5, -Constants.CUSHION_SIZE * 0.5),
            new Vector2(halfCushionWidth * 0.5 - Constants.POCKET_RADIUS, Constants.CUSHION_SIZE * 0.5),
            new Vector2(-halfCushionWidth * 0.5 + Constants.POCKET_RADIUS * 0.1, Constants.CUSHION_SIZE * 0.5));

        Polygon bottomRightCushionGeom = Geometry.createPolygon(
                new Vector2(-halfCushionWidth * 0.5, -Constants.CUSHION_SIZE * 0.5),
                new Vector2(halfCushionWidth * 0.5, -Constants.CUSHION_SIZE * 0.5),
                new Vector2(halfCushionWidth * 0.5 - Constants.POCKET_RADIUS * 0.1, Constants.CUSHION_SIZE * 0.5),
                new Vector2(-halfCushionWidth * 0.5 + Constants.POCKET_RADIUS, Constants.CUSHION_SIZE * 0.5));

        Polygon bottomLeftCushionGeom = Geometry.createPolygon(
            new Vector2(-halfCushionWidth * 0.5, -Constants.CUSHION_SIZE * 0.5),
            new Vector2(halfCushionWidth * 0.5, -Constants.CUSHION_SIZE * 0.5),
            new Vector2(halfCushionWidth * 0.5 - Constants.POCKET_RADIUS, Constants.CUSHION_SIZE * 0.5),
            new Vector2(-halfCushionWidth * 0.5 + Constants.POCKET_RADIUS * 0.1, Constants.CUSHION_SIZE * 0.5));

        bottomRightCushionGeom.rotate(Math.toRadians(180));
        bottomLeftCushionGeom.rotate(Math.toRadians(180));

        topLeftCushionGeom.translate(-halfCushionX, -Constants.HEIGHT * 0.5);
        topRightCushionGeom.translate(halfCushionX, -Constants.HEIGHT * 0.5);
        bottomLeftCushionGeom.translate(-halfCushionX, Constants.HEIGHT * 0.5);
        bottomRightCushionGeom.translate(halfCushionX, Constants.HEIGHT * 0.5);

        cushions.add(new BodyFixture(rightCushionGeom));
        cushions.add(new BodyFixture(leftCushionGeom));
        cushions.add(new BodyFixture(topLeftCushionGeom));
        cushions.add(new BodyFixture(topRightCushionGeom));
        cushions.add(new BodyFixture(bottomLeftCushionGeom));
        cushions.add(new BodyFixture(bottomRightCushionGeom));
        
        for (BodyFixture c : cushions) {
            c.setUserData(TablePart.CUSHION);
            c.setDensity(Constants.CUSHION_DENSITY);
            c.setFriction(Constants.CUSHION_FRICTION);
            c.setRestitution(Constants.CUSHION_RESTITUTION);

            this.tableBody.addFixture(c);
        }
    }

    private void createPockets() {
        List<BodyFixture> pockets = new ArrayList<>();

        Circle topLeftPocketGeom = Geometry.createCircle(Constants.POCKET_RADIUS);
        Circle bottomLeftPocketGeom = Geometry.createCircle(Constants.POCKET_RADIUS);
        Circle topRightPocketGeom = Geometry.createCircle(Constants.POCKET_RADIUS);
        Circle bottomRightPocketGeom = Geometry.createCircle(Constants.POCKET_RADIUS);
        Circle topCenterPocketGeom = Geometry.createCircle(Constants.POCKET_RADIUS); 
        Circle bottomCenterPocketGeom = Geometry.createCircle(Constants.POCKET_RADIUS);

        topLeftPocketGeom.translate(-Constants.WIDTH * 0.5, -Constants.HEIGHT * 0.5);
        bottomLeftPocketGeom.translate(-Constants.WIDTH * 0.5, Constants.HEIGHT * 0.5);
        topRightPocketGeom.translate(Constants.WIDTH * 0.5, -Constants.HEIGHT * 0.5);
        bottomRightPocketGeom.translate(Constants.WIDTH * 0.5, Constants.HEIGHT * 0.5);
        topCenterPocketGeom.translate(0, -Constants.HEIGHT * 0.5);
        bottomCenterPocketGeom.translate(0, Constants.HEIGHT * 0.5);

        pockets.add(new BodyFixture(topLeftPocketGeom));
        pockets.add(new BodyFixture(bottomLeftPocketGeom));
        pockets.add(new BodyFixture(topRightPocketGeom));
        pockets.add(new BodyFixture(bottomRightPocketGeom));
        pockets.add(new BodyFixture(topCenterPocketGeom));
        pockets.add(new BodyFixture(bottomCenterPocketGeom));

        for (BodyFixture p : pockets) {
            p.setSensor(true);
            p.setUserData(TablePart.POCKET);

            this.tableBody.addFixture(p);
        }
    }

    public static class Constants {
        // the size of a billard table is 2.5 x 1.3 m
        public final static double WIDTH = 2.5;
        public final static double HEIGHT = 1.3;
        // cushion size is 5 centimeter
        public final static double CUSHION_SIZE = 0.05;

        // Corner pockets have a width of 0.125 - 0.135 m front and 0.105 to 115 m at the back. 
        // center pockets have a width of 0.135 - 0.145 m front and 0.110 to 120 m at the back. 
        // For sake of simplicity we assume a round hole and all pockets same diameter
        private final static double POCKET_DIAMETER = 0.135;
        private final static double POCKET_RADIUS = POCKET_DIAMETER / 2.0;

        private final static double CUSHION_DENSITY = 0.1;
        private final static double CUSHION_FRICTION = 0; //0.14;
        private final static double CUSHION_RESTITUTION = 0; //0.98;
    }
}
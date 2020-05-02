package me.acdean.ziqqurat;

import static me.acdean.ziqqurat.Main.SIZE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processing.core.PApplet;
import static processing.core.PApplet.constrain;
import processing.core.PConstants;
import static processing.core.PConstants.TWO_PI;
import processing.core.PShape;
import processing.core.PVector;

/*
** Mutant
*/
public class Mutant extends Enemy {

    Main p;
    static PShape shape;
    private static final Logger LOG = LoggerFactory.getLogger(Mutant.class);
    PVector[] delta = {
        new PVector(1, 0, 0),   // 0 x increases    right?
        new PVector(0, 1, 0),   // 1 y increases    down
        new PVector(-1, 0, 0),  // 2 x decreases    left
        new PVector(0, -1, 0)   // 3 y decreases    up
    };
    float rx, ry, rz;
    float dx, dy, dz;

    private static final float XA = PApplet.sqrt(8 / 9.0f) * SIZE / 2;
    private static final float XB = -PApplet.sqrt(2 / 9.0f) * SIZE / 2;
    private static final float XC = -PApplet.sqrt(2 / 9.0f) * SIZE / 2;
    private static final float YB = PApplet.sqrt(2 / 3.0f) * SIZE / 2;
    private static final float YC = -PApplet.sqrt(2 / 3.0f) * SIZE / 2;
    private static final float Z = (-1 / 3.0f) * SIZE / 2;

    // mutant wanders randomly on same platform (so where new height is the same)
    Mutant(Main main) {
        p = main;
        if (shape == null) {
            // TODO Mutant should be messy? green tetra?
            shape = p.createShape();
            shape.beginShape(PConstants.TRIANGLES);
            shape.fill(0);
            shape.stroke(0, 255, 0);

            shape.vertex(0, 0, SIZE / 2);   // D
            shape.vertex(XA, 0, Z);         // A
            shape.vertex(XB, YB, Z);        // B

            shape.vertex(0, 0, SIZE / 2);   // D
            shape.vertex(XB, YB, Z);        // B
            shape.vertex(XC, YC, Z);        // C

            shape.vertex(0, 0, SIZE / 2);   // D
            shape.vertex(XC, YC, Z);        // C
            shape.vertex(XA, 0, Z);         // A

            shape.vertex(XA, 0, Z);         // A
            shape.vertex(XB, YB, Z);        // B
            shape.vertex(XC, YC, Z);        // C

            shape.endShape();
        }
        // random position (on platform or stair, aligned to square)
        int cx, cy;
        do {
            cx = (int)p.random(p.floor.count2);
            cy = (int)p.random(p.floor.count2);
            LOG.info("random {} {} {}", cx, cy, p.floor.heights[cx][cy]);
        } while (p.floor.heights[cx][cy] <= 0); // no spikes in space / on top of other stuff
        // TODO no mutants on stairs?
        x = cx;
        y = cy;
        z = p.floor.heights[cx][cy];    // remember initial floor height
        // rotations
        rx = p.random(TWO_PI);
        ry = p.random(TWO_PI);
        rz = p.random(TWO_PI);
        dx = p.random(-.05f, .05f);
        dy = p.random(-.05f, .05f);
        dz = p.random(-.04f, .05f);
    }

    @Override
    void move() {
        rx += dx;
        ry += dy;
        rz += dz;
        if (p.random(100) > 10) {
            return;
        }
        int dir = (int)p.random(4);
        int cx = constrain((int)(x + delta[dir].x), 0, p.floor.count2 - 1);
        int cy = constrain((int)(y + delta[dir].y), 0, p.floor.count2 - 1);
        if (p.floor.heights[cx][cy] == z) {  // must stay on same level
            x = cx;
            y = cy;
        }
    }

    @Override
    void draw() {
        p.pushMatrix();
        p.translate(x * SIZE, y * SIZE, z * SIZE + SIZE);
        p.rotateX(rx);
        p.rotateY(ry);
        p.rotateZ(rz);
        shape.setStroke(0xff000000 | (int)p.random(0x01000000));
        shape.setFill(0xff000000 | (int)p.random(0x01000000));
        p.shape(shape);
        p.popMatrix();
    }
}

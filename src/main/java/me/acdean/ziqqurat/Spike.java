package me.acdean.ziqqurat;

import static me.acdean.ziqqurat.Main.SIZE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processing.core.PConstants;
import processing.core.PShape;

/*
** Spike
*/
public class Spike extends Enemy {

    private static final int SPIKE = -2;
    Main p;
    static PShape shape;
    private static final Logger LOG = LoggerFactory.getLogger(Spike.class);

    // just a spike on a random platform
    Spike(Main main) {
        p = main;
        if (shape == null) {
            shape = p.createShape();
            shape.beginShape(PConstants.TRIANGLE_FAN);
            shape.fill(0);
            shape.stroke(255, 0, 0);
            //shape.strokeWeight(2);
            shape.vertex(0, 0, 40);
            shape.vertex(-SIZE / 2, -SIZE / 2, 0);
            shape.vertex(-SIZE / 2, SIZE / 2, 0);
            shape.vertex(SIZE / 2, SIZE / 2, 0);
            shape.vertex(SIZE / 2, -SIZE / 2, 0);
            shape.vertex(-SIZE / 2, -SIZE / 2, 0);
            shape.endShape();
        }
        // random position (on platform or stair, aligned to square)
        int cx, cy;
        do {
            cx = (int)p.random(p.floor.count2);
            cy = (int)p.random(p.floor.count2);
            LOG.info("random {} {} {}", cx, cy, p.floor.heights[cx][cy]);
        } while (p.floor.heights[cx][cy] <= 0); // no spikes in space / on top of other stuff
        x = cx;
        y = cy;
        z = p.floor.heights[cx][cy];
        // mark this square as a spike
        p.floor.heights[cx][cy] = SPIKE;
    }

    @Override
    void draw() {
        p.pushMatrix();
        p.translate(x * SIZE, y * SIZE, z * SIZE + (SIZE / 2));
        p.shape(shape);
        p.popMatrix();
    }
}

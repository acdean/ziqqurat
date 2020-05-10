package me.acdean.ziqqurat;

import static me.acdean.ziqqurat.Main.SIZE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processing.core.PConstants;
import processing.core.PShape;

/*
** Circler - just circles
*/
public class Circler extends Enemy {

    private static final Logger LOG = LoggerFactory.getLogger(Spike.class);
    private static final float H = 30;  // height
    private static final int SIDES = 16; // number of sides

    Main p;
    static PShape shape;
    int ox, oy, oz; // location of origin
    float rad;          // radius
    float rot, delta;   // rotation angle and speed

    // just a spike on a random platform
    Circler(Main main) {
        p = main;
        PShape sides, top;
        if (shape == null) {
            shape = p.createShape(PConstants.GROUP);
            sides = p.createShape();
            sides.beginShape(PConstants.TRIANGLE_STRIP);
            sides.fill(0, 0, 0);
            sides.stroke(255, 0, 255);
            top = p.createShape();
            top.beginShape();
            top.fill(0, 0, 0);
            top.stroke(255, 0, 255);
            boolean isTop = true;
            // NB more than SIDES - need to repeat the first point for join
            for (int i = 0 ; i < SIDES + 2 ; i++) {
                float a = PConstants.TWO_PI * i / SIDES;
                float c = (SIZE / 2) * Main.cos(a);
                float s = (SIZE / 2) * Main.sin(a);
                if (isTop) {
                    sides.vertex(c, s, H);
                    top.vertex(c, s, H);
                } else {
                    sides.vertex(c, s, SIZE / 2);
                }
                isTop = !isTop;
            }
            sides.endShape();
            shape.addChild(sides);
            top.endShape();
            shape.addChild(top);
        }
        // circles around a star at random radius
        do {
            // these are the char positions of a star, so centre of a platform
            ox = (int)p.random(p.floor.count);
            oy = (int)p.random(p.floor.count);
            oz = p.floor.platform[ox][oy];
            LOG.info("random {} {} {}", ox, oy, oz);
        } while (oz == 0);  // ignore non-platform space
        // translate to pixel space
        oz = p.floor.toPixelZ(ox, oy);
        ox = p.floor.toPixelX(ox) + ((p.floor.platSize - 1) * SIZE / 2);
        oy = p.floor.toPixelY(oy) + ((p.floor.platSize - 1) * SIZE / 2);

        rad = (SIZE / 2) * p.random(4, p.floor.platSize); // ??? radius should be /2 but that's not enough
        delta = p.random(.01f, .02f);
        if (p.random(100) < 50.0f) {
            delta = -delta;
        }
    }

    @Override
    void move() {
        rot += delta;
        x = ox + rad * Main.cos(rot);
        y = oy + rad * Main.sin(rot);
    }

    @Override
    void draw() {
        p.pushMatrix();
        p.translate(x, y, oz);
        p.shape(shape);
        p.popMatrix();
    }

    // TODO collision
}

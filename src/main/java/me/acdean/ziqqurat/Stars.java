package me.acdean.ziqqurat;

import java.util.ArrayList;
import processing.core.PApplet;
import static processing.core.PApplet.println;
import processing.core.PConstants;
import processing.core.PShape;

/*
** Stars
*/

class Stars extends ArrayList<Stars.Star> {

    Main p;
    PShape starShape;

    Stars(Main p) {
        super();
        this.p = p;
    }

    // at start of stage, add a bunch of stars
    // requires a floor
    void init() {
        // add some stars
        for (int i = 0 ; i < 100 ; i++) {
            int x = (int)p.random(0, 10);
            int y = (int)p.random(0, 10);
            int z = p.floor.grid[x][y];
            if (z != 0) {
                add(p.floor.toX(x), p.floor.toY(y), p.floor.toZ(x, y));
            }
        }
        println("Stars: ", size());
    }

    void add(int x, int y, int z) {
        add(new Star(x, y, z));
    }

    void draw() {
        for (Star s : this) {
            s.draw();
        }
    }

    class Star {

        float x, y, z;
        int count;

        Star(int x, int y, int z) {
            println("star", x, y, z);
            this.x = x;
            this.y = y;
            this.z = z;
            count = 0;
        }

        void draw() {
            if (starShape == null) {
                starShape = p.createShape();
                starShape.beginShape();
                starShape.noFill();
                starShape.strokeWeight(2);
                starShape.stroke(255);
                for (int i = 0; i < 10; i++) {
                    float a = PConstants.TWO_PI * i / 10;
                    float r = 10.0f;
                    if ((i % 2) == 0) {
                        r *= .34;
                    }
                    starShape.vertex(r * PApplet.sin(a), 0, -r * PApplet.cos(a));
                }
                starShape.endShape(PConstants.CLOSE);
            }
            if (count >= 0) {
                //PApplet.println("Star", x, y, z);
                p.pushMatrix();
                p.translate(x, y, z + 20);
                p.rotateZ(PApplet.radians(count * 5));
                //starShape.setStroke(p.colours.get(count));
                p.shape(starShape);
                p.popMatrix();
            }
            count++;
        }
    }
}

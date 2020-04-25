package me.acdean.ziqqurat;

import java.util.ArrayList;
import java.util.List;
import processing.core.PApplet;

/*
** Baubles
*/

public class Baubles {

    private final List<Bauble> baubles = new ArrayList<>();
    private final PApplet p;

    Baubles(PApplet p) {
        this.p = p;
    }

    void init() {
        for (int y = -400 ; y < 400 ; y += 60) {
            for (int x = -400 ; x < 400 ; x += 60) {
              baubles.add(new Bauble(p, x, y));
            }
        }
    }

    void draw() {
        for (Bauble b : baubles) {
            b.draw();
        }
    }
}

class Bauble {
    PApplet p;
    float x, y, z;
    float rx, ry, rz;

    Bauble(PApplet p, int x, int y) {
        this.p = p;
        this.x = x;
        this.y = y;
    }

    void draw() {

        // update rotations
        rx += .01f;
        ry += .02f;
        rz += .03f;
        // wavy
        z = 50 * PApplet.sin((x + 2 * y) * .004f + p.frameCount * .05f);

        p.pushMatrix();
        p.translate(x, y, z);
        p.rotateX(rx);
        p.rotateY(ry);
        p.rotateZ(rz);

        p.noStroke();
        p.fill(255, 0, 0);
        p.box(10, 30, 50);
        p.fill(0, 255, 0);
        p.box(30, 50, 10);
        p.fill(0, 0, 255);
        p.box(50, 10, 30);

        // alternate hollow boxes
//        p.noFill();
//        p.strokeWeight(25);
//        p.stroke(255, 0, 0);
//        p.box(10, 30, 50);
//        p.stroke(0, 255, 0);
//        p.box(30, 50, 10);
//        p.stroke(0, 0, 255);
//        p.box(50, 10, 30);

        p.popMatrix();
    }
}

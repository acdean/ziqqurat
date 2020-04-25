package me.acdean.ziqqurat;

import static processing.core.PApplet.println;
import processing.core.PConstants;

/*
** Colours - a list of rainbow colours (because pshapes don't do HSB)
*/

public class Colours {

    private static final int COLOURS = 255;

    Main p;
    int colours[] = null;

    Colours(Main p) {
        this.p = p;
    }

    void init() {
        println("Colours");
        p.colorMode(PConstants.HSB, COLOURS, COLOURS, COLOURS);
        colours = new int[COLOURS];
        for (int i = 0; i < COLOURS; i++) {
            int c = p.color(i, 255, 255);
            int r = (int)p.red(c);
            int g = (int)p.green(c);
            int b = (int)p.blue(c);
            println("Colours", i, r, g, b);
            colours[i] = 0xff << 24 | r << 16 | g << 8 | b;
        }
        p.colorMode(PConstants.RGB);
    }

    int get(int i) {
        if (colours == null) {
            init();
        }
        return colours[i  % COLOURS];
    }
}

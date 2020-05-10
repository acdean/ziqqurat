package me.acdean.ziqqurat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processing.core.PConstants;

/*
** Colours - a list of rainbow colours (because pshapes don't do HSB)
*/

public class Colours {
    public static final int BLACK = 0xff000000;
    public static final int BLUE = 0xff0000ff;
    public static final int FORCEFIELD_COLOUR = 0x80FF8080;   // transparent and pale
    public static final int MAGENTA = 0xffff00ff;
    public static final int RED = 0xffff0000;

    private static final Logger LOG = LoggerFactory.getLogger(Colours.class);

    private static final int COLOURS = 255;

    Main p;
    int colours[] = null;

    Colours(Main p) {
        this.p = p;
    }

    void init() {
        LOG.debug("Colours");
        p.colorMode(PConstants.HSB, COLOURS, COLOURS, COLOURS);
        colours = new int[COLOURS];
        for (int i = 0; i < COLOURS; i++) {
            int c = p.color(i, 255, 255);
            int r = (int)p.red(c);
            int g = (int)p.green(c);
            int b = (int)p.blue(c);
            LOG.debug("Colours {} {} {} {}", i, r, g, b);
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

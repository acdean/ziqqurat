package me.acdean.ziqqurat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processing.core.PVector;

/*
** Floor
 */
public class Floor {

    Logger logger = LoggerFactory.getLogger(Floor.class);
    private static final int COUNT = 10;

    public static final int STEPS = 5;
    public static final int STEP_SIZE = 10;
    public static final int SIZE = 100;

    Main p;
    int[][] grid = new int[COUNT][COUNT];

    Floor(Main p) {
        this.p = p;
        int seed = (int)p.random(10);
        Main.println("Seed:", seed);
        p.randomSeed(seed);
        p.noiseSeed(seed);

        // random walk
        for (int f = 0; f < 3; f++) {
            int rx = 0, ry = 0;
            int sum = 0;
            for (int i = 0; i < 20; i++) {
                int xinc = (int)p.random(0, 2);  // 0 or 1
                if (p.random(100) < 50) {
                    xinc = -xinc;  // -1, 0, 1
                }
                sum += xinc;
                //println("random", xinc);
                rx = Main.constrain(rx + xinc, 0, COUNT - 1);  // these are inclusive
                // don't allow diagonals
                if (xinc == 0) {
                    int yinc = (int)p.random(0, 2);
                    if (p.random(100) < 50) {
                        yinc = -yinc;  // -1, 0, 1
                    }
                    ry = Main.constrain(ry + yinc, 0, COUNT - 1);
                }
                grid[rx][ry] = 1;
            }
            Main.println("sum", sum);
        }

        // now heightmap the drawn cells
        for (int y = 0; y < COUNT; y++) {
            for (int x = 0; x < COUNT; x++) {
                if (grid[x][y] != 0) {
                    grid[x][y] = (int)(15 * p.noise(x * .06f, y * .05f));
                    Main.print(grid[x][y] % 10);
                } else {
                    Main.print("-");
                }
            }
            Main.println("");
        }

        // normalise the grid
        int min = 999;
        for (int y = 0; y < COUNT; y++) {
            for (int x = 0; x < COUNT; x++) {
                if (grid[x][y] != 0 && grid[x][y] < min) {
                    min = grid[x][y];
                }
            }
        }
        for (int y = 0; y < COUNT; y++) {
            for (int x = 0; x < COUNT; x++) {
                if (grid[x][y] != 0) {
                    grid[x][y] -= (min - 1);
                    int sx = toX(x);
                    int sy = toY(y);
                    int sz = toZ(x, y);
                    Main.println(x, y, sx, sy, sz);
                }
            }
        }
    }

    void draw() {
        p.fill(0);
        p.strokeWeight(3);
        p.stroke(255, 0, 0);
        for (int y = 0 ; y < COUNT ; y++) {
            for (int x = 0 ; x < COUNT ; x++) {
                if (grid[x][y] != 0) {
                drawPlatform(x, y);
                }
            }
        }
    }

    void drawPlatform(int x, int y) {
        p.pushMatrix();
        int sx = toX(x);
        int sy = toY(y);
        int sz = toZ(x, y);
        p.translate(sx, sy, sz);
        p.box(SIZE, SIZE, STEP_SIZE);
        if (y < COUNT - 1) {
            drawSteps(x, y, false);
        }
        if (x < COUNT - 1) {
            drawSteps(x, y, true);
        }
        p.popMatrix();
    }

    // direction = true => x increases
    void drawSteps(int x, int y, boolean direction) {
        int sz = grid[x][y];    // start z
        int ez, w, h, xinc, yinc, zinc, tx, ty;
        if (direction) {
            ez = grid[x + 1][y];  // end z
            if (ez == 0) {
                return;
            }
            w = STEP_SIZE;      // shape of steps
            h = SIZE / 2;
            xinc = STEP_SIZE;   // difference between steps
            yinc = 0;
            tx = (int)(xinc * (STEPS - .5));  // initial translation
            ty = 0;
        } else {
            ez = grid[x][y + 1];
            if (ez == 0) {
                return;
            }
            w = SIZE / 2;
            h = STEP_SIZE;
            xinc = 0;
            yinc = STEP_SIZE;
            tx = 0;
            ty = (int)(yinc * (STEPS - .5));
        }
        if (Main.abs(sz - ez) > 1) {
            // stairs too steep - skip
            return;
        }
        p.pushMatrix();
        zinc = STEP_SIZE * (ez - sz);
        if (sz == ez) {
            // flat platform
            p.translate(xinc * (2.5f + STEPS), yinc * (2.5f + STEPS));  // hacky
            p.box(SIZE / 2, SIZE / 2, 10);
        } else {
            p.translate(tx, ty, 0);
            // steps
            for (int i = 0 ; i < STEPS ; i++) {
                // NB these translations are accumulative
                x += xinc;
                y += yinc;
                sz += zinc;
                p.translate(xinc, yinc, zinc);
                p.box(w, h, STEP_SIZE);
            }
        }
        p.popMatrix();
    }

    final int toX(int x) {
        return x * (Floor.SIZE + (Floor.STEPS * Floor.STEP_SIZE));
    }

    final int toY(int y) {
        return y * (Floor.SIZE + (Floor.STEPS * Floor.STEP_SIZE));
    }

    final int toZ(int x, int y) {
        return grid[x][y] * (Floor.STEPS + 1) * Floor.STEP_SIZE;
    }

    final PVector position(int x, int y, int z) {
        return new PVector(toX(x), toY(y), toZ(x, y));
    }
}

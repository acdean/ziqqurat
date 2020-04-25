package me.acdean.ziqqurat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processing.core.PVector;
import static processing.core.PApplet.print;
import static processing.core.PApplet.println;

/*
** Floor
** TODO make COUNT and PLAT_SIZE random (subject to a maximum product)
** PLAT_SIZE should be odd, probably between 7 and 25
**
**     ### ###  
**     #######  
**     ### ###  
**          #   
**         ###  
**         ###  
**         ###  
**
*/
public class Floor {

    Logger logger = LoggerFactory.getLogger(Floor.class);

    public static final int STEPS = 5;                      // 5 steps between platforms
    public static final int STEP_SIZE = 10;                 // basic block size

    // platforms physical size is STEP_SIZE * plat_size
    // the random platform size determines the nnumber of platforms in the grid
    // ie there are more 5x5 platforms than 21x21 platforms
    Main p;
    int plat_size;  // size of the platforms
    int count;      // number of platforms in grid
    int size;       // physical size of the platform
    int[][] grid;   // this is for the platforms (depends on random count)
    int[][] floor;  // this is for the floor
    int colour;

    Floor(Main p) {
        this.p = p;
        int seed = (int)p.random(10);
        println("Seed:", seed);
        p.randomSeed(seed);
        p.noiseSeed(seed);

        // 1 - 19
        // 2 - 19
        // 3 - 19
        // 4 - 19
        // 5 - 19
        // 6 - 19
        // 7 - 19
        // 8 - 19
        
        // platform size
        p.random(100);
        plat_size = 1 + 2 * (int)p.random(2, 12);   // random odd number 5 - 25
        size = plat_size * STEP_SIZE;   // dimensions for the platform
        // we want the overall size to be about 256x256 
        count = 256 / plat_size;
        grid = new int[count][count];
        int count2 = (count * plat_size) + ((count - 1) * STEPS);
        floor = new int[count2][count2];
        println("plat_size", plat_size, "size", size, "count", count, "count2", count2);

        // random walk
        int rx = 0, ry = 0;
        for (int f = 0; f < (int)p.random(4, 8); f++) {
            if (p.random(100) < 20) {
                // reset position to origin (else continue)
                rx = 0;
                ry = 0;
            }
            int sum = 0;
            for (int i = 0; i < (int)p.random(20, 30); i++) {
                int xinc = (int)p.random(0, 2);  // 0 or 1
                if (p.random(100) < 50) {
                    xinc = -xinc;  // -1, 0, 1
                }
                sum += xinc;
                //println("random", xinc);
                rx = Main.constrain(rx + xinc, 0, count - 1);  // these are inclusive
                // don't allow diagonals
                if (xinc == 0) {
                    int yinc = (int)p.random(0, 2);
                    if (p.random(100) < 50) {
                        yinc = -yinc;  // -1, 0, 1
                    }
                    ry = Main.constrain(ry + yinc, 0, count - 1);
                }
                grid[rx][ry] = 1;
            }
            println("sum", sum);
        }

        // now heightmap the drawn cells
        for (int y = 0; y < count; y++) {
            for (int x = 0; x < count; x++) {
                if (grid[x][y] != 0) {
                    grid[x][y] = (int)(15 * p.noise(x * .06f, y * .05f));
                    print(grid[x][y] % 10);
                } else {
                    print("-");
                }
            }
            println("");
        }

        // normalise the grid
        int min = 999;
        for (int y = 0; y < count; y++) {
            for (int x = 0; x < count; x++) {
                if (grid[x][y] != 0 && grid[x][y] < min) {
                    min = grid[x][y];
                }
            }
        }
        for (int y = 0; y < count; y++) {
            for (int x = 0; x < count; x++) {
                if (grid[x][y] != 0) {
                    grid[x][y] -= (min - 1);
                    // debug
                    int sx = toX(x);
                    int sy = toY(y);
                    int sz = toZ(x, y);
                    println(x, y, sx, sy, sz);
                }
            }
        }
        int r = (int)(p.random(64, 256));
        int g = (int)(p.random(64, 256));
        int b = (int)(p.random(64, 256));
        println("Colour:", r, g, b);
        colour = 255 << 24 | r << 16 | g << 8 | b;
    }

    void draw() {
        p.fill(0);
        p.strokeWeight(3);
        p.stroke(colour);
        for (int y = 0 ; y < count ; y++) {
            for (int x = 0 ; x < count ; x++) {
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
        p.box(size, size, STEP_SIZE);
        if (y < count - 1) {
            drawSteps(x, y, false);
        }
        if (x < count - 1) {
            drawSteps(x, y, true);
        }
        p.popMatrix();

        //setFloor(x, y, sz, 15, 15);
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
            h = STEPS * STEP_SIZE;
            xinc = STEP_SIZE;   // difference between steps
            yinc = 0;
            tx = (int)(xinc * ((plat_size - 1) / 2));  // initial translation
            ty = 0;
        } else {
            ez = grid[x][y + 1];
            if (ez == 0) {
                return;
            }
            w = STEPS * STEP_SIZE;
            h = STEP_SIZE;
            xinc = 0;
            yinc = STEP_SIZE;
            tx = 0;
            ty = (int)(yinc * ((plat_size - 1) / 2));
        }
        if (Main.abs(sz - ez) > 1) {
            // stairs too steep - skip
            return;
        }
        p.pushMatrix();
        zinc = STEP_SIZE * (ez - sz);
        if (sz == ez) {
            // flat platform
            p.translate(xinc * (plat_size + STEPS) / 2, yinc * (plat_size + STEPS) / 2);  // hacky
            p.box(STEPS * STEP_SIZE, STEPS * STEP_SIZE, STEP_SIZE);
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
        return x * (size + (STEPS * STEP_SIZE));
    }

    final int toY(int y) {
        return y * (size + (STEPS * STEP_SIZE));
    }

    final int toZ(int x, int y) {
        return grid[x][y] * (STEPS + 1) * STEP_SIZE;
    }

    final PVector position(int x, int y, int z) {
        return new PVector(toX(x), toY(y), toZ(x, y));
    }
}

package me.acdean.ziqqurat;

import static me.acdean.ziqqurat.Main.SIZE;
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

    private static final Logger LOG = LoggerFactory.getLogger(Floor.class);

    public static final int MAX_SIZE = 200;                 // maximum size of heightmap
    public static final int STEPS = 5;                      // 5 steps between platforms

    // platforms physical size is STEP_SIZE * plat_size
    // the random platform size determines the nnumber of platforms in the grid
    // ie there are more 5x5 platforms than 21x21 platforms
    Main p;
    int platSize;   // size of the platforms
    int count;      // number of platforms in grid
    int size;       // physical size of the platform
    int[][] grid;   // this is for the platforms (depends on random count)
    int count2;     // height grid size in character positions
    int[][] heights;// this is for the character positions
    int colour;
    int seed;
    int platforms;

    Floor(Main p) {
        this.p = p;
        seed = (int)p.random(10);
        LOG.debug("Seed: {}", seed);
        p.randomSeed(seed);
        p.noiseSeed(seed);

        // platform size
        p.random(100);
        platSize = 1 + 2 * (int)p.random(2, 12);   // random odd number 5 - 25
        size = platSize * SIZE;   // dimensions for the platform
        // we want the overall size to be about 256x256 
        count = MAX_SIZE / platSize;
        grid = new int[count][count];
        count2 = (count * platSize) + ((count - 1) * STEPS);
        heights = new int[count2][count2];
        LOG.debug("plat_size {} size {} count {} count2 {}", platSize, size, count, count2);

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
            LOG.debug("sum: {}", sum);
        }

        // now heightmap the drawn cells
        for (int y = 0; y < count; y++) {
            for (int x = 0; x < count; x++) {
                if (grid[x][y] != 0) {
                    platforms++;    // total number of platforms
                    grid[x][y] = (int)(15 * p.noise(x * .06f, y * .05f));
                    print(grid[x][y] % 10);
                } else {
                    print("-");
                }
            }
            println("");
        }
        LOG.info("Platofmrs {}", platforms);

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
                    grid[x][y] -= (min - 1);    // platform grid
                    // debug
                    int sx = toX(x);
                    int sy = toY(y);
                    int sz = toZ(x, y);
                    LOG.debug("Normalised x {} y {} z {} s {} {} {}", x, y, grid[x][y], sx, sy, sz);
                }
            }
        }

        heights = generateHeightMap(grid, platSize);

        int r = (int)(p.random(64, 256));
        int g = (int)(p.random(64, 256));
        int b = (int)(p.random(64, 256));
        LOG.debug("Colour: {} {} {}", r, g, b);
        colour = 255 << 24 | r << 16 | g << 8 | b;
    }

    // TODO use grid[][] and platSize to generate geometry as a single PShape
    // TODO use grid[][] and platSize to generate height map - generateHeightMap()

    void draw() {
        p.fill(0);
        //p.strokeWeight(1);
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
        if (p.frameCount == 1) {
            LOG.debug("platform {} {} {}", x, y, sz);
        }
        p.translate(sx, sy, sz);
        p.box(size, size, SIZE);
        if (y < count - 1) {
            drawSteps(x, y, false);
        }
        if (x < count - 1) {
            drawSteps(x, y, true);
        }
        p.popMatrix();
    }

    // direction = true => x increases
    void drawSteps(int px, int py, boolean direction) {
        int sz = grid[px][py];    // start z
        int ez, w, h, xinc, yinc, zinc, tx, ty;
        if (direction) {
            ez = grid[px + 1][py];  // end z
            if (ez == 0) {
                return;
            }
            w = SIZE;      // shape of steps
            h = STEPS * SIZE;
            xinc = SIZE;   // difference between steps
            yinc = 0;
            tx = (int)(xinc * ((platSize - 1) / 2));  // initial translation
            ty = 0;
        } else {
            ez = grid[px][py + 1];
            if (ez == 0) {
                return;
            }
            w = STEPS * SIZE;
            h = SIZE;
            xinc = 0;
            yinc = SIZE;
            tx = 0;
            ty = (int)(yinc * ((platSize - 1) / 2));
        }
        if (Main.abs(sz - ez) > 1) {
            // stairs too steep - skip
            return;
        }
        p.pushMatrix();
        zinc = SIZE * (ez - sz);
        if (sz == ez) {
            // flat platform
            p.translate(xinc * (platSize + STEPS) / 2, yinc * (platSize + STEPS) / 2);  // hacky
            p.box(STEPS * SIZE, STEPS * SIZE, SIZE);
        } else {
            p.translate(tx, ty, 0);
            // steps
            for (int i = 0 ; i < STEPS ; i++) {
                // NB these translations are accumulative
                px += xinc;
                py += yinc;
                sz += zinc;
                p.translate(xinc, yinc, zinc);
                p.box(w, h, SIZE);
            }
        }
        p.popMatrix();
    }

    final int toX(int x) {
        return x * (size + (STEPS * SIZE));
    }

    final int toY(int y) {
        return y * (size + (STEPS * SIZE));
    }

    final int toZ(int x, int y) {
        return grid[x][y] * (STEPS + 1) * SIZE;
    }

    final PVector position(int x, int y, int z) {
        return new PVector(toX(x), toY(y), toZ(x, y));
    }

    // move to separate class?
    // input = platform map and platform size
    // output = character position height map
    // NB input map is square so it's safe to use pm.length for x and y
    int[][] generateHeightMap(int[][] pm, int ps) {
        // new size includes stairs
        int s = (pm.length * ps) + (pm.length - 1) * STEPS;
        int[][] h = new int[s][s];

        // big platforms
        for (int y = 0; y < pm.length; y++) {
            for (int x = 0; x < pm.length; x++) {
                if (pm[x][y] == 0) {
                    continue;   // platform missing
                }
                int xoff = x * (ps + STEPS);
                int yoff = y * (ps + STEPS);
                int z = pm[x][y] * (STEPS + 1);
                for (int y1 = 0 ; y1 < ps ; y1++) {
                    for (int x1 = 0 ; x1 < ps ; x1++) {
                        h[xoff + x1][yoff + y1] = z;
                    }
                }
            }
        }

        // east-west stairs
        for (int y = 0; y < pm.length; y++) {
            // stop before edge
            for (int x = 0; x < pm.length - 1; x++) {
                if (pm[x][y] == 0 || pm[x + 1][y] == 0) {
                    continue;   // platform missing
                }
                // top left of the stairs
                int xoff = (x + 1) * (ps + STEPS) - STEPS;
                int yoff = y * (ps + STEPS) + ((ps - STEPS) / 2);
                int z = pm[x][y] * (STEPS + 1);
                int zinc = (pm[x + 1][y] - pm[x][y]);
                // add the steps
                for (int x1 = 0 ; x1 < STEPS ; x1++) {
                    z += zinc;
                    for (int y1 = 0 ; y1 < STEPS ; y1++) {
                        h[xoff + x1][yoff + y1] = z;
                    }
                }
            }
        }

        // north-south stairs
        // stop before edge
        for (int y = 0; y < pm.length - 1; y++) {
            for (int x = 0; x < pm.length; x++) {
                if (pm[x][y + 1] == 0 || pm[x][y] == 0) {
                    continue;   // platform missing
                }
                // top left of the stairs
                int xoff = x * (ps + STEPS) + ((ps - STEPS) / 2);
                int yoff = (y + 1) * (ps + STEPS) - STEPS;
                int z = pm[x][y] * (STEPS + 1);
                int zinc = pm[x][y + 1] - pm[x][y];
                // add the steps
                for (int y1 = 0 ; y1 < STEPS ; y1++) {
                    z += zinc;
                    for (int x1 = 0 ; x1 < STEPS ; x1++) {
                        h[xoff + x1][yoff + y1] = z;
                    }
                }
            }
        }

        // debug ascii art height map
        LOG.debug("Heights");
        for (int y = 0; y < count2; y++) {
            for (int x = 0; x < count2; x++) {
                if (h[x][y] != 0) {
                    print(h[x][y] % 10);
                } else {
                    print(".");
                }
            }
            println();
        }
        return h;
    }
}

package me.acdean.ziqqurat;

import com.jogamp.opengl.GL;        // additive blending only
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.PJOGL;     // additive blending only

/*
** Main replaces the processing sketch file.
*/

public class Main extends PApplet {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static final int SIZE = 10; // basic block size
    private static final int DIST = 200;
    public static PVector[] views = {
        new PVector(DIST, DIST),
        new PVector(DIST, -DIST),
        new PVector(-DIST, -DIST),
        new PVector(-DIST, DIST),
    };

    public PeasyCam cam;
    Baubles baubles = new Baubles(this);
    Stars stars;
    Floor floor;
    Player player;
    Enemies enemies;
    Colours colours;
    boolean camera; // peaycam on / off - c to toggle
    int view = 0;
    boolean up = true;

    public static void main(String[] args) {
        PApplet.main("me.acdean.ziqqurat.Main");
    }

    @Override
    public void settings() {
        //fullScreen(P3D);
        size(1600, 800, P3D);
        LOG.debug("Size {} {}", width, height);
    }

    @Override
    public void setup() {
        initAll();
    }

    // set / reset the screen
    void initAll() {
        int seed = (int)random(10);

        frameRate(25);

        // these need to be in setup() (not settings())
        floor = new Floor(this, seed);  // floor must be first
        stars = new Stars(this);
        player = new Player(this);
        colours = new Colours(this);
        enemies = new Enemies(this);

        if ((seed & 0x1) != 0) {
            // add spikes, 4 per platform
            // um, these don't need to be real enemies - they don't move
            for (int i = 0 ; i < floor.platforms * 2 ; i++) {
                enemies.add(new Spike(this));
            }
        }
        if ((seed & 0x2) != 0) {
            for (int i = 0 ; i < floor.platforms ; i++) {
                enemies.add(new ForceField(this));
            }
        }
//        if ((seed & 0x2) != 0) {
//            for (int i = 0 ; i < floor.platforms * 2 ; i++) {
//                enemies.add(new Circler(this));
//            }
//        }

        // add stars
        stars.init();

        // start looking at player
        PVector pos = player.position;
        cam = new PeasyCam(this, pos.x, pos.y, pos.z, 2500f);
    }

    @Override
    public void draw() {
        background(0);

        cam.setActive(camera);
        if (!camera) {
            // if peasy is off, then look at the player from an angle
            int dist = 250;
            float x = player.position.x * SIZE;
            float y = player.position.y * SIZE;
            float z = player.position.z;    // this is already sized
            //LOG.info("Camera {} {} {} {} {} {} {} {} {}", x + 100, y + 100, z + 100, x, y, z, 0, 0, -1);
            if (up) {
                camera(x + views[view].x, y + views[view].y, z + dist / 2,
                        x, y, z,
                        0, 0, -1);
            } else {
                camera(x + views[view].x, y + views[view].y, z - dist / 2,
                        x, y, z,
                        0, 0, 1);
            }
        }

        // axes
//        stroke(255, 0, 0);
//        line(0, 0, 0, 1000, 0, 0);
//        stroke(0, 255, 00);
//        line(0, 0, 0, 0, 1000, 0);
//        stroke(0, 0, 255);
//        line(0, 0, 0, 0, 0, 1000);

        if (keyPressed) {
            if (key == 'z') {
                player.forwards();
            }
        }

        enemies.move();

        pushMatrix();
        translate((floor.platSize - 1) * SIZE / 2, (floor.platSize - 1) * SIZE / 2);    // EVEN MORE HACKY
        floor.draw();
        popMatrix();
        stars.draw();
        enemies.draw();
        player.draw();

        // turn on additive blending, always fun
//        additiveBlending();
    }

    // currently unused... maybe for some sparkles
    void additiveBlending() {
        GL gl = ((PJOGL)beginPGL()).gl.getGL();
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
        gl.glDisable(GL.GL_DEPTH_TEST);
    }

    @Override
    public void keyPressed() {
        if (key == 's') {
            LOG.debug("Saving frame");
            saveFrame("ziqqurat_#####.png");
        }
        if (key == '.') {   // player rotate
            player.clockwise();
        }
        if (key == ',') {   // player rotate
            player.anti();
        }
//        if (key == 'z') {
//            player.forwards();
//        }
        if (key == 'c') {   // camera toggle
            camera = !camera;
        }
        if (key == ';') {   // view
            view = (view + 1) % 4;
        }
        if (key == 'k') {   // vire
            view = (view + 3) % 4;
        }
        if (key == 'l') {   // jump to underside
            up = !up;
            // rotate view so it matches the old one
            view = (view + 2) % 4;
        }
        if (key == 'r') {   // re-init
            // need to reseed else we get stuck in loops
            randomSeed(System.currentTimeMillis());
            this.initAll();
        }
    }
}

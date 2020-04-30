package me.acdean.ziqqurat;

import peasy.PeasyCam;
import processing.core.PVector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// these are only used for additive blending
import com.jogamp.opengl.GL;
import processing.core.PApplet;
import processing.opengl.PJOGL;

/*
** Main replaces the processing sketch file.
*/

public class Main extends PApplet {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public PeasyCam cam;
    Baubles baubles = new Baubles(this);
    Stars stars;
    Floor floor;
    Player player;
    Colours colours;

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

        // these need to be in setup() (not settings())
        floor = new Floor(this);    // floor must be first
        stars = new Stars(this);
        player = new Player(this);
        colours = new Colours(this);

        stars.init();

        // start looking at player
        PVector pos = player.position;
        cam = new PeasyCam(this, pos.x, pos.y, pos.z, 2500f);
    }

    @Override
    public void draw() {
        background(0);

        stroke(255, 0, 0);
        line(0, 0, 0, 1000, 0, 0);
        stroke(0, 255, 00);
        line(0, 0, 0, 0, 1000, 0);
        stroke(0, 0, 255);
        line(0, 0, 0, 0, 0, 1000);

        pushMatrix();
        translate((floor.platSize - 1) * Floor.STEP_SIZE / 2, (floor.platSize - 1) * Floor.STEP_SIZE / 2);
        floor.draw();
        stars.draw();
        popMatrix();
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
        if (key == '.') {
            player.clockwise();
        }
        if (key == ',') {
            player.anti();
        }
        if (key == 'z') {
            player.forwards();
        }
    }
}

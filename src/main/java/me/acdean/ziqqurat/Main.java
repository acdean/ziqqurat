package me.acdean.ziqqurat;

import peasy.PeasyCam;
import processing.core.PVector;

// these are only used for additive blending
import com.jogamp.opengl.GL;
import processing.core.PApplet;
import processing.opengl.PJOGL;

/*
** Main replaces the processing sketch file.
*/

public class Main extends PApplet {

    public PeasyCam cam;
    Baubles baubles = new Baubles(this);
    Stars stars = new Stars(this);
    Floor floor = new Floor(this);
    Player player = new Player(this);   // should be after floor
    Colours colours;    // needs to be after size()

    public static void main(String[] args) {
        PApplet.main("me.acdean.ziqqurat.Main");
    }

    @Override
    public void settings() {
        //fullScreen(P3D);
        size(1600, 800, P3D);
        PApplet.println("Size", width, height);
        colours = new Colours(this);
    }

    @Override
    public void setup() {

        stars.init();

        // start looking at player
        PVector pos = player.position;
        cam = new PeasyCam(this, pos.x, pos.y, pos.z, 500f);
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

        floor.draw();
        stars.draw();
        player.draw();

        // turn on additive blending, always fun
//        additiveBlending();

        // draw all the baubles
//        baubles.draw();
    }

    void additiveBlending() {
        GL gl = ((PJOGL)beginPGL()).gl.getGL();
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
        gl.glDisable(GL.GL_DEPTH_TEST);
    }

    @Override
    public void keyPressed() {
        if (key == 's') {
            System.out.println("Saving frame");
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

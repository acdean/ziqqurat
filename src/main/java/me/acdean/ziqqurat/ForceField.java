package me.acdean.ziqqurat;

import static me.acdean.ziqqurat.Colours.BLACK;
import static me.acdean.ziqqurat.Colours.FORCEFIELD_COLOUR;
import static me.acdean.ziqqurat.Colours.MAGENTA;
import static me.acdean.ziqqurat.Colours.RED;
import static me.acdean.ziqqurat.Main.SIZE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processing.core.PConstants;
import processing.core.PShape;

/*
** Forcefield. Four posts, random lightning.
*/

public class ForceField extends Enemy {

    public static final int PILLAR = -3;    // grid marker

    private static final Logger LOG = LoggerFactory.getLogger(ForceField.class);
    private static final float H = 30;  // height
    private static final int SIDES = 16; // number of sides
    private static PShape pillar;   // all pillars are the same, some one static shape
    private final PShape fields;    // fields depend on rad so one per class

    Main p;
    int ox, oy, oz; // location of origin
    int rad;
    float rot, delta; // forcefield on / off

    // four pillars
    ForceField(Main main) {
        p = main;
        PShape sides, top;
        if (pillar == null) {
            pillar = p.createShape(PConstants.GROUP);
            sides = p.createShape();
            sides.beginShape(PConstants.TRIANGLE_STRIP);
            sides.fill(BLACK);
            sides.stroke(MAGENTA);
            top = p.createShape();
            top.beginShape();
            top.fill(BLACK);
            top.stroke(MAGENTA);
            boolean isTop = true;
            // NB more than SIDES - need to repeat the first point for join
            for (int i = 0 ; i < SIDES + 2 ; i++) {
                float a = PConstants.TWO_PI * i / SIDES;
                float c = (SIZE / 2) * Main.cos(a);
                float s = (SIZE / 2) * Main.sin(a);
                if (isTop) {
                    sides.vertex(c, s, H);
                    top.vertex(c, s, H);
                } else {
                    sides.vertex(c, s, SIZE / 2);
                }
                isTop = !isTop;
            }
            sides.endShape();
            pillar.addChild(sides);
            top.endShape();
            pillar.addChild(top);
        }
        // forecfield around a star
        do {
            // choose a random platform
            ox = (int)p.random(p.floor.count);
            oy = (int)p.random(p.floor.count);
            oz = p.floor.platform[ox][oy];
            LOG.info("random {} {} {}", ox, oy, oz);
        } while (oz == 0);  // ignore non-platform space
        // translate to character space
        oz = p.floor.toPixelZ(ox, oy);
        // centre of platform (as characters)
        ox = p.floor.toCentre(ox) / SIZE;
        oy = p.floor.toCentre(oy) / SIZE;

        rad = (int)p.random(1, 1 + p.floor.platSize / 2); // at least one, at most on the edge
        delta = p.random(.02f, .04f);

        // mark these squares as a forcefield
        p.floor.heights[ox + rad][oy + rad] = PILLAR;
        p.floor.heights[ox + rad][oy - rad] = PILLAR;
        p.floor.heights[ox - rad][oy + rad] = PILLAR;
        p.floor.heights[ox - rad][oy - rad] = PILLAR;

        // we can make the fields now we know the size
        fields = p.createShape();
        fields.beginShape(PConstants.QUAD_STRIP);
        fields.noStroke();
        fields.fill(FORCEFIELD_COLOUR);
        fields.vertex(SIZE * (ox - rad), SIZE * (oy - rad), oz);
        fields.vertex(SIZE * (ox - rad), SIZE * (oy - rad), oz + H);
        fields.vertex(SIZE * (ox - rad), SIZE * (oy + rad), oz);
        fields.vertex(SIZE * (ox - rad), SIZE * (oy + rad), oz + H);
        fields.vertex(SIZE * (ox + rad), SIZE * (oy + rad), oz);
        fields.vertex(SIZE * (ox + rad), SIZE * (oy + rad), oz + H);
        fields.vertex(SIZE * (ox + rad), SIZE * (oy - rad), oz);
        fields.vertex(SIZE * (ox + rad), SIZE * (oy - rad), oz + H);
        // first two again
        fields.vertex(SIZE * (ox - rad), SIZE * (oy - rad), oz);
        fields.vertex(SIZE * (ox - rad), SIZE * (oy - rad), oz + H);
        fields.endShape();
    }

    @Override
    void move() {
        rot += delta;
    }

    @Override
    void draw() {
        // four copies
        if (Main.cos(rot) < 0) {
            // TODO forcefield on half the time
            pillar.setStroke(RED);
            p.shape(fields);
        } else {
            pillar.setStroke(MAGENTA);
        }
        p.pushMatrix();
        p.translate((ox + rad) * SIZE, (oy + rad) * SIZE, oz);
        p.shape(pillar);
        p.popMatrix();
        p.pushMatrix();
        p.translate((ox + rad) * SIZE, (oy - rad) * SIZE, oz);
        p.shape(pillar);
        p.popMatrix();
        p.pushMatrix();
        p.translate((ox - rad) * SIZE, (oy + rad) * SIZE, oz);
        p.shape(pillar);
        p.popMatrix();
        p.pushMatrix();
        p.translate((ox - rad) * SIZE, (oy - rad) * SIZE, oz);
        p.shape(pillar);
        p.popMatrix();
    }

    // TODO collision
}

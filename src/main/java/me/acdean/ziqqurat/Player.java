package me.acdean.ziqqurat;

import static me.acdean.ziqqurat.Main.SIZE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static processing.core.PApplet.constrain;
import processing.core.PVector;

/*
** Player
*/

public class Player {

    private static final Logger LOG = LoggerFactory.getLogger(Player.class);

    Main p;
    int direction = 0;
    PVector position = new PVector();   // this is NOT floor position
    PVector[] offset = {
        new PVector(1, 0, 0),   // 0 x increases    right?
        new PVector(0, 1, 0),   // 1 y increases    down
        new PVector(-1, 0, 0),  // 2 x decreases    left
        new PVector(0, -1, 0)   // 3 y decreases    up
    };

    Player(Main p) {
        this.p = p;
        position = p.floor.position(0, 0, 0);
    }

    void draw() {
        p.pushMatrix();
        p.stroke(0, 255, 0);
        p.fill(0);
        p.translate(position.x * SIZE, position.y * SIZE, position.z + SIZE + (SIZE / 2));  // this is centred, so add height
        p.box(SIZE, SIZE, SIZE * 2);
        p.popMatrix();
    }

    void clockwise() {
        direction = (direction + 1) % 4;
    }

    void anti() {
        direction = ((direction - 1) + 4) % 4;
    }

    // TODO if step on a SPIKE (heights = -1) then die
    void forwards() {
        int x1 = constrain((int)(position.x + 1 * offset[direction].x), 0, p.floor.count2);
        int y1 = constrain((int)(position.y + 1 * offset[direction].y), 0, p.floor.count2);
        int h = p.floor.heights[x1][y1];
        if (h != 0) {
            position.x = x1;
            position.y = y1;
            position.z = h * SIZE;
            LOG.info("Player {} {} {}", position.x, position.y, position.z);
        }
    }
}

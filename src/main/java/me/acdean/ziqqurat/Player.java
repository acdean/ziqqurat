package me.acdean.ziqqurat;

import processing.core.PVector;

/*
** Player
*/

public class Player {

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
        p.translate(position.x, position.y, position.z + 20);  // this is centred, so add height
        p.box(10, 10, 20);
        p.popMatrix();
    }

    void clockwise() {
        direction = (direction + 1) % 4;
    }

    void anti() {
        direction = ((direction - 1) + 4) % 4;
    }
    
    void forwards() {
        int x1 = (int)(position.x + 10 * offset[direction].x);
        int y1 = (int)(position.y + 10 * offset[direction].y);
        position.x = x1;
        position.y = y1;
    }
}

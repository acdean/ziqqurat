package me.acdean.ziqqurat;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
** Enemy - base class for all the enemies
*/

public class Enemies extends ArrayList<Enemy> {

    Main p;
    Logger logger = LoggerFactory.getLogger(Enemies.class);

    Enemies(Main p) {
        super();
        this.p = p;
    }

    void move() {
        for (Enemy e : this) {
            e.move();
        }
    }

    void draw() {
        for (Enemy e : this) {
            e.draw();
        }
    }
}

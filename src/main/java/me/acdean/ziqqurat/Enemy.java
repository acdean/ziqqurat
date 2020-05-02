package me.acdean.ziqqurat;

/*
** Enemy, mostly something to override
** Spike, just a spike
** Circler - rotates around platform
** Mutant - random steps
** Follower - moves towards (if close?)
** teleport?
** COllapsing stairs - will mean stairs are single use - impossible courses??
*/

public class Enemy {

    float x, y, z;  // position

    void move() {
    }

    void draw() {
    }

    boolean collision() {
        return true;
    }
}

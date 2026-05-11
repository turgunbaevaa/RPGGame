package com.game.units;

/**
 * Represents a unit that can force enemies to target it.
 */
public interface Tauntable {
    boolean isTaunting();
    void setTaunting(boolean taunting);
}

package com.game.factory;

public enum EnemyType {
    GOBLIN_GRUNT(40),    // High chance
    SKELETON_ARCHER(35), // Medium chance
    ORC_SHAMAN(25);      // Low chance

    private final int weight;

    EnemyType(int weight) {
        this.weight = weight;
    }

    public int getWeight() { return weight; }
}

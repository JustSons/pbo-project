package io.github.some_example_name.entities.enemies;

import io.github.some_example_name.entities.GameEntity; // Import GameEntity

public abstract class Enemy extends GameEntity { // Inheritance: Enemy IS A GameEntity
    protected int goldDrop; // Musuh menjatuhkan emas

    public Enemy(int maxHealth, int attackPower, int goldDrop, String texturePath) {
        super(maxHealth, attackPower, texturePath);
        this.goldDrop = goldDrop;
    }

    @Override
    public void attack(GameEntity target) {
        System.out.println(this.getClass().getSimpleName() + " attacks " + target.getClass().getSimpleName() + " for " + attackPower + " damage.");
        target.takeDamage(attackPower);
    }

    public int getGoldDrop() {
        return goldDrop;
    }
}

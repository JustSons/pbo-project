package io.github.some_example_name.entities.enemies;

import io.github.some_example_name.entities.GameEntity;

public abstract class Enemy extends GameEntity { // Inheritance: Enemy IS A GameEntity
    protected int goldDrop; // Musuh menjatuhkan emas

    // MODIFIKASI: Konstruktor Enemy sekarang menerima parameter animasi
    public Enemy(int maxHealth, int attackPower, int goldDrop,
         String idleSpriteSheetPath, int idleFrameCols, int idleFrameRows, float idleFrameDuration,
         String attackSpriteSheetPath, int attackFrameCols, int attackFrameRows, float attackFrameDuration,
         String hitSpriteSheetPath, int hitFrameCols, int hitFrameRows, float hitFrameDuration,
         String dyingSpriteSheetPath, int dyingFrameCols, int dyingFrameRows, float dyingFrameDuration, // BARU: Dying
         float displayWidth, float displayHeight) {
        super(maxHealth, attackPower, idleSpriteSheetPath, idleFrameCols, idleFrameRows, idleFrameDuration,
            displayWidth, displayHeight);
        this.goldDrop = goldDrop;

        setAttackAnimation(attackSpriteSheetPath, attackFrameCols, attackFrameRows, attackFrameDuration);
        setHitAnimation(hitSpriteSheetPath, hitFrameCols, hitFrameRows, hitFrameDuration);
        setDyingAnimation(dyingSpriteSheetPath, dyingFrameCols, dyingFrameRows, dyingFrameDuration); // BARU: Set Dying Animation
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

package io.github.some_example_name.effects.status;

import io.github.some_example_name.entities.GameEntity; // Import GameEntity
import com.badlogic.gdx.Gdx; // Untuk Gdx.graphics.getDeltaTime() atau serupa

public class PoisonEffect extends StatusEffect {
    private int damagePerTick;
    private float tickInterval = 1.0f; // Setiap 1 detik
    private float tickTimer = 0;

    public PoisonEffect(int duration, int damagePerTick) {
        super("Poison", duration);
        this.damagePerTick = damagePerTick;
    }

    @Override
    public void apply(GameEntity target) {
        System.out.println(target.getClass().getSimpleName() + " is poisoned!");
    }

    @Override
    public void update(GameEntity target, float delta) {
        timer += delta;
        tickTimer += delta;

        if (tickTimer >= tickInterval) {
            target.takeDamage(damagePerTick);
            System.out.println(target.getClass().getSimpleName() + " takes " + damagePerTick + " poison damage.");
            tickTimer -= tickInterval; // Reset timer untuk tick berikutnya
            duration--; // Kurangi durasi setiap tick
        }
    }

    @Override
    public void remove(GameEntity target) {
        System.out.println(target.getClass().getSimpleName() + " is no longer poisoned.");
    }
}

package io.github.some_example_name.effects.status;

import io.github.some_example_name.entities.GameEntity; // Import GameEntity

public abstract class StatusEffect {
    protected String name;
    protected int duration; // Durasi dalam "tick" atau detik
    protected float timer; // Untuk melacak waktu

    public StatusEffect(String name, int duration) {
        this.name = name;
        this.duration = duration;
        this.timer = 0;
    }

    public abstract void apply(GameEntity target);
    public abstract void update(GameEntity target, float delta); // Delta waktu dari render loop
    public abstract void remove(GameEntity target);

    public boolean isFinished() {
        return duration <= 0;
    }
}

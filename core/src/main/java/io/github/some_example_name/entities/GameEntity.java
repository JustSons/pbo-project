package io.github.some_example_name.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import io.github.some_example_name.interfaces.Attackable;
import io.github.some_example_name.interfaces.Renderable;
import io.github.some_example_name.effects.status.StatusEffect; // Import StatusEffect

public abstract class GameEntity implements Attackable, Renderable {
    protected int health;
    protected int maxHealth;
    protected int attackPower;
    protected Texture texture;
    protected float x, y; // Posisi di layar

    protected Array<StatusEffect> activeEffects; // KOMPOSISI: Entitas memiliki daftar efek status

    public GameEntity(int maxHealth, int attackPower, String texturePath) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.attackPower = attackPower;
        this.texture = new Texture(texturePath);
        this.activeEffects = new Array<>();
    }

    // Implementasi dari Attackable
    @Override
    public void takeDamage(int amount) {
        this.health -= amount;
        if (this.health < 0) {
            this.health = 0;
        }
        System.out.println(this.getClass().getSimpleName() + " took " + amount + " damage. Health: " + this.health);
    }

    // --- BARU: Tambahkan metode heal() di sini ---
    public void heal(int amount) {
        this.health += amount;
        if (this.health > maxHealth) {
            this.health = maxHealth; // Jangan melebihi max health
        }
        System.out.println(this.getClass().getSimpleName() + " healed for " + amount + ". Health: " + this.health);
    }
    // --- AKHIR BARU ---

    public int getAttackPower() {
        return attackPower;
    }

    @Override
    public boolean isAlive() {
        return this.health > 0;
    }

    @Override
    public int getHealth() { return health; }
    @Override
    public int getMaxHealth() { return maxHealth; }


    // Metode abstrak yang harus diimplementasikan oleh sub-kelas
    public abstract void attack(GameEntity target);

    // Implementasi dari Renderable
    @Override
    public void render(SpriteBatch batch, float x, float y) {
        this.x = x; // Update posisi render
        this.y = y;
        batch.draw(texture, x, y);
        // TODO: Mungkin gambar health bar di sini
    }

    // Untuk update efek status
    public void updateEffects(float delta) {
        Array<StatusEffect> effectsToRemove = new Array<>();
        for (StatusEffect effect : activeEffects) {
            effect.update(this, delta);
            if (effect.isFinished()) {
                effectsToRemove.add(effect);
            }
        }
        for (StatusEffect effect : effectsToRemove) {
            effect.remove(this);
            activeEffects.removeValue(effect, true);
        }
    }

    public void addStatusEffect(StatusEffect effect) {
        activeEffects.add(effect);
        effect.apply(this);
    }

    @Override
    public void dispose() {
        if (texture != null) { // Pastikan texture tidak null sebelum dispose
            texture.dispose();
        }
    }
}

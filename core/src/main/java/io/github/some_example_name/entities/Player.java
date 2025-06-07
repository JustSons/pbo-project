package io.github.some_example_name.entities;

import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.items.Item;
import io.github.some_example_name.items.weapons.Weapon;
import io.github.some_example_name.items.potions.HealthPotion;

public class Player extends GameEntity {
    private int score;
    private Array<Item> inventory;
    private Weapon equippedWeapon;

    public Player(String basePath) { // Menerima base path untuk semua animasi Player
        // MODIFIKASI: Panggil super() HANYA dengan parameter animasi IDLE
        // Ini adalah 8 argumen yang diharapkan oleh konstruktor GameEntity
        super(200, 10, // maxHealth, attackPower
            basePath + "Idle.png", 10, 1, 0.085f, // idleSpriteSheetPath, idleFrameCols, idleFrameRows, idleFrameDuration
            500, 500); // displayWidth, displayHeight

        this.score = 0;
        this.inventory = new Array<>();

        // BARU: Panggil metode setter untuk animasi Attack dan Hit setelah super()
        // Ini adalah tempat 8 argumen tambahan Anda sebelumnya akan digunakan
        setAttackAnimation(basePath + "Attack.png", 7, 1, 0.1f);
        setHitAnimation(basePath + "Hit.png", 3, 1, 0.15f);
        setDyingAnimation(basePath + "Death.png",11,1,0.2f);

        System.out.println("Player created!");
    }

    @Override
    public void attack(GameEntity target) {
        int damageDealt = getAttackPower();
        System.out.println("Player attacks " + target.getClass().getSimpleName() + " for " + damageDealt + " damage.");
        target.takeDamage(damageDealt);
    }

    @Override
    public int getAttackPower() {
        int totalAttack = super.getAttackPower();
        if (equippedWeapon != null) {
            totalAttack += equippedWeapon.getBonusAttack();
        }
        return totalAttack;
    }

    public void addScore(int points) {
        this.score += points;
        System.out.println("Score: " + score);
    }

    public void addItem(Item item) {
        inventory.add(item);
        System.out.println("Player picked up: " + item.getName());
        if (item instanceof HealthPotion) {
            System.out.println("Player now has " + getHealthPotions() + " health potions.");
        }
    }

    public Array<Item> getInventory() {
        return inventory;
    }

    public void equipWeapon(Weapon newWeapon) {
        this.equippedWeapon = newWeapon;
        System.out.println("Player equipped: " + newWeapon.getName());
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public int getScore() {
        return score;
    }

    public void addHealthPotion(HealthPotion potion) {
        addItem(potion);
    }

    public int getHealthPotions() {
        int count = 0;
        for (Item item : inventory) {
            if (item instanceof HealthPotion) {
                count++;
            }
        }
        return count;
    }

    public void useHealthPotion() {
        for (int i = 0; i < inventory.size; i++) {
            Item item = inventory.get(i);
            if (item instanceof HealthPotion) {
                HealthPotion potion = (HealthPotion) item;
                potion.interact(this);
                inventory.removeIndex(i);
                System.out.println("Used Health Potion. Remaining: " + getHealthPotions());
                return;
            }
        }
        System.out.println("No Health Potions to use!");
    }
}

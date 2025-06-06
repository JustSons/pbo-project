package io.github.some_example_name.entities;

import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.items.Item; // Import Item
import io.github.some_example_name.items.weapons.Weapon; // Import Weapon
import io.github.some_example_name.items.potions.HealthPotion; // BARU: Import HealthPotion

public class Player extends GameEntity { // Inheritance: Player IS A GameEntity
    private int score;
    private Array<Item> inventory; // Komposisi: Player HAS A list of Items
    private Weapon equippedWeapon; // Komposisi: Player HAS A Weapon
    private int healthPotionsCount; // BARU: Field untuk menghitung Health Potion

    public Player(String texturePath) {
        super(200, 10, texturePath); // Health 200, Attack 10 (dari GameEntity)
        this.score = 0;
        this.inventory = new Array<>();
        this.healthPotionsCount = 0; // BARU: Inisialisasi jumlah potion
        System.out.println("Player created!");
    }

    @Override
    public void attack(GameEntity target) {
        int damageDealt = getAttackPower(); // Panggil getAttackPower() yang sudah di-override
        System.out.println("Player attacks " + target.getClass().getSimpleName() + " for " + damageDealt + " damage.");
        target.takeDamage(damageDealt);
    }

    // BARU: Override getAttackPower untuk memasukkan damage dari senjata
    @Override
    public int getAttackPower() {
        int totalAttack = super.getAttackPower(); // Ambil base attack dari GameEntity
        if (equippedWeapon != null) {
            totalAttack += equippedWeapon.getBonusAttack(); // Tambahkan bonus dari senjata
        }
        return totalAttack;
    }
    // AKHIR BARU

    public void addScore(int points) {
        this.score += points;
        System.out.println("Score: " + score);
    }

    public void addItem(Item item) {
        inventory.add(item);
        System.out.println("Player picked up: " + item.getName());
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

    // --- BARU: Metode untuk Health Potion ---
    // --- Health Potion Specific Methods ---
    public void addHealthPotion() {
        this.healthPotionsCount++;
        System.out.println("Player gained a Health Potion. Total: " + healthPotionsCount);
    }

    public int getHealthPotions() {
        return healthPotionsCount;
    }

    public void useHealthPotion() {
        if (healthPotionsCount > 0) {
            // Coba temukan HealthPotion di inventaris
            for (int i = 0; i < inventory.size; i++) {
                Item item = inventory.get(i);
                if (item instanceof HealthPotion) {
                    HealthPotion potion = (HealthPotion) item;
                    potion.interact(this); // Interaksi potion dengan pemain
                    inventory.removeIndex(i); // Hapus potion dari inventaris setelah digunakan
                    healthPotionsCount--; // Kurangi hitungan juga
                    System.out.println("Used Health Potion. Remaining: " + healthPotionsCount);
                    return; // Keluar setelah menggunakan satu potion
                }
            }
            System.out.println("No Health Potions found in inventory!"); // Seharusnya tidak tercapai jika healthPotionsCount > 0
        } else {
            System.out.println("No Health Potions to use!");
        }
    }
    // --- End Health Potion Methods ---
    // --- AKHIR BARU ---
}

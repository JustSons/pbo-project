package io.github.some_example_name.entities;

import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.items.Item;
import io.github.some_example_name.items.weapons.Weapon;
import io.github.some_example_name.items.potions.HealthPotion; // Pastikan ini diimpor

public class Player extends GameEntity {
    private int score;
    private Array<Item> inventory; // Komposisi: Player HAS A list of Items
    private Weapon equippedWeapon;

    // HAPUS healthPotionsCount; kita akan menghitungnya dari inventaris
    // private int healthPotionsCount;

    public Player(String texturePath) {
        super(200, 10, texturePath);
        this.score = 0;
        this.inventory = new Array<>();
        // HAPUS inisialisasi healthPotionsCount
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
        // Tambahkan logika khusus jika ini HealthPotion
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

    // --- Health Potion Specific Methods (MODIFIKASI) ---

    // MODIFIKASI: addHealthPotion sekarang menerima objek HealthPotion
    public void addHealthPotion(HealthPotion potion) {
        addItem(potion); // Gunakan metode addItem yang sudah ada untuk menambahkannya ke inventaris
    }

    // MODIFIKASI: getHealthPotions sekarang menghitung potion di inventaris
    public int getHealthPotions() {
        int count = 0;
        for (Item item : inventory) {
            if (item instanceof HealthPotion) {
                count++;
            }
        }
        return count;
    }

    // MODIFIKASI: useHealthPotion mencari dan menggunakan potion dari inventaris
    public void useHealthPotion() {
        // Cari HealthPotion pertama di inventaris
        for (int i = 0; i < inventory.size; i++) {
            Item item = inventory.get(i);
            if (item instanceof HealthPotion) {
                HealthPotion potion = (HealthPotion) item;
                potion.interact(this); // Interaksi potion dengan pemain (akan memanggil player.heal())
                inventory.removeIndex(i); // Hapus potion dari inventaris setelah digunakan
                System.out.println("Used Health Potion. Remaining: " + getHealthPotions()); // Perbarui tampilan
                return; // Keluar setelah menggunakan satu potion
            }
        }
        System.out.println("No Health Potions to use!"); // Jika tidak ada potion ditemukan
    }
    // --- End Health Potion Methods ---
}

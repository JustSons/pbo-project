package io.github.some_example_name.items.weapons;

import io.github.some_example_name.items.Item; // Import Item
import io.github.some_example_name.entities.Player; // Import Player

public abstract class Weapon extends Item { // Inheritance: Weapon IS A Item
    protected int bonusAttack;

    public Weapon(String name, String texturePath, int bonusAttack) {
        super(name, texturePath);
        this.bonusAttack = bonusAttack;
    }

    public int getBonusAttack() {
        return bonusAttack;
    }

    @Override
    public void interact(Player player) { // Polymorphism: Override interact()
        System.out.println("Player picked up " + getName() + ". Equipping it.");
        player.equipWeapon(this); // Saat berinteraksi, pemain melengkapi senjata ini
    }
}

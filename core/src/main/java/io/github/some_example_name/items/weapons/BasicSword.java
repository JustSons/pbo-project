package io.github.some_example_name.items.weapons;

public class BasicSword extends Weapon { // Inheritance: BasicSword IS A Weapon
    public BasicSword() {
        super("Basic Sword", "items/basic_sword.png", 5); // Bonus attack 15
        System.out.println("Basic Sword created.");
    }
}

package io.github.some_example_name.items.weapons;

public class CommonSword extends Weapon { // Inheritance: BasicSword IS A Weapon
    public CommonSword() {
        super("Common Sword", "items/common_sword.png", 15); // Bonus attack 15
        System.out.println("Common Sword created.");
    }
}

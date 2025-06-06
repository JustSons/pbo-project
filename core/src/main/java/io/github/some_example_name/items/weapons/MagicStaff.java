package io.github.some_example_name.items.weapons;

public class MagicStaff extends Weapon { // Inheritance: MagicStaff IS A Weapon
    public MagicStaff() {
        super("Magic Staff", "items/magic_staff.png", 15); // Bonus attack 15
        System.out.println("Magic Staff created.");
    }
}

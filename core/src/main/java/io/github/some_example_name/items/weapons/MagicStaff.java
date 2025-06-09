package io.github.some_example_name.items.weapons;

public class MagicStaff extends Weapon { // Inheritance: MagicStaff IS A Weapon
    public MagicStaff() {
        super("Mr. Alby's Chosen One", "items/magic_staff.png", 999); // Bonus attack 15
        System.out.println("Mr. Alby's Chosen One");
    }
}

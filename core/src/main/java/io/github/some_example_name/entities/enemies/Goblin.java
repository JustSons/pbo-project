package io.github.some_example_name.entities.enemies;

public class Goblin extends Enemy { // Inheritance: Goblin IS A Enemy
    public Goblin(String texturePath) {
        super(30, 5, 10, texturePath); // Health 30, Attack 5, Gold 10
        System.out.println("Goblin spawned!");
    }
}

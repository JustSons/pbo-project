package io.github.some_example_name.entities.enemies;

public class Dragon extends Enemy { // Inheritance: Dragon IS A Enemy (Boss)
    public Dragon(String texturePath) {
        super(200, 25, 100, texturePath); // Health 200, Attack 25, Gold 100
        System.out.println("Dragon (BOSS) spawned!");
    }
}

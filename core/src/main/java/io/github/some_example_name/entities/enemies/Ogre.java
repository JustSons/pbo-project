package io.github.some_example_name.entities.enemies;

public class Ogre extends Enemy { // Inheritance: Ogre IS A Enemy
    public Ogre(String texturePath) {
        super(80, 15, 30, texturePath); // Health 80, Attack 15, Gold 30
        System.out.println("Ogre spawned!");
    }
}

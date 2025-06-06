package io.github.some_example_name.entities.enemies;

public class Ogre extends Enemy { // Inheritance: Ogre IS A Enemy
//    public Ogre(String spriteSheetPath) {
//        super(80, 15, 30, spriteSheetPath,4,1,0.15f,100,100); // Health 80, Attack 15, Gold 30
//        System.out.println("Ogre spawned!");
//    }
public Ogre(String basePath) { // Menerima base path untuk semua animasi Goblin
    // MODIFIKASI: Panggil super() dengan semua parameter animasi Goblin
    // Asumsi struktur folder: characters/goblin/Idle.png, characters/goblin/Attack.png, characters/goblin/Hit.png
    super(30, 5, 10,
        basePath + "Idle.png", 4, 1, 0.15f,  // Idle Animation (4 frames, 1 row, 0.15s per frame)
        basePath + "Attack.png", 6, 1, 0.1f, // Attack Animation (contoh: 6 frames, 1 row, 0.1s per frame)
        basePath + "Hit.png", 2, 1, 0.15f,  // Hit Animation (contoh: 2 frames, 1 row, 0.15s per frame)
        100, 100); // Ukuran display (width, height)
    System.out.println("Goblin spawned!");
}
}

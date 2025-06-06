package io.github.some_example_name.entities.enemies;

public class Ogre extends Enemy { // Inheritance: Ogre IS A Enemy
//    public Ogre(String spriteSheetPath) {
//        super(80, 15, 30, spriteSheetPath,4,1,0.15f,100,100); // Health 80, Attack 15, Gold 30
//        System.out.println("Ogre spawned!");
//    }
public Ogre(String basePath) { // Menerima base path untuk semua animasi Goblin
    // MODIFIKASI: Panggil super() dengan semua parameter animasi Goblin
    // Asumsi struktur folder: characters/goblin/Idle.png, characters/goblin/Attack.png, characters/goblin/Hit.png
    super(80, 15, 30,
        basePath + "Idle.png", 4, 1, 0.15f,
        basePath + "Attack.png", 8, 1, 0.1f, // frameCols Attack = 8, sesuai yang Anda berikan terakhir
        basePath + "Hit.png", 4, 1, 0.15f,  // frameCols Hit = 4, sesuai yang Anda berikan terakhir
        basePath + "Death.png", 4, 1, 0.15f, // BARU: Death Animation (contoh: 6 frames, 1 row, 0.15s per frame)
        500, 500);
    System.out.println("Ogre spawned!");
}
}

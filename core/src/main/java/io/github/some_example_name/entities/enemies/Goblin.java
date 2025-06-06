package io.github.some_example_name.entities.enemies;

import io.github.some_example_name.entities.GameEntity;

public class Goblin extends Enemy { // Inheritance: Goblin IS A Enemy
    public Goblin(String basePath) { // Menerima base path untuk semua animasi Goblin
        // MODIFIKASI: Panggil super() dengan semua parameter animasi Goblin
        // Asumsi struktur folder: characters/goblin/Idle.png, characters/goblin/Attack.png, characters/goblin/Hit.png
        super(30, 5, 10,
            basePath + "Idle.png", 4, 1, 0.15f,  // Idle Animation (4 frames, 1 row, 0.15s per frame)
            basePath + "Attack.png", 8, 1, 0.1f, // Attack Animation (contoh: 6 frames, 1 row, 0.1s per frame)
            basePath + "Hit.png", 4, 1, 0.15f,  // Hit Animation (contoh: 2 frames, 1 row, 0.15s per frame)
            500, 500); // Ukuran display (width, height)
        System.out.println("Goblin spawned!");
    }

    // Metode attack() tetap ada atau bisa dihapus jika sudah diimplementasikan di Enemy abstrak
    // @Override
    // public void attack(GameEntity target) {
    //     // Implementasi serangan spesifik Goblin jika ada
    //     super.attack(target); // Memanggil implementasi dari Enemy
    // }
}

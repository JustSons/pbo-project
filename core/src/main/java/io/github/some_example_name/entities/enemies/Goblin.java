package io.github.some_example_name.entities.enemies;

import io.github.some_example_name.entities.GameEntity;

public class Goblin extends Enemy {
    public Goblin(String basePath) {
        // MODIFIKASI: Panggil super() dengan semua parameter animasi Goblin, termasuk DYING
        super(30, 5, 10,
            basePath + "Idle.png", 4, 1, 0.15f,
            basePath + "Attack.png", 8, 1, 0.1f, // frameCols Attack = 8, sesuai yang Anda berikan terakhir
            basePath + "Hit.png", 4, 1, 0.15f,  // frameCols Hit = 4, sesuai yang Anda berikan terakhir
            basePath + "Death.png", 4, 1, 0.15f, // BARU: Death Animation (contoh: 6 frames, 1 row, 0.15s per frame)
            500, 500);
        System.out.println("Goblin spawned!");
    }


    // Metode attack() tetap ada atau bisa dihapus jika sudah diimplementasikan di Enemy abstrak
    // @Override
    // public void attack(GameEntity target) {
    //     // Implementasi serangan spesifik Goblin jika ada
    //     super.attack(target); // Memanggil implementasi dari Enemy
    // }
}

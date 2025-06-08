package io.github.some_example_name.entities.enemies;

public class Wizard extends Enemy {
    public Wizard(String basePath) {
        // MODIFIKASI: Panggil super() dengan semua parameter animasi Goblin, termasuk DYING
        super(500, 20, 1000,
            basePath + "Idle.png", 8, 1, 0.15f,
            basePath + "Attack.png", 8, 1, 0.1f, // frameCols Attack = 8, sesuai yang Anda berikan terakhir
            basePath + "Hit.png", 4, 1, 0.15f,  // frameCols Hit = 4, sesuai yang Anda berikan terakhir
            basePath + "Death.png", 5, 1, 0.15f, // BARU: Death Animation (contoh: 6 frames, 1 row, 0.15s per frame)
            500, 500);
        System.out.println("Wizard (Final Boss) spawned!");
    }


    // Metode attack() tetap ada atau bisa dihapus jika sudah diimplementasikan di Enemy abstrak
    // @Override
    // public void attack(GameEntity target) {
    //     // Implementasi serangan spesifik Goblin jika ada
    //     super.attack(target); // Memanggil implementasi dari Enemy
    // }
}

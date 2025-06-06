package io.github.some_example_name.entities.enemies;

public class Dragon extends Enemy { // Inheritance: Dragon IS A Enemy (Boss)
//    public Dragon(String spriteSheetPath) {
//        super(200, 25, 100, spriteSheetPath,4,1,0.15f,100,100); // Health 200, Attack 25, Gold 100
//        System.out.println("Dragon (BOSS) spawned!");
//    }

    //ini punyanya goblin pakai sementara
    public Dragon(String basePath) { // Menerima base path untuk semua animasi Goblin
        // MODIFIKASI: Panggil super() dengan semua parameter animasi Goblin
        // Asumsi struktur folder: characters/goblin/Idle.png, characters/goblin/Attack.png, characters/goblin/Hit.png
        super(200, 25, 100,
            basePath + "Idle.png", 8, 1, 0.15f,
            basePath + "Attack.png", 8, 1, 0.1f, // frameCols Attack = 8, sesuai yang Anda berikan terakhir
            basePath + "Hit.png", 4, 1, 0.15f,  // frameCols Hit = 4, sesuai yang Anda berikan terakhir
            basePath + "Death.png", 4, 1, 0.15f, // BARU: Death Animation (contoh: 6 frames, 1 row, 0.15s per frame)
            500, 500);
        System.out.println("Dragon spawned!");
    }
}

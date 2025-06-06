package io.github.some_example_name.lwjgl3; // Pastikan package ini sesuai dengan yang Anda gunakan

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Import kelas utama game Anda
import io.github.some_example_name.BookwormGame;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        // Panggil metode createApplication() untuk meluncurkan game
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        // Lwjgl3Application membutuhkan instance dari ApplicationListener.
        // Karena BookwormGame meng-extend Game (yang merupakan implementasi ApplicationListener),
        // kita cukup membuat instance dari BookwormGame di sini.
        return new Lwjgl3Application(new BookwormGame(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("BookwormGame"); // Judul jendela game Anda
        configuration.setWindowedMode(800, 600); // Ukuran jendela game (lebar, tinggi)
        configuration.useVsync(true); // Mengaktifkan VSync untuk sinkronisasi vertikal (menghindari screen tearing)
        configuration.setForegroundFPS(60); // Batasi Frames Per Second (FPS) ketika jendela dalam fokus
        // configuration.setIdleFPS(10); // Opsional: Batasi FPS ketika jendela tidak dalam fokus (misalnya, di latar belakang)
        // configuration.setResizable(false); // Opsional: Untuk membuat jendela tidak bisa diubah ukurannya
        // configuration.setMaximized(true); // Opsional: Untuk memulai dalam mode maximized
        // configuration.setInitialVisible(true); // Opsional: Memastikan jendela terlihat saat aplikasi dimulai
        // configuration.setDecorated(true); // Opsional: Menampilkan dekorasi jendela (misalnya, tombol minimize/maximize/close)
        // configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png"); // Opsional: Menentukan ikon jendela

        return configuration;
    }
}

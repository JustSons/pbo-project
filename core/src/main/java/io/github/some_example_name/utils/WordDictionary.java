package io.github.some_example_name.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random; // Untuk memilih kata acak

public class WordDictionary {
    // Root dari Trie (prefix tree) kita
    private static TrieNode root;
    private static final String DICTIONARY_PATH = "dictionary.txt"; // Pastikan file ini ada di folder assets
    private static final Random RANDOM = new Random(); // Untuk memilih kata acak

    // Huruf umum untuk pengacakan berbobot (English distribution example)
    // Sesuaikan ini jika game Anda menggunakan bahasa Indonesia
    private static final String COMMON_LETTERS = "AAAAAABBBCCCDDDDEEEEEEEEEEFFGGHHHHIIIIIIIIJJKKLLLLMMNNNNNOOOOOOOPPPQRRRRRRSSSSSTTTTTTUUUUVVWWXYYZ";

    /**
     * Inner class yang merepresentasikan sebuah node dalam Trie.
     * Setiap node memiliki map ke children (huruf berikutnya) dan flag
     * untuk menandai apakah node ini adalah akhir dari sebuah kata.
     */
    private static class TrieNode {
        Map<Character, TrieNode> children;
        boolean isEndOfWord;

        public TrieNode() {
            children = new HashMap<>();
            isEndOfWord = false;
        }
    }

    /**
     * Memuat kamus dari file ke dalam struktur data Trie.
     * Metode ini akan dipanggil sekali saat inisialisasi game.
     */
    public static void loadDictionary() {
        if (root == null) { // Hanya muat sekali
            root = new TrieNode(); // Inisialisasi root Trie
            try {
                FileHandle dictionaryFile = Gdx.files.internal(DICTIONARY_PATH);
                BufferedReader reader = new BufferedReader(dictionaryFile.reader());
                String line;
                int wordCount = 0;
                while ((line = reader.readLine()) != null) {
                    String word = line.trim().toUpperCase(); // Pastikan huruf besar
                    // Hanya tambahkan kata yang panjangnya minimal 3
                    if (word.length() >= 3) {
                        addWord(word); // Tambahkan kata ke Trie
                        wordCount++;
                    }
                }
                reader.close();
                Gdx.app.log("WordDictionary", "Loaded " + wordCount + " words into Trie.");
            } catch (IOException e) {
                Gdx.app.error("WordDictionary", "Error loading dictionary: " + e.getMessage());
            }
        }
    }

    /**
     * Menambahkan satu kata ke dalam Trie.
     * Dipanggil secara internal oleh loadDictionary().
     * @param word Kata yang akan ditambahkan.
     */
    private static void addWord(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            // Jika child untuk karakter ini belum ada, buat node baru
            current.children.putIfAbsent(ch, new TrieNode());
            current = current.children.get(ch); // Pindah ke child node
        }
        current.isEndOfWord = true; // Tandai akhir kata
    }

    /**
     * Memeriksa apakah sebuah string adalah kata yang valid dalam kamus.
     * @param word Kata yang akan diperiksa.
     * @return true jika kata valid, false jika tidak.
     */
    public static boolean isValidWord(String word) {
        if (root == null) {
            Gdx.app.error("WordDictionary", "Dictionary not loaded (isValidWord called before loadDictionary)!");
            return false;
        }
        TrieNode current = root;
        for (char ch : word.toUpperCase().toCharArray()) { // Pastikan huruf besar
            if (!current.children.containsKey(ch)) {
                return false; // Karakter tidak ditemukan, bukan kata valid
            }
            current = current.children.get(ch); // Pindah ke child node
        }
        return current.isEndOfWord; // Hanya valid jika ini adalah akhir dari sebuah kata
    }

    /**
     * Memeriksa apakah sebuah string adalah prefiks dari setidaknya satu kata dalam kamus.
     * Sangat efisien dengan Trie.
     * @param prefix Prefiks yang akan diperiksa.
     * @return true jika prefiks ditemukan, false jika tidak.
     */
    public static boolean isPrefix(String prefix) {
        if (root == null) {
            Gdx.app.error("WordDictionary", "Dictionary not loaded (isPrefix called before loadDictionary)!");
            return false;
        }
        TrieNode current = root;
        for (char ch : prefix.toUpperCase().toCharArray()) { // Pastikan huruf besar
            if (!current.children.containsKey(ch)) {
                return false; // Karakter tidak ditemukan, bukan prefiks
            }
            current = current.children.get(ch); // Pindah ke child node
        }
        return true; // Semua karakter prefiks ditemukan dalam Trie
    }

    /**
     * Mengembalikan satu huruf acak berdasarkan distribusi huruf umum.
     * @return String yang berisi satu huruf acak.
     */
    public static String getRandomCommonLetter() {
        if (COMMON_LETTERS.isEmpty()) return "A"; // Fallback jika string kosong
        return String.valueOf(COMMON_LETTERS.charAt(RANDOM.nextInt(COMMON_LETTERS.length())));
    }
}

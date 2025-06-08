package io.github.some_example_name.utils; // Buat package 'utils'

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random; // Tambahkan import Random jika ingin memilih kata acak

public class WordDictionary {
    private static HashSet<String> dictionary;
    private static final String DICTIONARY_PATH = "dictionary.txt"; // Pastikan file ini ada di folder assets
    private static final Random RANDOM = new Random(); // Untuk memilih kata acak

    // Huruf umum untuk pengacakan berbobot (English distribution example)
    // Sesuaikan ini jika game Anda menggunakan bahasa Indonesia
    private static final String COMMON_LETTERS = "AAAAAABBBCCCDDDDEEEEEEEEEEFFGGHHHHIIIIIIIIJJKKLLLLMMNNNNNOOOOOOOPPPQRRRRRRSSSSSTTTTTTUUUUVVWWXYYZ";

    public static void loadDictionary() {
        if (dictionary == null) {
            dictionary = new HashSet<>();
            try {
                FileHandle dictionaryFile = Gdx.files.internal(DICTIONARY_PATH);
                BufferedReader reader = new BufferedReader(dictionaryFile.reader());
                String line;
                while ((line = reader.readLine()) != null) {
                    // Hanya tambahkan kata yang panjangnya minimal 3
                    if (line.trim().length() >= 3) {
                        dictionary.add(line.trim().toUpperCase()); // Simpan dalam huruf besar untuk pencocokan yang mudah
                    }
                }
                reader.close();
                Gdx.app.log("WordDictionary", "Loaded " + dictionary.size() + " words.");
            } catch (IOException e) {
                Gdx.app.error("WordDictionary", "Error loading dictionary: " + e.getMessage());
            }
        }
    }

    public static boolean isValidWord(String word) {
        if (dictionary == null) {
            Gdx.app.error("WordDictionary", "Dictionary not loaded!");
            return false;
        }
        return dictionary.contains(word.toUpperCase());
    }

    public static String getRandomCommonLetter() {
        if (COMMON_LETTERS.isEmpty()) return "A"; // Fallback
        return String.valueOf(COMMON_LETTERS.charAt(RANDOM.nextInt(COMMON_LETTERS.length())));
    }

    // Metode untuk mencari apakah ada kata yang berawal dengan prefiks ini
    // (Bisa lebih efisien dengan Trie, tapi untuk HashSet kita harus iterasi atau menggunakan asumsi)
    public static boolean isPrefix(String prefix) {
        if (dictionary == null) return false;
        for (String word : dictionary) {
            if (word.startsWith(prefix.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}

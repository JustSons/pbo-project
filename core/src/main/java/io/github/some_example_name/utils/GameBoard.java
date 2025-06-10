package io.github.some_example_name.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // Not directly used in this class, but keeping for context
import com.badlogic.gdx.graphics.g2d.TextureRegion; // Not directly used for rendering letters, but for background
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array; // Not directly used in this class, but keeping for context

import io.github.some_example_name.tiles.BasicLetterTile;
import io.github.some_example_name.tiles.FireTile;
import io.github.some_example_name.tiles.GemTile;
import io.github.some_example_name.tiles.Tile;
import io.github.some_example_name.effects.tile.BonusDamageEffect; // Not directly used in board gen, but good for context

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashSet;
import java.util.Collections;

public class GameBoard {
    public Tile[][] tileGrid;
    private int gridRows;
    private int gridCols;
    private float tileSize;
    private float gridStartX;
    private float gridStartY;
    private TextureRegion defaultTileRegion;

    // Arah untuk DFS (8 arah)
    private final int[] DR = {-1, -1, -1, 0, 0, 1, 1, 1};
    private final int[] DC = {-1, 0, 1, -1, 1, -1, 0, 1};

    private static final char[] RARE_LETTERS_FOR_GEM = {'X', 'Y', 'Z', 'Q'};
    private static final Random randomGenerator = new Random(); // Objek Random yang reusable

    // NEW: Batas maksimal panjang kata yang dicari oleh DFS
    private final int MAX_DFS_WORD_LENGTH = 15; // Sesuaikan ini jika perlu (e.g., 10, 15)

    public GameBoard(int rows, int cols, float tileSize, float startX, float startY, TextureRegion defaultTileRegion) {
        this.gridRows = rows;
        this.gridCols = cols;
        this.tileSize = tileSize;
        this.gridStartX = startX;
        this.gridStartY = startY;
        this.defaultTileRegion = defaultTileRegion;

        tileGrid = new Tile[gridRows][gridCols];
        initializeAndValidateBoard(); // Panggil ini untuk mengisi dan memvalidasi papan awal
    }

    private void initializeAndValidateBoard() {
        boolean validBoardFound = false;
        long startTime = System.currentTimeMillis();
        int attempts = 0;
        final int MIN_REQUIRED_WORDS = 3; // Minimal 3 kata yang bisa dibentuk
        final int MAX_ATTEMPTS = 500; // Batas percobaan untuk menghindari loop tak terbatas

        while (!validBoardFound && attempts < MAX_ATTEMPTS) {
            fillBoardWithRandomTiles(); // Mengisi ulang papan
            List<String> solutions = findAllValidWords(); // Mencari semua kata di papan
            if (solutions.size() >= MIN_REQUIRED_WORDS) { // Cek jika jumlah kata mencukupi
                validBoardFound = true;
                Collections.sort(solutions); // Urutkan solusi untuk tampilan lebih baik
                // Tampilkan solusi kata di terminal (hanya 10 pertama untuk menghindari log yang terlalu panjang)
                Gdx.app.log("GameBoard", "Generated board with " + solutions.size() + " valid words (first 10): " + solutions.subList(0, Math.min(solutions.size(),200)));
            }
            attempts++;
        }

        if (!validBoardFound) {
            Gdx.app.error("GameBoard", "Failed to generate a board with at least " + MIN_REQUIRED_WORDS + " valid words after " + attempts + " attempts. Proceeding with potentially invalid board.");
            fillBoardWithRandomTiles(); // Tetap isi papan meskipun validasi gagal
        }
        long endTime = System.currentTimeMillis();
        Gdx.app.log("GameBoard", "Board generation took " + (endTime - startTime) + " ms in " + attempts + " attempts.");
    }

    private void fillBoardWithRandomTiles() {
        for (int r = 0; r < gridRows; r++) {
            for (int c = 0; c < gridCols; c++) {
                // Pastikan tile lama di-dispose sebelum diganti dengan yang baru
                if (tileGrid[r][c] != null) {
                    tileGrid[r][c].dispose();
                    tileGrid[r][c] = null; // Set to null after disposing
                }
                tileGrid[r][c] = generateRandomTile(r, c); // Gunakan helper method
            }
        }
    }

    // NEW: Helper method to generate a single random tile
    private Tile generateRandomTile(int r, int c) {
        char letterForTile;
        float tileX = gridStartX + c * tileSize;
        float tileY = gridStartY + r * tileSize;

        float chance = MathUtils.random.nextFloat();

        if (MathUtils.random.nextFloat() < 0.05f) { // Misalnya 5% kemungkinan huruf langka untuk GemTile
            letterForTile = getRandomRareLetterForGem();
        } else {
            letterForTile = WordDictionary.getRandomCommonLetter().charAt(0);
        }

        // Kemudian, tentukan jenis tile berdasarkan huruf yang dipilih
        boolean isRareLetter = false;
        for (char rareChar : RARE_LETTERS_FOR_GEM) {
            if (letterForTile == rareChar) {
                isRareLetter = true;
                break;
            }
        }

        if (isRareLetter) {
            // Jika hurufnya langka, SELALU jadi GemTile
            String gemColor = (MathUtils.random.nextFloat() < 0.5f) ? "Blue" : "Green"; // Pilih warna gem
            return new GemTile(letterForTile, gemColor, 2, tileX, tileY, tileSize, tileSize);
        } else if (chance < 0.10f) { // Jika bukan huruf langka, ada 10% kemungkinan FireTile
            return new FireTile(letterForTile, tileX, tileY, tileSize, tileSize);
        } else { // Sisanya BasicLetterTile
            return new BasicLetterTile(letterForTile, tileX, tileY, tileSize, tileSize);
        }
    }

    private char getRandomRareLetterForGem() {
        // Pilih secara acak dari array RARE_LETTERS_FOR_GEM
        return RARE_LETTERS_FOR_GEM[randomGenerator.nextInt(RARE_LETTERS_FOR_GEM.length)];
    }

    public List<String> findAllValidWords() {
        HashSet<String> foundWords = new HashSet<>();
        boolean[][] visited;

        for (int r = 0; r < gridRows; r++) {
            for (int c = 0; c < gridCols; c++) {
                visited = new boolean[gridRows][gridCols];
                findWordsFromTile(r, c, "", visited, foundWords);
            }
        }
        List<String> result = new ArrayList<>(foundWords);
        return result;
    }

    private void findWordsFromTile(int r, int c, String currentWord, boolean[][] visited, HashSet<String> foundWords) {
        if (r < 0 || r >= gridRows || c < 0 || c >= gridCols || visited[r][c]) {
            return;
        }

        // MODIFIED: Hentikan pencarian jika panjang kata melebihi batas
        if (currentWord.length() >= MAX_DFS_WORD_LENGTH) {
            return;
        }

        char letter = tileGrid[r][c].getLetter();
        String nextWord = currentWord + letter;

        // MODIFIED: WordDictionary.isPrefix() sekarang sangat cepat karena pakai Trie
        if (!WordDictionary.isPrefix(nextWord)) {
            return;
        }

        visited[r][c] = true;

        // MODIFIED: WordDictionary.isValidWord() sekarang sangat cepat karena pakai Trie
        if (nextWord.length() >= 3 && WordDictionary.isValidWord(nextWord)) {
            foundWords.add(nextWord);
        }

        for (int i = 0; i < 8; i++) {
            findWordsFromTile(r + DR[i], c + DC[i], nextWord, visited, foundWords);
        }

        visited[r][c] = false; // Backtrack
    }

    // REMOVED: Metode replaceUsedTiles dihapus karena Anda ingin reset board penuh
    // public void replaceUsedTiles(Array<Tile> usedTiles) { ... }

    public Tile getTile(int r, int c) {
        if (r >= 0 && r < gridRows && c >= 0 && c < gridCols) {
            return tileGrid[r][c];
        }
        return null;
    }

    public int getGridRows() {
        return gridRows;
    }

    public int getGridCols() {
        return gridCols;
    }

    public float getTileSize() {
        return tileSize;
    }

    public float getGridStartX() {
        return gridStartX;
    }

    public float getGridStartY() {
        return gridStartY;
    }

    public void dispose() {
        for (int r = 0; r < gridRows; r++) {
            for (int c = 0; c < gridCols; c++) {
                if (tileGrid[r][c] != null) {
                    tileGrid[r][c].dispose();
                    tileGrid[r][c] = null; // Set to null after disposing
                }
            }
        }
    }
}

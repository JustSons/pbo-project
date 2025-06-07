package io.github.some_example_name.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import io.github.some_example_name.tiles.BasicLetterTile;
import io.github.some_example_name.tiles.FireTile;
import io.github.some_example_name.tiles.GemTile;
import io.github.some_example_name.tiles.Tile;
import io.github.some_example_name.effects.tile.BonusDamageEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashSet;
import java.util.Collections; // Tambahkan import Collections

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

    public GameBoard(int rows, int cols, float tileSize, float startX, float startY, TextureRegion defaultTileRegion) {
        this.gridRows = rows;
        this.gridCols = cols;
        this.tileSize = tileSize;
        this.gridStartX = startX;
        this.gridStartY = startY;
        this.defaultTileRegion = defaultTileRegion;

        tileGrid = new Tile[gridRows][gridCols];
        initializeAndValidateBoard();
    }

    private void initializeAndValidateBoard() {
        boolean validBoardFound = false;
        long startTime = System.currentTimeMillis();
        int attempts = 0;
        final int MIN_REQUIRED_WORDS = 3; // Minimal 3 kata yang bisa dibentuk
        final int MAX_ATTEMPTS = 500; // Batas percobaan untuk menghindari loop tak terbatas

        while (!validBoardFound && attempts < MAX_ATTEMPTS) {
            fillBoardWithRandomTiles();
            List<String> solutions = findAllValidWords(); // Mencari semua kata di papan
            if (solutions.size() >= MIN_REQUIRED_WORDS) { // Cek jika jumlah kata mencukupi
                validBoardFound = true;
                Collections.sort(solutions); // Urutkan solusi untuk tampilan lebih baik
                // Tampilkan solusi kata di terminal
                Gdx.app.log("GameBoard", "Generated board with " + solutions.size() + " valid words (first 10): " + solutions.subList(0, Math.min(solutions.size(), 10)));
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

//                char randomLetter = WordDictionary.getRandomCommonLetter().charAt(0);
                char letterForTile;
                float tileX = gridStartX + c * tileSize;
                float tileY = gridStartY + r * tileSize;

                float chance = MathUtils.random.nextFloat();
                if (chance < 0.05f) { // 5% kemungkinan GemTile
                    // Jika ini adalah slot GemTile, maka hurufnya HARUS dari set langka
                    letterForTile = getRandomRareLetterForGem();
                    tileGrid[r][c] = new GemTile(letterForTile, "Red", 2, tileX, tileY, tileSize, tileSize);
                } else if (chance < 0.15f) { // 10% kemungkinan FireTile (0.05 + 0.10)
                    // FireTile bisa berupa huruf umum
                    letterForTile = WordDictionary.getRandomCommonLetter().charAt(0);
                    tileGrid[r][c] = new FireTile(letterForTile, tileX, tileY, tileSize, tileSize);
                } else { // Sisanya BasicLetterTile
                    // BasicLetterTile bisa berupa huruf umum
                    letterForTile = WordDictionary.getRandomCommonLetter().charAt(0);
                    tileGrid[r][c] = new BasicLetterTile(letterForTile, tileX, tileY, tileSize, tileSize);
                }
            }
        }
    }

    // HAPUS metode ini karena tidak lagi diperlukan, kita menggunakan findAllValidWords()
    // public boolean hasAtLeastOneValidWord() { ... }
    // private boolean findWordFromTileForValidation(...) { ... }

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
        // Gdx.app.log("GameBoard", "Found " + result.size() + " valid words on the board."); // Logging ini pindah ke initializeAndValidateBoard
        return result;
    }

    private void findWordsFromTile(int r, int c, String currentWord, boolean[][] visited, HashSet<String> foundWords) {
        if (r < 0 || r >= gridRows || c < 0 || c >= gridCols || visited[r][c]) {
            return;
        }

        char letter = tileGrid[r][c].getLetter();
        String nextWord = currentWord + letter;

        if (!WordDictionary.isPrefix(nextWord)) {
            return;
        }

        visited[r][c] = true;

        if (nextWord.length() >= 3 && WordDictionary.isValidWord(nextWord)) {
            foundWords.add(nextWord);
        }

        for (int i = 0; i < 8; i++) {
            findWordsFromTile(r + DR[i], c + DC[i], nextWord, visited, foundWords);
        }

        visited[r][c] = false;
    }

    // Metode replaceUsedTiles tidak lagi dipanggil di GameScreen setelah setiap kata valid
    // Karena kita akan melakukan full board scramble, metode ini tidak lagi relevan
    // untuk kasus penggantian tile setelah kata terbentuk.
    public void replaceUsedTiles(Array<Tile> usedTiles) {
        // Anda bisa memilih untuk menghapus metode ini jika tidak ada penggunaan lain
        // atau membiarkannya jika Anda berencana menggunakannya untuk mekanik berbeda.
        // Untuk tujuan ini, tidak ada perubahan yang diperlukan pada isi metode ini.
        for (Tile usedTile : usedTiles) {
            for (int r = 0; r < gridRows; r++) {
                for (int c = 0; c < gridCols; c++) {
                    if (tileGrid[r][c] == usedTile) {
                        usedTile.dispose();

                        char randomLetter = WordDictionary.getRandomCommonLetter().charAt(0);
                        float tileX = gridStartX + c * tileSize;
                        float tileY = gridStartY + r * tileSize;

                        float chance = MathUtils.random.nextFloat();
                        if (chance < 0.05f) {
                            tileGrid[r][c] = new GemTile(randomLetter, "Green", 2, tileX, tileY, tileSize, tileSize);
                        } else if (chance < 0.10f) {
                            tileGrid[r][c] = new FireTile(randomLetter, tileX, tileY, tileSize, tileSize);
//                            ((FireTile) tileGrid[r][c]).addEffect(new BonusDamageEffect(5));
                        } else {
                            tileGrid[r][c] = new BasicLetterTile(randomLetter, tileX, tileY, tileSize, tileSize);
                        }
                        break;
                    }
                }
            }
        }
    }

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

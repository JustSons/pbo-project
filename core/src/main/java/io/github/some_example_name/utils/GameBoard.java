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
import java.util.HashSet; // Tambahkan import HashSet

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

        while (!validBoardFound && attempts < 100) {
            fillBoardWithRandomTiles();
            // Cek apakah ada kata valid. Jika Anda mau, Anda bisa memanggil `findAllValidWords` di sini
            // dan cek apakah list-nya tidak kosong.
            if (hasAtLeastOneValidWord()) {
                validBoardFound = true;
            }
            attempts++;
        }

        if (!validBoardFound) {
            Gdx.app.error("GameBoard", "Failed to generate a board with at least one valid word after " + attempts + " attempts. Proceeding with potentially invalid board.");
            fillBoardWithRandomTiles();
        }
        long endTime = System.currentTimeMillis();
        Gdx.app.log("GameBoard", "Board generation took " + (endTime - startTime) + " ms in " + attempts + " attempts.");
    }

    private void fillBoardWithRandomTiles() {
        for (int r = 0; r < gridRows; r++) {
            for (int c = 0; c < gridCols; c++) {
                if (tileGrid[r][c] != null) {
                    tileGrid[r][c].dispose();
                }

                char randomLetter = WordDictionary.getRandomCommonLetter().charAt(0);
                float tileX = gridStartX + c * tileSize;
                float tileY = gridStartY + r * tileSize;

                float chance = MathUtils.random.nextFloat();
                if (chance < 0.05f) {
                    tileGrid[r][c] = new GemTile(randomLetter, "Red", 2, tileX, tileY, tileSize, tileSize);
                } else if (chance < 0.10f) {
                    tileGrid[r][c] = new FireTile(randomLetter, tileX, tileY, tileSize, tileSize);
                    ((FireTile) tileGrid[r][c]).addEffect(new BonusDamageEffect(5));
                } else {
                    tileGrid[r][c] = new BasicLetterTile(randomLetter, tileX, tileY, tileSize, tileSize);
                }
            }
        }
    }

    // Metode ini digunakan untuk memastikan minimal ada satu kata (seperti sebelumnya)
    public boolean hasAtLeastOneValidWord() {
        boolean[][] visited;
        for (int r = 0; r < gridRows; r++) {
            for (int c = 0; c < gridCols; c++) {
                visited = new boolean[gridRows][gridCols];
                if (findWordFromTileForValidation(r, c, "", visited)) { // Ganti nama metode untuk menghindari kebingungan
                    return true;
                }
            }
        }
        return false;
    }

    private boolean findWordFromTileForValidation(int r, int c, String currentWord, boolean[][] visited) {
        if (r < 0 || r >= gridRows || c < 0 || c >= gridCols || visited[r][c]) {
            return false;
        }

        char letter = tileGrid[r][c].getLetter();
        String nextWord = currentWord + letter;

        if (!WordDictionary.isPrefix(nextWord)) {
            return false;
        }

        visited[r][c] = true;

        // Cek validitas kata hanya jika panjangnya minimal 3 (sesuai logika game Anda)
        if (nextWord.length() >= 3 && WordDictionary.isValidWord(nextWord)) {
            visited[r][c] = false; // Backtrack
            return true;
        }

        for (int i = 0; i < 8; i++) {
            if (findWordFromTileForValidation(r + DR[i], c + DC[i], nextWord, visited)) {
                visited[r][c] = false; // Backtrack
                return true;
            }
        }

        visited[r][c] = false; // Backtrack
        return false;
    }

    // --- METODE BARU UNTUK MENCARI SEMUA KATA ---
    public List<String> findAllValidWords() {
        HashSet<String> foundWords = new HashSet<>(); // Gunakan HashSet untuk menghindari duplikat
        boolean[][] visited;

        for (int r = 0; r < gridRows; r++) {
            for (int c = 0; c < gridCols; c++) {
                visited = new boolean[gridRows][gridCols]; // Reset visited untuk setiap titik awal
                findWordsFromTile(r, c, "", visited, foundWords);
            }
        }
        List<String> result = new ArrayList<>(foundWords);
        Gdx.app.log("GameBoard", "Found " + result.size() + " valid words on the board.");
        return result;
    }

    private void findWordsFromTile(int r, int c, String currentWord, boolean[][] visited, HashSet<String> foundWords) {
        if (r < 0 || r >= gridRows || c < 0 || c >= gridCols || visited[r][c]) {
            return; // Berhenti jika di luar batas atau sudah dikunjungi
        }

        char letter = tileGrid[r][c].getLetter();
        String nextWord = currentWord + letter;

        // Pruning: jika nextWord bukan prefiks dari kata apapun di kamus, berhenti jelajah jalur ini
        if (!WordDictionary.isPrefix(nextWord)) {
            return;
        }

        visited[r][c] = true; // Tandai sebagai dikunjungi

        // Jika kata valid dan panjangnya minimal 3, tambahkan ke daftar
        if (nextWord.length() >= 3 && WordDictionary.isValidWord(nextWord)) {
            foundWords.add(nextWord);
        }

        // Jelajahi tetangga (8 arah)
        for (int i = 0; i < 8; i++) {
            findWordsFromTile(r + DR[i], c + DC[i], nextWord, visited, foundWords);
        }

        visited[r][c] = false; // Backtrack: batalkan tanda kunjungan untuk jalur lain
    }
    // --- AKHIR METODE BARU ---

    public void replaceUsedTiles(Array<Tile> usedTiles) {
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
                            ((FireTile) tileGrid[r][c]).addEffect(new BonusDamageEffect(5));
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
                }
            }
        }
    }
}

package io.github.some_example_name.utils;

import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.tiles.Tile; // Import Tile
import io.github.some_example_name.tiles.GemTile; // Import GemTile (spesifik untuk efek)

public class WordCalculator {
    public static int calculateWordValue(Array<Tile> selectedTiles) {
        int totalValue = 0;
        int wordMultiplier = 1;

        for (Tile tile : selectedTiles) {
            totalValue += tile.getValue(); // Polymorphic call: getValue() dari BasicLetterTile, GemTile, dll.
            if (tile instanceof GemTile) {
                // Contoh logika: Semua gem di kata memberikan multiplier ke total kata
                wordMultiplier *= ((GemTile) tile).getBonusMultiplier(); // Mengambil multiplier spesifik GemTile
            }
        }
        return totalValue * wordMultiplier;
    }
}

package io.github.some_example_name.utils;

import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.tiles.Tile; // Import Tile
import io.github.some_example_name.tiles.GemTile; // Import GemTile (spesifik untuk efek)

import io.github.some_example_name.tiles.FireTile; // BARU: Import FireTile

public class WordCalculator {
    public static int calculateWordValue(Array<Tile> selectedTiles) {
        int totalTileValues = 0; // Penjumlahan nilai dasar setiap tile (termasuk +2 dari FireTile)
        int wordMultiplier = 1;
        int fireTileDirectDamageBonus = 0; // BARU: Untuk bonus damage +5 dari FireTile

        for (Tile tile : selectedTiles) {
            totalTileValues += tile.getValue(); // Mengambil nilai dasar tile (yang sudah termasuk bonus +2 dari FireTile jika itu FireTile)

            if (tile instanceof GemTile) {
                wordMultiplier *= ((GemTile) tile).getBonusMultiplier();
            } else if (tile instanceof FireTile) {
                // Menambahkan bonus damage spesifik dari FireTile (yang bernilai 2)
                fireTileDirectDamageBonus += ((FireTile) tile).getBonusDamage();
            }
        }
        System.out.println("firegemtile:"+fireTileDirectDamageBonus);
        System.out.println("wordmultiplier:"+wordMultiplier);
        System.out.println("totaltilevalue:"+totalTileValues);
        // Total damage adalah (penjumlahan nilai dasar * multiplier) + bonus damage langsung dari FireTile
        return (totalTileValues * wordMultiplier) + fireTileDirectDamageBonus;
    }
}

package io.github.some_example_name.tiles;

// FireTile juga bisa memiliki efek status yang diaplikasikan pada musuh
public class FireTile extends Tile { // Inheritance: FireTile IS A Tile
    private int bonusDamage;

    public FireTile(char letter, float x, float y, float width, float height) {
        super(letter, "tiles/fire_tile.png", calculateBasicValue(letter) + 2, x, y, width, height); // Nilai dasar + bonus
        this.bonusDamage = 2;
        System.out.println("Fire Tile '" + letter + "' created.");
    }

    // Metode ini bisa dipanggil oleh TileEffect yang dikomposisikan
    public int getBonusDamage() {
        return bonusDamage;
    }
}

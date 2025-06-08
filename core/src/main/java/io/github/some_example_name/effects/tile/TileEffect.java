package io.github.some_example_name.effects.tile;

import io.github.some_example_name.tiles.Tile; // Import Tile
import io.github.some_example_name.entities.GameEntity; // Import GameEntity

public abstract class TileEffect {
    protected String name;
    protected String description;

    public TileEffect(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public abstract void apply(Tile tile); // Dipanggil saat efek ditambahkan ke tile
    public abstract void onWordUse(Tile tile, GameEntity target); // Dipanggil saat tile digunakan dalam kata

    public String getName() {
        return name;
    }
}

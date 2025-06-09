package io.github.some_example_name.effects.tile;

import io.github.some_example_name.tiles.Tile; // Import Tile
import io.github.some_example_name.entities.GameEntity; // Import GameEntity

public class BonusDamageEffect extends TileEffect {
    private int bonusDamage;

    public BonusDamageEffect(int damage) {
        super("Bonus Damage", "Adds " + damage + " bonus damage to the word.");
        this.bonusDamage = damage;
    }

    @Override
    public void apply(Tile tile) {
        System.out.println("Tile '" + tile.getLetter() + "' now has " + bonusDamage + " bonus damage effect.");
    }

    @Override
    public void onWordUse(Tile tile, GameEntity target) {
        System.out.println("Applying " + bonusDamage + " bonus damage from tile '" + tile.getLetter() + "'.");
        target.takeDamage(bonusDamage); // Langsung berikan kerusakan tambahan
    }
}

package io.github.some_example_name.tiles;

public class GemTile extends Tile { // Inheritance: GemTile IS A Tile
    private int bonusMultiplier;
    private String gemType;

    public GemTile(char letter, String gemType, int bonusMultiplier, float x, float y, float width, float height) {
        super(letter, "tiles/gem_tile_" + gemType.toLowerCase() + ".png", calculateBasicValue(letter), x, y, width, height);
        this.gemType = gemType;
        this.bonusMultiplier = bonusMultiplier;
        System.out.println(gemType + " Gem Tile '" + letter + "' created with multiplier " + bonusMultiplier);
    }

    @Override
    public int getValue() {
        return super.getValue() * bonusMultiplier; // Polymorphism: Override getValue
    }

    public String getGemType() {
        return gemType;
    }

    public int getBonusMultiplier() {
        return bonusMultiplier;
    }
}

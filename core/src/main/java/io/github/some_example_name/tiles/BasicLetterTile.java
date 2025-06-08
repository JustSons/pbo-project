package io.github.some_example_name.tiles;

public class BasicLetterTile extends Tile { // Inheritance: BasicLetterTile IS A Tile
    public BasicLetterTile(char letter, float x, float y, float width, float height) {
        super(letter, "tiles/basic_tile.png", calculateBasicValue(letter), x, y, width, height);
        System.out.println("Basic Letter Tile '" + letter + "' created.");
    }
}

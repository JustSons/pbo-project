package io.github.some_example_name.tiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import io.github.some_example_name.interfaces.Renderable;
import io.github.some_example_name.interfaces.Clickable;
import io.github.some_example_name.effects.tile.TileEffect; // Import TileEffect

public abstract class Tile implements Renderable, Clickable { // Tile implements Renderable and Clickable
    protected char letter;
    protected Texture texture;
    protected int value; // Nilai dasar untuk kata

    // Untuk Clickable
    public float x, y, width, height;

    protected Array<TileEffect> activeEffects; // Komposisi: Tile HAS A list of TileEffects

    public Tile(char letter, String texturePath, int value, float x, float y, float width, float height) {
        this.letter = Character.toUpperCase(letter);
        this.texture = new Texture(texturePath);
        this.value = value;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.activeEffects = new Array<>();
    }

    public char getLetter() {
        return letter;
    }

    public int getValue() {
        return value;
    }

    @Override
    public void render(SpriteBatch batch, float x, float y) {
        batch.draw(texture, x, y, width, height);
        // Asumsi huruf akan digambar di GameScreen
    }

    // Implementasi Clickable
    @Override
    public boolean contains(float cx, float cy) {
        return cx >= x && cx < x + width && cy >= y && cy < y + height;
    }

    @Override
    public void onClick(float cx, float cy) {
        System.out.println("Tile '" + letter + "' at (" + (int)x + "," + (int)y + ") clicked.");
        // Logika detail pemilihan kata ada di GameScreen
    }

    public void addEffect(TileEffect effect) {
        activeEffects.add(effect);
        effect.apply(this);
    }

    public Array<TileEffect> getActiveEffects() {
        return activeEffects;
    }

    @Override
    public void dispose() {
        texture.dispose();
    }

    // Utility method untuk menghitung nilai dasar huruf
    protected static int calculateBasicValue(char letter) {
        switch (Character.toUpperCase(letter)) {
            case 'A': case 'E': case 'I': case 'O': case 'U': case 'L': case 'N': case 'R': case 'S': case 'T':
                return 1;
            case 'D': case 'G':
                return 2;
            case 'B': case 'C': case 'M': case 'P':
                return 3;
            case 'F': case 'H': case 'V': case 'W': case 'Y':
                return 4;
            case 'K':
                return 5;
            case 'J': case 'X':
                return 8;
            case 'Q': case 'Z':
                return 10;
            default:
                return 0;
        }
    }
}

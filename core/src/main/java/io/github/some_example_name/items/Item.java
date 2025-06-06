package io.github.some_example_name.items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.some_example_name.interfaces.Renderable;
import io.github.some_example_name.interfaces.Interactable; // Import Interactable

public abstract class Item implements Renderable, Interactable { // Item implements Renderable, Interactable
    protected String name;
    protected Texture texture;

    public Item(String name, String texturePath) {
        this.name = name;
        this.texture = new Texture(texturePath);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void render(SpriteBatch batch, float x, float y) {
        batch.draw(texture, x, y);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}

package io.github.some_example_name.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Renderable {
    void render(SpriteBatch batch, float x, float y);
    void dispose(); // Penting untuk manajemen memori di LibGDX
}

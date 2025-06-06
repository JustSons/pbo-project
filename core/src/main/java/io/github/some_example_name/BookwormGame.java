package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.viewport.Viewport; // Import Viewport
import com.badlogic.gdx.utils.viewport.ScreenViewport; // Import ScreenViewport (atau FitViewport, StretchViewport)

public class BookwormGame extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
    private Texture tileTexture;
    private TextureRegion tileTextureRegion;
    private Texture tileHighlightTexture;
    public Viewport viewport; // Tambahkan viewport di sini

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Inisialisasi viewport
        viewport = new ScreenViewport(); // Pilih jenis viewport yang sesuai dengan kebutuhan Anda

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pixel_font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        parameter.color = Color.WHITE;
        font = generator.generateFont(parameter);
        generator.dispose();

        tileTexture = new Texture(Gdx.files.internal("tiles/basic_tile.png"));
        tileTextureRegion = new TextureRegion(tileTexture);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        tileHighlightTexture = new Texture(pixmap);
        pixmap.dispose();

        setScreen(new GameScreen(this));
    }

    public TextureRegion getTileTextureRegion() {
        return tileTextureRegion;
    }

    public Texture getTileHighlightTexture() {
        return tileHighlightTexture;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height, true); // Update viewport saat ukuran layar berubah
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        tileTexture.dispose();
        if (tileHighlightTexture != null) {
            tileHighlightTexture.dispose();
        }
        super.dispose();
    }
}

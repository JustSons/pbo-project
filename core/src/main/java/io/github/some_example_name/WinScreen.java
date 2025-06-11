package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

// UI Imports
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

public class WinScreen extends ScreenAdapter {
    final BookwormGame game;
    OrthographicCamera camera;
    SpriteBatch batch;
    private int finalScore;
    private GlyphLayout winLayout; // Mengubah nama dari gameOverLayout
    private GlyphLayout scoreLayout;

    private Stage stage;
    private Skin skin;
    private TextButton restartButton;
    private TextButton exitButton;

    public WinScreen(final BookwormGame game, int finalScore) {
        this.game = game;
        this.finalScore = finalScore;
        batch = game.batch;

        camera = new OrthographicCamera();
        // Gunakan ukuran yang sama dengan GameScreen untuk konsistensi
        camera.setToOrtho(false, 800, 600);

        winLayout = new GlyphLayout(); // Mengubah nama dari gameOverLayout
        scoreLayout = new GlyphLayout();

        stage = new Stage(game.viewport, batch); // Gunakan viewport dari game
        skin = new Skin(Gdx.files.internal("skins/uiskin.json"));
        skin.add("default-font", game.font);

        // Restart Button
        restartButton = new TextButton("Play Again!", skin); // Mengubah teks tombol
        restartButton.setSize(200, 70);
        restartButton.setPosition(stage.getWidth() / 2 - restartButton.getWidth() / 2, stage.getHeight() / 2 - 50);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game)); // Mulai permainan baru
                dispose();
            }
        });
        stage.addActor(restartButton);

        // Exit Button
        exitButton = new TextButton("Exit Game", skin);
        exitButton.setSize(200, 70);
        exitButton.setPosition(stage.getWidth() / 2 - exitButton.getWidth() / 2, stage.getHeight() / 2 - 150);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit(); // Tutup aplikasi
            }
        });
        stage.addActor(exitButton);

        Gdx.input.setInputProcessor(stage); // Atur input processor ke stage untuk elemen UI
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        game.font.setColor(Color.YELLOW); // Mengubah warna font menjadi kuning
        winLayout.setText(game.font, "YOU GOT THE RESURRECTION STONE!"); // Mengubah teks pesan
        game.font.draw(batch, winLayout, (stage.getWidth() - winLayout.width) / 2, stage.getHeight() / 2 + 150);

        game.font.setColor(Color.WHITE);
        scoreLayout.setText(game.font, "Final Score: " + finalScore);
        game.font.draw(batch, scoreLayout, (stage.getWidth() - scoreLayout.width) / 2, stage.getHeight() / 2 + 100);

        batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
//        // Pastikan skin juga di-dispose jika tidak di-manage oleh AssetManager global
//        if (skin != null) {
//            skin.dispose();
//        }
    }
}

package io.github.some_example_name;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import io.github.some_example_name.tiles.Tile;

public class TileInputProcessor extends InputAdapter {
    private GameScreen screen;

    public TileInputProcessor(GameScreen screen) {
        this.screen = screen;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 worldCoordinates = new Vector3(screenX, screenY, 0);
        screen.camera.unproject(worldCoordinates);

        for (int r = 0; r < screen.gameBoard.getGridRows(); r++) {
            for (int c = 0; c < screen.gameBoard.getGridCols(); c++) {
                Tile tile = screen.gameBoard.tileGrid[r][c];
                if (tile != null && tile.contains(worldCoordinates.x, worldCoordinates.y)) {
                    // Panggil onClick pada tile (interface Clickable)
                    tile.onClick(worldCoordinates.x, worldCoordinates.y);
                    // Kirim tile yang diklik ke GameScreen untuk logika pemilihan kata
                    screen.handleTileClick(tile);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // Hapus pemanggilan processWord() di sini.
        // processWord() sekarang dipicu oleh tombol SUBMIT.
        return false; // Event tidak dikonsumsi oleh TileInputProcessor lagi di touchUp
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (Gdx.input.isButtonPressed(0)) {
            Vector3 worldCoordinates = new Vector3(screenX, screenY, 0);
            screen.camera.unproject(worldCoordinates);

            if (screen.getSelectedTiles().size > 0) {
                Tile lastSelected = screen.getSelectedTiles().peek();
                for (int r = 0; r < screen.gameBoard.getGridRows(); r++) {
                    for (int c = 0; c < screen.gameBoard.getGridCols(); c++) {
                        Tile tile = screen.gameBoard.tileGrid[r][c];
                        if (tile != null && tile.contains(worldCoordinates.x, worldCoordinates.y) && tile != lastSelected) {
                            screen.handleTileClick(tile);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}

package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.MathUtils;

// UI Imports
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;

// Import kelas-kelas OOP Anda
import io.github.some_example_name.entities.Player;
import io.github.some_example_name.entities.enemies.Enemy;
import io.github.some_example_name.entities.enemies.Goblin;
import io.github.some_example_name.entities.enemies.Ogre;
import io.github.some_example_name.tiles.Tile;
import io.github.some_example_name.effects.tile.TileEffect;
import io.github.some_example_name.effects.tile.BonusDamageEffect;
import io.github.some_example_name.utils.WordCalculator;
import io.github.some_example_name.utils.WordDictionary;
import io.github.some_example_name.utils.GameBoard;
import io.github.some_example_name.items.weapons.BasicSword;
import io.github.some_example_name.items.weapons.MagicStaff; // BARU: Import MagicStaff
import io.github.some_example_name.items.weapons.Weapon; // BARU: Import Weapon (untuk random weapon)
import io.github.some_example_name.items.potions.HealthPotion; // BARU: Import HealthPotion
import io.github.some_example_name.items.Item; // BARU: Import kelas Item

import java.util.List;
import java.util.Collections;

public class GameScreen extends ScreenAdapter {
    final BookwormGame game;
    OrthographicCamera camera;
    SpriteBatch batch;

    Player player;
    Enemy currentEnemy;
    public GameBoard gameBoard;

    private Array<Tile> selectedTiles;
    private String currentWord;

    private float tileSize = 64;
    private int gridRows = 8;
    private int gridCols = 8;
    private float gridStartX;
    private float gridStartY;

    private GlyphLayout glyphLayout;
    private TextureRegion defaultTileTextureRegion;

    // UI elements
    private Stage stage;
    private Skin skin;
    private TextButton submitButton;
    private TextButton clearButton;
    private TextButton usePotionButton; // BARU: Tombol untuk menggunakan potion
    private GlyphLayout potionCountLayout; // BARU: Untuk menampilkan jumlah potion
    private TextButton backpackButton; // BARU: Tombol untuk membuka backpack
    private boolean isBackpackOpen = false; // BARU: State untuk backpack
    private com.badlogic.gdx.scenes.scene2d.ui.Window backpackWindow; // BARU: Jendela backpack
    private com.badlogic.gdx.scenes.scene2d.ui.Table inventoryTable; // BARU: Tabel di dalam jendela

    public GameScreen(final BookwormGame game) {
        this.game = game;
        batch = game.batch;

        WordDictionary.loadDictionary();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        gridStartX = (800 - (gridCols * tileSize)) / 2;
        gridStartY = (600 - (gridRows * tileSize)) / 2;

        player = new Player("characters/player.png");
        player.equipWeapon(new BasicSword()); // Set initial weapon

        currentEnemy = new Goblin("characters/goblin.png");

        defaultTileTextureRegion = game.getTileTextureRegion();

        // Inisialisasi papan awal
        initializeNewBoard();

        selectedTiles = new Array<>();
        currentWord = "";

        glyphLayout = new GlyphLayout();
        potionCountLayout = new GlyphLayout(); // BARU: Inisialisasi untuk hitungan potion

        stage = new Stage(game.viewport, batch);
        skin = new Skin(Gdx.files.internal("skins/uiskin.json"));
        skin.add("default-font", game.font);

        submitButton = new TextButton("SUBMIT", skin);
        submitButton.setSize(100, 50);
        submitButton.setPosition(gridStartX + gridCols * tileSize / 2 - submitButton.getWidth() - 10, gridStartY - submitButton.getHeight() - 10);
        submitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                processWord();
            }
        });
        stage.addActor(submitButton);

        clearButton = new TextButton("CLEAR", skin);
        clearButton.setSize(100, 50);
        clearButton.setPosition(gridStartX + gridCols * tileSize / 2 + 10, gridStartY - clearButton.getHeight() - 10);
        clearButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resetWordSelection();
            }
        });
        stage.addActor(clearButton);

        // BARU: Tombol untuk menggunakan Health Potion
        usePotionButton = new TextButton("Use Potion", skin);
        usePotionButton.setSize(120, 50);
        usePotionButton.setPosition(50, Gdx.graphics.getHeight() - 100);
        usePotionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Saat tombol ini diklik, cukup panggil useHealthPotion() di Player
                player.useHealthPotion();
                // Setelah digunakan, update konten backpack jika terbuka,
                // karena jumlah potion bisa berubah
                if (isBackpackOpen) {
                    updateBackpackContent();
                }
            }
        });
        stage.addActor(usePotionButton);
        // AKHIR BARU

        // ... (existing clearButton and usePotionButton setup)

        // BARU: Tombol Backpack
        backpackButton = new TextButton("Backpack", skin);
        backpackButton.setSize(120, 50);
        backpackButton.setPosition(50, Gdx.graphics.getHeight() - 170); // Posisikan di bawah tombol potion
        backpackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isBackpackOpen = !isBackpackOpen; // Toggle state backpack
                if (isBackpackOpen) {
                    openBackpack();
                } else {
                    closeBackpack();
                }
            }
        });
        stage.addActor(backpackButton);

        // BARU: Inisialisasi jendela backpack
        backpackWindow = new com.badlogic.gdx.scenes.scene2d.ui.Window("Backpack", skin);
        backpackWindow.setSize(300, 400); // Ukuran jendela
        backpackWindow.setPosition(Gdx.graphics.getWidth() / 2 - backpackWindow.getWidth() / 2, Gdx.graphics.getHeight() / 2 - backpackWindow.getHeight() / 2);
        backpackWindow.setVisible(false); // Sembunyikan secara default
        backpackWindow.setModal(true); // Membuatnya modal agar input lain terblokir
        backpackWindow.setMovable(true); // Bisa dipindah
        stage.addActor(backpackWindow);

        inventoryTable = new com.badlogic.gdx.scenes.scene2d.ui.Table(skin);
        inventoryTable.setFillParent(true); // Mengisi seluruh jendela
        backpackWindow.add(inventoryTable).expand().fill(); // Tambahkan tabel ke jendela

        // Tambahkan tombol Close ke jendela backpack
        TextButton closeBackpackButton = new TextButton("Close", skin);
        closeBackpackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeBackpack();
            }
        });
        backpackWindow.row(); // Baris baru
        backpackWindow.add(closeBackpackButton).width(80).height(30).padBottom(10).center(); // Tambahkan tombol close

        // Gdx.input.setInputProcessor(new com.badlogic.gdx.InputMultiplexer(stage, new TileInputProcessor(this))); // Tetap seperti ini

        Gdx.input.setInputProcessor(new com.badlogic.gdx.InputMultiplexer(stage, new TileInputProcessor(this)));
    }

    // BARU: Metode untuk membuka backpack
    private void openBackpack() {
        isBackpackOpen = true;
        backpackWindow.setVisible(true);
        updateBackpackContent(); // Perbarui isi setiap kali dibuka
        // Blokir input lain saat backpack terbuka
        Gdx.input.setInputProcessor(stage);
    }

    // BARU: Metode untuk menutup backpack
    private void closeBackpack() {
        isBackpackOpen = false;
        backpackWindow.setVisible(false);
        // Kembalikan input processor ke multiplexer
        Gdx.input.setInputProcessor(new com.badlogic.gdx.InputMultiplexer(stage, new TileInputProcessor(this)));
    }

    // BARU: Metode untuk memperbarui isi backpack
    private void updateBackpackContent() {
        inventoryTable.clearChildren(); // Hapus item lama

        // Tambahkan label header
        inventoryTable.add("Your Items:").colspan(2).padBottom(10).row();

        if (player.getInventory().size == 0) {
            inventoryTable.add("Backpack is empty.").colspan(2).row();
        } else {
            for (final Item item : player.getInventory()) {
                inventoryTable.add(item.getName()).pad(5);

                // Tambahkan tombol "Use" atau "Equip"
                TextButton actionButton = new TextButton("Use/Equip", skin);
                actionButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        // --- BARU: Logika penggunaan item dari backpack ---
                        if (item instanceof HealthPotion) {
                            // Jika item adalah HealthPotion, panggil metode useHealthPotion() dari player
                            // Metode ini akan menangani penyembuhan DAN penghapusan dari inventaris
                            player.useHealthPotion();
                        } else if (item instanceof Weapon) {
                            // Jika item adalah Weapon, panggil interact() pada weapon tersebut
                            // Ini akan melengkapi senjata (tidak menghapus dari inventaris)
                            item.interact(player);
                        }
                        // Anda bisa menambahkan logika 'else if' untuk tipe Item lain di sini

                        updateBackpackContent(); // Perbarui tampilan backpack setelah item digunakan/dilengkapi
                        // Juga pastikan tampilan jumlah potion diperbarui (ini akan terjadi saat render berikutnya)
                    }
                });
                inventoryTable.add(actionButton).width(80).height(30).pad(5).row();
            }
        }
    }

    private void initializeNewBoard() {
        if (gameBoard != null) {
            gameBoard.dispose();
        }
        gameBoard = new GameBoard(gridRows, gridCols, tileSize, gridStartX, gridStartY, defaultTileTextureRegion);

        List<String> solutions = gameBoard.findAllValidWords();
        Collections.sort(solutions);
        Gdx.app.log("GameScreen", "Initial Board Solutions: " + solutions);
    }

    public Array<Tile> getSelectedTiles() {
        return selectedTiles;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public void handleTileClick(Tile clickedTile) {
        if (selectedTiles.size == 0) {
            selectedTiles.add(clickedTile);
            currentWord += clickedTile.getLetter();
            System.out.println("Selected: " + currentWord);
        } else {
            Tile lastTile = selectedTiles.peek();
            if (isAdjacent(lastTile, clickedTile) && !selectedTiles.contains(clickedTile, true)) {
                selectedTiles.add(clickedTile);
                currentWord += clickedTile.getLetter();
                System.out.println("Selected: " + currentWord);
            } else if (selectedTiles.contains(clickedTile, true)) {
                int index = selectedTiles.indexOf(clickedTile, true);
                if (index != selectedTiles.size - 1) {
                    for (int i = selectedTiles.size - 1; i > index; i--) {
                        selectedTiles.removeIndex(i);
                    }
                    currentWord = "";
                    for (Tile t : selectedTiles) {
                        currentWord += t.getLetter();
                    }
                    System.out.println("Deselected. Current: " + currentWord);
                } else {
                    selectedTiles.removeIndex(selectedTiles.size - 1);
                    if (selectedTiles.size > 0) {
                        currentWord = "";
                        for (Tile t : selectedTiles) {
                            currentWord += t.getLetter();
                        }
                    } else {
                        currentWord = "";
                    }
                    System.out.println("Backspace. Current: " + currentWord);
                }
            } else {
                System.out.println("Invalid selection. Resetting word and starting new selection.");
                resetWordSelection();
                selectedTiles.add(clickedTile);
                currentWord += clickedTile.getLetter();
            }
        }
    }

    private boolean isAdjacent(Tile tile1, Tile tile2) {
        int r1 = -1, c1 = -1, r2 = -1, c2 = -1;
        for (int r = 0; r < gameBoard.getGridRows(); r++) {
            for (int c = 0; c < gameBoard.getGridCols(); c++) {
                if (gameBoard.tileGrid[r][c] == tile1) {
                    r1 = r; c1 = c;
                }
                if (gameBoard.tileGrid[r][c] == tile2) {
                    r2 = r; c2 = c;
                }
            }
        }

        if (r1 == -1 || c1 == -1 || r2 == -1 || c2 == -1) return false;

        int dr = Math.abs(r1 - r2);
        int dc = Math.abs(c1 - c2);

        return (dr <= 1 && dc <= 1) && (dr != 0 || dc != 0);
    }

    public void processWord() {
        if (currentWord.length() < 3) {
            System.out.println("Word too short: " + currentWord + ". Minimum 3 letters required. Resetting selection.");
            resetWordSelection();
            return;
        }

        boolean isValidWord = WordDictionary.isValidWord(currentWord);

        if (isValidWord) {
            System.out.println("Valid word formed: " + currentWord);
            int wordValue = WordCalculator.calculateWordValue(selectedTiles);
            int totalDamageToEnemy = wordValue + player.getAttackPower(); // Menggunakan getAttackPower() Player

            System.out.println("Player attacks " + currentEnemy.getClass().getSimpleName() + " for " + totalDamageToEnemy + " damage.");
            currentEnemy.takeDamage(totalDamageToEnemy);

            for (Tile tile : selectedTiles) {
                for (TileEffect effect : tile.getActiveEffects()) {
                    effect.onWordUse(tile, currentEnemy);
                }
            }

            if (!currentEnemy.isAlive()) {
                player.addScore(currentEnemy.getGoldDrop());
                System.out.println("Enemy defeated! Spawning new enemy.");

                // BARU: Small chance to get HealthPotion
                if (MathUtils.random.nextFloat() < 1f) { // 30% chance
                    HealthPotion newPotion = new HealthPotion(); // BUAT OBJEK HEALTHPOTION NYATA
                    player.addHealthPotion(newPotion); // TAMBAHKAN KE INVENTORY PLAYER
                    System.out.println("You found a Health Potion!");
                }

                // Very small chance to get new weapon
                if (MathUtils.random.nextFloat() < 0.05f) { // 5% chance
                    Weapon droppedWeapon; // Deklarasikan variabel weapon
                    if (MathUtils.random.nextFloat() < 0.5f) {
                        droppedWeapon = new BasicSword();
                        System.out.println("You found a new weapon: Basic Sword!");
                    } else {
                        droppedWeapon = new MagicStaff();
                        System.out.println("You found a new weapon: Magic Staff!");
                    }
                    player.addItem(droppedWeapon); // <--- TAMBAHKAN KE INVENTORY, JANGAN LANGSUNG EQUIP
                }

                currentEnemy.dispose(); // Hapus musuh lama
                // Spawn musuh baru secara acak
                float enemySpawnChance = MathUtils.random.nextFloat();
                if (enemySpawnChance < 0.4f) { // 40% Goblin
                    currentEnemy = new Goblin("characters/goblin.png");
                } else if (enemySpawnChance < 0.8f) { // 40% Ogre
                    currentEnemy = new Ogre("characters/ogre.png");
                } else { // 20% Dragon (jika ada Dragon class)
                    // Asumsi ada kelas Dragon atau musuh lain yang lebih kuat
                    currentEnemy = new Ogre("characters/dragon.png"); // Menggunakan Ogre sebagai placeholder
                }
                System.out.println("New enemy spawned: " + currentEnemy.getClass().getSimpleName());

            } else {
                // BARU: Musuh menyerang balik jika masih hidup
                int enemyDamage = currentEnemy.getAttackPower(); // Asumsi Enemy memiliki getAttackPower()
                player.takeDamage(enemyDamage);
                System.out.println(currentEnemy.getClass().getSimpleName() + " attacks player for " + enemyDamage + " damage.");

                // BARU: Cek Game Over
                if (!player.isAlive()) {
                    System.out.println("Game Over! You were defeated by " + currentEnemy.getClass().getSimpleName() + ".");
                    game.setScreen(new GameOverScreen(game, player.getScore())); // Transisi ke GameOverScreen
                    dispose(); // Buang GameScreen saat ini
                    return; // Hentikan eksekusi metode ini
                }
            }

            gameBoard.replaceUsedTiles(selectedTiles);
            // --- Cek apakah ada kata yang tersisa setelah tile diganti ---
            List<String> solutions = gameBoard.findAllValidWords();
            if (solutions.isEmpty()) {
                Gdx.app.log("GameScreen", "No valid words left on the board! Resetting board...");
                initializeNewBoard(); // Panggil ulang untuk membuat papan baru
            } else {
                Collections.sort(solutions);
                Gdx.app.log("GameScreen", "New Board Solutions after submit: " + solutions);
            }
            // --- Akhir Pembaruan ---

        } else {
            System.out.println("Invalid word: " + currentWord + ". Try again!");
        }
        resetWordSelection();
    }

    private void resetWordSelection() {
        selectedTiles.clear();
        currentWord = "";
        System.out.println("Word selection reset.");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        for (int r = 0; r < gameBoard.getGridRows(); r++) {
            for (int c = 0; c < gameBoard.getGridCols(); c++) {
                Tile tile = gameBoard.tileGrid[r][c];
                if (tile != null) {
                    tile.render(batch, tile.x, tile.y);

                    String letterStr = String.valueOf(tile.getLetter());
                    glyphLayout.setText(game.font, letterStr);
                    float textWidth = glyphLayout.width;
                    float textHeight = glyphLayout.height;
                    game.font.draw(batch, letterStr, tile.x + (tile.width - textWidth) / 2, tile.y + (tile.height + textHeight) / 2);

                    if (selectedTiles.contains(tile, true)) {
                        batch.setColor(Color.YELLOW.cpy().mul(0.7f));
                        if (game.getTileHighlightTexture() != null) {
                            batch.draw(game.getTileHighlightTexture(), tile.x, tile.y, tile.width, tile.height);
                        }
                        batch.setColor(Color.WHITE);
                    }
                }
            }
        }

        player.render(batch, 50, 50);
        game.font.draw(batch, "Player HP: " + player.getHealth() + "/" + player.getMaxHealth(), 50, 150);
        game.font.draw(batch, "Player Score: " + player.getScore(), 50, 130);
        game.font.draw(batch, "Equipped: " + player.getEquippedWeapon().getName(), 50, 110);

        // BARU: Tampilkan jumlah Health Potion
        String potionText = "Potions: " + player.getHealthPotions();
        potionCountLayout.setText(game.font, potionText);
        // Posisikan teks di bawah tombol "Use Potion"
        game.font.draw(batch, potionText, usePotionButton.getX() + (usePotionButton.getWidth() - potionCountLayout.width) / 2, usePotionButton.getY() - potionCountLayout.height - 5);
        // AKHIR BARU

        if (currentEnemy.isAlive()) {
            currentEnemy.render(batch, 600, 400);
            game.font.draw(batch, currentEnemy.getClass().getSimpleName() + " HP: " + currentEnemy.getHealth() + "/" + currentEnemy.getMaxHealth(), 600, 450);
        } else {
            game.font.draw(batch, "Enemy Defeated!", 600, 450);
        }

        game.font.draw(batch, "Current Word: " + currentWord, 10, Gdx.graphics.getHeight() - 20);

        batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        player.updateEffects(delta);
        if (currentEnemy != null && currentEnemy.isAlive()) { // Tambahkan null check untuk currentEnemy
            currentEnemy.updateEffects(delta);
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        if (player != null) { // Tambahkan null check
            player.dispose();
        }
        if (currentEnemy != null) {
            currentEnemy.dispose();
        }
        if (gameBoard != null) {
            gameBoard.dispose();
        }
        if (stage != null) { // Tambahkan null check
            stage.dispose();
        }
        if (skin != null) { // Tambahkan null check
            skin.dispose();
        }
    }
}

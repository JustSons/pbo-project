package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.g2d.BitmapFont; // BARU: Import BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator; // BARU: Import FreeTypeFontGenerator

// UI Imports
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;

// Import kelas-kelas OOP Anda
import io.github.some_example_name.entities.GameEntity;
import io.github.some_example_name.entities.Player;
import io.github.some_example_name.entities.enemies.*;
import io.github.some_example_name.items.weapons.*;
import io.github.some_example_name.tiles.Tile;
import io.github.some_example_name.effects.tile.TileEffect;
import io.github.some_example_name.effects.tile.BonusDamageEffect;
import io.github.some_example_name.utils.WordCalculator;
import io.github.some_example_name.utils.WordDictionary;
import io.github.some_example_name.utils.GameBoard;
import io.github.some_example_name.items.potions.HealthPotion; // BARU: Import HealthPotion
import io.github.some_example_name.items.Item; // BARU: Import kelas Item
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane; // BARU: Import ScrollPane
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
    private ScrollPane inventoryScrollPane;
    private BitmapFont tileFont;

    private float enemyX;
    private float enemyY;

    private Texture backgroundImage;
    private Music backgroundMusic;
    private Sound playerHitSound; // BARU: Sound effect ketika player terkena hit
    private Sound playerDeathSound;
    private Sound playerAttackSound;

    private Sound enemyHitSound;
    private Sound enemyDeathSound;
    private Sound enemyAttackSound;

    private float progress = 0f;

    private enum BattleState {
        PLAYER_INPUT,           // Menunggu input kata dari player
        PLAYER_ATTACK_ANIMATION, // Animasi serangan player sedang berlangsung
        ENEMY_HIT_ANIMATION,    // Animasi musuh terkena serangan sedang berlangsung
        ENEMY_TURN_ATTACK_ANIMATION, // Animasi serangan musuh sedang berlangsung
        PLAYER_HIT_ANIMATION,   // Animasi player terkena serangan sedang berlangsung
        ENEMY_DYING_ANIMATION, // BARU: State untuk animasi kematian musuh
        PLAYER_DYING_ANIMATION, // BARU: State untuk animasi kematian player
        CHECK_ROUND_END,        // Mengecek apakah ada yang mati, ganti musuh, dll.
        GAME_OVER_SCREEN        // Game berakhir
    }
    private BattleState currentBattleState;

    public GameScreen(final BookwormGame game) {
        this.game = game;
        batch = game.batch;

        WordDictionary.loadDictionary();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        gridStartX = (1000 - (gridCols * tileSize)) / 2;
        gridStartY = (800 - (gridRows * tileSize)) / 2;

        player = new Player("characters/player/");
        player.equipWeapon(new BasicSword()); // Set initial weapon

        currentEnemy = new Goblin("characters/goblin/");
        enemyHitSound = Gdx.audio.newSound(Gdx.files.internal("characters/goblin/goblin_hit.mp3"));
        enemyAttackSound = Gdx.audio.newSound(Gdx.files.internal("characters/goblin/goblin_attack.mp3"));
        enemyDeathSound = Gdx.audio.newSound(Gdx.files.internal("characters/goblin/goblin_death.mp3"));

        defaultTileTextureRegion = game.getTileTextureRegion();

        enemyX = 629;
        enemyY = 450;

        backgroundImage = new Texture(Gdx.files.internal("background.jpg")); // Sesuaikan path ini
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background_music.mp3")); // Sesuaikan path ini
        backgroundMusic.setLooping(true); // Atur agar musik berulang
        backgroundMusic.setVolume(0.25f); // Atur volume (0.0 - 1.0)
        backgroundMusic.play();

        playerHitSound = Gdx.audio.newSound(Gdx.files.internal("characters/player/player_hit.wav")); // BARU: Muat sound effect
        playerDeathSound = Gdx.audio.newSound(Gdx.files.internal("characters/player/death.wav")); //
        playerAttackSound = Gdx.audio.newSound(Gdx.files.internal("characters/player/player_attack.mp3"));


        // --- BARU: Inisialisasi tileFont ---
        // Ganti "fonts/your_tile_font.ttf" dengan jalur font yang Anda inginkan
        // Anda bisa mencoba "fonts/pixel_font.ttf" yang sudah ada jika ingin font berbeda dari UI
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/tile_font.ttf")); // Sesuaikan path font
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32; // Ukuran font, sesuaikan agar pas di bucket
        parameter.color = Color.BLACK; // Warna huruf, sesuaikan agar terlihat di bucket (atau WHITE)
        // Opsional: tambahkan outline untuk font
        // parameter.borderColor = Color.WHITE;
        // parameter.borderWidth = 1;
        tileFont = generator.generateFont(parameter);
        generator.dispose(); // Generator harus di-dispose setelah membuat font
        // --- AKHIR BARU ---

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
//        inventoryTable.setFillParent(true); // Mengisi seluruh jendela
        // BARU: Buat ScrollPane dan bungkus inventoryTable di dalamnya
        inventoryScrollPane = new ScrollPane(inventoryTable, skin);
        inventoryScrollPane.setFadeScrollBars(false); // Agar scrollbar selalu terlihat
        inventoryScrollPane.setScrollingDisabled(true, false); // Hanya scroll vertikal

        // Tambahkan ScrollPane ke backpackWindow, bukan inventoryTable langsung
        backpackWindow.add(inventoryScrollPane).expand().fill().row(); // Penting: .row() setelahnya

        // Tambahkan tombol Close ke jendela backpack
        TextButton closeBackpackButton = new TextButton("Close", skin);
        closeBackpackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeBackpack();
            }
        });
        backpackWindow.add(closeBackpackButton).width(80).height(30).padBottom(10).center(); // Tambahkan tombol close

        // Gdx.input.setInputProcessor(new com.badlogic.gdx.InputMultiplexer(stage, new TileInputProcessor(this))); // Tetap seperti ini

        currentBattleState = BattleState.PLAYER_INPUT;

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

//        List<String> solutions = gameBoard.findAllValidWords();
//        Collections.sort(solutions);
//        Gdx.app.log("GameScreen", "Initial Board Solutions: " + solutions);
    }

    public Array<Tile> getSelectedTiles() {
        return selectedTiles;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public void handleTileClick(Tile clickedTile) {
        if (currentBattleState != BattleState.PLAYER_INPUT) {
            // Jangan izinkan pemilihan tile jika bukan dalam state PLAYER_INPUT
            return;
        }
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
             // Reset seleksi setelah kata valid

            // Mulai sequence serangan player
            currentBattleState = BattleState.PLAYER_ATTACK_ANIMATION;
            player.setState(GameEntity.CharacterState.ATTACKING);
            currentEnemy.setState(GameEntity.CharacterState.IDLE); // Pastikan musuh dalam keadaan idle
            // stateTimer = 0f; // stateTime diupdate otomatis di GameEntity.update()
            playerAttackSound.play(0.25f);
        } else {
            System.out.println("Invalid word: " + currentWord + ". Try again!");
            resetWordSelection();
        }
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

        batch.draw(backgroundImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        for (int r = 0; r < gameBoard.getGridRows(); r++) {
            for (int c = 0; c < gameBoard.getGridCols(); c++) {
                Tile tile = gameBoard.tileGrid[r][c];
                if (tile != null) {
                    tile.render(batch, tile.x, tile.y);

                    String letterStr = String.valueOf(tile.getLetter());
                    glyphLayout.setText(tileFont, letterStr); // MODIFIKASI: Gunakan tileFont di sini
                    float textWidth = glyphLayout.width;
                    float textHeight = glyphLayout.height;
                    // MODIFIKASI: Gunakan tileFont untuk menggambar huruf
                    tileFont.draw(batch, letterStr, tile.x + (tile.width - textWidth) / 2, tile.y + (tile.height + textHeight) / 2);

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

        player.render(batch, -160, -80);
        game.font.draw(batch, "Player HP: " + player.getHealth() + "/" + player.getMaxHealth(), 20, 80);
        game.font.draw(batch, "Player Score: " + player.getScore(), 20, 60);
        game.font.draw(batch, "Equipped: " + player.getEquippedWeapon().getName(), 20, 40);

        // BARU: Tampilkan jumlah Health Potion
        String potionText = "Potions: " + player.getHealthPotions();
        potionCountLayout.setText(game.font, potionText);
        // Posisikan teks di bawah tombol "Use Potion"
        game.font.draw(batch, potionText, usePotionButton.getX() + (usePotionButton.getWidth() - potionCountLayout.width) / 2, usePotionButton.getY() - potionCountLayout.height - 5);
        // AKHIR BARU

        if (currentEnemy.getCurrentState() != GameEntity.CharacterState.DYING || !currentEnemy.getCurrentPlayingAnimation().isAnimationFinished(currentEnemy.getStateTime())) {
            currentEnemy.render(batch, enemyX, enemyY);
            game.font.draw(batch, currentEnemy.getClass().getSimpleName() + " HP: " + currentEnemy.getHealth() + "/" + currentEnemy.getMaxHealth(), 789,595);
        } else {
            // Opsional: Tampilkan "Enemy Defeated!" atau hapus dari layar sepenuhnya
            game.font.draw(batch, "Enemy Defeated!", enemyX, enemyY + 110);
        }

        // --- POSISI "Current Word:" (KIRI ATAS) ---
//        String currentWordText = "Current Word: " + currentWord;
//        glyphLayout.setText(game.font, currentWordText);
//        float currentWordTextX = 10;
//        float currentWordTextY = Gdx.graphics.getHeight() - 20;
//
//        game.font.draw(batch, currentWordText, currentWordTextX, currentWordTextY);
        // --- AKHIR POSISI "Current Word:" ---

        // --- POSISI "Current Word:" (Tengah) ---
        String currentWordText = "Current Word: " + currentWord;
        glyphLayout.setText(game.font, currentWordText);
        float currentWordTextWidth = glyphLayout.width;
        float currentWordTextX = (Gdx.graphics.getWidth() - currentWordTextWidth) / 2;
        float currentWordTextY = gridStartY + gridRows * tileSize + 20 + usePotionButton.getHeight() + 10 + 20;

        game.font.draw(batch, currentWordText, currentWordTextX, currentWordTextY);
        batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        player.update(Gdx.graphics.getDeltaTime());
        if (currentEnemy != null) { // Tambahkan null check
            currentEnemy.update(Gdx.graphics.getDeltaTime());
        }

        switch (currentBattleState) {
            case PLAYER_INPUT:
                break;

            case PLAYER_ATTACK_ANIMATION:
                if (player.getCurrentState() == GameEntity.CharacterState.IDLE) {
                    int wordValue = WordCalculator.calculateWordValue(selectedTiles);
                    int totalDamageToEnemy = wordValue + player.getAttackPower();
                    currentEnemy.takeDamage(totalDamageToEnemy);
                    resetWordSelection();
                    System.out.println(wordValue);
                    System.out.println(totalDamageToEnemy);
                    System.out.println("Player attacks " + currentEnemy.getClass().getSimpleName() + " for " + totalDamageToEnemy + " damage.");

                    for (Tile tile : selectedTiles) {
                        for (TileEffect effect : tile.getActiveEffects()) {
                            effect.onWordUse(tile, currentEnemy);
                        }
                    }

                    initializeNewBoard();

                    if (!currentEnemy.isAlive() && currentEnemy.getHealth() <=0) { // Cek apakah musuh mati setelah serangan
                        currentEnemy.setState(GameEntity.CharacterState.DYING);
                        currentBattleState = BattleState.ENEMY_DYING_ANIMATION;
                        enemyDeathSound.play(0.25f);
                    } else {
                        currentEnemy.setState(GameEntity.CharacterState.HIT);
                        currentBattleState = BattleState.ENEMY_HIT_ANIMATION;
                        enemyHitSound.play(0.25f);
                    }
                }
                break;

            case ENEMY_HIT_ANIMATION:
                if (currentEnemy.getCurrentState() == GameEntity.CharacterState.IDLE) {
                    currentBattleState = BattleState.CHECK_ROUND_END;
                }
                break;

            case CHECK_ROUND_END:
                if (!currentEnemy.isAlive()) { // Cek apakah musuh mati (bukan hanya health <= 0)
                    currentEnemy.setState(GameEntity.CharacterState.DYING); // Pastikan state DYING
                    currentBattleState = BattleState.ENEMY_DYING_ANIMATION;
                } else if (!player.isAlive()) { // Player mati
                    player.setState(GameEntity.CharacterState.DYING); // Pastikan state DYING
                    currentBattleState = BattleState.PLAYER_DYING_ANIMATION;
                } else {
                    // Jika musuh tidak mati, musuh menyerang balik
                    currentBattleState = BattleState.ENEMY_TURN_ATTACK_ANIMATION;
                    currentEnemy.setState(GameEntity.CharacterState.ATTACKING);
                    if (currentEnemy instanceof Wizard){
                        enemyAttackSound.play(0.5f); // sfx wizard attack agak kecil
                    } else {
                        enemyAttackSound.play(0.255f);
                    }
                    player.setState(GameEntity.CharacterState.IDLE);
                }
                break;

            case ENEMY_TURN_ATTACK_ANIMATION:
                if (currentEnemy.getCurrentState() == GameEntity.CharacterState.IDLE) {
                    int enemyDamage = currentEnemy.getAttackPower();
                    player.takeDamage(enemyDamage);
                    System.out.println(currentEnemy.getClass().getSimpleName() + " attacks player for " + enemyDamage + " damage.");

                    if (!player.isAlive() && player.getHealth() <=0) { // Cek apakah player mati setelah serangan musuh
                        player.setState(GameEntity.CharacterState.DYING);
                        playerDeathSound.play(0.25f);
                        currentBattleState = BattleState.PLAYER_DYING_ANIMATION;
                    } else {
                        player.setState(GameEntity.CharacterState.HIT);
                        playerHitSound.play(0.25f);
                        currentBattleState = BattleState.PLAYER_HIT_ANIMATION;
                    }
                }
                break;

            case PLAYER_HIT_ANIMATION:
                if (player.getCurrentState() == GameEntity.CharacterState.IDLE) {
                    currentBattleState = BattleState.PLAYER_INPUT;
                }
                break;

            case ENEMY_DYING_ANIMATION: // BARU: Logika untuk animasi kematian musuh
                // currentEnemy.isAlive() di GameEntity sekarang return true juga untuk DYING
                // Jadi kita cek state-nya DAN apakah animasinya sudah selesai
                if (currentEnemy.getCurrentState() == GameEntity.CharacterState.DYING &&
                    currentEnemy.getCurrentPlayingAnimation().isAnimationFinished(currentEnemy.getStateTime())) {
                    boolean isWizardDefeated = (currentEnemy instanceof Wizard);
                    // Animasi kematian musuh selesai, lanjutkan dengan reward dan spawn musuh baru
                    player.addScore(currentEnemy.getGoldDrop());
                    System.out.println("Enemy defeated! Spawning new enemy.");
                    progress += 0.05f;

                    // Drop item (HealthPotion atau Weapon)
                    if (MathUtils.random.nextFloat() < 0.4f) {
                        HealthPotion newPotion = new HealthPotion();
                        player.addHealthPotion(newPotion);
                        System.out.println("You found a Health Potion!");
                    }
                    if (MathUtils.random.nextFloat() < 0.15f) {
                        Weapon droppedWeapon;
                        float weaponchance = MathUtils.random.nextFloat();
                        if (weaponchance < 0.01f) {
                            droppedWeapon = new MagicStaff();
                            System.out.println("You found a new weapon: Mr. Alby's Chosen One!");
                        } else if(weaponchance < 0.1f){
                            droppedWeapon = new LegendarySword();
                            System.out.println("You found a new weapon: Legendary Sword!");
                        } else if (weaponchance < 0.3f) {
                            droppedWeapon = new EpicSword();
                            System.out.println("You found a new weapon: Epic Sword!");
                        }else if (weaponchance < 0.5f) {
                            droppedWeapon = new RareSword();
                            System.out.println("You found a new weapon: Rare Sword!");
                        }else {
                            droppedWeapon = new CommonSword();
                            System.out.println("You found a new weapon: Common Sword!");
                        }
                        player.addItem(droppedWeapon);
                    }

                    currentEnemy.dispose(); // Hapus musuh lama sepenuhnya
                    if (enemyAttackSound != null) enemyAttackSound.dispose();
                    if (enemyDeathSound != null) enemyDeathSound.dispose();
                    if (enemyHitSound != null) enemyHitSound.dispose();

                    // --- Logika SPESIFIK untuk Kemenangan Wizard atau Spawn Musuh Biasa ---
                    if (isWizardDefeated) {
                        System.out.println("Wizard defeated! You win!");
                        // Hentikan musik latar belakang karena game selesai
                        if (backgroundMusic != null) {
                            backgroundMusic.stop();
                        }
                        // Transisi ke layar kemenangan
                        game.setScreen(new WinScreen(game, player.getScore()));
                        dispose(); // Dispose GameScreen ini karena sudah selesai
                    }else {
                        float enemySpawnChance = MathUtils.random.nextFloat();
                        if (progress >= 1f) {
                            currentEnemy = new Wizard("characters/wizard/");
                            progress = 0f;
                        } else if (enemySpawnChance < 0.4f) {
                            currentEnemy = new Goblin("characters/goblin/");
                        } else if (enemySpawnChance < 0.8f) {
                            currentEnemy = new Ogre("characters/ogre/");
                        } else if (enemySpawnChance < 0.98f) {
                            currentEnemy = new Dragon("characters/dragon/");
                        } else {
                            currentEnemy = new Wizard("characters/wizard/");
                        }
                        System.out.println("New enemy spawned: " + currentEnemy.getClass().getSimpleName());
                        currentEnemy.setState(GameEntity.CharacterState.IDLE);
                        // tiap enemy sfxnya blm tentu sama
                        if (currentEnemy instanceof Goblin) {
                            enemyHitSound = Gdx.audio.newSound(Gdx.files.internal("characters/goblin/goblin_hit.mp3"));
                            enemyAttackSound = Gdx.audio.newSound(Gdx.files.internal("characters/goblin/goblin_attack.mp3"));
                            enemyDeathSound = Gdx.audio.newSound(Gdx.files.internal("characters/goblin/goblin_death.mp3"));
                        } else if (currentEnemy instanceof Ogre) {
                            enemyHitSound = Gdx.audio.newSound(Gdx.files.internal("characters/ogre/ogre_hit.mp3"));
                            enemyAttackSound = Gdx.audio.newSound(Gdx.files.internal("characters/ogre/ogre_attack.mp3"));
                            enemyDeathSound = Gdx.audio.newSound(Gdx.files.internal("characters/goblin/goblin_death.mp3"));
                        } else if (currentEnemy instanceof Dragon) {
                            enemyHitSound = Gdx.audio.newSound(Gdx.files.internal("characters/dragon/dragon_hit.mp3"));
                            enemyAttackSound = Gdx.audio.newSound(Gdx.files.internal("characters/dragon/dragon_attack.mp3"));
                            enemyDeathSound = Gdx.audio.newSound(Gdx.files.internal("characters/goblin/goblin_death.mp3"));
                        } else if (currentEnemy instanceof Wizard) {
                            enemyHitSound = Gdx.audio.newSound(Gdx.files.internal("characters/wizard/wizard_hit.mp3"));
                            enemyAttackSound = Gdx.audio.newSound(Gdx.files.internal("characters/wizard/wizard_attack.mp3"));
                            enemyDeathSound = Gdx.audio.newSound(Gdx.files.internal("characters/wizard/wizard_death.mp3"));
                        }

//                    gameBoard.replaceUsedTiles(selectedTiles);
//                    List<String> solutions = gameBoard.findAllValidWords();
//                    if (solutions.isEmpty()) {
//                        Gdx.app.log("GameScreen", "No valid words left on the board! Resetting board...");
//                        initializeNewBoard();
//                    } else {
//                        Collections.sort(solutions);
//                        Gdx.app.log("GameScreen", "New Board Solutions after submit: " + solutions);
//                    }

                        currentBattleState = BattleState.PLAYER_INPUT; // Kembali ke input player
                        player.setState(GameEntity.CharacterState.IDLE);
                    }
                }
                break;

            case PLAYER_DYING_ANIMATION: // BARU: Logika untuk animasi kematian player
                if (player.getCurrentState() == GameEntity.CharacterState.DYING &&
                    player.getCurrentPlayingAnimation().isAnimationFinished(player.getStateTime())) {
                    // Animasi kematian player selesai, game over
                    currentBattleState = BattleState.GAME_OVER_SCREEN;
                    game.setScreen(new GameOverScreen(game, player.getScore()));
                    dispose(); // Dispose GameScreen ini
                }
                break;

            case GAME_OVER_SCREEN:
                // Jangan lakukan apa-apa, GameScreen akan dispose dan transisi ke GameOverScreen
                break;
        }
        // --- AKHIR LOGIKA STATE PERTARUNGAN ---
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
//        if (skin != null) { // Tambahkan null check
//            skin.dispose();
//        }
        if (tileFont != null) {
            tileFont.dispose();
        }
        if (backgroundMusic != null){
            backgroundMusic.dispose();
        }
    }
}

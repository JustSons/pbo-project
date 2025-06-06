package io.github.some_example_name.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import io.github.some_example_name.interfaces.Attackable;
import io.github.some_example_name.interfaces.Renderable;
import io.github.some_example_name.effects.status.StatusEffect; // Import StatusEffect

public abstract class GameEntity implements Attackable, Renderable {
    public enum CharacterState {
        IDLE,
        ATTACKING,
        HIT,
        DYING // Opsional, jika ada animasi mati
    }

    protected int health;
    protected int maxHealth;
    protected int attackPower;
    // HAPUS: protected Texture texture; // Ini untuk gambar statis, akan diganti dengan animasi
    protected float x, y; // Posisi di layar

    protected Array<StatusEffect> activeEffects;

    // --- BARU: Untuk Animasi ---
    protected Animation<TextureRegion> idleAnimation;
    protected Animation<TextureRegion> attackAnimation;
    protected Animation<TextureRegion> hitAnimation;
    protected Animation<TextureRegion> dyingAnimation;
    protected float stateTime; // Waktu yang berlalu sejak animasi dimulai
    protected Texture spriteSheet; // Texture untuk sprite sheet (gambar asli)
    protected float displayWidth; // Lebar karakter saat digambar di layar
    protected float displayHeight; // Tinggi karakter saat digambar di layar
    // --- AKHIR BARU ---
    protected Animation<TextureRegion> currentPlayingAnimation; // Animasi yang sedang diputar
    protected Texture idleSpriteSheet; // Texture untuk sprite sheet idle
    protected Texture attackSpriteSheet; // Texture untuk sprite sheet attack
    protected Texture hitSpriteSheet; // Texture untuk sprite sheet hit
    protected CharacterState currentState;


    // MODIFIKASI: Konstruktor sekarang menerima parameter untuk animasi
    public GameEntity(int maxHealth, int attackPower,
                                              String idleSpriteSheetPath, int idleFrameCols, int idleFrameRows, float idleFrameDuration,
                                              float displayWidth, float displayHeight) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.attackPower = attackPower;
        this.activeEffects = new Array<>();

        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;

        // --- BARU: Inisialisasi idleAnimation ---
        idleSpriteSheet = new Texture(idleSpriteSheetPath);
        TextureRegion[][] tmpIdle = TextureRegion.split(idleSpriteSheet,
            idleSpriteSheet.getWidth() / idleFrameCols,
            idleSpriteSheet.getHeight() / idleFrameRows);
        Array<TextureRegion> idleFrames = new Array<TextureRegion>(TextureRegion.class);
        for (int i = 0; i < idleFrameRows; i++) {
            for (int j = 0; j < idleFrameCols; j++) {
                idleFrames.add(tmpIdle[i][j]);
            }
        }
        idleAnimation = new Animation<TextureRegion>(idleFrameDuration, idleFrames);
        // --- AKHIR BARU ---

        currentPlayingAnimation = idleAnimation; // Default ke animasi idle
        currentState = CharacterState.IDLE; // Default state
        stateTime = 0f;
    }

    // Implementasi dari Attackable
    @Override
    public void takeDamage(int amount) {
        this.health -= amount;
        if (this.health < 0) {
            this.health = 0;
        }
        System.out.println(this.getClass().getSimpleName() + " took " + amount + " damage. Health: " + this.health);
    }

    // --- BARU: Tambahkan metode heal() di sini ---
    public void heal(int amount) {
        this.health += amount;
        if (this.health > maxHealth) {
            this.health = maxHealth; // Jangan melebihi max health
        }
        System.out.println(this.getClass().getSimpleName() + " healed for " + amount + ". Health: " + this.health);
    }
    // --- AKHIR BARU ---

    public int getAttackPower() {
        return attackPower;
    }

    @Override
    public boolean isAlive() {
        return this.health > 0;
    }

    @Override
    public int getHealth() { return health; }
    @Override
    public int getMaxHealth() { return maxHealth; }


    // Metode abstrak yang harus diimplementasikan oleh sub-kelas
    public abstract void attack(GameEntity target);

    public void setAttackAnimation(String attackSpriteSheetPath, int attackFrameCols, int attackFrameRows, float attackFrameDuration) {
        if (this.attackSpriteSheet != null) this.attackSpriteSheet.dispose(); // Dispose jika sudah ada
        this.attackSpriteSheet = new Texture(attackSpriteSheetPath);
        TextureRegion[][] tmpAttack = TextureRegion.split(this.attackSpriteSheet,
            this.attackSpriteSheet.getWidth() / attackFrameCols,
            this.attackSpriteSheet.getHeight() / attackFrameRows);
        Array<TextureRegion> attackFrames = new Array<TextureRegion>(TextureRegion.class);
        for (int i = 0; i < attackFrameRows; i++) {
            for (int j = 0; j < attackFrameCols; j++) {
                attackFrames.add(tmpAttack[i][j]);
            }
        }
        attackAnimation = new Animation<TextureRegion>(attackFrameDuration, attackFrames);
    }

    public void setHitAnimation(String hitSpriteSheetPath, int hitFrameCols, int hitFrameRows, float hitFrameDuration) {
        if (this.hitSpriteSheet != null) this.hitSpriteSheet.dispose(); // Dispose jika sudah ada
        this.hitSpriteSheet = new Texture(hitSpriteSheetPath);
        TextureRegion[][] tmpHit = TextureRegion.split(this.hitSpriteSheet,
            this.hitSpriteSheet.getWidth() / hitFrameCols,
            this.hitSpriteSheet.getHeight() / hitFrameRows);
        Array<TextureRegion> hitFrames = new Array<TextureRegion>(TextureRegion.class);
        for (int i = 0; i < hitFrameRows; i++) {
            for (int j = 0; j < hitFrameCols; j++) {
                hitFrames.add(tmpHit[i][j]);
            }
        }
        hitAnimation = new Animation<TextureRegion>(hitFrameDuration, hitFrames);
    }

    // Implementasi dari Renderable
    public void setState(CharacterState newState) {
        if (this.currentState == newState) return; // Tidak perlu ganti jika state sama

        this.currentState = newState;
        this.stateTime = 0f; // Reset waktu animasi saat state berubah

        switch (newState) {
            case IDLE:
                currentPlayingAnimation = idleAnimation;
                break;
            case ATTACKING:
                if (attackAnimation != null) {
                    currentPlayingAnimation = attackAnimation;
                } else {
                    currentPlayingAnimation = idleAnimation; // Fallback jika tidak ada animasi attack
                }
                break;
            case HIT:
                if (hitAnimation != null) {
                    currentPlayingAnimation = hitAnimation;
                } else {
                    currentPlayingAnimation = idleAnimation; // Fallback jika tidak ada animasi hit
                }
                break;
            case DYING: // Jika Anda menambahkan animasi mati
                // currentPlayingAnimation = dyingAnimation;
                // stateTime = 0f; // Biasanya tidak looping
                // Jika tidak ada animasi dying, bisa kembali ke idle atau frame terakhir hit
                currentPlayingAnimation = idleAnimation; // Fallback
                break;
        }
    }

    public CharacterState getCurrentState() {
        return currentState;
    }
    // --- AKHIR BARU ---

    @Override
    public void render(SpriteBatch batch, float x, float y) {
        this.x = x;
        this.y = y;

        // currentPlayingAnimation tidak boleh null. Jika null, akan error.
        // Pastikan animasi idle selalu diinisialisasi di konstruktor.
        TextureRegion currentFrame = currentPlayingAnimation.getKeyFrame(stateTime, true); // true untuk looping (untuk idle)

        // Untuk animasi ATTACKING atau HIT, biasanya tidak looping.
        // GameScreen akan mendeteksi selesainya animasi ini.
        if (currentState == CharacterState.ATTACKING || currentState == CharacterState.HIT) {
            currentFrame = currentPlayingAnimation.getKeyFrame(stateTime, false); // false agar tidak looping
        }

        batch.draw(currentFrame, x, y, displayWidth, displayHeight);
    }


    public void update(float delta) {
        stateTime += delta; // Update waktu animasi

        // --- BARU: Otomatis kembali ke IDLE setelah animasi satu kali selesai ---
        if ((currentState == CharacterState.ATTACKING && currentPlayingAnimation.isAnimationFinished(stateTime)) ||
            (currentState == CharacterState.HIT && currentPlayingAnimation.isAnimationFinished(stateTime))) {
            setState(CharacterState.IDLE); // Kembali ke idle setelah animasi selesai
        }
        // --- AKHIR BARU ---

        updateEffects(delta);
    }

    public void updateEffects(float delta) {
        Array<StatusEffect> effectsToRemove = new Array<>();
        for (StatusEffect effect : activeEffects) {
            effect.update(this, delta);
            if (effect.isFinished()) {
                effectsToRemove.add(effect);
            }
        }
        for (StatusEffect effect : effectsToRemove) {
            effect.remove(this);
            activeEffects.removeValue(effect, true);
        }
    }

    public void addStatusEffect(StatusEffect effect) {
        activeEffects.add(effect);
        effect.apply(this);
    }

    @Override
    public void dispose() {
        if (idleSpriteSheet != null) idleSpriteSheet.dispose();
        if (attackSpriteSheet != null) attackSpriteSheet.dispose();
        if (hitSpriteSheet != null) hitSpriteSheet.dispose();
        // Dispose sprite sheets lainnya jika ada
    }
}

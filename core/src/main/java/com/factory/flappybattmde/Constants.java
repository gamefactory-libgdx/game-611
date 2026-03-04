package com.factory.flappybattmde;

public final class Constants {
    private Constants() {}

    public static final String GAME_TITLE = "FLAPPY BAT";

    // Virtual world dimensions (portrait)
    public static final float WORLD_WIDTH  = 480f;
    public static final float WORLD_HEIGHT = 800f;

    // Bat
    public static final float BAT_WIDTH   = 56f;
    public static final float BAT_HEIGHT  = 40f;
    public static final float BAT_START_X = 80f;
    public static final float BAT_START_Y = 400f;

    // Physics
    public static final float GRAVITY         = -1200f;  // pixels/s^2
    public static final float FLAP_VELOCITY   =  550f;   // pixels/s upward on tap

    // Pipes
    public static final float PIPE_WIDTH       = 70f;
    public static final float PIPE_GAP         = 200f;   // vertical gap between top/bottom pipe
    public static final float PIPE_SPEED       = 180f;   // horizontal scroll speed px/s
    public static final float PIPE_SPAWN_DIST  = 240f;   // horizontal distance between pipe pairs
    public static final float PIPE_GAP_MIN_Y   = 120f;   // min Y of gap bottom
    public static final float PIPE_GAP_MAX_Y   = 500f;   // max Y of gap bottom

    // Difficulty – increases pipe speed every N pipes passed
    public static final int   DIFF_PIPES_THRESHOLD = 5;
    public static final float PIPE_SPEED_STEP      = 20f;
    public static final float PIPE_SPEED_MAX       = 360f;

    // Score – 1 point per pipe pair passed
    public static final int SCORE_PER_PIPE = 1;

    // Asset paths
    public static final String BG_MENU = "backgrounds/bg_main.png";
    public static final String BG_GAME = "backgrounds/bg_game.png";

    // SharedPreferences
    public static final String PREFS_NAME      = "FlappyBatPrefs";
    public static final String PREF_HIGH_SCORE = "highScore";
    public static final String PREF_MUSIC_ON   = "musicEnabled";
    public static final String PREF_SFX_ON     = "sfxEnabled";
    public static final String PREF_LB_PREFIX  = "lb_";
    public static final int    LEADERBOARD_SIZE = 10;
}

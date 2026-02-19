package org.galaxy.snake.game.core;

/**
 * Classe responsável por armazenar todas as constantes do jogo.
 * Isso facilita a manutenção e evita "magic numbers" espalhados no código.
 * 
 * Esta classe é final e possui construtor privado para impedir instanciação,
 * seguindo o padrão de classe utilitária de constantes.
 */
public final class GameConstants {
    
    // Impede a instanciação da classe
    private GameConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    // <=== DIMENSÕES DA TELA ===>
    /** Largura total da tela do jogo em pixels */
    public static final int SCREEN_WIDTH = 1000;
    
    /** Altura total da tela do jogo em pixels */
    public static final int SCREEN_HEIGHT = 600;
    
    /** Largura do painel superior (HUD) em pixels */
    public static final int PANEL_WIDTH = 1000;
    
    /** Altura do painel superior (HUD) em pixels */
    public static final int PANEL_HEIGHT = 77;
    
    /** Dimensões da tela do lobby */
    public static final int LOBBY_WIDTH = 1000;
    public static final int LOBBY_HEIGHT = 500;

    // <=== DIMENSÕES DOS TILES ===>
    /** Tamanho base de cada tile do grid em pixels */
    public static final int TILE_SIZE = 15;
    
    /** Tamanho visual de cada segmento da cobra em pixels */
    public static final int SNAKE_TILE_SIZE = 10;
    
    /** Tamanho visual da nave (comida) em pixels */
    public static final int NAVE_TILE_SIZE = 35;
    
    // <=== TILES CALCULADOS ===>
    /** Número máximo de tiles no eixo X */
    public static final int MAX_TILES_X = SCREEN_WIDTH / TILE_SIZE;
    
    /** Número máximo de tiles no eixo Y */
    public static final int MAX_TILES_Y = SCREEN_HEIGHT / TILE_SIZE;

    // <=== VELOCIDADES E TIMERS ===>
    /** Velocidade normal do jogo (delay em ms entre movimentos) */
    public static final int NORMAL_SPEED = 85;
    
    /** Velocidade com boost ativo (delay em ms) */
    public static final int BOOST_SPEED = 10;
    
    /** Delay do timer de renderização (~60 FPS) */
    public static final int TIMER_PANEL_DELAY = 16;
    
    /** Delay normal do timer de movimento da cobra */
    public static final int TIMER_SNAKE_NORMAL = 50;
    
    /** Delay do timer com boost ativo */
    public static final int TIMER_SNAKE_BOOST = 10;
    
    /** Duração do boost em milissegundos */
    public static final int BOOST_DURATION = 800;

    // <=== LIMITES DO JOGO ===>
    /** Posição Y mínima (em pixels) - limite superior jogável */
    public static final int MIN_Y_POSITION = 75;
    
    /** Posição Y máxima (em pixels) - limite inferior jogável */
    public static final int MAX_Y_POSITION = 590;
    
    /** Tile Y mínimo para spawn da nave (evita painel superior) */
    public static final int NAVE_MIN_Y_SPAWN = 20;
    
    /** Tile Y mínimo calculado para a cobra */
    public static final int MIN_Y_TILE = MIN_Y_POSITION / TILE_SIZE;

    // <=== POSIÇÃO INICIAL DA COBRA ===>
    /** Posição X inicial da cabeça da cobra (em tiles) */
    public static final int SNAKE_START_X = 10;
    
    /** Posição Y inicial da cabeça da cobra (em tiles) */
    public static final int SNAKE_START_Y = 10;
    
    // <=== POSIÇÃO INICIAL DA NAVE ===>
    /** Posição X inicial da nave (em tiles) */
    public static final int NAVE_START_X = 30;
    
    /** Posição Y inicial da nave (em tiles) */
    public static final int NAVE_START_Y = 25;

    // <=== PONTUAÇÃO PARA MUDANÇA DE BACKGROUND ===>
    /** Pontuação para mudar para o nível 2 */
    public static final int SCORE_LEVEL_1 = 10;
    
    /** Pontuação para mudar para o nível 3 */
    public static final int SCORE_LEVEL_2 = 25;
    
    /** Pontuação para mudar para o nível 4 */
    public static final int SCORE_LEVEL_3 = 40;

    // <=== PATHS DE RECURSOS - SONS ===>
    /** Caminho da música de fundo */
    public static final String SOUND_BACKGROUND = "/sounds/wav/background_music.wav";
    
    /** Caminho do som de explosão */
    public static final String SOUND_EXPLOSION = "/sounds/wav/explosion_sound.wav";
    
    /** Caminho do som de boost */
    public static final String SOUND_BOOST = "/sounds/wav/boost_sound.wav";

    // <=== PATHS DE RECURSOS - IMAGENS ===>
    /** Caminho da imagem da nave */
    public static final String IMAGE_NAVE = "/images/nave's/spaceships/Spaceship_3.png";
    
    /** Caminho do ícone do jogo */
    public static final String IMAGE_ICON = "/images/icons/snakeIcon_1.png";
    
    /** Caminho do background nível 1 */
    public static final String IMAGE_BG_1 = "/images/backgrounds/Background_2.png";
    
    /** Caminho do background nível 2 */
    public static final String IMAGE_BG_2 = "/images/backgrounds/Background_4.png";
    
    /** Caminho do background nível 3 */
    public static final String IMAGE_BG_3 = "/images/backgrounds/Background_5.png";
    
    /** Caminho do background nível 4 / lobby */
    public static final String IMAGE_BG_4 = "/images/backgrounds/Background_7.png";
    
    /** Caminho do efeito visual do boost */
    public static final String IMAGE_BOOST = "/images/boost/boost_1.gif";
    
    /** Caminho do ícone de pause */
    public static final String IMAGE_PAUSE = "/images/icons/pauseIcon_1.png";

    // <=== PATH DE FONTE ===>
    /** Caminho da fonte customizada */
    public static final String FONT_PATH = "/fonts/SuperLegendBoy/SuperLegendBoy.ttf";

    // <=== MENSAGENS ===>
    /** Título da janela do jogo */
    public static final String GAME_TITLE = "GALAXY SNAKE GAME";
    
    /** Título da janela do lobby */
    public static final String LOBBY_TITLE = "LOBBY SNAKE GAME";
    
    /** Mensagem exibida no game over */
    public static final String GAME_OVER_MESSAGE = "YOU CAN TRY AGAIN!!! WOULD YOU LIKE TO??";
    
    /** Título do diálogo de game over */
    public static final String GAME_OVER_TITLE = "YOU LOSE";
}
package org.galaxy.snake.game.ui;

import org.galaxy.snake.game.core.GameConstants;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.*;

/**
 * Classe singleton responsável por carregar e gerenciar todos os recursos visuais.
 * Imagens e fontes são carregadas uma única vez e reutilizadas.
 * 
 * Recursos gerenciados:
 * -Imagens do background (cacheadas por nível).
 * -Imagem da nave.
 * -Ícones.
 * -Fontes customizadas.
 */

public class Assets 
{

    //<=== SINGLETON PATTERN ===>
    private static volatile Assets instance;
    private static final Object LOCK = new Object();
    
    //<=== ENUM PARA BACKGROUNDS ===>
    public enum BackgroundLevel{
        LEVEL_1(GameConstants.IMAGE_BG_1),
        LEVEL_2(GameConstants.IMAGE_BG_2),
        LEVEL_3(GameConstants.IMAGE_BG_3),
        LEVEL_4(GameConstants.IMAGE_BG_4);

        private final String path;

        BackgroundLevel(String path){
            this.path = path;
        }

        public String getPath(){
            return path;
        }
    }

    //<=== IMAGENS DO JOGO ===>
    private Image naveImageGame;
    private Image currentBackgroundGame;
    final private Image pauseIcon;
    final private Image iconImageGame;
    final private Image snakeBoostGame;
    final private Image backgroundImageLobby;

    //<=== CACHE DE BACKGROUND ===>
    private final Map<BackgroundLevel, Image> backgroundCache;
    private BackgroundLevel currentLevel;

    //<=== FONTES ===>
    private Font font1;
    private Font font2;

    //<=== PLACEHOLDER PARA IMEGENS FALTANTES ===>
    private static final int PLACEHOLDER_SIZE = 50;

    /**
     * Construtor privado para implementar Singleton.
     * Carrega todos os recursos visuais.
     */
    private Assets()
    {
        //Inicializa o cache de backgrounds.
        backgroundCache = new EnumMap<>(BackgroundLevel.class);

        //Carrega imagens
        naveImageGame = loadImage(GameConstants.IMAGE_NAVE);
        iconImageGame = loadImage(GameConstants.IMAGE_ICON);
        snakeBoostGame = loadImage(GameConstants.IMAGE_BOOST);
        pauseIcon = loadImage(GameConstants.IMAGE_PAUSE);
        backgroundImageLobby = loadImage(GameConstants.IMAGE_BG_4);

        //Pré-carrega todos os backgrounds no cache.
        preloadBackgrounds();

        //Define background inicial.
        currentLevel = BackgroundLevel.LEVEL_1;
        currentBackgroundGame = backgroundCache.get(currentLevel);

        //Carrega fontes.
        loadFonts();
    }  

    /**
     * Obtém a instância única do gerenciador de assets.
     * Implementação thread-safe.
     * 
     * @return Intância única da Assets.
     */
    public static Assets getInstance(){
        if(instance == null){
            synchronized(LOCK){
                if(instance == null){
                    instance = new Assets();
                }
            }
        }
        return instance;
    }

    /**
     * Pré-carrega todos os backgrounds no cache.
     * Isso evita carregar imagens durante o jogo.
    */
    private void preloadBackgrounds(){
        for(BackgroundLevel level : BackgroundLevel.values()){
            Image bg = loadImage(level.getPath());
            backgroundCache.put(level, bg);
        }
    }

    /**
     * Carrega uma imagem a partir do caminho especificado.
     * Usa getResource para funcionar tanto em desenvolvimento quanto em JAR.
     * 
     * @param path Caminho da imagem (relativo ao classpath).
     * @return Image carregada ou null se houver erro.
     */
    private Image loadImage(String path){
        try{
            java.net.URL url = getClass().getResource(path);
            if (url == null) {
                System.err.println("Imagem não encontrada: " + path);
                return createPlaceholder();
            }
            return new ImageIcon(url).getImage();
        }catch(Exception e){
            System.err.println("Erro ao carregar a imagem " + path + e.getMessage());
            return createPlaceholder();
        }
    }

    /**
     * Cria uma imagem placeholder para quando a imagem real não é encontrada.
     * Isso evita NullPointerExceptions na renderização.
     * 
     * @return Imagem placeholder (quadrado magenta com X).
     */
    private Image createPlaceholder(){
        //Cria um BufferedImage para desenhar o placeholder.
        java.awt.image.BufferedImage placeholder = new java.awt.image.BufferedImage(
            PLACEHOLDER_SIZE, PLACEHOLDER_SIZE,
            java.awt.image.BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = placeholder.createGraphics();

        //Fundo magenta (cor padrão para "missing texture").
        g.setColor(Color.MAGENTA);
        g.fillRect(0, 0, PLACEHOLDER_SIZE, PLACEHOLDER_SIZE);

        //X preto para indicar erro.
        g.setColor(Color.BLACK);
        g.drawLine(0, 0, PLACEHOLDER_SIZE, PLACEHOLDER_SIZE);
        g.drawLine(PLACEHOLDER_SIZE, 0, 0, PLACEHOLDER_SIZE);

        //Borda preta.
        g.drawRect(0, 0, PLACEHOLDER_SIZE - 1, PLACEHOLDER_SIZE - 1);

        g.dispose();

        return placeholder;
    }

    /**
     * Carrega as fontes customizadas do jogo.
     * Se falhar, usa as fontes definidas como padrão.
     */
    private void loadFonts(){
        try(InputStream fontStream = getClass().getResourceAsStream(GameConstants.FONT_PATH))
        {
            if(fontStream == null){
                System.err.println("Arquivo de fonte não encontrado: " + GameConstants.FONT_PATH);
                useFallBackFonts();
                return;
            }

            //Cria a fonte a partir do stream.
            Font baseFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);

            //Registra a fonte no ambiente gráfico (opcional, mas útil).
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(baseFont);

            // Cria versões com tamanhos diferentes.
            font1 = baseFont.deriveFont(26f);
            font2 = baseFont.deriveFont(18f);

        }catch(IOException | FontFormatException e){
            System.err.println("Erro ao carregar fonte customizada: " + e.getMessage());
            useFallBackFonts();
        }
    }

    /**
     * Define fontes padrões quando a fonte customizada não pode ser carregada.
     */
    private void useFallBackFonts(){
        font1 = new Font("Arial", Font.BOLD, 26);
        font2 = new Font("Arial", Font.BOLD, 18);
    }

    /**
     * Troca o background baseado na pontuação.
     * 
     * @param score Pontuação do jogador.
     */
    public void updateBackgroundByScore(int score){
        BackgroundLevel newLevel;

        if (score < GameConstants.SCORE_LEVEL_1) {
            newLevel = BackgroundLevel.LEVEL_1;
        }else if (score < GameConstants.SCORE_LEVEL_2) {
            newLevel = BackgroundLevel.LEVEL_2;
        }else if (score < GameConstants.SCORE_LEVEL_3) {
            newLevel = BackgroundLevel.LEVEL_3;
        }else{
            newLevel = BackgroundLevel.LEVEL_4;
        }

        if(newLevel != currentLevel){
            currentLevel = newLevel;
            currentBackgroundGame = backgroundCache.get(currentLevel);
        }
    }

    /**
     * Reseta o background para o nível inicial.
     */
    public void resetBackground(){
        currentLevel = BackgroundLevel.LEVEL_1;
        currentBackgroundGame = backgroundCache.get(currentLevel);
    }

    /**
     * Libera todos os recursos de imagem.
     * 
     * Deve ser chamado quando o jogo é encerrado para liberar a memória ocupada pelas imagens.
     * 
     * Após chamar dispose():
     * -Todas as referências de imagens são invalidadas.
     * -A instância singleton é resetada.
     * -Uma nova chamada a getInstance() criará uma nova instância.
     */
    public void dispose(){
        //Flush libera os recursos nativos das imagens.

        //Limpa a imagens da nave.
        if (naveImageGame != null) {
            naveImageGame.flush();
            naveImageGame = null;
        }

        //Limpa ícone de pause.
        if (pauseIcon != null) {
            //Pause não pode ser null, pois é final, mas é possível a aplicar o FLUSHER a ele.
            pauseIcon.flush();
        }

        //Limpa imagem do boost.
        if (snakeBoostGame != null) {
            snakeBoostGame.flush();
        }

        //Limpa background do lobby.
        if(backgroundImageLobby != null){
            backgroundImageLobby.flush();
        }

        //Limpa os backgrouds em cache.
        for(Image bg : backgroundCache.values()){
            if (bg != null) {
                bg.flush();
            }
        }
        backgroundCache.clear();

        //Reseta o background atual.
        currentBackgroundGame = null;

        //Limpa a instância singleton para permitir recriação.
        synchronized(LOCK){
            instance = null;
        }

        System.out.println("Assets: Recursos liberados com sucesso!");
    }

    /**
     * Verifica se a instância foi inicializada.
     * 
     * @return true getInstance() já foi chamado e dispose() não foi.
     */
    public static boolean isInitialized(){
        return instance != null;
    }

    //<==== GETTERS ===>
    public Font getFont1(){
        return font1;
    }

    public Font getFont2(){
        return font2;
    }

    public Image getNaveImg(){
        return naveImageGame;
    }

    public Image getBackgroundImageGame(){
        return currentBackgroundGame;
    }

    public Image getIcon(){
        return iconImageGame;
    }

    public Image getBoost(){
        return snakeBoostGame;
    }

    public Image getBackgroundImageLobby(){
        return backgroundImageLobby;
    }

    public Image getPauseIcon(){
        return pauseIcon;
    }

    public BackgroundLevel getCurrentBackgroundLevel(){
        return currentLevel;
    }

    //<=== SETTERS ===>
    public void setBackgroundImageGame(Image newBackground){
        this.currentBackgroundGame = newBackground;
    }
}

package org.galaxy.snake.game.logic;

import org.galaxy.snake.game.core.GameConstants;

/**
 * Classe principal que gerencia a lógica do jogo.
 * Coordena interações entre Snake, Nave e verifica colisões.
 * 
 * Responsabilidade:
 * -Gerenciar estado do jogo (rodando, pausado e gameOver).
 * -Verifica colisões.
 * -Atualizar pontuação e background.
 * -Controlar reinício e encerramento.
 */

public class Game
{
    //<=== COMPONENTES DO JOGO ===>
    private final Snake snake;
    private final Nave nave;
    private final Sounds sounds;

    //<=== ESTADO DO JOGO ===>
    private boolean gameOver;
    private boolean gameOverHandled; //Evit múltiplos diálogos.

    //<=== LISTENERS ===>
    private GameListener gameListener; //Para notificar o UI sobre os eventos.

    /**
     * Interface para notificar a Ui sobre os eventos.
     */
    public interface GameListener{
        void onGameOver();
        void onGameReset();
        void onRequestRestart();
        void onRequestExit();
        void onScoreChanged(int score);
    }

    /**
     * Construtor inicializa todos os componentes do jogo.
     * Usa Singletons para Assets e Sounds.
     */
    public Game(){
        //Usa Singleton instance
        sounds = Sounds.getInstance();

        //Cria novos objetos de jogo
        snake = new Snake();
        nave = new Nave();

        gameOver = false;
        gameOverHandled = false;

        //Inicia a música de fundo automaticamente
        sounds.playBackgroundMusic();
    }

    /**
     * Define o listener para eventos do jogo.
     * 
     * @param listener Implementação do GameListener.
     */
    public void setGameListener(GameListener listener){
        this.gameListener = listener;
    }

    /**
     * Atualiza a posição da cobra.
     * Chamado a cada tick do timer.
     */
    public void updateSnakeMove() {
        if (!gameOver) {
            snake.move();
        }
    }

    /**
     * Atualiza o estado geral do jogo.
     * Verifica colisões e atualiza o visual.
     */
    public void updateGame()
    {
        if(gameOver){
            return;
        }
        notifyScoreChanged();
        checkAllCollisions();
    }

    /**
     * Verifica todas as colisões possíveis no jogo
     */
    private void checkAllCollisions(){
        //Colisão com nave (comida) - evento positivo.
        if(checkCollisionNave()){
            handleNaveCollision();
        }

        //Colisões que causam game over
        if(checkCollisionLimits() || checkCollisionBody()){
            triggerGameOver();
        }
    }

    /**
     * Atualiza o background baseado na pontuação atual.
     */
    private void notifyScoreChanged(){
        if (gameListener != null) {
            gameListener.onScoreChanged(snake.getScore());
        }
    }

    /**
     * Verifica colisão entre a cabeça da cobra e a nave.
     * Usa detecção de colisão AABB (Axis-Aligned Bounding Box).
     * 
     * @return true se houver colisão.
     */
    private boolean checkCollisionNave(){
        return snake.getHead().collidesWith(
            nave.getPosition(), 
            GameConstants.TILE_SIZE
        );
    }

    /**
     * Processo a colisão com a nave.
     * Toca som de explosão, reposiciona a nave e aumenta a cobra.
     */
    private void handleNaveCollision(){
        //Toca som de explosão (Clip é assíncrono, não precisa de thread).
        sounds.playExplosion();

        //Faz a cobra crescer.
        snake.grow();
        
        //Reposiciona a nave em local aleatório.
        nave.place(snake.getOccupiedPositions());
    }

    /**
     * Verifica se a cobra colidiu com os limites da tela.
     * 
     * @return true se houver colisão com limites
     */
    private boolean checkCollisionLimits(){
        int headX = snake.getHead().getX();
        int headY = snake.getHead().getY();

        //Verifica limites do grid.
        return headX < 0 ||
               headY < GameConstants.MIN_Y_TILE ||
               headX >= GameConstants.MAX_TILES_X ||
               headY >= GameConstants.MAX_TILES_Y; 
    }

    /**
     * Verifica se a cobra colidiu com o próprio corpo.
     * 
     * @return true se houver colisão com corpo
     */
    private boolean checkCollisionBody(){
        Tile head = snake.getHead();

        //Percorre todos os segmentos do corpo.
        //(não precisa ignorar o último, grow() já cuida disso).
        for(Tile segment : snake.getBodyReadOnly()){
            if (head.samePosition(segment)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Dispara o estado de GameOver.
     */
    private void triggerGameOver(){
        gameOver = true;

        //Para a música.
        sounds.stopBackgroundMusic();

        //Notifica a UI se houver listener.
        if (gameListener != null) {
            gameListener.onGameOver();
        }
    }

    /**
     * Processa a decisão do jogador após o GameOver.
     * Chamado pela UI após o jogador responder ao diálogo.
     * 
     * @param restart true para reiniciar, false para encerrar.
     */
    public void handleGameOverDecision(boolean restart){
        if(gameOverHandled){
            return;
        }
        gameOverHandled = true;

        if(restart){
            resetGame();
        }else{
            //Notifica a UI para encerrar.
            if (gameListener != null) {
                gameListener.onRequestExit();
            }
        }
    }

    /**
     * Reinicia o jogo para o estado inicial.
     */
    private void resetGame(){
        //Reseta cobra.
        snake.reset();

        //Reseta a nave.
        nave.reset();

        //Reseta estado do jogo.
        gameOver = false;
        gameOverHandled = false;

        //Reinicia a música.
        sounds.playBackgroundMusic();

        //Notifica a UI para reiniciar timers e estado visual.
        if (gameListener != null) {
            gameListener.onGameReset();
        }
    }

    //<=== GETTERS ===>
    public Snake getSnake(){
        return snake;
    }

    public Nave getNave(){
        return nave;
    }

    public Sounds getSounds(){
        return sounds;
    }

    public boolean isGameOver(){
        return gameOver;
    }

    /**
     * Libera os recursos de audio.
     */
    public void dispose(){
        sounds.dispose();
    }
}

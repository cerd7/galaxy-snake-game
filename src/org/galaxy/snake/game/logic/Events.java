package org.galaxy.snake.game.logic;

import org.galaxy.snake.game.core.GameConstants;

import javax.swing.Timer;
import java.awt.event.*;

/**
 * Classe responsável por gerenciar eventos de entrada do jogador.
 * Processa teclas do teclado para movimento e boost.
 * 
 * Controles:
 * -WASD ou Setas: movimento.
 * -Espaço: Ativar boost.
 */
public class Events implements KeyListener
{
    //<=== COMPONENTES ===>
    private final Game game;
    private final Timer boostTimer;

    //<=== ESTADO DO BOOST ===>
    private boolean boostActive;
    private int currentDelay; //Delay atual do Timer.


    /**
     * Construtor inicializa o gerenciador de eventos.
     */
    public Events(Game game)
    {
        this.game = game;
        boostActive = false;
        currentDelay = GameConstants.TIMER_SNAKE_NORMAL;

        //Timer para controlar duração do boost.
        //Dispara uma vez após BOOST_DURATION milissegundo.
        boostTimer = new Timer(GameConstants.BOOST_DURATION, this::handleBoostEnd);
        boostTimer.setRepeats(false);         
    }

    /**
     * Processa teclas pressionadas.
     * WASD para movimento, SPACE para boost
     * 
     * @param e Evento das teclas.
     */
    @Override
    public void keyPressed(KeyEvent e)
    {
        //Ignora entrada se o jogo acabou.
        if(game.isGameOver()){
            return;
        }

        switch (e.getKeyCode())
        {
            //MOVIMENTO PARA CIMA (W ou SETA).
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                moveUp();
                break;
            
            //MOVIMENTO PARA BAIXO (S ou SETA).
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                moveDown();
                break;

            //MOVIMENTO PARA A ESQUERDA (A ou SETA).
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                moveLeft();
                break;

            //MOVIMENTO PARA A DIREITA (D ou SETA).
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                moveRight();
                break;

            //ATIVAR BOOST (Espaço).
            case KeyEvent.VK_SPACE:
                activeBoost();
                break;
        }
    }

    /**
     * Move a cobra para cima.
     * Só permite se não estiver indo para baixo (evita 180°)
     */
    private void moveUp(){
        if (game.getSnake().getVelocityY() != 1) {
            game.getSnake().setVelocity(0, -1);
        }
    }

    /**
     * Move a cobra para baixo.
     * Só permite se não estiver indo para cima.
     */
    private void moveDown(){
        if (game.getSnake().getVelocityY() != -1) {
            game.getSnake().setVelocity(0, 1);
        }
    }
    
    /**
     * Move a cobra para esquerda.
     * Só permite se não estiver indo para direita.
     */
    private void moveLeft(){
        if (game.getSnake().getVelocityX() != 1) {
            game.getSnake().setVelocity(-1, 0);
        }
    }

    /**
     * Move a cobra para direita.
     * Só permite se não estiver indo para esquerda.
     */
    private void moveRight(){
        if (game.getSnake().getVelocityX() != -1) {
            game.getSnake().setVelocity(1, 0);
        }
    }

    /**
     * Ativa o modo boost se não estiver ativo.
     * O boost aumenta a velocidade temporariamente.
     */
    private void activeBoost(){
        if(!boostActive && game.getSnake().isMoving()){
            boostActive = true;
            currentDelay = GameConstants.TIMER_SNAKE_BOOST;
            game.getSounds().playBoost();

            //inicia o timer para desativar o boost
            boostTimer.restart();
        }
    }

    /**
     * Desativa o boost quando o timer termina.
     * 
     * @param e Evento do timer.
     */
    private void handleBoostEnd(ActionEvent e){
        boostActive = false;
        currentDelay = GameConstants.TIMER_SNAKE_NORMAL;
    }

    /**
     * Para o timer de boost.
     * Deve ser chamado quando o jogo pausa.
     */
    public void pauseBoost(){
        if (boostTimer.isRunning()) {
            boostTimer.stop();
        }
    }

    /**
     * Reseta o estado do boost.
     * Deve ser chamado quando o jogo reinicia.
     */
    public void resetBoost(){
        boostActive = false;
        currentDelay = GameConstants.TIMER_SNAKE_NORMAL;
        boostTimer.stop();
    }

    /**
     * Retorna a string de velocidade para exibição.
     * Calcula o multiplicador dinamicamente.
     * 
     * @return String formatada com multiplicador de velocidade.
     */
    public String getVelocityDisplay(){
        //Calcula o multiplicador: velocidade normal / velocidade atual.
        float multiplier = (float) GameConstants.TIMER_SNAKE_NORMAL / currentDelay;
        return String.format("%.1fx", multiplier);
    }

    //<=== MÉTODOS NÃO UTILIZADOS DA INTERFACE===>
    @Override
    public void keyTyped(KeyEvent e){
        //Não utilizado
    }

    @Override
    public void keyReleased(KeyEvent e){
        //Não utilizado
    }

    //<=== GETTERS ===>
    public Game getGame(){
        return game;
    }

    public boolean isBoostActive(){
        return boostActive;
    }

    public int getCurrentDelay(){
        return currentDelay;
    }
}

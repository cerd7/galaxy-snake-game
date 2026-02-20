package org.galaxy.snake.game.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.galaxy.snake.game.core.GameConstants;

/**
 * Classe que representa a cobra do jogo.
 * Gerencia a cabeça, corpo e movimento.
 * 
 * A cobra é composta por:
 * -Uma cabeça (Tile) que se move na direçãi atual.
 * -Um corpo (Lista de Tiles) onde cada segmento segue o anterior.
 * -Velocidade (velocityX, velocityY) que indica a direção do movimento.
 */
public class Snake 
{
    //<=== ATRIBUTOS ===>
    private Tile head; //Cabeça da cobra.
    final private ArrayList<Tile> body; //Corpo (lista de segmentos)
    private int velocityX; //Direção horizontal (-1,0,1)
    private int velocityY; //Direção vertical (-1,0,1)
    private Tile lasTailPosition; //Marca a ultima posição da calda.

    /**
     * Construtor inicializa a cobra na posição inicial.
     * A cobra começa parada (velocidade 0) sem corpo.
     */
    public Snake() 
    {
        head = new Tile(
            GameConstants.SNAKE_START_X, 
            GameConstants.SNAKE_START_Y, 
            GameConstants.SNAKE_TILE_SIZE, 
            GameConstants.SNAKE_TILE_SIZE
        );
        //Inicializa lista vazia para o corpo.
        body = new ArrayList<>();
        lasTailPosition = new Tile(head);
        
        //Velocidade 0 = cobra inicia parada.
        velocityX = 0;
        velocityY = 0;
    }

    /**
     * Move a cobra para a direção atual.
     * 
     * O movimento funciona assim:
     * 1 - Cada segmento do corpo assume a posição do segmento à frente.
     * 2 - O primeiro segmento assume a posição da cabeça.
     * 3 - A cabeça move na direção da velocidade atual.
     * 
     * Isso cria o efeito de "seguir" característico da snake.
     */
    public void move() 
    {
        //Só move se tiver alguma velocidade.
        if(velocityX == 0 && velocityY == 0){
            return;
        }

        //Salva a posição da cauda antes de se mover.
        if(!body.isEmpty()){
            lasTailPosition = new Tile(body.get(body.size() - 1));
        }else{
            lasTailPosition = new Tile(head);
        }

        /**
         * Move o corpo de trás para frente.
         * Começamos do último segmento para não sobrescrever as posições.
         */
        for (int i = body.size() - 1; i > 0; i--) 
        {
            Tile current = body.get(i);
            Tile previous = body.get(i - 1);
            current.setX(previous.getX());
            current.setY(previous.getY());
        }

        if (!body.isEmpty()) {
            Tile first = body.get(0);
            first.setX(head.getX());
            first.setY(head.getY());
        }

        //Move a cabeça na direção atual.
        head.setX(head.getX() + velocityX);
        head.setY(head.getY() + velocityY);
    }

    /**
     * Faz a cobra crescer adicionando um novo segmento.
     * O novo segmento é adicionado na posição atual da cauda.
     * 
     * Este método deve ser chamado quando a cobra come a nave.
     */
    public void grow(){
        //Adiciona ao final do corpo o novo segmento.
        body.add(new Tile(lasTailPosition));
    }

    /**
     * Reseta a cobra para o estado inicial.
     */
    public void reset(){        
        //para o movimento.
        velocityX = 0;
        velocityY = 0;

        //remove todos os segmentos do corpo.
        body.clear();

        //Reposiciona a cabeça.
        lasTailPosition = new Tile(head);
    }

    /**
     * Retorna lista com todas as posições ocupadas pela cobra.
     * Inclui a cabeça e todos os segmentos do corpo.
     * 
     * 
     * @return Lista imutável de tiles ocupados.
     */
    public List<Tile> getOccupiedPositions(){
        List<Tile> positions = new ArrayList<>();

        //Adiciona a cabeça.
        positions.add(head.copy());

        //adiciona cada segmento do corpo
        for(Tile segment : body){
            positions.add(segment.copy());
        }
        return Collections.unmodifiableList(positions);
    }

    /**
     * Verifica se a cobra está em movemento.
     * 
     * @return true se a velocidade for diferente de zero.
     */
    public boolean isMoving(){
        return velocityX != 0 || velocityY != 0; 
    }

    /**
     * Retorna o tamanho total da cobra (cabeça + corpo).
     * 
     * @return Número total de segmentos.
     */
    public int getSize(){
        return 1 + body.size();// 1 da cabeça + qty do corpo.
    }

    /**
     * Retorna a pontuação atual (baseada no tamanho do corpo).
     * 
     * @return Número de segmentos do corpo
     */
    public int getScore(){
        return body.size();
    }

    //<=== GETTERS E SETTERS ===>

    /**
     * Define uma nova cabeça para a cobra.
     * @param newHead Novo tile para a cabeça
     */
    public void setHead(Tile newHead) {
        this.head = newHead;
    }

    /**
     * Retorna a cabeça da cobra.
     * @return Tile da cabeça
     */
    public Tile getHead() {
        return head;
    }

    /**
    * Retorna a lista do corpo.
    * NOTA: Retorna a referência direta para compatibilidade.
    * Prefira usar getOccupiedPositions() para leitura segura.
    * 
    * @return ArrayList do corpo
    */
    public ArrayList<Tile> getBody() {
        return body;
    }
    
    /**
     * Retorna uma visão somente-leitura do corpo.
     * 
     * @return Lista imutável dos segmentos
     */
    public List<Tile> getBodyReadOnly() {
        return Collections.unmodifiableList(body);
    }

    /**
     * Retorna velocidade no eixo X.
     * @return -1 (esquerda), 0 (parado), ou 1 (direita)
     */
    public int getVelocityX() {
        return velocityX;
    }

    /**
     * Define velocidade no eixo X.
     * @param velocityX Nova velocidade (-1, 0, ou 1)
     */
    public void setVelocityX(int velocityX) {
        this.velocityX = velocityX;
    }

    /**
     * Retorna velocidade no eixo Y.
     * @return -1 (cima), 0 (parado), ou 1 (baixo)
     */
    public int getVelocityY() {
        return velocityY;
    }

    /**
     * Define velocidade no eixo Y.
     * @param velocityY Nova velocidade (-1, 0, ou 1)
     */
    public void setVelocityY(int velocityY) {
        this.velocityY = velocityY;
    }
    
    /**
     * Define ambas velocidades de uma vez.
     * @param vx Velocidade X
     * @param vy Velocidade Y
     */
    public void setVelocity(int vx, int vy) {
        this.velocityX = vx;
        this.velocityY = vy;
    }
}

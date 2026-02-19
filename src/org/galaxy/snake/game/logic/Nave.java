package org.galaxy.snake.game.logic;
import java.util.List;
import java.util.Random;

import org.galaxy.snake.game.core.GameConstants;

/**
 * Classe que representa a nave (comida) do jogo.
 * Responsável por posicionamento aleatório.
 * 
 * A nave spawna em posuções aleatórias, evitando.
 * -O painel superior (HUD).
 * -Posições ocupadas pela cobra.
 * -Áreas muito próximas das bordas.
 */
public class Nave 
{
    //<=== ATRIBUTOS ===>
    final private Tile position; //Posição atual da nava.
    final private Random random; //Gerador de números aleatórios.

    //Constantes locais para cálculo de spawn.
    private static final int MARGIN_TILES = 2; //Margem das bordas em riles.
    private static final int MAX_SPAWN_ATTEMPTS = 100; //Limites de tentativas.

    /**
     * Construtor inicializa a nave com posição inicial.
     */
    public Nave() 
    {
        position = new Tile(
            GameConstants.NAVE_START_X,
            GameConstants.NAVE_START_Y,
            GameConstants.NAVE_TILE_SIZE,
            GameConstants.NAVE_TILE_SIZE   
        );
        random = new Random();
    }

    /**
     * Posiciona a nave em um local aleatório válido.
     * Versão simples que não verifica a posição da cobra.
     * 
     * Garante que a nave não apareça:
     * -No painel superior.
     * -Parcialmente fora da tela.
     */
    public void place()
    {
        //Calcula limites considerando o tamanho da nave.
        int naveTiles = (GameConstants.NAVE_TILE_SIZE / GameConstants.TILE_SIZE) + 1;
        int maxX = GameConstants.MAX_TILES_X - naveTiles - MARGIN_TILES;
        int maxY = GameConstants.MAX_TILES_Y - naveTiles - MARGIN_TILES;
        int minY = GameConstants.NAVE_MIN_Y_SPAWN;

        //Gera posições aleatórias dentro dos limites válidos.
        position.setX(MARGIN_TILES + random.nextInt(maxX - MARGIN_TILES));
        position.setY(minY + random.nextInt(maxY - minY));
    }

    /**
     * Posiciona a nave em um local aleatório evitando posições ocupadas.
     * Esta versão recebe a lista de tiles ocupados pela cobra.
     * 
     * @param occupiedPositions Lista de tiles onde a nave NÃO pode spawnar.
     */
    public void place(List<Tile> occupiedPositions){
        //Se não há posições para evitar, usa o método simples
        if(occupiedPositions == null || occupiedPositions.isEmpty()){
            place();
            return;
        }

        //Calcula limites considerando o tamanho da nave
        int naveTiles = (GameConstants.NAVE_TILE_SIZE / GameConstants.TILE_SIZE) + 1;
        int maxX = GameConstants.MAX_TILES_X - naveTiles - MARGIN_TILES;
        int maxY = GameConstants.MAX_TILES_Y - naveTiles - MARGIN_TILES;
        int minY = GameConstants.NAVE_MIN_Y_SPAWN;

        //Tenta encontrar posições válidas.
        int attempts = 0;
        boolean validPosition;

        do{
            //Gera posições candidata
            position.setX(MARGIN_TILES + random.nextInt(maxX - MARGIN_TILES));
            position.setY(minY + random.nextInt(maxY - minY));

            //Verifica se não colide com nenhuma posição ocupada
            validPosition = true;
            for(Tile occupied : occupiedPositions){
                if (isOverlapping(occupied)) {
                    validPosition = false;
                    break;
                }
            }
            attempts++;
            //Evita loop infinito se a cobra ocupar quase tudo.
        }while(!validPosition && attempts < MAX_SPAWN_ATTEMPTS);

        //Se não encontrou posição válida após muitas tentativas,
        //usa a última posição gerada (melhor que travar).
        if(!validPosition){
            System.err.println("Aviso: Não foi possível encontrar posição ideal para a nave");
        }
    }

    /**
     * Verifica se a nave sobrepõe um tile específico.
     * Considera a área expandida da nave (pois ela é maior que um tile).
     * 
     * @param Tile a verificar.
     * @return true se houver sobreposição.
     */
    private boolean isOverlapping(Tile tile){
        //Calcula a área ocupada pela nave em tiles
        int naveTiles = (GameConstants.NAVE_TILE_SIZE / GameConstants.TILE_SIZE) + 1;

        //Verifica se o tile está dentro da área da nave
        int naveRight = position.getX() + naveTiles;
        int naveBottom = position.getY() + naveTiles;

        return tile.getX() >= position.getX() && tile.getX() < naveRight &&
               tile.getY() >= position.getY() && tile.getY() < naveBottom;
    }

    /**
     * Reseta a nave para a posição 
     */
    public void reset(){
        position.setPosition(GameConstants.NAVE_START_X, GameConstants.NAVE_START_Y);
    }

    public Tile getPosition() 
    {                                                                     
        return position;
    }
}

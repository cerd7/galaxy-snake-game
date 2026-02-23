package org.galaxy.snake.game.logic;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    //<=== LOG -  mais eficiente e personalizável ===>
    private static final Logger LOGGER = Logger.getLogger(Nave.class.getName());

    //<=== ATRIBUTOS ===>
    final private Tile position; //Posição atual da nava.

    //Constantes locais para cálculo de spawn.
    private static final int MARGIN_TILES = 2; //Margem das bordas em riles.
    private static final int MAX_SPAWN_ATTEMPTS = 100; //Limites de tentativas.

    //Número de Tile ocupados pela nave.
    private static final int NAVE_TILES = (GameConstants.NAVE_TILE_SIZE / GameConstants.TILE_SIZE) + 1;

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
    }

    /**
     * Posiciona a nave em um local aleatório e válido.
     * Delega para o método com lista vazia quando não há posições para evitar.
     */
    public void place(){
        place(Collections.emptyList());
    }

    /**
     * Posiciona a nave em um local aleatório evitando posições ocupadas.
     * 
     * Garante que a nave não apareça:
     * -No painel superior (HUD)
     * -Parcialmente fora da tela.
     * -Sobre posições ocupadas pela cobra.
     * 
     * @param occupiedPositions Lista de tiles onde a nave não pode spawnar,
     *                          Pode ser vazia, mas não null.
     */
    public void place(List<Tile> occupiedPositions)
    {
        //Trata o null como lista vazia para robustez.
        if (occupiedPositions == null) {
            occupiedPositions = Collections.emptyList();
        }

        //Calcula limites considerando o tamanho da nave.
        int maxX = GameConstants.MAX_TILES_X - NAVE_TILES - MARGIN_TILES;
        int maxY = GameConstants.MAX_TILES_Y - NAVE_TILES - MARGIN_TILES;
        int minY = GameConstants.NAVE_MIN_Y_SPAWN;

        // Validação de segurança para evitar IllegalArgumentException no nextInt.
        int rangeX = maxX - MARGIN_TILES;
        int rangeY = maxY - minY;
        if (rangeX <= 0 || rangeY <= 0) {
            LOGGER.log(Level.WARNING, "Área de spawn inválida: rengeX={0}, rangeY={1}",
                new Object[]{rangeX, rangeY}
            );
            return;
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        int attempts = 0;
        boolean validPosition;

        do{
            //Gera posições aleatórias.
            position.setX(MARGIN_TILES + random.nextInt(rangeX));
            position.setY(minY + random.nextInt(rangeY));

            //Verifica se não colide com nenhuma posição ocupada.
            validPosition = true;
            for(Tile occupied : occupiedPositions){
                if(isOverlapping(occupied)){
                    validPosition = false;
                    break;
                }
            }
            attempts++;
        } while(!validPosition && attempts < MAX_SPAWN_ATTEMPTS);

        if (!validPosition) {
            LOGGER.log(Level.FINE, "Não encontrou posição ideal para a nave após {0} tentativas", attempts);
        }
    }

    /**
     * Verifica se a nave sobrepõe um tile específico.
     * Considera a área expandida da nave (pois ela é maior que um tile).
     * 
     * @param tile Tile a verificar.
     * @return true se houver sobreposição.
     */
    private boolean isOverlapping(Tile tile) {
        int naveRight = position.getX() + NAVE_TILES;
        int naveBottom = position.getY() + NAVE_TILES;

        return tile.getX() >= position.getX() && tile.getX() < naveRight
            && tile.getY() >= position.getY() && tile.getY() < naveBottom;
    }

    /**
     * Reseta a nave para a posição inicial definida em GameConstants.
     */
    public void reset() {
        position.setPosition(GameConstants.NAVE_START_X, GameConstants.NAVE_START_Y);
    }

    /**
     * Retorna a posição atual da nave.
     * 
     * @return Tile representando a posição.
     */
    public Tile getPosition() {                                                                     
        return position;
    }
}

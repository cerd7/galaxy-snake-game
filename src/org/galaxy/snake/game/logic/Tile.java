package org.galaxy.snake.game.logic;

import java.util.Objects;

/**
 * Classe que representa um tile (bloco) no grid do jogo.
 * Usado para posições da cobra, nave e outros elementos.
 * 
 * Cada tile possui:
 * -Coordenadas (x, y) no sistema de grid (não em pixels).
 * -Dimensões (width, height) em pixels para renderização.
 */
public class Tile {
    private int x; //Posição do eixo X no grid (não em pixels)
    private int y; //Posição do eixo Y no grid (não em pixels)
    private final int width; //Largura em pixels
    private final int height; //Altura em pixels

    /**
     * Construtor cria um tile com posições e dimensões
     * 
     * @param x Coordenada X no grid
     * @param y Coordenada Y no grid
     * @param width Largura em pixels
     * @param height Altura em pixels
     */
    public Tile(int x, int y, int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException(
                String.format("Dimensões devem ser não-negativas: width=%d, height=%d", width, height)
            );
        }

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Construtor de cópia - cria um novo Tile com os mesmos valores de outro.
     * Útil para criar cópias independentes sem compartilhar referências.
     * 
     * @param other Tile a ser copiado
     */
    public Tile(Tile other){
        Objects.requireNonNull(other, "Tile copiado não pode ser null");
        this.x = other.x;
        this.y = other.y;
        this.width = other.width;
        this.height = other.height;
    }

    //<=== MÉTODOS DE COMPARAÇÃO ===>
    /**
     * Verifica se dois tiles são iguais baseado nas coordenadas do grid.
     * Dois tiles são considerados iguais se ocupam a mesma posição (x, y).
     * 
     * @param obj Objeto para comparar
     * @return true se as coordenadas forem iguais.
     */
    @Override
    public boolean equals(Object obj){
        //Se é o mesmo objeto, retorna true.
        if (this == obj) {
            return true;
        }
        //Se é null ou de uma classe diferente, retorna false.
        if(obj == null || getClass() != obj.getClass()){
            return false;
        }
        //Faz cast e compara as coordenadas
        Tile other = (Tile) obj;
        return this.x == other.x && this.y == other.y;
    }

    /**
     * Gera um código hash baseado nas coordenadas.
     * Necessário quando equals é sobrescrito para o uso em HashMaps/HashSets.
     * 
     * @return Código hash baseado em x e y.
     */
    @Override
    public int hashCode(){
        return Objects.hash(x, y);
    }

    /**
     * Verifica se este tile colide com o outro usando AABB (Axis-Aligned Bounding Box).
     * 
     * @param other Outro tile para verificar colisão.
     * @param tileSize Tamanho do tile em pixels para converter grid->pixels.
     * @returntrue se houver sobreposição.
     */
    public boolean collidesWith(Tile other, int tileSize){
        Objects.requireNonNull(other, "Tile para colisão não pode ser null");
        if (tileSize < 0) {
            throw new IllegalArgumentException("tileSize deve ser positivo: " + tileSize);
        }

        //Calcula os limites deste tile em pixels
        int thisLeft = this.x * tileSize;
        int thisTop = this.y * tileSize;
        //Adiciona as dimensões.
        int  thisRight = thisLeft + this.width;
        int thisBottom = thisTop + this.height;

        //Calcula os limites do outro tile pixels
        int otherLeft = other.x * tileSize;
        int otherTop = other.y * tileSize;
        int otherRight = otherLeft + other.width;
        int otherBottom = otherTop + other.height;

        //Verifica a sobreposição do retângulo (AABB).
        return thisLeft < otherRight &&
               thisRight > otherLeft &&
               thisTop < otherBottom &&
               thisBottom > otherTop;
    }

    /**
     * Verifica se este tile está na mesma posição de grid que outro.
     * Ignora as dimensões, apenas compara x e y.
     * 
     * @param other Outro Tile.
     * @return true se for a mesma posição.
     */
    public boolean samePosition(Tile other){
        return this.x
         == other.x && this.y == other.y;
    }

    /**
     * Cria uma cópia deste tile.
     * 
     * @return Novo Tile com os mesmos valores.
     */
    public Tile copy(){
        return new Tile(this);
    }

    /**
     * Representação em String para debug.
     * 
     * @return String no formato "Tile[x=?, y=?, w=?, h=?]"
     */
    @Override
    public String toString() {
        return String.format("Tile[x=%d, y=%d, w=%d, h=%d]", x, y, width, height);
    }

    //<=== GETTERS ===>

    /**
     * Retorna a coordenada do eixo X no grid.
     * @return Posição X.
     */
    public int getX(){
        return x;
    }

    /**
     * Retorna a coordenada do eixo Y no grid.
     * @return Posição Y.
     */
    public int getY(){
        return y;
    }

    /**
     * Retorna a largura em pixels.
     * @return Largura.
     */
    public int getWidth(){
        return width;
    }

    /**
     * Retorna a altura em pixels.
     * @return Altura.
     */
    public int getHeight(){
        return height;
    }

    //<=== SETTERS ===>

    /**
     * Define a coordenada X no grid.
     * @param x Nova posição X.
     */
    public void setX(int x){
        this.x = x;
    }

    /**
     * Define a coordenada Y no grid.
     * @param y nova posição Y.
     */
    public void setY(int y){
        this.y = y;
    }

    /**
     * Define ambas as coordenadas de uma vez.
     * @param x Nova posição x.
     * @param y Nova posição y.
     */
    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }
}

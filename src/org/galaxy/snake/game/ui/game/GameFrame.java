package org.galaxy.snake.game.ui.game;

import org.galaxy.snake.game.core.GameConstants;
import org.galaxy.snake.game.ui.Assets;

import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serial;

import javax.swing.*;

/**
 * Frame principal do jogo.
 * Contém o GamePanel e configurações da Janela.
 */
public class GameFrame extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;

    private final GamePanel gamePanel;

    /**
     * Construtor inicializa o frame.
     */
    public GameFrame(){
        gamePanel = new GamePanel();
        configureFrame();
    }

    /**
     * Configura as propriedades do frame.
     */
    public void configureFrame(){
        //Configuração básica.
        setTitle(GameConstants.GAME_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        //Ícone da janela.
        Image icon = Assets.getInstance().getIcon();
        if (icon != null) {
            setIconImage(icon);
        }

        //Adiciona ao painel.
        add(gamePanel);

        //Ajusta o tamanho ao conteúdo.
        pack();

        //Centraliza a tela.
        setLocationRelativeTo(null);

        //Listener para a limpeza ao fechar.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gamePanel.stopTimers();
            }
        });
    }

    /**
     * Exibe a janela do jogo.
     * Renomeado de show() para evitar conflitos com o método deprecated.
     */
    public void display(){
        setVisible(true);

        //Solicita o foco para o painel após ficar visível.
        gamePanel.requestFocusInWindow();
    }

    /**
     * Retorna o painel do jogo.
     * 
     * @return gamePanel.
     */
    public GamePanel getGamePanel(){
        return gamePanel;
    }

}

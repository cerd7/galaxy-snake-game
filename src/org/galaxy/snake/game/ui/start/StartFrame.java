package org.galaxy.snake.game.ui.start;

import java.awt.Image;
import java.io.Serial;

import javax.swing.*;

import org.galaxy.snake.game.core.GameConstants;
import org.galaxy.snake.game.ui.Assets;

/**
 * Frame da tela inicial do jogo.
 * Exibe menu e opções antes de iniciar o jogo.
 */
public class StartFrame extends JFrame{
    @Serial
    private static final long serialVersionUID = 1L;

    private final StartMenu startMenu;
    private final StartPanel startPanel;
    private Timer closeTimer;

    /**
     * Construtor inicializa componentes.
     */
    public StartFrame(){
        startMenu = new StartMenu();
        startPanel = new StartPanel();
        configureFrame();
    }

    /**
     * Configura as propriedades do frame.
     */
    public void configureFrame(){
        setTitle(GameConstants.LOBBY_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        //ícone da janela.
        Image icon = Assets.getInstance().getIcon();
        if(icon != null){
            setIconImage(icon);
        }

        //Adiciona componentes.
        add(startPanel);
        setJMenuBar(startMenu.createMenuBar());

        //Ajusta tamanho e posição.
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Exibe a janela do lobby.
     */
    public void display(){
        setVisible(true);
        startCloseTimer();
    }

    /**
     * Iniciar timer que monitora se o jogo foi iniciado.
     * Fecha esta janela quando o jogo começa.
     */
    private void startCloseTimer(){
        closeTimer = new Timer(500, e ->{
            if(startPanel.hasStarted()){
                closeTimer.stop();
                dispose();
            }
        });
        closeTimer.start();
    }
}

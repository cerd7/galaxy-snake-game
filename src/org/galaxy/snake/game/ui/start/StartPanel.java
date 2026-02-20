package org.galaxy.snake.game.ui.start;

import org.galaxy.snake.game.core.GameConstants;
import org.galaxy.snake.game.ui.Assets;
import org.galaxy.snake.game.ui.game.GameFrame;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.Serial;

/**
 * Painel da tela inicial com botões START e EXIT.
 * Exibe o background do lobby e permite iniciar o jogo.
 */
public class StartPanel extends JPanel
{
    //SerialVerison
    @Serial
    private static final long serialVersionUID = 1L;

    //<=== COMPONENTES ===>
    private final JButton buttonStart;
    private final JButton buttonExit;
    private final Border buttonBorder;
    private final Assets assets;

    //<=== ESTADO ===>
    private boolean gameStarted;

    //<=== CONSTANTES DE LAYOUT ===>
    private static final int BUTTON_WIDTH = 285;
    private static final int BUTTON_HEIGHT = 50;
    private static final int BUTTON_X = 350;
    private static final int BUTTON_START_Y = 200;
    private static final int BUTTON_EXIT_Y = 280;
    // private static final int BUTTON_SPACING = 80;

    /**
     * Construtor Inicializa componentes visuais.
     */
    public StartPanel()
    {
        //Configura o painel.
        setPreferredSize(new Dimension(GameConstants.LOBBY_WIDTH, GameConstants.LOBBY_HEIGHT));
        setBackground(Color.LIGHT_GRAY);
        setDoubleBuffered(true);
        setFocusable(true);

        //Layout nulo para posicionamento absoluto.
        setLayout(null);

        //Obtém singleton de assets.
        assets = Assets.getInstance();

        //Borda comum para os botões.
        buttonBorder = BorderFactory.createLineBorder(Color.BLACK,2);

        //Cria os botões.
        buttonStart = createButton("START", BUTTON_START_Y);
        buttonExit = createButton("EXIT", BUTTON_EXIT_Y);

        //Estado inicial.
        gameStarted = false;
    }

    /**
     * Cria um botão com configurações padrões.
     * 
     * @param text Texto do botão
     * @param y Posição Y do botão
     * @return Botão configurado
     */
    private JButton createButton(String text, int y){
        JButton button = new JButton(text);
        button.setFont(assets.getFont1());
        button.setBackground(Color.YELLOW);
        button.setForeground(Color.BLACK);
        button.setBorder(buttonBorder);
        button.setFocusPainted(false);
        
        //Posicionamento.
        button.setBounds(BUTTON_X, y, BUTTON_WIDTH, BUTTON_HEIGHT);

        //Adicionar listener correto.
        if (text.equals("START")) {
            button.addActionListener(this::handleStartButton);
        }else{
            button.addActionListener(this::handleExitButton);
        }

        add(button);
        return button;
    }

    /**
     * Handle do botão START.
     * Cria e exibe o frame do jogo.
     */
    private void handleStartButton(ActionEvent e){
        if (gameStarted) {
            return;
        }

        gameStarted = true;
        buttonStart.setEnabled(false);
        buttonExit.setEnabled(false);

        //Cria a janela e exibe o jogo.
        GameFrame gameFrame = new GameFrame();
        gameFrame.display();
    }

    /**
     * Handler do botão EXIT.
     * Encerra o aplicativo.
     */
    private void handleExitButton(ActionEvent e){
        System.exit(0);
    }

    /**
     * Renderiza os elementos visuais do painel.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        drawBackground(g2);
        drawTitle(g2);
        drawCopyright(g2);
    }

    /**
     * Desenha o background do lobby
     */
    private void drawBackground(Graphics2D g2){
        Image background = assets.getBackgroundImageLobby();
        if (background != null) {
            g2.drawImage(background, 0, 0, GameConstants.LOBBY_WIDTH, GameConstants.LOBBY_HEIGHT, null);
        }
    }

    /**
     * Desenha o título do jogo.
     */
    private void drawTitle(Graphics2D g2){
        g2.setColor(Color.WHITE);
        g2.setFont(assets.getFont1());

        //Centraliza o título.
        String title = GameConstants.GAME_TITLE;
        FontMetrics fm = g2.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        int x = (GameConstants.LOBBY_WIDTH - titleWidth) / 2;

        g2.drawString(title, x, 100);
    }

    /**
     * Desenha o texto de copyright.
     */
    private void drawCopyright(Graphics2D g2){
        g2.setColor(Color.WHITE);
        g2.setFont(assets.getFont2());
        g2.drawString("@Cerd7", 440, 490);
    }

    //<=== GETTER ===>
    public boolean hasStarted(){
        return gameStarted;
    }
}

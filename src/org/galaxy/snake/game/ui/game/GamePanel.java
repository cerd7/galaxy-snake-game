package org.galaxy.snake.game.ui.game;

import org.galaxy.snake.game.core.GameConstants;
import org.galaxy.snake.game.logic.Game;
import org.galaxy.snake.game.logic.Events;
import org.galaxy.snake.game.logic.Tile;
import org.galaxy.snake.game.ui.Assets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.Serial;

/**
 * Painel principal do jogo que renderiza todos os elementos visuais.
 * Gerencia os timers de atualizações e renderização.
 * 
 * Responsabilidades:
 * -Renderizar background, cobra, nave e HUD.
 * -Gerenciar timer de atualizações.
 * -Controlar pausa.
 * -Processar eventos de teclados.
 */
public class GamePanel extends JPanel implements Game.GameListener{
    @Serial
    private static final long serialVersionUID = 1L;

    //<=== COMPONENTES ===>
    private final Events events;
    private final Game game;
    private final Assets assets;
    private final JButton pauseButton;

    //<=== TIMERS ===>
    private final Timer renderTimer;
    private final Timer gameTimer;

    //<=== ESTADO ===>
    private boolean paused;
    private boolean gameOverDialogShown;

    /**
     * Construtor inicializa o painel e todos os componentes.
     */
    public GamePanel() 
    {
        setPreferredSize(new Dimension(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);

        //Define layout nulo para usar setBounds.
        setLayout(null);

        //Inicializa eventos
        game = new Game();
        events = new Events(game);
        addKeyListener(events);

        //Obtém singleton de assets uma vez no construtor
        assets = Assets.getInstance();

        //Registra como listener do jogo.
        events.getGame().setGameListener(this);

        //Cria e inclui o botão de pause.
        pauseButton = createPauseButton();
        add(pauseButton);

        //Estado inicial.
        paused = false;
        gameOverDialogShown = false;

        //Timer de renderização (60fps)
        renderTimer = new Timer(GameConstants.TIMER_PANEL_DELAY, this::handleRenderTick);
        
        //Timer de movimento da cobra
        gameTimer = new Timer(GameConstants.TIMER_SNAKE_NORMAL, this::handleGameTick);
        
        //Inicia timers.
        renderTimer.start();
        gameTimer.start();

        //Solicita foco após adicionar ao frame.
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    /**
     * Cria e configura o botão de pause.
     * 
     * @return Botão de pause configurado.
     */
    private JButton createPauseButton(){
        JButton button = new JButton("PAUSE");
        button.setFont(assets.getFont1());
        button.setBackground(Color.YELLOW);
        button.setForeground(Color.BLACK);
        button.setBounds(10,13,300,50);

        //Usa ActionListener.
        button.addActionListener(e -> togglePause());

        return button;
    }

    /**
     * Alterna entre o pause e o jogo ativo.
     */
    private void togglePause(){
        paused = !paused;

        if(paused){
            //Jogo pausado
            renderTimer.stop();
            gameTimer.stop();
            game.getSounds().stopBackgroundMusic();

            //Atualiza o visual do botão.
            pauseButton.setText("CONTINUE");
            pauseButton.setBackground(new Color(10, 20, 40));
            pauseButton.setForeground(Color.WHITE);
        }else{
            //Retomar a jogo
            renderTimer.start();
            gameTimer.start();
            game.getSounds().stopBackgroundMusic();

            //Restaura o visual do botão.
            pauseButton.setText("PAUSE");
            pauseButton.setBackground(Color.YELLOW);
            pauseButton.setForeground(Color.BLACK);

            //Recupera o foco para o painek.
            requestFocusInWindow();
        }
        repaint();
    }

    /**
     * Callback do timer de renderização.
     * Atualiza a lógica do jogo e repinta.
     */
    private void handleRenderTick(ActionEvent e){
        game.updateGame();
        repaint();

        //Verifica o gameOver.
        if(events.getGame().isGameOver() && !gameOverDialogShown){
            handleGameOverInUi();
        }
    }

    /**
     * Callback do timer de movimentos.
     * Move a cobra e ajusta a velocidade com base no boost.
     */
    private void handleGameTick(ActionEvent e){
        game.updateSnakeMove();

        //Ajusta a velocidade com base no boost
        int newDelay = events.getCurrentDelay();
        if (gameTimer.getDelay() != newDelay) {
            gameTimer.setDelay(newDelay);
        }
    }

    /**
     * Processa o gameOver na thread da UI.
     */
    private void handleGameOverInUi(){
        gameOverDialogShown = true;
        renderTimer.stop();
        gameTimer.stop();

        SwingUtilities.invokeLater(() -> {
            int response = JOptionPane.showConfirmDialog(
                this,
                GameConstants.GAME_OVER_MESSAGE,
                GameConstants.GAME_OVER_TITLE,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            events.getGame().handleGameOverDecision(response == JOptionPane.YES_OPTION);
        });
    }

    //<=== IMPLEMENTAÇÂO DE Game.GameListener ===>

    @Override
    public void onGameOver() {
        //Callback quando o jogo detecta gameOver.
        //o diálogo será ostrado por handleRenderClick.
    }

    @Override
    public void onGameReset() {
        // Callback quando o jogo é reiniciado.
        gameOverDialogShown = false;
        events.resetBoost();
        assets.resetBackground();
        gameTimer.setDelay(GameConstants.TIMER_SNAKE_NORMAL);
        

        //Reinicia os timers.
        renderTimer.start();
        gameTimer.start();

        //Recupera o foco.
        requestFocusInWindow();
    }

    @Override
    public void onRequestRestart() {
        //Game já faz o reset internamente via resetGame().
        //onGameReset() será chamado.
    }

    @Override
    public void onRequestExit() {
        game.dispose();
        System.exit(0);
    }

    @Override
    public void onScoreChanged(int score){
        assets.updateBackgroundByScore(score);
    }

    /**
     * Renderiza todos os elementos do jogo.
     */
    @Override
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);

        //Ativa anti-aliasing para gráficos suaves
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        //Desenha os elementos na ordem correta (fundo para frente).
        drawBackground(g2);
        drawNave(g2);
        drawSnake(g2);
        drawTopPanel(g2);
        drawScore(g2);
        drawVelocity(g2);
        
        //Desenha o ícone de pause se estiver pausado
        if(paused){
            drawPauseOverlay(g2);
        }
    }

    /**
     * Desenha o background do jogo.
     */
    private void drawBackground(Graphics2D g2){
        Image background = assets.getBackgroundImageGame();
        if(background != null){
            g2.drawImage(background, 0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT, null);
        }
    }

    /**
     * Desenha a nave (comida).
     */
    private void drawNave(Graphics2D g2){
        Tile navePos = game.getNave().getPosition();
        Image naveImage = assets.getNaveImg();

        if(naveImage != null){
            int x = navePos.getX() * GameConstants.TILE_SIZE;
            int y = navePos.getY() * GameConstants.TILE_SIZE;
            g2.drawImage(naveImage, x, y, GameConstants.NAVE_TILE_SIZE, GameConstants.NAVE_TILE_SIZE, null);
        }
    }

    /**
     * Desenha a cobra (cabeça e corpo)
     */
    private void drawSnake(Graphics2D g2){
        //Cor da cobra.
        g2.setColor(Color.WHITE);

        //Desenha cobeça
        Tile head = game.getSnake().getHead();
        int headX = head.getX() * GameConstants.TILE_SIZE;
        int headY = head.getY() * GameConstants.TILE_SIZE;
        g2.fillRect(headX, headY, GameConstants.SNAKE_TILE_SIZE, GameConstants.SNAKE_TILE_SIZE);

        //Desenhar o corpo
        for(Tile part : game.getSnake().getOccupiedPositions()){
            int partX = part.getX() * GameConstants.TILE_SIZE;
            int partY = part.getY() * GameConstants.TILE_SIZE;
            g2.fillRect(partX, partY, GameConstants.SNAKE_TILE_SIZE, GameConstants.SNAKE_TILE_SIZE);
        }
    }

    /**
     * Desenha o painel superior (barra preta).
     */
    private void drawTopPanel(Graphics2D g2){
        //fundo preto
        g2.setColor(Color.BLACK);
        g2.fillRect(0,0, GameConstants.PANEL_WIDTH, GameConstants.PANEL_HEIGHT);

        //borda branca
        g2.setColor(Color.WHITE);
        g2.drawRect(0,0, GameConstants.PANEL_WIDTH - 1, GameConstants.PANEL_HEIGHT - 1);
    }

    /**
     * Desenha a pontuação.
     */
    private void drawScore(Graphics2D g2){
        g2.setColor(Color.WHITE);
        g2.setFont(assets.getFont1());

        int score = game.getSnake().getScore();
        g2.drawString("SCORE: " + score, 430, 50);
    }

    /**
     * Desenha o indicador de velocidade
     */
    private void drawVelocity(Graphics2D g2){
        g2.setColor(Color.WHITE);
        g2.setFont(assets.getFont1());

        String velocityText = "Velocity - " + events.getVelocityDisplay();
        g2.drawString(velocityText, 680, 50);
    }

    /**
     * Desenha o overlay de pausue (ícone no centro).
     */
    private void drawPauseOverlay(Graphics2D g2){
        //Fundo semi-transparente.
        g2.setColor(new Color(0,0,0,150));
        g2.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        //Ícone de pause.
        Image pauseIcon = assets.getPauseIcon();
        if(pauseIcon != null){
            int iconSize = 200;
            int x = (GameConstants.SCREEN_WIDTH - iconSize) / 2;
            int y = (GameConstants.SCREEN_HEIGHT - iconSize) / 2;
            g2.drawImage(pauseIcon, x, y, iconSize, iconSize, null);
        }
    }

    /**
     * Interrmpe todos os timers.
     * Deve ser chamado quando o frame é fechado.
     */
    public void stopTimers(){
        renderTimer.stop();
        gameTimer.stop();
    }


}

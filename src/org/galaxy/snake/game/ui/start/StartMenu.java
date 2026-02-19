package org.galaxy.snake.game.ui.start;

import java.awt.BorderLayout;

import javax.swing.*;

import org.galaxy.snake.game.logic.Sounds;

/**
 * Menu de opções da tela inicial.
 * Fornece acesso a configurações e outras funcionalidades.
 */
public class StartMenu{
    private final JMenuBar menuBar;
    private final JMenu optionsMenu;
    private final JMenu settingsMenu;
    private final JCheckBoxMenuItem muteItem;
    private final JMenuItem volumeItem;

    /**
     * Construtor inicializa os componentes do menu.
     */
    public StartMenu(){
        menuBar = new JMenuBar();

        optionsMenu = new JMenu("Options");

        settingsMenu = new JMenu("Settings");

        volumeItem = new JMenuItem("Volume...");

        muteItem = new JCheckBoxMenuItem("Mute");

        buildMenu();
        addListeners();
    }

    /**
     * Constrói a estrutura do menu.
     */
    private void buildMenu(){
        settingsMenu.add(volumeItem);
        settingsMenu.add(muteItem);
        
        optionsMenu.add(settingsMenu);

        menuBar.add(optionsMenu);
    }

    /**
     * Adiciona listeners aos itens do menu.
     */
    private void addListeners(){
        //Listener para abrir o diálogo de volume.
        volumeItem.addActionListener(e -> showVolumeDialog());

        //Listener para mute/unmute.
        muteItem.addActionListener(e -> {
            //Retorna true quando o check de mute está marcardo.
            boolean muted = muteItem.isSelected();
            //Se marcado desabilita o som.
            //Se desmarcado habilita o som.
            Sounds.getInstance().setSoundEnabled(!muted);

            //Volta a tocar o som de fundo (desmuta).
            if (!muted) {
                Sounds.getInstance().resumeBackgroundMusic();
            }
        });
    }

    /**
     * Exibe diálogo para ajustar o volume.
     */
    private void showVolumeDialog(){
        //Obtém volume atual (0-100).
        int currentVolume = (int) (Sounds.getInstance().getVolume() * 100);

        //Cria slider.
        JSlider volumeSlider = new JSlider(0, 100, currentVolume);
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);

        //Painel com slider.
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(new JLabel("Volume:"), BorderLayout.NORTH);
        panel.add(volumeSlider, BorderLayout.CENTER);

        //Atualiza volume em tempo real,
        volumeSlider.addChangeListener(e -> {
            float volume = volumeSlider.getValue() / 100.0f;
            Sounds.getInstance().setVolume(volume);
        });

        //Exibe diálogo.
        JOptionPane.showMessageDialog(
            null, 
            panel,
            "Adjust Volume",
            JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Retorna a barra de menu com suas respectivas configurações.
     * 
     * @return JMenuBar pronta para uso.
     */
    public JMenuBar createMenuBar(){
        return menuBar;
    }
}


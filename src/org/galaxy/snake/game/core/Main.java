package org.galaxy.snake.game.core;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.galaxy.snake.game.ui.start.StartFrame;

/**
 * Classe principal que inicia o aplicativo.
 * Ponto de entrada do programa.
 */
public class Main{
    /**
     * Método Main - ponto de entrada da aplicação.
     * @param args Argumentos da linha de comando.
     */
    public static void main(String[] args){
        //Configura Look and Feel do sistema.
        configureLookAndFeel();

        //Garante que a interface gráfica seja criada na EDT (Event Dispatch Thread).
        SwingUtilities.invokeLater(() -> {
            try{
                StartFrame startFrame = new StartFrame();
                startFrame.display();
            }catch(Exception e){
                handleStartupError(e);
            }
        });
    }

    /**
     * Configura o Look and Feel para usar o estilo nativo do sistema.
     */
    private static void configureLookAndFeel(){
        try{
            //Tenta usar o Look and Feel do sistema operacional.
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){
            //Se falhar, continua com o padrão.
            System.err.println("Não foi possível configurar o Look and Feel: " + e.getMessage());
        }
    }

    /**
     * Tratar erros durante a inicialização.
     * 
     * @param e Exceções que ocorreu. 
     */
    private static void handleStartupError(Exception e){
        //Log de erro.
        System.err.println("Erro fatal durante a inicialização: " + e.getMessage());
        e.printStackTrace();

        //Mostra diálogo para o usuário.
        JOptionPane.showMessageDialog(
            null, 
            "Erro ao iniciar o jogo:\n" + e.getMessage(),
            "Erro fatal",
            JOptionPane.ERROR_MESSAGE
        );

        //Enecerra o programa.
        System.exit(1);
    }
}
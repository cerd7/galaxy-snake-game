package org.galaxy.snake.game.logic;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.EnumMap;
import java.util.Map;

import javax.sound.sampled.*;

import org.galaxy.snake.game.core.GameConstants;

/**
 * Classe Singleton responsável por gerenciar todos os sons do jogo.
 * Garante que apenas uma instância exista e centraliza o controle de áudio.
 * 
 * Funcionalidades:
 * -Carregamento de arquivos WAV.
 * -Reprodução de músicas de fundo em loop.
 * -Reprodução de efeitos sonoros.
 * -Controle de volume.
 */

public class Sounds{
    //Log mais eficiente e personalizável.
    private static final Logger LOGGER = Logger.getLogger(Sounds.class.getName());

    // <=== SINGLETON PATTERN (Thread-Safe) ===>
    private static volatile Sounds instance;
    private static final Object LOCK = new Object();

    /**
     * Enum que define os tipos de sons disponíveis.
     */
    public enum SoundType{
        BACKGROUND,
        EXPLOSION,
        BOOST
    }

    // <=== ATRIBUTOS ===>
    private final Map<SoundType, Clip> clips;
    private float masterVolume = 0.0f; //Volume em dicibéis (0 = normal, -80 = mudo).
    private boolean soundEnabled = true;

    /**
     * Construtor privado para implementar o Singleton.
     * Carrega todos os sons necessários no início.
    */
    private Sounds(){
        //EnumMap é mais eficiente que HashMap para chaves enum.
        clips = new EnumMap<>(SoundType.class);
        loadAllSounds();
    }

    /**
     * Obtém a instância única do gerenciador de sons.
     * Se não existir, criar uma nova instância.
     * 
     * @return Instância única de Sounds
     */
    public static Sounds getInstance(){
        //Primeira verificação (sem sincronização para performance).
        if(instance == null){
            //Sincroniza apenas se precisar criar.
            synchronized(LOCK){
                //Segunda verificação (dentro do bloco sincronizado).
                if(instance == null){
                    instance = new Sounds();
                }
            }
        }
        return instance;
    }

    /**
     * Carrega todos os sons de jogo na inicialização.
     * Centraliza o tratamento de erros de carregamento.
     */
    private void loadAllSounds(){
        loadSound(SoundType.BACKGROUND, GameConstants.SOUND_BACKGROUND);
        loadSound(SoundType.EXPLOSION, GameConstants.SOUND_EXPLOSION);
        loadSound(SoundType.BOOST, GameConstants.SOUND_BOOST);
    }

    /**
     * Carrega um arquivo de som específico e o armazena no Map.
     * 
     * @param type Tipo do som (BACKGROUND, EXPLOSION, BOOST)
     * @param filePath Caminho do arquivo de áudio.
     */
    private void loadSound(SoundType type, String filePath){
        try(InputStream resourceStream = getClass().getResourceAsStream(filePath);)
        {
            // Verifica se o arquivo existe antes de tentar carregar.
            if (resourceStream == null) {
                LOGGER.log(Level.WARNING, "Arquivo de áudio não encontrado: {0}" + filePath);
                return;
            }
            try(
                //BufferedInputStream é necessário para mark/reset que o AudioSystem usa.
                BufferedInputStream bufferedStream = new BufferedInputStream(resourceStream);

                //Cria o AudioInputStream a partir do stream.
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedStream);
            ){

                //Obtém um Clip do sistema de áudio.
                Clip clip = AudioSystem.getClip();
                
                //Abre o clip com os dados de áudio.
                clip.open(audioStream);
                
                //Armazena no mapa.
                clips.put(type, clip);
            }
        }catch(UnsupportedAudioFileException e){
            LOGGER.log(Level.WARNING,"Formato de áudio não suportado: {0}" + filePath);
        }catch(IOException e){
            LOGGER.log(Level.WARNING,"Erro de I/O ao carregar áudio: {0} " + filePath);
        }catch(LineUnavailableException e){
            LOGGER.log(Level.WARNING,"Linha de áudio não disponível: {0}" + filePath);
        }
    }

    /**
     * Inicia a reprodução de música de fundo em loop contínuo.
     */
    public void playBackgroundMusic(){
        Clip music = clips.get(SoundType.BACKGROUND);
        if(music != null && !music.isRunning()){
            music.setFramePosition(0);
            applyVolume(music);
            music.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }    

    /**
     * Para a música de fundo e reseta a posição.
     */
    public void stopBackgroundMusic(){
        Clip music = clips.get(SoundType.BACKGROUND);
        if (music != null && music.isRunning()) {
            music.stop();
            music.setFramePosition(0);
        }
    }

    /**
     * Pausa à música de fundo mantendo a posição atual.
     */
    public void pauseBackgroundMusic(){
        Clip music = clips.get(SoundType.BACKGROUND);
        if(music != null && music.isRunning()){
            music.stop(); //Vai manter a posição, diferente de setFramePosition(0).
        }
    }

    /**
     * Retoma a música de fundo da posição onde parou.
     */
    public void resumeBackgroundMusic(){
        if(!soundEnabled) return;

        Clip music = clips.get(SoundType.BACKGROUND);
        if(music != null && !music.isRunning()){
            music.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /**
     * Reproduz o som de explosão.
     * Para qualquer reprodução anterior e reinicia.
     */
    public void playExplosion(){
        playOnce(SoundType.EXPLOSION);
    }

    /**
     * Reproduz o som de boost.
     */
    public void playBoost(){
        playOnce(SoundType.BOOST);
    }

    /**
     * Método genérico para reproduzir um som uma vez.
     * 
     * @param type Tipo do som a reproduzir.
     */
    private void playOnce(SoundType type){
        if(!soundEnabled) return;

        Clip clip = clips.get(type);
        if(clip != null){
            //Para a reprodução atual se houver.
            clip.stop();
            //Volta ao início.
            clip.setFramePosition(0);
            //Aplica o volume atual.
            applyVolume(clip);
            //Inicia a reprodução.
            clip.start();
        }
    }

    /**
     * Define o volume master para todos os sons.
     * 
     * @param volumePercent Porcentagem do volume (0.0 a 1.0).
     */
    public void setVolume(float volumePercent){
        //Converte porcentagem (0-1) para decibéis.
        //0% = -80dB (praticamente mudo), 100% = 0dB (volume máximo).
        if(volumePercent <= 0){
            masterVolume = -80.0f;
        }else if(volumePercent >= 1){
            masterVolume = 0.0f;
        }else{
            //Fórmula logarítimica para volume natural.
            masterVolume = (float) (20.0 * Math.log10(volumePercent));
        }

        //Aplica a todos os clips carregados.
        for(Clip clip : clips.values()){
            applyVolume(clip);
        }
    }

    /**
     * Retorna o volume atual em porcentagem.
     * 
     * @return Volume de 0.0 a 1.0.
     */
    public float getVolume(){
        //Converte de decibéis para porcentagem.
        return (float) Math.pow(10, masterVolume / 20.0);
    }

    /**
     * Aplica o volume master a um clip específico.
     * 
     * @param clip Clip de áudio.
     */
    private void applyVolume(Clip clip){
        if(clip == null) return;

        //Verifica se o controle de volume é suportado.
        if(clip.isControlSupported(FloatControl.Type.MASTER_GAIN)){
            FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        
            //Garante que está dentro dos limites do controle.
            float min = volumeControl.getMinimum();
            float max = volumeControl.getMaximum();
            float adjustedVolume = Math.max(min, Math.min(max, masterVolume));

            volumeControl.setValue(adjustedVolume);
        }
    }

    /**
     * Ativa ou desativa todos os sons.
     * 
     * @param enabled true para ativar, false para desativar.
     */
    public void setSoundEnabled(boolean enabled){
        this.soundEnabled = enabled;

        if(!enabled){
            //Para todos os sons quando desativados.
            for(Clip clip : clips.values()){
                if (clip != null && clip.isRunning()) {
                    clip.stop();
                }
            }
        }
    }

    /**
     * Verifica se o som está habilitado.
     * 
     * @return true se os sons estão ativas, e false caso não.
     */
    public boolean isSoundEnabled(){
        return soundEnabled;
    }

    /**
     * Libera todos os recursos de áudio.
     * Vai ser chamado quando o jogo for encerrado.
     */
    public void dispose(){
        for(Clip clip : clips.values()){
            if(clip != null){
                if (clip.isRunning()) {
                    clip.stop();
                }
                clip.close();
            }
        }
        clips.clear();

        //reseta singleton para consistência com Assests.
        synchronized(LOCK){
            instance = null;
        }
    }
}

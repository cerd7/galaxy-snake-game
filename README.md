# Galaxy Snake Game

Um jogo de Snake clássico desenvolvido em Java com interface gráfica modernizada, sistema de boost e efeitos sonoros. O projeto implementa padrões de arquitetura em camadas, separando completamente a lógica de jogo da apresentação visual.

## Características Principais

- **Mecânica de Jogo Traditional**: Movimento em grid baseado em tiles com detecção de colisão AABB (Axis-Aligned Bounding Box)
- **Sistema de Boost**: Aceleração temporária ativada pelo espaço, aumentando a velocidade de 85ms para 10ms e alterando o visual da cobra
- **Backgrounds Dinâmicos**: Quatro níveis de progresso visual que mudam conforme a pontuação evolui
- **Controles Responsivos**: Suporta WASD e setas direcionais com prevenção de movimentos inválidos (180 graus)
- **Áudio Integrado**: Música de fundo em loop com efeitos sonoros para explosão e boost
- **Pausa Funcional**: Freezing completo de todos os timers mantendo o estado da aplicação

## Requisitos

- Java 11 ou superior
- Sistema operacional: Windows, macOS ou Linux com display gráfico

## Construção e Execução

### Via Linha de Comando

```bash
# Compilar
javac -d bin -sourcepath src src/org/galaxy/snake/game/core/Main.java

# Executar
java -cp bin org.galaxy.snake.game.core.Main
```

### Via IDE

1. Importar projeto como Java Project
2. Configurar SDK (Java 11+)
3. Executar `Main.java` do pacote `org.galaxy.snake.game.core`

## Controles

| Entrada | Ação |
|---------|------|
| W / Seta Acima | Mover para cima |
| A / Seta Esquerda | Mover para esquerda |
| S / Seta Abaixo | Mover para baixo |
| D / Seta Direita | Mover para direita |
| Espaço | Ativar boost (800ms) |
| P | Pausar/Retomar |
| Menu > Settings > Mute | Mutar áudio |
| Menu > Settings > Volume | Ajustar volume |

## Estrutura do Projeto

```
src/org/galaxy/snake/game/
├── core/
│   ├── GameConstants.java    # Centraliza todas as constantes
│   └── Main.java              # Ponto de entrada
├── logic/
│   ├── Game.java              # Orchestração da lógica do jogo
│   ├── Snake.java             # Modelo da cobra e movimento
│   ├── Nave.java              # Modelo do alvo (comida)
│   ├── Tile.java              # Representação de posição em grid
│   ├── Events.java            # Processamento de entrada
│   └── Sounds.java            # Gerenciador de áudio (Singleton)
└── ui/
    ├── Assets.java            # Gerenciador de recursos visuais (Singleton)
    ├── game/
    │   ├── GameFrame.java     # Janela principal do jogo
    │   └── GamePanel.java     # Renderização e coordenação gráfica
    └── start/
        ├── StartFrame.java    # Janela do menu inicial
        ├── StartPanel.java    # Painel de botões (Start/Exit)
        └── StartMenu.java     # Barra de menu do lobby
```

## Arquitetura e Padrões

### Separação em Camadas

- **Core**: Configurações centralizadas e ponto de entrada
- **Logic**: Regras do jogo, modelos de entidade e processamento de eventos, completamente independente da renderização
- **UI**: Apresentação visual, gerenciamento de janelas e timers

### Padrões Implementados

**Singleton**: Utilizado em `Assets` e `Sounds` para garantir instância única e acesso centralizado aos recursos durante o ciclo de vida da aplicação.

**Observer Pattern**: `Game.GameListener` permite que a camada UI reaja aos eventos do jogo (gameOver, reset, pontuação) sem acoplamento direto.

**Enum Patterns**: Utilizados em `SoundType` e `BackgroundLevel` para type-safety e facilitar manutenção de enumerações.

## Fluxo de Execução

1. `Main.java` inicializa a Look-and-Feel nativa e dispara `StartFrame` na EDT (Event Dispatch Thread)
2. Usuário clica em "START" - `StartPanel` cria `GameFrame` e monitora sua inicialização
3. `GameFrame` instancia `GamePanel` que cria `Game` e `Events`
4. Dois timers são iniciados:
   - Timer de renderização (16ms ~60fps) dispara `paintComponent()`
   - Timer de movimento da cobra (50-10ms conforme boost) atualiza posição
5. `Events` processa entrada do teclado e invoca métodos de movimento
6. `Game.updateGame()` verifica colisões e notifica listener
7. Ao detectar gameOver, dialog é exibido via Swing (thread-safe)

## Detalhes Técnicos das Colisões

### AABB (Axis-Aligned Bounding Box)

O sistema de colisão utiliza verificação retangular simples:

```
Colisão ocorre quando:
head.x < nave.x + nave.width  AND
head.x + head.width > nave.x  AND
head.y < nave.y + nave.height AND
head.y + head.height > nave.y
```

Este método é eficiente O(1) para verificação de colisão nave vs cabeça.

### Verificação de Corpo

A colisão com o próprio corpo itero sobre todos os segmentos comparando posições de grid (não pixels), prevenindo assim auto-intersecção.

### Limites da Tela

Limites em pixels (não em tiles) para permitir maior granularidade:
- Y mínimo (superior): 75px (abaixo do HUD)
- Y máximo (inferior): 590px

## Sistema de Boost

O boost funciona mediante um mecanismo de mudança de delay:

- Velocidade normal: 85ms entre movimentos
- Velocidade com boost: 10ms entre movimentos
- Duração: 800ms
- Multiplicador visual: 8.5x (85/10)

Timer de boost é gerenciado por `Events` e sincronizado com `GameTimer` para consistência.

## Gerenciamento de Recursos

### Assets

Pré-carrega todos os backgrounds no cache durante inicialização para evitar I/O durante gameplay. Imagens são redimensionadas uma única vez.

### Sounds

Implementa pool de clips de áudio. Música de fundo utiliza loop contínuo enquanto efeitos de explosão e boost são reproduzidos com stop/play para reinicialização.

### Threads

A aplicação respeita o modelo EDT de Swing:
- Toda manipulação de UI ocorre na Event Dispatch Thread
- `SwingUtilities.invokeLater()` é utilizado para operações iniciais
- Timers já dispararem na EDT automaticamente

## Configurações do Jogo

Todas as constantes estão centralizadas em `GameConstants.java`:

| Constante | Valor | Função |
|-----------|-------|--------|
| SCREEN_WIDTH | 1000 | Resolução horizontal |
| SCREEN_HEIGHT | 600 | Resolução vertical |
| TILE_SIZE | 15 | Dimensão base do grid |
| SNAKE_TILE_SIZE | 10 | Render da cobra |
| NAVE_TILE_SIZE | 35 | Render do alvo |
| NORMAL_SPEED | 85ms | Delay entre movimentos |
| BOOST_SPEED | 10ms | Delay com boost |
| BOOST_DURATION | 800ms | Duração do boost |

Modificar essas constantes altera o comportamento do jogo globalmente.

## Desenvolvimento

### Adicionando Novos Efeitos Sonoros

1. Adicionar arquivo WAV em `resources/sounds/wav/`
2. Adicionar opção ao enum `SoundType` em Sounds.java
3. Carregar som em `loadAllSounds()`
4. Invocar `playSound(SoundType.NOVO_SOM)` conforme necessário

### Adicionando Novos Backgrounds

1. Adicionar imagem em `resources/images/backgrounds/`
2. Adicionar constante em `GameConstants.java`
3. Adicionar nível ao enum `BackgroundLevel` em Assets.java
4. Modificar lógica de progressão em `Game.notifyScoreChanged()`

### Ajustes de Balanceamento

Todos os valores numéricos podem ser ajustados em `GameConstants.java` sem recompilação dependendo da IDE utilizada (hot reload).

## Tratamento de Erros

A aplicação implementa tratamento centralizado de erros:

- Erros de startup disparam diálogo e encerram com `System.exit(1)`
- Erros de carregamento de recursos são logados via `java.util.logging.Logger`
- Exceções de áudio não interrompem a execução do jogo
- Game over é processado na EDT para segurança de thread

## Instalação em JAR

Para distribuir como executável:

```bash
# Compilar com manifest
jar cvfm GalaxySnake.jar manifest.mf -C bin org/

# Executar
java -jar GalaxySnake.jar
```

Arquivo `manifest.mf`:
```
Manifest-Version: 1.0
Main-Class: org.galaxy.snake.game.core.Main
```

## Licença

Projeto de estudo. Freely distributable.

## Autor

Desenvolvido como exercício prático de desenvolvimento de aplicações Swing em Java com foco em arquitetura em camadas e padrões de design.

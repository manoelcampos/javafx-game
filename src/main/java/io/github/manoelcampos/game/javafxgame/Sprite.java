package io.github.manoelcampos.game.javafxgame;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

/// Gerencia um game sprite, que é um arquivo de imagem
/// contendo várias figuras de um personagem em posições diferentes,
/// que exibidas em sequência, dão a impressão de movimento do personagem.
///
/// A classe permite carregar sprites a partir de imagens PNG na
/// pasta {@link #SPRITES_DIR} dentro de /resources.
///
/// Formato suportado - arquivo único por personagem:
///    src/main/resources/sprites/<nome>.png
///    - Grade com 4 linhas (ordem de linhas: → ← ↑ ↓) e N colunas (N frames por direção)
///    - Cada célula é quadrada (tileWidth == tileHeight)
///    - tileHeight = altura total / 4; tileWidth = tileHeight
///
/// Caso nenhum dos formatos seja encontrado, o loader gera frames placeholders
/// em memória para manter o protótipo funcional.
public class Sprite {
    public static final String SPRITES_DIR = "/sprites/";

    private final Map<Direction, Image[]> frames = new EnumMap<>(Direction.class);
    /** Nome do arquivo de sprite (sem a extensão) a ser carregado. */
    private final String name;
    private boolean mirrorLeftFromRight = false;

    /**
     * Cria um objeto para gerenciar as imagens de um
     * arquivo de sprit.
     * @param name Nome do arquivo de sprit, sem extensão.
     */
    public Sprite(final String name) {
        this.name = name;
        tryLoadSpriteSheet();
    }

    /**
     * {@return a lista de figuras do personagem quando ele estiver
     * se movimentando em uma determinada direção, para
     * simular o movimento naquela direção pela troca
     * sequencial dessas figuras}
     * @param direction direção do movimento
     */
    public Image[] getFrames(final Direction direction) {
        return frames.getOrDefault(direction, new Image[0]);
    }

    /**
     * Indica se devemos espelhar a direção esquerda (E) a partir
     * dos frames da direita (D) quando E não existir no resources.
     */
    public boolean shouldMirrorLeftFromRight() {
        return mirrorLeftFromRight;
    }

    /**
     * Tenta carregar sprites a partir de um spritesheet único em /sprites/<nome>.png.
     * Espera 4 linhas (→ ← ↑ ↓) e N colunas de frames; cada célula quadrada.
     */
    private void tryLoadSpriteSheet() {
        final var sheetPath = SPRITES_DIR + name + ".png";
        final Image sheet = tryLoadImage(sheetPath);
        if (sheet == null)
            return;

        final int sheetW = (int) Math.round(sheet.getWidth());
        final int sheetH = (int) Math.round(sheet.getHeight());
        if (sheetW <= 0 || sheetH <= 0 || sheetH % 4 != 0) {
            return;
        }

        final int tileH = sheetH / 4;
        final int tileW = tileH; // célula quadrada
        if (tileW <= 0 || sheetW < tileW)
            return;

        final int cols = sheetW / tileW;
        if (cols <= 0)
            return;

        final Direction[] lines = new Direction[]{Direction.RIGHT, Direction.LEFT, Direction.UP, Direction.DOWN};
        final var pr = sheet.getPixelReader();
        if (pr == null)
            return;

        for (int r = 0; r < 4; r++) {
            final var frameLines = new ArrayList<>();
            for (int c = 0; c < cols; c++) {
                int x = c * tileW;
                int y = r * tileH;

                // Evitar sair fora, caso a última coluna não complete o tile
                if (x + tileW > sheetW || y + tileH > sheetH)
                    break;

                final var sub = new WritableImage(pr, x, y, tileW, tileH);
                frameLines.add(sub);
            }

            frames.put(lines[r], frameLines.toArray(new Image[0]));
        }

        // Se a linha E ficou vazia, reutiliza D com espelhamento
        if (!hasFrame(Direction.LEFT) && hasFrame(Direction.RIGHT)) {
            frames.put(Direction.LEFT, frames.get(Direction.RIGHT));
            mirrorLeftFromRight = true;
        }
    }

    /**
     * Verifica se existe imagem para o movimento do personagem
     * em um determinada direção.
     * @param d direção atual do movimento
     * @return
     */
    private boolean hasFrame(final Direction d) {
        return frames.get(d) == null || frames.get(d).length == 0;
    }

    private Image tryLoadImage(final String resourcePath) {
        // 1) Classpath
        InputStream is = getClass().getResourceAsStream(resourcePath);
        if (is != null) {
            return new Image(is);
        }

        // 2) Fallback: caminhos de desenvolvimento (executando via Maven/IDE)
        //    Tenta ler diretamente do sistema de arquivos, caso os PNGs tenham sido
        //    gerados durante a execução, mas ainda não copiados para target/classes.
        var image = getImageFromFilePath("src/main/resources", resourcePath);
        if (image != null)
            return image;

        return getImageFromFilePath("target/classes", resourcePath);
    }

    private static Image getImageFromFilePath(final String dir, final String resourcePath) {
        Path p1 = Paths.get(dir + resourcePath);
        if (Files.exists(p1)) {
            try (InputStream fis = Files.newInputStream(p1)) {
                return new Image(fis);
            } catch (IOException _) {
                // retorna null se não conseguiu abrir o arquivo
            }
        }

        return null;
    }
}

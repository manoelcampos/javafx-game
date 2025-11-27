package io.github.manoelcampos.game.javafxgame;

import javafx.animation.*;

import static javafx.util.Duration.millis;

/**
 * Personagens que se movimentam "pulando".
 * Além de transladar, realiza um zoom in/out durante o movimento.
 */
public class Jumper extends Persona {
    /**
     * Zoom in/out preservando a orientação horizontal atual e sincronizando
     * a duração total do zoom com a translação (~260ms no total).
     * 1.8= 80% de zoom para simular um pulo
    */
    private static final double ZOOM_FACTOR = 1.8;

    // Guarda a última animação de pulo para evitar sobreposição e acúmulo de escala
    private ParallelTransition currentJump;

    /**
     * Crie um personagem que se movimenta pulando.
     * @param spriteName nome do arquivo da imagem em {@link Sprite#SPRITES_DIR}, sem extensão.
     */
    public Jumper(final double x, final double y, final String spriteName) {
        this(x, y, new Sprite(spriteName));
    }

    private Jumper(final double x, final double y, final Sprite sprite) {
        super(x, y, sprite);
    }

    @Override
    protected void moveInternal(final Direction d, final double[] coords) {
        // Preserva a orientação (espelhamento) em X: -1 para esquerda quando espelhando, 1 caso contrário
        final double signX = getSignX();
        imageView.setScaleX(signX);
        imageView.setScaleY(1.0);

        final var translate = new TranslateTransition(millis(260), imageView);
        translate.setToX(coords[0]);
        translate.setToY(coords[1]);
        translate.setInterpolator(Interpolator.EASE_OUT);

        // Duas transições em sequência (in/out) para evitar drift de escala
        final var zoomIn = new ScaleTransition(millis(130), imageView);
        zoomIn.setFromX(signX);
        zoomIn.setToX(signX * ZOOM_FACTOR);
        zoomIn.setFromY(1.0);
        zoomIn.setToY(ZOOM_FACTOR);
        zoomIn.setInterpolator(Interpolator.EASE_BOTH);

        final var zoomOut = new ScaleTransition(millis(130), imageView);
        zoomOut.setFromX(signX * ZOOM_FACTOR);
        zoomOut.setToX(signX);
        zoomOut.setFromY(ZOOM_FACTOR);
        zoomOut.setToY(1.0);
        zoomOut.setInterpolator(Interpolator.EASE_BOTH);

        final var zoomSeq = new SequentialTransition(zoomIn, zoomOut);
        currentJump = new ParallelTransition(translate, zoomSeq);
        resetZoomOnFinish(signX);

        currentJump.play();
    }

    private double getSignX() {
        return imageView.getScaleX() < 0 ? -1.0 : 1.0;
    }

    /**
     * Garante que o personagem volte ao baseline após o pulo.
     * @param signX
     */
    private void resetZoomOnFinish(final double signX) {
        currentJump.setOnFinished(ev -> {
            imageView.setScaleX(signX);
            imageView.setScaleY(1.0);
            currentJump = null;
        });
    }

    @Override
    protected void stopPreviousMoveEffect() {
        // Evita acúmulo de zoom entre pulos: para animação anterior, se houver, e
        // normaliza a escala para um baseline previsível antes de iniciar um novo pulo.
        if (currentJump != null) {
            currentJump.stop();
            currentJump = null;
        }
    }
}

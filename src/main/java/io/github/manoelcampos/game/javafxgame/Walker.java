package io.github.manoelcampos.game.javafxgame;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;

import static javafx.util.Duration.millis;

/**
 * Personagens que se movimentam "andando".
 * A animação troca o frame do sprite e translada o personagem alguns pixels.
 */
public class Walker extends Persona {
    /**
     * Crie um personagem que se movimenta andando.
     * @param spriteName nome do arquivo da imagem em {@link Sprite#SPRITES_DIR}, sem extensão.
     */
    public Walker(final double x, final double y, final String spriteName) {
        this(x, y, new Sprite(spriteName));
    }

    private Walker(final double x, final double y, final Sprite sprite) {
        super(x, y, sprite);
    }

    @Override
    protected void moveInternal(final Direction d, final double[] coords) {
        final var translate = new TranslateTransition(millis(180), imageView);
        translate.setToX(coords[0]);
        translate.setToY(coords[1]);
        translate.setInterpolator(Interpolator.EASE_BOTH);
        translate.play();
    }

    @Override
    protected void stopPreviousMove() {
        /* Não é preciso fazer nenhuma operação para
        * interromper o movimento anterior de um personagem
        * que apenas caminha. */
    }
}

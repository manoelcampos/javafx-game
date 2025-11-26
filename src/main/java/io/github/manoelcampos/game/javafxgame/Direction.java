package io.github.manoelcampos.game.javafxgame;

import javafx.scene.input.KeyCode;

/**
 * Direções possíveis de movimento de um personagem.
 */
public enum Direction {
    LEFT(-1, 0),
    RIGHT(1, 0),
    UP(0, -1),
    DOWN(0, 1);

    /**
     * Distância horizontal do movimento, negativo ou positivo
     * para indicar, respectivamente, se o movimento é pra trás ou pra frente.
     */
    public final int dx;

    /**
     * Distância vertical do movimento, negativo ou positivo
     * para indicar, respectivamente, se o movimento é pra cima ou pra baixo.
     */
    public final int dy;

    Direction(final int dx, final int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Converte um caractere de direção em {@link Direction}.
     */
    public static Direction from(final KeyCode pressedKey) {
        return switch (pressedKey) {
            case KeyCode.LEFT -> Direction.LEFT;
            case KeyCode.RIGHT -> Direction.RIGHT;
            case KeyCode.UP -> Direction.UP;
            case KeyCode.DOWN -> Direction.DOWN;
            default -> Direction.RIGHT;
        };
    }
}

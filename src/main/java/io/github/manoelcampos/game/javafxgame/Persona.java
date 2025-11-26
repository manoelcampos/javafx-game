package io.github.manoelcampos.game.javafxgame;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

/**
 * Superclasse abstrata para personagens do jogo.
 * Demonstra polimorfismo dinâmico com o método abstrato {@link #moveInternal(Direction, double[])}.
 */
public abstract class Persona {
    protected final ImageView imageView;
    /**
     * Objeto que
     */
    protected final Sprite sprite;
    protected int frameIndex = 0;
    protected double step = 48; // distância em pixels por movimento
    protected double x;
    protected double y;

    private final DropShadow activeFx = new DropShadow(20, Color.web("#00ffcc"));

    protected Persona(final double x, final double y, final Sprite sprite) {
        this.x = x;
        this.y = y;
        this.sprite = sprite;
        this.imageView = new ImageView();
        this.imageView.setSmooth(true);
        this.imageView.setPreserveRatio(true);
        this.imageView.setFitWidth(64);
        this.imageView.setFitHeight(64);
        updateImage(Direction.RIGHT); // imagem inicial
        this.imageView.setTranslateX(x);
        this.imageView.setTranslateY(y);
    }

    public String getName(){
        return sprite.getName();
    }

    public Node getNode() {
        return imageView;
    }

    public void setActive(final boolean active) {
        imageView.setEffect(active ? activeFx : null);
        imageView.setOpacity(active ? 1.0 : 0.85);
    }

    /**
     * Atualiza a imagem do personagem atual, conforme
     * a direção para onde ele está indo,
     * para simular o movimento.
     * @param d
     */
    private void updateImage(final Direction d) {
        final Image[] frames = sprite.getFrames(d);
        if (frames.length == 0)
            return;

        frameIndex = (frameIndex + 1) % frames.length;
        imageView.setImage(frames[frameIndex]);

        // Espelhamento somente quando não houver frames específicos para ←
        // e estivermos reutilizando os frames para →
        if (d == Direction.LEFT)
            imageView.setScaleX(sprite.shouldMirrorLeftFromRight() ? -1 : 1);
        else if (d == Direction.RIGHT)
            imageView.setScaleX(1);
    }

    /**
     * {@return A nova posição x para o movimento na direção indicada}
     * @param d direção do movimento
     */
    protected double newX(final Direction d) { return imageView.getTranslateX() + d.dx * step; }

    /**
     * {@return A nova posição y para o movimento na direção indicada}
     * @param d direção do movimento
     */
    protected double newY(final Direction d) { return imageView.getTranslateY() + d.dy * step; }

    /**
     * Limita o destino do personagem à área visível da cena.
     * @return as coordenadas x,y limites onde o personagem
     *         pode ser posicionado para continuar visível
     */
    private double[] clampToScene(final double nx, final double ny) {
        if (imageView.getScene() == null)
            return new double[]{ nx, ny };

        final double w = imageView.getScene().getWidth();
        final double h = imageView.getScene().getHeight();
        final double vw = imageView.getFitWidth();
        final double vh = imageView.getFitHeight();

        final double minX = 0;
        final double minY = 0;
        final double maxX = Math.max(0, w - vw);
        final double maxY = Math.max(0, h - vh);

        final double cx = Math.min(Math.max(nx, minX), maxX);
        final double cy = Math.min(Math.max(ny, minY), maxY);

        return new double[]{ cx, cy };
    }

    /**
     * Faz o personagem se mover.
     * As subclasses implementam comportamentos distintos (andar vs pular).
     * O método só é chamado no {@link StartApplication}
     * se for pressionada uma tecla de seta no teclado.
     * @param arrowKeyPressed tecla de seta pressionada pelo usuário
     */
    public final void move(final KeyCode arrowKeyPressed){
        final var d = Direction.from(arrowKeyPressed);
        // Troca a imagem do sprite para simular passos
        updateImage(d);
        stopPreviousMove();
        final double[] coords = clampToScene(newX(d), newY(d));
        moveInternal(d, coords);
    }

    /**
     * Realiza as animações para simular que o personagem
     * está de fato se movimentando.
     * As subclasses implementam animações para comportamentos distintos
     * (andar vs pular).
     *
     * @param d      direção para onde o personagem vai se mover
     * @param coords coordenadas x, y para onde o personagem deve ir
     */
    protected abstract void moveInternal(Direction d, double[] coords);

    /**
     * Operações a serem executadas para interromper
     * a animação de movimento anterior,
     * se necessário.
     */
    protected abstract void stopPreviousMove();
}

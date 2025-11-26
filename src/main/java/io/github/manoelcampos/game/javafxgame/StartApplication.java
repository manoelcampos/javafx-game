package io.github.manoelcampos.game.javafxgame;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class StartApplication extends Application {
    private Label instructions;
    private Walker chicken;
    private Jumper frog;
    private Persona activePersona;

    @Override
    public void start(final Stage stage) {
        final double width = 900;
        final double height = 600;

        final var world = new Pane();
        world.setPrefSize(width, height);

        // Cria dois personagens: galinha (anda) e sapo (pula)
        final double y = height / 2;
        this.chicken = new Walker(100, y, "chicken");
        this.frog = new Jumper(300, y, "frog");

        // Destacar personagem ativo
        this.activePersona = chicken;
        chicken.setActive(true);

        world.getChildren().addAll(chicken.getNode(), frog.getNode());

        // Legenda para troca de personagem ativo
        this.instructions = new Label(getActiveText());
        instructions.setTextFill(Color.WHITE);
        final var box = new StackPane(instructions);
        box.setAlignment(Pos.TOP_LEFT);
        box.setMouseTransparent(true);
        box.setPickOnBounds(false);
        box.setPrefWidth(width);
        box.setPrefHeight(height);

        // Fundo simples
        var background = new Rectangle(width, height, Color.DARKSLATEGRAY);

        var root = new Group(background, world, box);
        var scene = new Scene(root, width, height);

        scene.setOnKeyPressed(this::onkeyPressed);

        stage.setTitle("POO + Polimorfismo Dinâmico: Andar vs Pular");
        stage.setScene(scene);
        stage.show();

        // Garantir foco para capturar as teclas
        root.requestFocus();
    }

    private String getActiveText() {
        return "Ativo: %s (Tab alterna)".formatted(activePersona.getName());
    }

    private void onkeyPressed(final KeyEvent e) {
        final KeyCode code = e.getCode();
        if (code == KeyCode.TAB) {
            activePersona.setActive(false);
            activePersona = activePersona == chicken ? frog : chicken;
            activePersona.setActive(true);
            instructions.setText(getActiveText());
            e.consume();
            return;
        }

        if (isDirectionKey(code)) {
            activePersona.move(code);
            e.consume();
        }
    }

    private static boolean isDirectionKey(final KeyCode code) {
        return code == KeyCode.LEFT || code == KeyCode.RIGHT || code == KeyCode.UP || code == KeyCode.DOWN;
    }
}

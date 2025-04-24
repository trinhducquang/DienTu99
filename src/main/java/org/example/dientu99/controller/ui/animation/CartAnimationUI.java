package org.example.dientu99.controller.ui.animation;

import javafx.animation.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class CartAnimationUI {

    private static final int CART_WIDTH = 300;
    private static final Duration ANIMATION_DURATION = Duration.millis(150);

    private final StackPane cartPane;
    private final VBox cartBox;

    private Timeline slideInTimeline;
    private Timeline slideOutTimeline;

    public CartAnimationUI(StackPane cartPane, VBox cartBox) {
        this.cartPane = cartPane;
        this.cartBox = cartBox;
    }

    public void initCartSlideAnimation() {
        cartBox.setPrefWidth(CART_WIDTH);
        cartBox.setTranslateX(CART_WIDTH);

        slideInTimeline = new Timeline(
                new KeyFrame(ANIMATION_DURATION,
                        new KeyValue(cartBox.translateXProperty(), 0, Interpolator.EASE_BOTH)
                )
        );

        slideOutTimeline = new Timeline(
                new KeyFrame(ANIMATION_DURATION,
                        new KeyValue(cartBox.translateXProperty(), CART_WIDTH, Interpolator.EASE_BOTH)
                )
        );
        slideOutTimeline.setOnFinished(event -> cartPane.setVisible(false));
    }

    public void toggleCart() {
        if (!cartPane.isVisible()) {
            cartPane.setVisible(true);
            slideInTimeline.play();
        } else {
            slideOutTimeline.play();
        }
    }

    public void closeCart() {
        slideOutTimeline.play();
    }
}
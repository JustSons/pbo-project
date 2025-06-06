package io.github.some_example_name.interfaces;

import io.github.some_example_name.entities.Player; // Perlu import Player

public interface Interactable {
    void interact(Player player);
    String getName();
}

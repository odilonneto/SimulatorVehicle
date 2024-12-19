package com.mygdx.game;

public class Bike extends Veiculo {
    public Bike(int id, double valor, int qtdRodas) {
        super(id, valor, qtdRodas, "bike.png");
    }

    @Override
    public void moverVeiculo() {
        mover(5, 0); // Move para a direita
    }
}

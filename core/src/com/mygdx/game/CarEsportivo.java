package com.mygdx.game;

public class CarEsportivo extends VeiculoMotorizado {
    private static final double GASTO_COMBUSTIVEL = 2.3;

    public CarEsportivo(int id, double valor, int qtdRodas) {
        super(id, valor, qtdRodas, "car.png", 50.0); // Caminho para a textura
    }

    @Override
    public void moverVeiculo() {
        if (getCombustivel() >= GASTO_COMBUSTIVEL && isIpvaPago()) {
            mover(10, 0); // Move para a direita
            consumirCombustivel(GASTO_COMBUSTIVEL);
        }
    }
}

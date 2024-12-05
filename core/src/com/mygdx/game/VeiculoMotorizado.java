package com.mygdx.game;

public abstract class VeiculoMotorizado extends Veiculo {
    private double combustivel;
    private boolean ipvaPago;

    public VeiculoMotorizado(int id, double valor, int qtdRodas, String caminhoTextura, double combustivelInicial) {
        super(id, valor, qtdRodas, caminhoTextura);
        this.combustivel = combustivelInicial;
        this.ipvaPago = false;
    }

    public void consumirCombustivel(double quantidade) {
        this.combustivel -= quantidade;
    }

    public double getCombustivel() {
        return combustivel;
    }

    public boolean isIpvaPago() {
        return ipvaPago;
    }

    public void pagarIpva() {
        this.ipvaPago = true;
    }

    @Override
    public abstract void moverVeiculo(); // Implementação nas subclasses
}


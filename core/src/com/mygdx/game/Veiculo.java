package com.mygdx.game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Veiculo {
    private int id;
    private int qtdRodas;
    private int distancia;
    private double valor;
    private Texture textura; // Adicionando a textura
    private float x, y; // Coordenadas na tela

    public Veiculo(int id, double valor, int qtdRodas, String caminhoTextura) {
        this.id = id;
        this.valor = valor;
        this.qtdRodas = qtdRodas;
        this.textura = new Texture(caminhoTextura); // Carregando a textura
        this.x = 0; // Posição inicial
        this.y = 0;
    }

    public void render(SpriteBatch batch) {
        batch.draw(textura, x, y);
    }

    public void mover(float dx, float dy) {
        this.x += dx;
        this.y += dy;
        distancia++;
    }

    public void dispose() {
        textura.dispose(); // Libera recursos
    }

    public int getId() {
        return id;
    }

    public double getValor() {
        return valor;
    }

    public int getDistancia() {
        return distancia;
    }

    public abstract void moverVeiculo();
}

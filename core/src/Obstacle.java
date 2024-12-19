import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Obstacle {
    public float x, y;
    private Texture textura;

    public Obstacle(float x, float y) {
        this.x = x;
        this.y = y;
        this.textura = new Texture("obstaculo.png");
    }

    public void render(SpriteBatch batch) {
        batch.draw(textura, x, y);
    }

    public void mover(float delta) {
        x -= 200 * delta; // Movimento para a esquerda
    }
}

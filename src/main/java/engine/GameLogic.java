package engine;

public interface GameLogic {

    void init();

    void input(Window window);

    void update(double delta);

    void render(Window window);

    void cleanup();
}

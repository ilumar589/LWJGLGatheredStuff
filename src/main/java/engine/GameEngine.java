package engine;

public class GameEngine implements Runnable {

    public static final int TARGET_FPS = 75;
    public static final int TARGET_UPS = 30;

    private final Window window;
    private final Thread gameLoopThread;
    private final Timer timer;
    private final GameLogic gameLogic;

    public GameEngine(final String windowTitle, int width, int height, boolean vSync, final GameLogic gameLogic) {
        this.gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
        this.window = new Window(windowTitle, width, height, vSync);
        this.gameLogic = gameLogic;
        this.timer = new Timer();
    }

    public void start() {
        String osName = System.getProperty("os.name");
        if ( osName.contains("Mac") ) {
            gameLoopThread.run();
        } else {
            gameLoopThread.start();
        }
    }

    @Override
    public void run() {
        init();
        gameLoop();
        cleanup();
    }

    private void init() {
        window.init();
        timer.init();
        gameLogic.init();
    }

    private void gameLoop() {
        double elapsedTime;
        double accumulator = 0d;
        double interval = 1f / TARGET_UPS;

        while (!window.windowShouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            input();

            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }

            render();

            if (!window.isvSync()) {
                sync();
            }
        }
    }

    private void input() {
        gameLogic.input(window);
    }

    private void update(double delta) {
        gameLogic.update(delta);
    }

    private void render() {
        gameLogic.render(window);
        window.update();
    }

    private void cleanup() {
        gameLogic.cleanup();
    }

    private void sync() {
        double loopSlot = 1f / TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored){}
        }
    }
}

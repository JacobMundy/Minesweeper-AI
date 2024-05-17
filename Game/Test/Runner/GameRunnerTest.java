package Runner;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameRunnerTest {

    @Test
    void startGame() {
        GameRunner gameRunner = new GameRunner("RandomPlayer");
        assertDoesNotThrow(() -> gameRunner.startGame(false));
    }

    @Test
    void startGameWithDisplay() throws InterruptedException {
        GameRunner gameRunner = new GameRunner("StrategyPlayer");
        gameRunner.startGame(true);
    }
}
package Players;

import java.util.ArrayList;

public abstract class Player {
    ArrayList<int[]> moves = new ArrayList<>();
    public abstract void play();
    protected abstract void makeMove();
    ArrayList<int[]> getMoves() {
        return moves;
    }


}

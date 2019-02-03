package gomoku.core;

public class Win {
    public final char player;
    public final int minX;
    public final int minY;
    public final int maxX;
    public final int maxY;

    public Win(char player, int minX, int minY, int maxX, int maxY) {
        this.player = player;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }
}

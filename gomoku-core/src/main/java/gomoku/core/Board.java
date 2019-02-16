package gomoku.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Board {
    public static final int SIZE = 15;
    public static final int WIN_SIZE = 5;

    public static final char P1 = 'x';
    public static final char P2 = 'o';
    public static final char EMPTY = '.';

    private char currentPlayer;
    private final char[][] cells;
    private Win win;
    private Set<Integer> moves = new HashSet<>();

    public static char nextPlayer(char player) {
        return player == P1 ? P2 : P1;
    }

    public Board() {
        this.cells = new char[SIZE][SIZE];
        clear();
    }

    public Board(Board board) {
        this.cells = board.cells.clone();
        this.moves = new HashSet<>(board.moves);
    }

    public int getSize() {
        return SIZE;
    }

    public Win getWin() {
        return win;
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public Set<Integer> getMoves() {
        return moves;
    }

    public void clear() {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                cells[x][y] = EMPTY;
            }
        }
        win = null;
        currentPlayer = P1;
    }

    public boolean isValidMove(int x, int y) {
        return win == null && x >= 0 && x < SIZE && y >= 0 && y < SIZE && cells[x][y] == EMPTY;
    }

    public boolean makeMove(int x, int y) {
        if (isValidMove(x, y)) {
            setValue(x, y, currentPlayer);
            currentPlayer = nextPlayer(currentPlayer);
            findWinner(x, y);
            return true;
        }
        return false;
    }

    public void clearValue(int x, int y) {
        setValue(x, y, EMPTY);
    }

    //return value at position (x,y)
    public char getValue(int x, int y) {
        return cells[x][y];
    }

    //put X or O at clear the cell at position (x,y)
    public void setValue(int x, int y, char player) {
        if (player == P1 || player == P2) {
            cells[x][y] = player;
            findMoves(x, y);
            moves.remove(move(x, y));
        } else {
            throw new IllegalArgumentException("player");
        }
    }

    public String print() {
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                sb.append(getValue(x, y));
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public Win findWinner(int x, int y) {
        win = this.checkCell(x, y, -1, 0, 1, 0);
        if (win == null) {
            win = this.checkCell(x, y, 0, -1, 0, 1);
        }
        if (win == null) {
            win = this.checkCell(x, y, -1, 1, 1, -1);
        }
        if (win == null) {
            win = this.checkCell(x, y, -1, -1, 1, 1);
        }
        return win;
    }

    private void findMoves(int cx, int cy) {
//        long start = System.nanoTime();
        int rad = 2;
        int x1 = Math.max(0, cx - rad);
        int y1 = Math.max(0, cy - rad);
        int x2 = Math.min(SIZE - 1, cx + rad);
        int y2 = Math.min(SIZE - 1, cy + rad);
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                if (cells[x][y] == EMPTY) {
                    moves.add(move(x, y));
                }
            }
        }
//        long end = System.nanoTime();
//        findMovesTime += (end - start);
    }

    private Win checkCell(int x, int y, int dx1, int dy1, int dx2, int dy2) {
        char player = this.getValue(x, y);
        if (player == EMPTY) {
            throw new IllegalStateException("Empty cell at " + x + "," + y);
        }
        int length = 0;
        int minX = SIZE;
        int minY = SIZE;
        int maxX = 0;
        int maxY = 0;
        {
            int tx = x;
            int ty = y;
            while (tx >= 0 && tx < SIZE && ty >= 0 && ty < SIZE) {
                if (this.getValue(tx, ty) == player) {
                    length++;
                    minX = Math.min(minX, tx);
                    minY = Math.min(minY, ty);
                    maxX = Math.max(maxX, tx);
                    maxY = Math.max(maxY, ty);
                } else {
                    break;
                }
                tx += dx1;
                ty += dy1;
            }
        }
        {
            int tx = x + dx2;
            int ty = y + dy2;
            while (tx >= 0 && tx < SIZE && ty >= 0 && ty < SIZE) {
                if (this.getValue(tx, ty) == player) {
                    length++;
                    minX = Math.min(minX, tx);
                    minY = Math.min(minY, ty);
                    maxX = Math.max(maxX, tx);
                    maxY = Math.max(maxY, ty);
                } else {
                    break;
                }
                tx += dx2;
                ty += dy2;
            }
        }
        if (length >= WIN_SIZE) {
            return new Win(player, minX, minY, maxX, maxY);
        } else {
            return null;
        }
    }


    public static int move(int x, int y) {
        return y * SIZE + x;
    }

}
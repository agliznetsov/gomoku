package gomoku.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Board {
    public static final int N = 15;
    public static final int M = 5;

    public static final char P1 = 'x';
    public static final char P2 = 'o';
    public static final char EMPTY = '.';

    private Set<Integer> moves = new HashSet<>();
    private final char[][] board = new char[N][N];
    final int width = N;
    final int height = N;
    final int winSize = M;
    private Win win;

    public static char nextPlayer(char player) {
        return player == P1 ? P2 : P1;
    }

    public Board() {
        clear();
    }

    public void clear() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                board[i][j] = EMPTY;
            }
        }
        moves.clear();
        moves.add(move(N / 2, N / 2));
        win = null;
    }

    public void makeMove(int move, char player) {
        int x = move % this.width;
        int y = move / this.width;
        setValue(x, y, player);
        findWinner(x, y);
        findMoves(x, y);
    }

    public int move(int x, int y) {
        return y * width + x;
    }

    public void clearValue(int x, int y) {
        setValue(x, y, EMPTY);
    }

    //return value at position (i,j)
    public char getValue(int i, int j) {
        return board[i][j];
    }

    //put X or O at clear the cell at position (x,y)
    public void setValue(int x, int y, char player) {
        board[x][y] = player;
    }

    public Set<Integer> getMoves() {
        return moves;
    }

    public String print() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                sb.append(getValue(i, j));
            }
            sb.append('\n');
        }
        return sb.toString();
    }


    public Win randomPlayout(char player) {
        while (!this.moves.isEmpty()) {
            int moveIndex = (int) (Math.random() * moves.size());
            int move = getMove(moveIndex);
            makeMove(move, player);
            if (win != null) {
                return win;
            } else {
                player = Board.nextPlayer(player);
            }
        }
        return win;
    }

    // ---------------------------- private -----------------------------------------


    private void findMoves(int cx, int cy) {
        int rad = 2;
        int x1 = Math.max(0, cx - rad);
        int y1 = Math.max(0, cy - rad);
        int x2 = Math.min(this.width - 1, cx + rad);
        int y2 = Math.min(this.height - 1, cy + rad);
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                int move = move(x, y);
                if (this.board[x][y] == EMPTY) {
                    this.moves.add(move);
                } else {
                    this.moves.remove(move);
                }
            }
        }
    }

    private void findWinner(int x, int y) {
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
    }

    private Win checkCell(int x, int y, int dx1, int dy1, int dx2, int dy2) {
        char player = this.getValue(x, y);
        if (player == EMPTY) {
            throw new IllegalStateException("Empty cell at " + x + "," + y);
        }
        int length = 0;
        int tx = x;
        int ty = y;
//        int cells = {};
        while (tx >= 0 && tx < this.width && ty >= 0 && ty < this.height) {
            if (this.getValue(tx, ty) == player) {
                length++;
//                cells[this.index(tx, ty)] = true;
            } else {
                break;
            }
            tx += dx1;
            ty += dy1;
        }
        int tx2 = x + dx2;
        int ty2 = y + dy2;
        while (tx2 >= 0 && tx2 < this.width && ty2 >= 0 && ty2 < this.height) {
            if (this.getValue(tx2, ty2) == player) {
                length++;
//                cells[this.index(tx2, ty2)] = true;
            } else {
                break;
            }
            tx2 += dx2;
            ty2 += dy2;
        }
        if (length >= this.winSize) {
            return new Win(player);
        } else {
            return null;
        }
    }

    private int getMove(int moveIndex) {
        Iterator<Integer> it = moves.iterator();
        int move = -1;
        for (int i = 0; i <= moveIndex; i++) {
            move = it.next();
        }
        return move;
    }

}
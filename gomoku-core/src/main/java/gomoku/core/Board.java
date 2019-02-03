package gomoku.core;

import java.util.LinkedList;
import java.util.List;

public class Board {
    public static final char P1 = 'x';
    public static final char P2 = 'o';
    public static final char EMPTY = '.';

    private char currentPlayer;
//    private Set<Integer> moves = new HashSet<>(100);
    private final char[][] board;
    private final int size;
    private final int winSize;
    private Win win;

    protected long findMovesTime = 0;
    protected long findWinTime = 0;
    protected long playoutTime = 0;

    public static char nextPlayer(char player) {
        return player == P1 ? P2 : P1;
    }

    public Board() {
        this(15, 5);
    }

    public Board(int size, int winSize) {
        this.board = new char[size][size];
        this.size = size;
        this.winSize = winSize;
        clear();
    }

    public int getSize() {
        return size;
    }

    public int getWinSize() {
        return winSize;
    }

    public Win getWin() {
        return win;
    }

    public void clear() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                board[x][y] = EMPTY;
            }
        }
//        moves.clear();
//        moves.add(move(N / 2, N / 2));
        win = null;
        currentPlayer = P1;
    }

    public boolean isValidMove(int x, int y) {
        return win == null && x >= 0 && x < size && y >= 0 && y < size && board[x][y] == EMPTY;
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

//    public void makeMove(int move, char player) {
//        int x = move % this.size;
//        int y = move / this.size;
//        setValue(x, y, player);
//        currentPlayer = nextPlayer(currentPlayer);
//        findWinner(x, y);
////        findMoves(x, y);
////        moves.remove(move);
//    }

//    public int move(int x, int y) {
//        return y * size + x;
//    }

    public void clearValue(int x, int y) {
        setValue(x, y, EMPTY);
    }

    //return value at position (x,y)
    public char getValue(int x, int y) {
        return board[x][y];
    }

    //put X or O at clear the cell at position (x,y)
    public void setValue(int x, int y, char player) {
        board[x][y] = player;
    }

//    public Set<Integer> getMoves() {
//        return moves;
//    }

    public String print() {
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                sb.append(getValue(x, y));
            }
            sb.append('\n');
        }
        return sb.toString();
    }


//    public Win randomPlayout(char player) {
//        long start = System.nanoTime();
//        while (!this.moves.isEmpty()) {
//            int moveIndex = (int) (Math.random() * moves.size());
//            int move = getMove(moveIndex);
//            makeMove(move, player);
//            if (win != null) {
//                break;
//            } else {
//                player = Board.nextPlayer(player);
//            }
//        }
//        long end = System.nanoTime();
//        playoutTime += (end - start);
//        return win;
//    }

    // ---------------------------- private -----------------------------------------


//    private void findMoves(int cx, int cy) {
//        long start = System.nanoTime();
//        int rad = 2;
//        int x1 = Math.max(0, cx - rad);
//        int y1 = Math.max(0, cy - rad);
//        int x2 = Math.min(this.size - 1, cx + rad);
//        int y2 = Math.min(this.size - 1, cy + rad);
//        for (int x = x1; x <= x2; x++) {
//            for (int y = y1; y <= y2; y++) {
//                if (this.board[x][y] == EMPTY) {
//                    this.moves.add(move(x, y));
//                }
//            }
//        }
//        long end = System.nanoTime();
//        findMovesTime += (end - start);
//    }

    private void findWinner(int x, int y) {
        long start = System.nanoTime();
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
        long end = System.nanoTime();
        findWinTime += (end - start);
    }

    private Win checkCell(int x, int y, int dx1, int dy1, int dx2, int dy2) {
        char player = this.getValue(x, y);
        if (player == EMPTY) {
            throw new IllegalStateException("Empty cell at " + x + "," + y);
        }
        int length = 0;
        int minX = size;
        int minY = size;
        int maxX = 0;
        int maxY = 0;
        {
            int tx = x;
            int ty = y;
            while (tx >= 0 && tx < this.size && ty >= 0 && ty < this.size) {
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
            while (tx >= 0 && tx < this.size && ty >= 0 && ty < this.size) {
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
        if (length >= this.winSize) {
            return new Win(player, minX, minY, maxX, maxY);
        } else {
            return null;
        }
    }

//    private int getMove(int moveIndex) {
//        Iterator<Integer> it = moves.iterator();
//        int move = -1;
//        for (int x = 0; x <= moveIndex; x++) {
//            move = it.next();
//        }
//        return move;
//    }

}
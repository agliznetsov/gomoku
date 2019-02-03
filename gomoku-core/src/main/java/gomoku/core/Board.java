package gomoku.core;

public class Board {
    private static final int SIZE = 15;
    private static final int WIN_SIZE = 5;
    
    public static final char P1 = 'x';
    public static final char P2 = 'o';
    public static final char EMPTY = '.';

    private char currentPlayer;
//    private Set<Integer> moves = new HashSet<>(100);
    private final char[][] cells;
    private Win win;

//    protected long findMovesTime = 0;
//    protected long findWinTime = 0;
//    protected long playoutTime = 0;

    public static char nextPlayer(char player) {
        return player == P1 ? P2 : P1;
    }

    public Board() {
        this.cells = new char[SIZE][SIZE];
        clear();
    }
    
    public Board(Board board) {
        this.cells = board.cells.clone();
    }

    public int getSize() {
        return SIZE;
    }

    public int getWinSize() {
        return WIN_SIZE;
    }

    public Win getWin() {
        return win;
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public void clear() {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                cells[x][y] = EMPTY;
            }
        }
//        moves.clear();
//        moves.add(move(N / 2, N / 2));
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

//    public void makeMove(int move, char player) {
//        int x = move % SIZE;
//        int y = move / SIZE;
//        setValue(x, y, player);
//        currentPlayer = nextPlayer(currentPlayer);
//        findWinner(x, y);
////        findMoves(x, y);
////        moves.remove(move);
//    }

//    public int move(int x, int y) {
//        return y * SIZE + x;
//    }

    public void clearValue(int x, int y) {
        setValue(x, y, EMPTY);
    }

    //return value at position (x,y)
    public char getValue(int x, int y) {
        return cells[x][y];
    }

    //put X or O at clear the cell at position (x,y)
    public void setValue(int x, int y, char player) {
        cells[x][y] = player;
    }

//    public Set<Integer> getMoves() {
//        return moves;
//    }

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


//    public Win randomPlayout(char player) {
//        long start = System.nanoTime();
//        while (!this.moves.isEmpty()) {
//            int moveIndex = (int) (Math.random() * moves.SIZE());
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
//        int x2 = Math.min(SIZE - 1, cx + rad);
//        int y2 = Math.min(SIZE - 1, cy + rad);
//        for (int x = x1; x <= x2; x++) {
//            for (int y = y1; y <= y2; y++) {
//                if (this.cells[x][y] == EMPTY) {
//                    this.moves.add(move(x, y));
//                }
//            }
//        }
//        long end = System.nanoTime();
//        findMovesTime += (end - start);
//    }

    private void findWinner(int x, int y) {
//        long start = System.nanoTime();
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
//        findWinTime += (end - start);
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

//    private int getMove(int moveIndex) {
//        Iterator<Integer> it = moves.iterator();
//        int move = -1;
//        for (int x = 0; x <= moveIndex; x++) {
//            move = it.next();
//        }
//        return move;
//    }

}
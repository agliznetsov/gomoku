package gomoku.ui;

import gomoku.core.Board;
import gomoku.core.Win;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class BoardView extends Canvas {
    private final int CELL_SIZE = 30;
    private final int STONE_SIZE = 26;

    private Board board;
    int mx = -1, my = -1;

    public BoardView(Board board) {
        this.board = board;
        setWidth((board.getSize() + 1) * CELL_SIZE);
        setHeight((board.getSize() + 1) * CELL_SIZE);
        draw();
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onClick);
        this.addEventHandler(MouseEvent.MOUSE_MOVED, this::onMove);
    }

    private void onMove(MouseEvent e) {
        mx = (int) Math.round((e.getX() - CELL_SIZE) / CELL_SIZE);
        my = (int) Math.round((e.getY() - CELL_SIZE) / CELL_SIZE);
        draw();
    }

    private void onClick(MouseEvent e) {
        int x = (int) Math.round((e.getX() - CELL_SIZE) / CELL_SIZE);
        int y = (int) Math.round((e.getY() - CELL_SIZE) / CELL_SIZE);
        if (board.makeMove(x, y)) {
            draw();
        }
    }

    public void draw() {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.setFill(Color.rgb(200, 155, 60));
        gc.fillRect(0, 0, getWidth(), getHeight());
        drawGrid(gc);
        drawBoard(gc);
        drawMark(gc);
        drawWin(gc);
    }

    private void drawWin(GraphicsContext gc) {
        Win w = board.getWin();
        if (w != null) {
            gc.setStroke(Color.RED);
            gc.strokeLine(w.minX * CELL_SIZE + CELL_SIZE, w.minY * CELL_SIZE + CELL_SIZE, w.maxX * CELL_SIZE + CELL_SIZE, w.maxY * CELL_SIZE + CELL_SIZE);
        }
    }

    private void drawMark(GraphicsContext gc) {
        if (board.isValidMove(mx, my)) {
            drawStone(gc, mx, my, null, Color.RED);
        }
    }

    private void drawStone(GraphicsContext gc, int x, int y, Paint fill, Paint stroke) {
        if (fill != null) {
            gc.setFill(fill);
            gc.fillOval(x * CELL_SIZE + CELL_SIZE - (STONE_SIZE / 2), y * CELL_SIZE + CELL_SIZE - (STONE_SIZE / 2), STONE_SIZE, STONE_SIZE);
        }
        if (stroke != null) {
            gc.setStroke(stroke);
            gc.strokeOval(x * CELL_SIZE + CELL_SIZE - (STONE_SIZE / 2), y * CELL_SIZE + CELL_SIZE - (STONE_SIZE / 2), STONE_SIZE, STONE_SIZE);
        }
    }

    private void drawBoard(GraphicsContext gc) {
        for (int x = 0; x < board.getSize(); x++) {
            for (int y = 0; y < board.getSize(); y++) {
                Paint fill = getPlayerColor(board.getValue(x, y));
                if (fill != null) {
                    drawStone(gc, x, y, fill, Color.BLACK);
                }
            }
        }
    }

    private Paint getPlayerColor(char v) {
        if (v == Board.P1) {
            return Color.BLACK;
        } else if (v == Board.P2){
            return Color.WHITE;
        }
        return null;
    }


    private void drawGrid(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        for (int x = 1; x < board.getSize() + 1; x++) {
            gc.fillRect(x * CELL_SIZE, CELL_SIZE, 1, (board.getSize() - 1) * CELL_SIZE);
        }
        for (int y = 1; y < board.getSize() + 1; y++) {
            gc.fillRect(CELL_SIZE, y * CELL_SIZE, (board.getSize() - 1) * CELL_SIZE, 1);
        }
    }
}

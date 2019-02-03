package gomoku.ui;

import gomoku.core.Board;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Board board = new Board();
//        board.setValue(0, 0, Board.P1);
        for(int i=0; i<4; i++) {
            board.setValue(i, i, Board.P1);
        }
        board.setValue(board.getSize() - 1, board.getSize() - 1, Board.P2);

        BorderPane pane = new BorderPane();
        pane.setCenter(new BoardView(board));
        Scene scene = new Scene(pane);

        primaryStage.setTitle("Gomoku");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
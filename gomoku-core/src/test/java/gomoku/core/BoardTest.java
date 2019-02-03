//package gomoku.core;
//
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Test;
//
//@Slf4j
//public class BoardTest {
//    //14K playouts per second
//    @Test
//    public void randomPlayout() {
//        int p1 = 0;
//        int p2 = 0;
//        int draw = 0;
//        Board board = new Board();
//        long start = System.currentTimeMillis();
//        int N = 100_000;
//        for (int i = 0; i < N; i++) {
//            board.clear();
//            Win win = board.randomPlayout(Board.P1);
//            if (win != null) {
//                if (win.player == Board.P1) {
//                    p1++;
//                } else {
//                    p2++;
//                }
//            } else {
//                draw++;
//            }
//        }
//        long end = System.currentTimeMillis();
//        log.info("P1:  {} P2: {} Draw {} Playout per second: {}", p1, p2, draw, N * 1000 / (end - start));
//        log.info("FindWin: {}%, FindMoves: {}%", board.findWinTime * 100 / board.playoutTime, board.findMovesTime * 100 / board.playoutTime);
//    }
//}

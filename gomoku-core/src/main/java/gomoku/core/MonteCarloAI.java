package gomoku.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static gomoku.core.Board.EMPTY;
import static gomoku.core.Board.SIZE;

public class MonteCarloAI {
    private Board originalBoard;
    private Node node;
    private int playCount;
    private Board board;
    private char player;

    MonteCarloAI(Board board) {
        this.player = board.getCurrentPlayer();
        this.originalBoard = board;
        this.node = new Node(null, Board.nextPlayer(player), -1);
        this.playCount = 0;
    }

    void step() {
        board = new Board(originalBoard);
        Win win = selection();
        if (win != null) {
            expansion();
            win = simulation();
        }
        backPropagation(win);
        playCount++;
        // console.log("# cycle/sec: ", Math.round(1000 * node.playCount / (end - start)));
    }

    Object getResult() {
//        let res:any = {};
//        let moves:any[];
//        moves = _.map(node.children, (it:any) =>{
//            return {move:it.move, value:it.playCount / playCount}
//        });
//        res.max = _.maxBy(moves, "value").value;
//        res.mean = __.meanBy(moves, "value");
//        res.confidence = (res.max - res.mean) / res.mean;
//        res.moves = _.orderBy(moves, "value", "desc");
//        return res;
        return null;
    }

    Win selection() {
        if (node.parent != null) {
            throw new IllegalStateException("invalid start node: " + node);
        }
        while (node.children != null && !node.children.isEmpty()) {
            node.children.forEach(it -> it.calculateUCB(node.playCount));
            double maxUcb = Integer.MIN_VALUE;
            Node nextNode = null;
            for (Node it : node.children) {
                if (it.ucb > maxUcb) {
                    maxUcb = it.ucb;
                    nextNode = it;
                }
            }
            if (nextNode != null) {
                makeMove(nextNode.move, nextNode.player);
                node = nextNode;
//            } else {
//                console.log("error");
            }
        }
        if (node.move != null) {
            if (node.win == null) {
                node.win = findWinner(node.move);
            }
        }
        return node.win;
    }

    void expansion() {
        if (node.children == null) {
            node.children = new LinkedList<>();
            Collection<Integer> moves = findMoves();
            if (!moves.isEmpty()) {
                char np = Board.nextPlayer(node.player);
                for (Integer m : moves) {
                    Node child = new Node(node, np, m);
                    node.children.add(child);
//                    if (evaluation && node.layer === 0) {
//                        let copy = board.clone();
//                        copy.setIndex(node.move, np);
//                        node.evaluation = evaluate();
//                        if (np == 2) {
//                            node.evaluation = -node.evaluation;
//                        }
//                    }
                }
                int moveIndex = (int) Math.floor(Math.random() * node.children.size());
                Node nextNode = node.children.get(moveIndex);
                makeMove(nextNode.move, nextNode.player);
                node = nextNode;
            }
        }
    }

    Win simulation() {
        Win win = findWinner(node.move);
        if (win != null) {
            return win;
        } else {
            return randomPlayout(Board.nextPlayer(node.player));
        }
    }

    void backPropagation(Win win) {
        while (true) {
            node.playCount++;
            if (win != null) {
                if (win.player == node.player) {
                    node.winCount++;
                    // } else {
                    //     node.winCount--;
                }
            } else {
//                node.winCount += 0.5; //half point for a tie
            }
            if (node.parent != null) {
                node = node.parent;
            } else {
                break;
            }
        }
    }


    public Win randomPlayout(char player) {
//        long start = System.nanoTime();
        while (!moves.isEmpty()) {
            int moveIndex = (int) (Math.random() * moves.SIZE());
            int move = getMove(moveIndex);
            makeMove(move, player);
            if (win != null) {
                break;
            } else {
                player = Board.nextPlayer(player);
            }
        }
//        long end = System.nanoTime();
//        playoutTime += (end - start);
        return win;
    }


    void makeMove(int move, char player) {
        int x = move % SIZE;
        int y = move / SIZE;
        board.setValue(x, y, player);
    }

    Win findWinner(int move) {
        int x = move % SIZE;
        int y = move / SIZE;
        return board.findWinner(x, y);
    }

    Collection<Integer> findMoves() {
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

    int move(int x, int y) {
        return y * SIZE + x;
    }

    static class Node {
        Win win;
        Node parent;
        char player;
        int move;
        List<Node> children;
        int playCount;
        int winCount;
        double ucb;
        double evaluation;

        Node(Node parent, char player, int move) {
            this.parent = parent;
            this.player = player;
            this.move = move;
        }

        void calculateUCB(int total) {
            double C = 1.4;
            double value;
            double exploration;
            double evaluation;
            if (this.playCount == 0) {
                value = 0;
                exploration = 1000000;
                evaluation = this.evaluation * 5;
            } else {
                exploration = (C * Math.sqrt(Math.log(total) / this.playCount));
                value = this.winCount / this.playCount;
                evaluation = this.evaluation * 5 / this.playCount;
            }
            this.ucb = value + evaluation + exploration;
        }
    }

}

package gomoku.core;

import lombok.AllArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

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

    public void step() {
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

    public List<Move> getResult() {
        List<Move> moves = node.children.stream().map(it -> new Move(it.move, (double)it.playCount / playCount)).collect(Collectors.toList());
//        res.max = _.maxBy(moves, "value").value;
//        res.mean = __.meanBy(moves, "value");
//        res.confidence = (res.max - res.mean) / res.mean;
        Comparator<Move> comparator = Comparator.comparing(it -> it.value);
        moves.sort(comparator.reversed());
        return moves;
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
//        if (node.move != null) {
            if (node.win == null) {
                node.win = findWinner(node.move);
            }
//        }
        return node.win;
    }

    void expansion() {
        if (node.children == null) {
            node.children = new LinkedList<>();
            Collection<Integer> moves = board.getMoves();
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
        while (!board.getMoves().isEmpty()) {
            int moveIndex = (int) (Math.random() * board.getMoves().size());
            int move = board.getMoves().stream().skip(moveIndex).findFirst().get();
            makeMove(move, player);
            findWinner(move);
            if (board.getWin() != null) {
                break;
            } else {
                player = Board.nextPlayer(player);
            }
        }
//        long end = System.nanoTime();
//        playoutTime += (end - start);
        return board.getWin();
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


    public static class Move {
        public final int x;
        public final int y;
        public final double value;

        private Move(int move, double value) {
            this.x = move % SIZE;
            this.y = move / SIZE;
            this.value = value;
        }
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

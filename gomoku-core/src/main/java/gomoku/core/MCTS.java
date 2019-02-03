package gomoku.core;

import java.util.LinkedList;
import java.util.List;

public class MTS {
    private Board originalBoard;
    private Node node;
    private int playCount;
    private Board board;
    private boolean evaluation;
    private char player;

    MTS(Board board) {
        this.player = board.getCurrentPlayer();
        this.originalBoard = board;
        this.node = new Node(null, Board.nextPlayer(player), -1);
        this.playCount = 0;
    }

    void step() {
        this.board = new Board(this.originalBoard);
        Win win = this.selection();
        if (win != null) {
            this.expansion();
            win = this.simulation();
        }
        this.backPropagation(win);
        this.playCount++;
        // console.log("# cycle/sec: ", Math.round(1000 * this.node.playCount / (end - start)));
    }

    Object getResult() {
        let res: any = {};
        let moves: any[];
        moves = _.map(this.node.children, (it: any) => {
            return {move: it.move, value: it.playCount / this.playCount}
        });
        res.max = _.maxBy(moves, "value").value;
        res.mean = __.meanBy(moves, "value");
        res.confidence = (res.max - res.mean) / res.mean;
        res.moves = _.orderBy(moves, "value", "desc");
        return res;
    }

    Win selection() {
        if (this.node.parent != null) {
            throw new IllegalStateException("invalid start node: " + this.node);
        }
        while (this.node.children != null && !this.node.children.isEmpty()) {
            this.node.children.forEach(it -> it.calculateUCB(this.node.playCount));
            double maxUcb = Integer.MIN_VALUE;
            Node nextNode;
            this.node.children.forEach(it -> {
            if (it.ucb > maxUcb) {
                maxUcb = it.ucb;
                nextNode = it;
            }
            });
            if (nextNode != null) {
                this.board.setIndex(nextNode.move, nextNode.player);
                this.node = nextNode;
            } else {
//                console.log("error");
            }
        }
        if (this.node.move != null) {
            if (this.node.win == null) {
                this.node.win = this.board.findWinnerAt(this.node.move);
            }
        }
        return this.node.win;
    }

    void expansion() {
        if (this.node.children == null) {
            this.node.children = new LinkedList<>();
            let moves = this.board.getMoves();
            if (moves.length) {
                let np = Board.nextPlayer(this.node.player);
                moves.forEach(m => {
                        let node = new Node(this.node, np, m);
                this.node.children.push(node);
                if (this.evaluation && this.node.layer === 0) {
                    let copy = this.board.clone();
                    copy.setIndex(node.move, np);
                    node.evaluation = copy.evaluate();
                    if (np == 2) {
                        node.evaluation = -node.evaluation;
                    }
//                    console.log("eval", node.evaluation);
                }
                });
                let moveIndex = Math.floor(Math.random() * this.node.children.length);
                let nextNode = this.node.children[moveIndex];
                this.board.setIndex(nextNode.move, nextNode.player);
                this.node = nextNode;
            }
        }
    }

    Win simulation() {
        Win win = this.board.findWinnerAt(this.node.move);
        if (win) {
            return win;
        } else {
            return this.board.randomPlayout(Board.nextPlayer(this.node.player));
        }
    }

    void backPropagation(Win win) {
        while (true) {
            this.node.playCount++;
            if (win != null) {
                if (win.player == this.node.player) {
                    this.node.winCount++;
                    // } else {
                    //     this.node.winCount--;
                }
            } else {
//                this.node.winCount += 0.5; //half point for a tie
            }
            if (this.node.parent != null) {
                this.node = this.node.parent;
            } else {
                break;
            }
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
            this.player = parent != null ? Board.nextPlayer(parent.player) : Board.P1;
        }

        void calculateUCB(double total) {
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

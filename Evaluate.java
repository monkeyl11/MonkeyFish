import java.util.*;

class Evaluate {
    private final static double WIN_EVAL = 10000.0;
    private final static double DRAWN_EVAL = 0;

    private final static double WIN_SCORE = 1;
    private final static double DRAWN_SCORE = 0.5;

    public static long total_nodes = 1;

    public static Stopwatch s = new Stopwatch();
    public static Stopwatch s2 = new Stopwatch();
    public static Stopwatch s3 = new Stopwatch();

    private static Position pos; //For use by comparator
    public static int maxDepth = 0;


    //search tree option 1, most simple depth-based minimax
    // public static double evalDepth(Position p, int depth, double alpha, double beta, Move[] bestMove) {
    //     if (p.isDrawn()) {
    //         return 0;
    //     }
    //     if (depth == 0) {
    //         return evaluatePosition(p);
    //     }
    //     s.start();
    //     List<Move> legalMoves = p.legalMoves();
    //     double posStatus = p.positionStatus();
    //     s.stop();
    //     if (posStatus == 0.5) { //Drawn by stalemate
    //         return posStatus;
    //     }
    //     else if (posStatus == 1) { //Checkmate
    //         return (p.activeColor == Color.WHITE ? -1 : 1) * WIN_EVAL; //checkmate
    //     }
    //     if (depth >= 4) {
    //         pos = p;
    //         Collections.sort(legalMoves, new MoveComparator());
    //         pos = null;
    //     }
    //     if (p.activeColor == Color.WHITE) {
    //         double maxEval = -Double.MAX_VALUE;
    //         for (Move m: legalMoves) {
    //             s2.start();
    //             p.makeMove(m);
    //             s2.stop();
    //             double eval = evalDepth(p, depth - 1, alpha, beta, null);
    //             if (eval > maxEval) {
    //                 maxEval = eval;
    //                 if (bestMove != null) {
    //                     bestMove[0] = m;
    //                 }
    //             }
    //             alpha = Math.max(maxEval, alpha);
    //             p.undoMove();
    //             if (maxEval >= beta) {
    //                 break;
    //             }
    //         }
    //         return maxEval;
    //     }
    //     else {
    //         double minEval = Double.MAX_VALUE;
    //         for (Move m: legalMoves) {
    //             s2.start();
    //             p.makeMove(m);
    //             s2.stop();
    //             double eval = evalDepth(p, depth - 1, alpha, beta, null);
    //             if (eval < minEval) {
    //                 minEval = eval;
    //                 if (bestMove != null) {
    //                     bestMove[0] = m;
    //                 }
    //             }
    //             beta = Math.min(minEval, beta);
    //             p.undoMove();
    //             if (minEval <= alpha) {
    //                 break;
    //             }
    //         }
    //         return minEval;
    //     }
    // }

    //search tree option 2, minimax returning when a node depth is hit
    public static double evalNodeCount(Position p, double nodeDepth, double alpha, double beta, Move[] bestMove, int depth, Node<String> currNode) {
        maxDepth = Math.max(depth, maxDepth);
        if (p.isDrawn()) {
            return 0;
        }
        if (nodeDepth <= 1) {
            return evaluatePosition(p);
        }
        List<Move> legalMoves = p.legalMoves();
        double posStatus = p.positionStatus();
        if (posStatus == 0.5) { //Drawn by stalemate
            return posStatus;
        }
        else if (posStatus == 1) { //Checkmate
            return (p.activeColor == Color.WHITE ? -1 : 1) * WIN_EVAL; //checkmate
        }
        if (nodeDepth >= 30000) {
            pos = p;
            Collections.sort(legalMoves, new MoveComparator());
            pos = null;
        }
        double numMoves = legalMoves.size();
        if (p.activeColor == Color.WHITE) {
            double maxEval = -Double.MAX_VALUE;
            for (Move m: legalMoves) {
                p.makeMove(m);
                currNode.children.add(new Node<String>());
                Node<String> child = currNode.children.get(currNode.children.size() - 1);
                child.children = new ArrayList<Node<String>>();
                double eval = evalNodeCount(p, nodeDepth / numMoves, alpha, beta, null, depth + 1, child);
                if (eval > maxEval) {
                    maxEval = eval;
                    if (bestMove != null) {
                        bestMove[0] = m;
                    }
                }
                child.data = m.toString() + " " + eval;
                alpha = Math.max(maxEval, alpha);
                p.undoMove();
                if (maxEval >= beta) {
                    break;
                }
            }
            return maxEval;
        }
        else {
            double minEval = Double.MAX_VALUE;
            for (Move m: legalMoves) {
                p.makeMove(m);
                currNode.children.add(new Node<String>());
                Node<String> child = currNode.children.get(currNode.children.size() - 1);
                child.children = new ArrayList<Node<String>>();
                double eval = evalNodeCount(p, nodeDepth / numMoves, alpha, beta, null, depth + 1, child);
                if (eval < minEval) {
                    minEval = eval;
                    if (bestMove != null) {
                        bestMove[0] = m;
                    }
                }
                child.data = m.toString() + " " + eval;
                beta = Math.min(minEval, beta);
                p.undoMove();
                if (minEval <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }



    public static double evalNodeCount(Position p, double nodeDepth, double alpha, double beta, Move[] bestMove, int depth, double prevEval, int numMoves) {
        maxDepth = Math.max(depth, maxDepth);
        if (p.isDrawn()) {
            return 0;
        }
        double posEval = evaluatePosition(p);
        if (Math.abs(posEval - prevEval) <= 1) {
            if (nodeDepth <= 1) {
                return posEval;
            }
        }
        else {
            nodeDepth *= numMoves;
        }
        // if (nodeDepth <= 1 && Math.abs(posEval - prevEval) <= 1) {
        //     return posEval;
        // }
        List<Move> legalMoves = p.legalMoves();
        double posStatus = p.positionStatus();
        if (posStatus == 0.5) { //Drawn by stalemate
            return posStatus;
        }
        else if (posStatus == 1) { //Checkmate
            return (p.activeColor == Color.WHITE ? -1 : 1) * WIN_EVAL; //checkmate
        }
        if (nodeDepth >= 30000) {
            pos = p;
            Collections.sort(legalMoves, new MoveComparator());
            pos = null;
        }
        int moveCount = legalMoves.size();
        if (p.activeColor == Color.WHITE) {
            double maxEval = -Double.MAX_VALUE;
            for (Move m: legalMoves) {
                p.makeMove(m);
                double eval = evalNodeCount(p, nodeDepth / moveCount, alpha, beta, null, depth + 1, posEval, moveCount);
                if (eval > maxEval) {
                    maxEval = eval;
                    if (bestMove != null) {
                        bestMove[0] = m;
                    }
                }
                alpha = Math.max(maxEval, alpha);
                p.undoMove();
                if (maxEval >= beta) {
                    break;
                }
            }
            return maxEval;
        }
        else {
            double minEval = Double.MAX_VALUE;
            for (Move m: legalMoves) {
                p.makeMove(m);
                double eval = evalNodeCount(p, nodeDepth / moveCount, alpha, beta, null, depth + 1, posEval, moveCount);
                if (eval < minEval) {
                    minEval = eval;
                    if (bestMove != null) {
                        bestMove[0] = m;
                    }
                }
                beta = Math.min(minEval, beta);
                p.undoMove();
                if (minEval <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }



    public static double evaluatePosition(Position p) {
        //cannot be called if legalMoves() has not been called on the position
        //isDrawn() should also be called before this

        //EVALUATION FUNCTION
        //extremely basic for now
        total_nodes++;
        s3.start();
        int eval = 0;
        for (ChessPiece piece: p.whitePieces) {
            eval += piece.pieceMaterialValue;
        }
        for (ChessPiece piece: p.blackPieces) {
            eval -= piece.pieceMaterialValue;
        }
        s3.stop();
        return eval;
    }

    public static void resetStopwatches() {
        s.reset();
        s2.reset();
        s3.reset();
    }

    static class MoveComparator implements Comparator<Move> {
        public int compare(Move m1, Move m2) {
            double[] moveVals = new double[2];
            int index = 0;
            for (Move m: new Move[]{m1, m2}) {
                moveVals[index] = 0;
                if (pos == null) {
                    throw new IllegalArgumentException("CANNOT ACCESS POSITION IF NULL");
                }
                pos.makeMove(m);
                List<Move> legalMoves = pos.legalMoves();
                double posStatus = pos.positionStatus();
                if (posStatus == 1) { //Checkmate
                    moveVals[index] = WIN_EVAL; //checkmate
                }
                if (pos.positionStatus.inCheck) {
                    moveVals[index] += 2;
                }
                if (m.capturedPiece != null) {
                    moveVals[index] += m.capturedPiece.pieceMaterialValue;
                }
                moveVals[index] += (20.0 / (double)legalMoves.size());
                index++;
                pos.undoMove();
            }
            return (int)(1000 * (moveVals[1] - moveVals[0]));
        }
    }


}
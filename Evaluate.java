import java.util.*;

class Evaluate {
    public final static double WIN_EVAL = 10000.0;
    private final static double DRAWN_EVAL = 0;

    private final static double WIN_SCORE = 1;
    private final static double DRAWN_SCORE = 0.5;

    private final static double DEPTH_FACTOR = Math.pow(2, -30);

    private static final byte bitmaskRank = 0b00000111;
    private static final byte bitmaskFile = 0b00111000;

    private static final int BOARD_INITIAL_SIZE = 32;

    private static final double[] PAWN_STACKS = {0, 0, -0.1, -0.6, -1, -1.5, -2, -2.5, -3};
    private static final double[] PAWN_ISLANDS = {0, 0, -0.1, -0.5, -1};
    private static final double CONNECTED_PAWN_MULTIPLIER_BONUS = 0.1;
    private static final double PASSED_PAWN_MULTIPLIER_BONUS = 0.5;
    private static final double ISOLATED_PAWN_MULTIPLIER_BONUS = -0.1;

    public static TranspositionTable tTable = new TranspositionTable();

    public static long total_nodes = 0;
    public static long total_eval_calls = 0;

    public static Stopwatch s = new Stopwatch();
    public static Stopwatch s2 = new Stopwatch();
    public static Stopwatch s3 = new Stopwatch();

    private static Position pos; //For use by comparator
    public static int maxDepth = 0;

    //for debugging


    public static void evaluate(Position p) {

    }


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
    public static double evalNodeCount(Position p, double nodeDepth, double alpha, double beta, Move[] bestMove, int depth, int numMoves, Move move) {
        total_eval_calls++;
        maxDepth = Math.max(depth, maxDepth);
        double tableEntry = tTable.getEntry(p, 0.8 * nodeDepth, p.turn);
        if (tableEntry != Double.MAX_VALUE) {
            return tableEntry + (tableEntry > 0 ? -1 : 1) * depth * DEPTH_FACTOR;
        }
        if (p.isDrawn()) {
            return 0;
        }
        if (nodeDepth < 1 && (move.capturedPiece == null || move.capturedPiece.pieceMaterialValue < 3)) {
            double eval = evaluatePosition(p);
            return eval + (eval > 0 ? -1 : 1) * depth * DEPTH_FACTOR;
        }
        nodeDepth = nodeDepth > 1 ? nodeDepth : 1;
        // if (nodeDepth <= 1 && Math.abs(posEval - prevEval) <= 1) {
        //     return posEval;
        // }
        List<Move> legalMoves = p.legalMoves();
        double posStatus = p.positionStatus();
        if (posStatus == 0.5) { //Drawn by stalemate
            tTable.addEntry(p, 0, nodeDepth, p.turn);
            return posStatus;
        }
        else if (posStatus == 1) { //Checkmate
            tTable.addEntry(p, (p.activeColor == Color.WHITE ? -1 : 1) * (WIN_EVAL), nodeDepth, p.turn);
            return (p.activeColor == Color.WHITE ? -1 : 1) * (WIN_EVAL - DEPTH_FACTOR * depth); //checkmate
        }
        if (nodeDepth >= 3000.0) {
            pos = p;
            Collections.sort(legalMoves, new MoveComparatorShallow());
            pos = null;
        }
        else {
            Collections.sort(legalMoves, new MoveComparatorDeep());
        }
        int moveCount = legalMoves.size();
        if (p.activeColor == Color.WHITE) {
            double maxEval = -Double.MAX_VALUE;
            for (Move m: legalMoves) {
                p.makeMove(m);
                //if recapture
                double eval;
                if (m.capturedPiece != null && move != null && move.capturedPiece != null && m.capturedPiece.currentSquare == move.capturedPiece.currentSquare)
                    eval = evalNodeCount(p, nodeDepth, alpha, beta, null, depth + 1, moveCount, m);
                else
                    eval = evalNodeCount(p, nodeDepth / moveCount, alpha, beta, null, depth + 1, moveCount, m);
                if (eval > maxEval) {
                    maxEval = eval;
                    if (bestMove != null) {
                        bestMove[0] = m;
                    }
                }
                alpha = Math.max(maxEval, alpha);
                p.undoMove();
                if (maxEval >= beta) {
                    tTable.addEntry(p, eval, nodeDepth, p.turn);
                    break;
                }
            }
            tTable.addEntry(p, maxEval, nodeDepth, p.turn);
            return maxEval;
        }
        else {
            double minEval = Double.MAX_VALUE;
            for (Move m: legalMoves) {
                p.makeMove(m);
                double eval;
                if (m.capturedPiece != null && move != null && move.capturedPiece != null && m.capturedPiece.currentSquare == move.capturedPiece.currentSquare)
                    eval = evalNodeCount(p, nodeDepth, alpha, beta, null, depth + 1, moveCount, m);
                else
                    eval = evalNodeCount(p, nodeDepth / moveCount, alpha, beta, null, depth + 1, moveCount, m);
                if (eval < minEval) {
                    minEval = eval;
                    if (bestMove != null) {
                        bestMove[0] = m;
                    }
                }
                beta = Math.min(minEval, beta);
                p.undoMove();
                if (minEval <= alpha) {
                    tTable.addEntry(p, eval, nodeDepth, p.turn);
                    break;
                }
            }
            tTable.addEntry(p, minEval, nodeDepth, p.turn);
            return minEval;
        }
    }

    public static double evalNodeCountDebug(Position p, double nodeDepth, double alpha, double beta, Move[] bestMove, int depth, int numMoves, Move move, Node<String> currNode) {
        total_eval_calls++;
        maxDepth = Math.max(depth, maxDepth);
        double tableEntry = tTable.getEntry(p, 0.8 * nodeDepth, p.turn);
        if (tableEntry != Double.MAX_VALUE) {
            return tableEntry + (tableEntry > 0 ? -1 : 1) * depth * DEPTH_FACTOR;
        }
        if (p.isDrawn()) {
            return 0;
        }
        if (nodeDepth < 1 && (move.capturedPiece == null || move.capturedPiece.pieceMaterialValue < 3)) {
            double eval = evaluatePosition(p);
            return eval + (eval > 0 ? -1 : 1) * depth * DEPTH_FACTOR;
        }
        nodeDepth = nodeDepth > 1 ? nodeDepth : 1;
        // if (nodeDepth <= 1 && Math.abs(posEval - prevEval) <= 1) {
        //     return posEval;
        // }
        List<Move> legalMoves = p.legalMoves();
        double posStatus = p.positionStatus();
        if (posStatus == 0.5) { //Drawn by stalemate
            tTable.addEntry(p, 0, nodeDepth, p.turn);
            return posStatus;
        }
        else if (posStatus == 1) { //Checkmate
            tTable.addEntry(p, (p.activeColor == Color.WHITE ? -1 : 1) * (WIN_EVAL), nodeDepth, p.turn);
            return (p.activeColor == Color.WHITE ? -1 : 1) * (WIN_EVAL - DEPTH_FACTOR * depth); //checkmate
        }
        if (nodeDepth >= 3000.0) {
            pos = p;
            Collections.sort(legalMoves, new MoveComparatorShallow());
            pos = null;
        }
        else {
            Collections.sort(legalMoves, new MoveComparatorDeep());
        }
        int moveCount = legalMoves.size();
        if (p.activeColor == Color.WHITE) {
            double maxEval = -Double.MAX_VALUE;
            for (Move m: legalMoves) {
                p.makeMove(m);
                //if recapture
                currNode.children.add(new Node<String>());
                Node<String> child = currNode.children.get(currNode.children.size() - 1);
                child.children = new ArrayList<Node<String>>();
                double eval;
                if (m.capturedPiece != null && move != null && move.capturedPiece != null && m.capturedPiece.currentSquare == move.capturedPiece.currentSquare)
                    eval = evalNodeCountDebug(p, nodeDepth, alpha, beta, null, depth + 1, moveCount, m, child);
                else
                    eval = evalNodeCountDebug(p, nodeDepth / moveCount, alpha, beta, null, depth + 1, moveCount, m, child);
                if (eval > maxEval) {
                    maxEval = eval;
                    if (bestMove != null) {
                        bestMove[0] = m;
                    }
                }
                child.data = m.toString() + " " + Evaluate.formatEval(eval) + " " + nodeDepth;
                alpha = Math.max(maxEval, alpha);
                p.undoMove();
                if (maxEval >= beta) {
                    tTable.addEntry(p, eval, nodeDepth, p.turn);
                    break;
                }
            }
            tTable.addEntry(p, maxEval, nodeDepth, p.turn);
            return maxEval;
        }
        else {
            double minEval = Double.MAX_VALUE;
            for (Move m: legalMoves) {
                p.makeMove(m);
                currNode.children.add(new Node<String>());
                Node<String> child = currNode.children.get(currNode.children.size() - 1);
                child.children = new ArrayList<Node<String>>();
                double eval;
                if (m.capturedPiece != null && move != null && move.capturedPiece != null && m.capturedPiece.currentSquare == move.capturedPiece.currentSquare)
                    eval = evalNodeCountDebug(p, nodeDepth, alpha, beta, null, depth + 1, moveCount, m, child);
                else
                    eval = evalNodeCountDebug(p, nodeDepth / moveCount, alpha, beta, null, depth + 1, moveCount, m, child);
                if (eval < minEval) {
                    minEval = eval;
                    if (bestMove != null) {
                        bestMove[0] = m;
                    }
                }
                child.data = m.toString() + " " + Evaluate.formatEval(eval) + " " + nodeDepth;
                beta = Math.min(minEval, beta);
                p.undoMove();
                if (minEval <= alpha) {
                    tTable.addEntry(p, eval, nodeDepth, p.turn);
                    break;
                }
            }
            tTable.addEntry(p, minEval, nodeDepth, p.turn);
            return minEval;
        }
    }





    public static double evaluatePosition(Position p) {
        //cannot be called if legalMoves() has not been called on the position
        //isDrawn() should also be called before this

        //EVALUATION FUNCTION
        //extremely basic for now
        double tableEntry = tTable.getEntry(p, 0, p.turn);
        if (tableEntry != Double.MAX_VALUE) {
            return tableEntry;
        }
        double eval = 0;
        s3.start();
        eval += evalBasicMaterial(p);
        double positionalFactor = Math.pow(0.8, Math.abs(eval)); //lower positional evaluation weight if large material imbalance
        eval += positionalFactor * evalPawns(p);
        double loneKingEval = evalLoneKingEndgame(p);
        if (loneKingEval != 0) {
            eval += loneKingEval;
        }
        else {
            eval += positionalFactor * evalPieces(p);
            eval += rightToMove(p);
        }
        s3.stop();
        total_nodes++;

        tTable.addEntry(p, eval, 1, p.turn);
        //basic engame technique
        return eval;
    }

    private static double evalBasicMaterial(Position p) {
        double eval = 0;
        for (ChessPiece piece: p.whitePieces) {
            eval += piece.pieceMaterialValue;
        }
        for (ChessPiece piece: p.blackPieces) {
            eval -= piece.pieceMaterialValue;
        }
        return eval;
    }

    public static double evalSpaceControl(Position p) {
        double eval = 0;
        //TO DO POSSIBLY

        return eval;
    }

    public static double evalPieces(Position p) {
        double eval = 0;
        ChessPiece blackKing = null;
        ChessPiece whiteKing = null;
        double whitePieceMatSum = 0;
        double blackPieceMatSum = 0;

        for (ChessPiece piece: p.whitePieces) {
            if (piece.id == PieceID.KNIGHT) {
                eval += EvalTables.KNIGHT_TABLE[EvalTables.tableConvWhite(piece.currentSquare)] / 100.0;
            }
            else if (piece.id == PieceID.BISHOP) {
                eval += EvalTables.BISHOP_TABLE[EvalTables.tableConvWhite(piece.currentSquare)] / 100.0;
            }
            else if (piece.id == PieceID.ROOK) {
                eval += EvalTables.ROOK_TABLE[EvalTables.tableConvWhite(piece.currentSquare)] / 100.0;
            }
            else if (piece.id == PieceID.QUEEN) {
                eval += EvalTables.QUEEN_TABLE[EvalTables.tableConvWhite(piece.currentSquare)] / 100.0;
            }
            else if (piece.id == PieceID.KING) {
                whiteKing = piece;
            }
            else {
                continue;
            }
            if (piece.id != PieceID.KING)
                whitePieceMatSum += piece.pieceMaterialValue;
        }
        for (ChessPiece piece: p.blackPieces) {
            if (piece.id == PieceID.KNIGHT) {
                eval -= EvalTables.KNIGHT_TABLE[EvalTables.tableConvBlack(piece.currentSquare)] / 100.0;
            }
            else if (piece.id == PieceID.BISHOP) {
                eval -= EvalTables.BISHOP_TABLE[EvalTables.tableConvBlack(piece.currentSquare)] / 100.0;
            }
            else if (piece.id == PieceID.ROOK) {
                eval -= EvalTables.ROOK_TABLE[EvalTables.tableConvBlack(piece.currentSquare)] / 100.0;
            }
            else if (piece.id == PieceID.QUEEN) {
                eval -= EvalTables.QUEEN_TABLE[EvalTables.tableConvBlack(piece.currentSquare)] / 100.0;
            }
            else if (piece.id == PieceID.KING) {
                blackKing = piece;
            }
            else {
                continue;
            }
            if (piece.id != PieceID.KING)
                blackPieceMatSum += piece.pieceMaterialValue;
        }

        if (blackPieceMatSum >= 15) {
            eval += EvalTables.KING_MG_TABLE[EvalTables.tableConvWhite(whiteKing.currentSquare)] / 100.0;
        }
        else {
            eval += EvalTables.KING_EG_TABLE[EvalTables.tableConvWhite(whiteKing.currentSquare)] / 100.0;
        }
        if (whitePieceMatSum >= 15) {
            eval -= EvalTables.KING_MG_TABLE[EvalTables.tableConvBlack(blackKing.currentSquare)] / 100.0;
        }
        else {
            eval -= EvalTables.KING_EG_TABLE[EvalTables.tableConvBlack(blackKing.currentSquare)] / 100.0;
        }

        return eval;
    }

    private static double evalPawns(Position p) {
        //evaluate pawn islands, pawn structure, pawn position
        double eval = 0;
        int numPiecesOnBoard = p.whitePieces.size() + p.blackPieces.size();
        ArrayList<ArrayList<ChessPiece>> whitePawnsByFile = new ArrayList<>(8);
        ArrayList<ArrayList<ChessPiece>> blackPawnsByFile = new ArrayList<>(8);
        ArrayList<ChessPiece> pawns;
        for (int i = 0; i < 8; i++) {
            whitePawnsByFile.add(new ArrayList<>(2));
            blackPawnsByFile.add(new ArrayList<>(2));
        }
        int pFile;
        double bonusPawnPts = 0;
        for (ChessPiece piece: p.whitePieces) {
            if (piece.id == PieceID.PAWN) {
                pFile = (piece.currentSquare & bitmaskFile) >> 3;
                whitePawnsByFile.get(pFile).add(piece);
                bonusPawnPts = EvalTables.PAWN_TABLE[EvalTables.tableConvWhite(piece.currentSquare)] / 100.0;
                piece.pieceMaterialValue += bonusPawnPts;
                eval += bonusPawnPts;
            } 
        }
        for (ChessPiece piece: p.blackPieces) {
            if (piece.id == PieceID.PAWN) {
                pFile = (piece.currentSquare & bitmaskFile) >> 3;
                blackPawnsByFile.get(pFile).add(piece);
                bonusPawnPts = EvalTables.PAWN_TABLE[EvalTables.tableConvBlack(piece.currentSquare)] / 100.0;
                piece.pieceMaterialValue += bonusPawnPts;
                eval -= bonusPawnPts;
            } 
        }

        //Check for pawn islands
        int bIslands = 0; int wIslands = 0;
        boolean bSpace = true; boolean wSpace = true;
        for (int file = 0; file < 8; file++) {
            if (blackPawnsByFile.get(file).isEmpty()) {
                bSpace = true;
            }
            else {
                if (bSpace)
                    bIslands++;
                bSpace = false;
            }
            if (whitePawnsByFile.get(file).isEmpty()) {
                wSpace = true;
            }
            else {
                if (wSpace)
                    wIslands++;
                wSpace = false;
            }
            //doubled pawns
            eval += PAWN_STACKS[whitePawnsByFile.get(file).size()];
            eval -= PAWN_STACKS[blackPawnsByFile.get(file).size()];
        }
        //System.out.println("Eval before pawn island calc: " + eval);
        //pawn islands
        //System.out.println("WHITE PAWN ISLANDS: " + wIslands);
        //System.out.println("BLACK PAWN ISLANDS: " + bIslands);
        eval += PAWN_ISLANDS[wIslands];
        eval -= PAWN_ISLANDS[bIslands];
        //checked for passed, isolated, connected pawns
        boolean passed, isolated, connected;

        int bMult = 1; //multiply by this when you switch to black's side
        ArrayList<ArrayList<ChessPiece>> currentSidePawns = whitePawnsByFile;
        ArrayList<ArrayList<ChessPiece>> oppSidePawns = blackPawnsByFile;
        for (int n = 0; n < 2; n++) {
            for (int file = 0; file < 8; file++) {
                for (ChessPiece pawn: currentSidePawns.get(file)) {
                    //System.out.println("Current evaluation: " + eval);
                    passed = true;
                    isolated = true;
                    connected = false;
                    //passed pawn check
                    for (ChessPiece oppPawn: oppSidePawns.get(file)) {
                        if (bMult * (oppPawn.currentSquare & bitmaskRank) > bMult * (pawn.currentSquare & bitmaskRank)) {
                            passed = false;
                        }
                    }
                    if (file > 0) {
                        //check to see if passed pawn
                        for (ChessPiece oppPawn: oppSidePawns.get(file - 1)) {
                            if (bMult * (oppPawn.currentSquare & bitmaskRank) > bMult * (pawn.currentSquare & bitmaskRank)) {
                                passed = false;
                            }
                        }
                        //check to see if pawn is connected
                        ChessPiece potentialProtector = p.b.getPieceFromSquare((byte)(pawn.currentSquare - 8 - bMult));
                        if (potentialProtector != null && potentialProtector.id == PieceID.PAWN
                            && potentialProtector.pieceColor == pawn.pieceColor) {
                                connected = true;
                        }
                        //check to see if pawn is isolated
                        if (!currentSidePawns.get(file - 1).isEmpty()) 
                            isolated = false;
                    }
                    if (file < 7) {
                        //check to see if passed pawn
                        for (ChessPiece oppPawn: oppSidePawns.get(file + 1)) {
                            if (bMult * (oppPawn.currentSquare & bitmaskRank) > bMult * (pawn.currentSquare & bitmaskRank)) {
                                passed = false;
                            }
                        }
                        //check to see if pawn is connected
                        ChessPiece potentialProtector = p.b.getPieceFromSquare((byte)(pawn.currentSquare + 8 - bMult));
                        if (potentialProtector != null && potentialProtector.id == PieceID.PAWN
                            && potentialProtector.pieceColor == pawn.pieceColor) {
                                connected = true;
                        }
                        //check to see if pawn is isolated
                        if (!currentSidePawns.get(file + 1).isEmpty()) 
                            isolated = false;
                    }
                    if (passed) {
                        eval += bMult * (pawn.pieceMaterialValue) * PASSED_PAWN_MULTIPLIER_BONUS * 0.02 * (50 - numPiecesOnBoard);
                        //System.out.println(pawn + " IS PASSED " + " BONUS = " + bMult * (pawn.pieceMaterialValue) * PASSED_PAWN_MULTIPLIER_BONUS * 0.02 * (50 - numPiecesOnBoard));
                    }
                    if (isolated) {
                        eval += bMult * (pawn.pieceMaterialValue) * ISOLATED_PAWN_MULTIPLIER_BONUS;
                        //System.out.println(pawn + " IS ISOLATED " + " BONUS = " + bMult * (pawn.pieceMaterialValue) * ISOLATED_PAWN_MULTIPLIER_BONUS);
                    }
                    if (connected) {
                        eval += bMult * (pawn.pieceMaterialValue) * CONNECTED_PAWN_MULTIPLIER_BONUS;
                        //System.out.println(pawn + " IS CONNECTED " + " BONUS = " + bMult * (pawn.pieceMaterialValue) * CONNECTED_PAWN_MULTIPLIER_BONUS);
                    }
                    pawn.pieceMaterialValue = 1;
                }
            }
            bMult = -1;
            currentSidePawns = blackPawnsByFile;
            oppSidePawns = whitePawnsByFile;
        }
        return eval;
    }

    public static double rightToMove(Position p) {
        return p.activeColor == Color.WHITE ? 0.15 : -0.15;
    }

    private static double evalLoneKingEndgame(Position p) {
        if (p.whitePieces.size() == 1 || p.blackPieces.size() == 1) {
            double eval = 0;
            int winningSide = p.whitePieces.size() == 1 ? -1 : 1;
            ChessPiece losingKing = winningSide > 0 ? p.blackPieces.stream().findFirst().get() : p.whitePieces.stream().findFirst().get();
            ChessPiece winningKing = null;
            for (ChessPiece piece: (winningSide > 0) ? p.whitePieces : p.blackPieces) {
                if (piece.id == PieceID.KING)
                    winningKing = piece;
            }
            int Lrank = losingKing.currentSquare & bitmaskRank;
            int Lfile = (losingKing.currentSquare & bitmaskFile) >> 3;
            int Wrank = winningKing.currentSquare & bitmaskRank;
            int Wfile = (winningKing.currentSquare & bitmaskFile) >> 3;
            eval += (winningSide < 0 ? -0.1 : 0.1) * (14 - (Math.abs(Lrank - Wrank) + Math.abs(Lfile - Wfile)));
            Lrank = Lrank > 3 ? 7 - Lrank : Lrank;
            Lfile = Lfile > 3 ? 7 - Lfile : Lfile;
            eval += (winningSide < 0 ? -1 : 1) * (0.9 - 0.15 * Lrank - 0.15 * Lfile);
            return eval;
        }
        else {
            return 0;
        }
    }

    public static void resetTable() {
        tTable = new TranspositionTable();
        tTable.size = 0;
        tTable.hits = 0;
        tTable.unwantedCollisions = 0;
    }

    public static void clearMovesFromTable(int moveCutoff) {
        tTable.cleanTable(moveCutoff);
    }



    public static void resetStopwatches() {
        s.reset();
        s2.reset();
        s3.reset();
    }

    public static String formatEval(double evaluation) {
        String evalFormatted;
        if (Math.abs(evaluation) > 9999) {
            evalFormatted = evaluation > 0 ? "#" : "#-";
            evaluation = (10000 - Math.abs(evaluation)) * Math.pow(2, 30);
            evalFormatted = evalFormatted + ((int)(evaluation + 1)/2);
        }
        else {
            return String.format("%.5f", evaluation);
        }
        return evalFormatted;
    }

    static class MoveComparatorShallow implements Comparator<Move> {
        public int compare(Move m1, Move m2) {
            double[] moveVals = new double[2];
            int index = 0;
            for (Move m: new Move[]{m1, m2}) {
                moveVals[index] = 0;
                if (pos == null) {
                    throw new IllegalArgumentException("CANNOT ACCESS POSITION IF NULL");
                }
                pos.makeMove(m);
                moveVals[index] = evaluatePosition(pos);
                pos.undoMove();
                index++;
            }
            if (m1.color == Color.WHITE)
                return (int)(1000 * (moveVals[1] - moveVals[0]));
            return (int)(1000 * (moveVals[0] - moveVals[1]));

        }
    }

    static class MoveComparatorDeep implements Comparator<Move> {
        public int compare(Move m1, Move m2) {
            double[] moveVals = new double[2];
            int index = 0;
            for (Move m: new Move[]{m1, m2}) {
                moveVals[index] = 0;
                if (m.capturedPiece != null) {
                    moveVals[index] += m.capturedPiece.pieceMaterialValue;
                }
                index++;
            }
            if (m1.color == Color.WHITE)
                return (int)(1000 * (moveVals[1] - moveVals[0]));
            return (int)(1000 * (moveVals[0] - moveVals[1]));
        }
    }


}
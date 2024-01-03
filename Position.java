import java.util.*;

class Position {
    private Board b;
    private Color sideToMove;
    private HashSet<ChessPiece> whitePieces;
    private HashSet<ChessPiece> blackPieces;
    private HashMap<Board, Integer> prevPositions; //tracking three-fold
    private int turns; //track 50-move rule (100 turns), also en passant

    private int lastCapture;
}
import java.util.*;

class Position {
    private Board b;
    private Color sideToMove;
    private HashSet<ChessPiece> whitePieces;
    private HashSet<ChessPiece> blackPieces;
    private HashMap<Position, Integer> prevPositions; //tracking three-fold
    private int turns; //track 50-move rule (100 turns), also en passant

    private int lastCapture;

    public Position(Board b, Color sideToMove) {
        this.b = b;
        this.sideToMove = sideToMove;
        ChessPiece p;
        for (byte i = 0; i < b.board.length * b.board.length; i++) {
            p = b.getPieceFromSquare(i);
            if (p != null) {
                if (p.pieceColor == Color.WHITE) {
                    whitePieces.add(p);
                }
                else {
                    blackPieces.add(p);
                }
            }
        }
    }
}
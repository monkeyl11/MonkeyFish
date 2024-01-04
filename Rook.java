import java.util.*;

class Rook extends ChessPiece {
    private int canCastle;

    public Rook (byte currentSquare, Color pieceColor, boolean canCastle) {
        //add pawn to square currentSquare
        super(5, currentSquare, pieceColor);
        id = PieceID.ROOK;
        this.canCastle = canCastle == true ? 0 : 1;
    }

    public Rook(byte currentSquare, Color pieceColor) {
        this(currentSquare, pieceColor, true);
    }

    public void possibleMoves(Board b, List<Move> moveList) {
        checkLine(b, moveList, 0, 1);
        checkLine(b, moveList, 0, -1);
        checkLine(b, moveList, 1, 0);
        checkLine(b, moveList, -1, 0);
    }

    public void movePiece(byte newSquare, boolean undoMove) {
        super.movePiece(newSquare, false);
        canCastle += undoMove ? -1 : 1;
    }

    public boolean validCastling() {
        return canCastle == 0;
    }

}
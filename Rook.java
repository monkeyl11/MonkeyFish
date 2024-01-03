import java.util.*;

class Rook extends ChessPiece {
    private boolean canCastle;

    public Rook(byte currentSquare, Color pieceColor) {
        //add pawn to square currentSquare
        super(5, currentSquare, pieceColor);
        id = PieceID.ROOK;
        canCastle = true;
    }

    public void legalMoves(Board b, List<Move> moveList) {
    }

    public boolean validCastling() {
        return canCastle;
    }

}
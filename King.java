import java.util.*;


class King extends ChessPiece {
    private boolean canCastle;

    public King(byte currentSquare, Color pieceColor) {
        //add pawn to square currentSquare
        super(1000, currentSquare, pieceColor);
        id = PieceID.KING;
        canCastle = true;
    }

    public void legalMoves(Board b, List<Move> moveList) {
    }

    public boolean validCastling() {
        return canCastle;
    }


}
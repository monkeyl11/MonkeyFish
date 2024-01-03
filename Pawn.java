import java.util.*;


class Pawn extends ChessPiece {
    private int enPassantTurn = 0; //tracks what turn pawn can be en-passanted, 0 means none

    public Pawn(byte currentSquare, Color pieceColor) {
        //add pawn to square currentSquare
        super(1, currentSquare, pieceColor);
        id = PieceID.PAWN;
    }

    public void legalMoves(Board b, List<Move> moveList) {
    }

    public void LoadMoves() {

    }

    public boolean canEnPassant(int currentTurn) {
        return currentTurn == enPassantTurn;
    }
}
import java.util.*;


class Pawn extends ChessPiece {
    private boolean moved;
    private Board board;

    public Pawn(byte currentSquare) {
        //add pawn to square currentSquare
        super(1, currentSquare, "Pawn");
        this.board = board;

    }

    public List<Move> LegalMoves() {
        return null;
    }
}
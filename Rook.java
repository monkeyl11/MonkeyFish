import java.util.*;

class Rook extends ChessPiece {
    public boolean kingsideRook;

    public Rook(byte currentSquare, Color pieceColor) {
        //add pawn to square currentSquare
        super(5, currentSquare, pieceColor);
        this.kingsideRook = false;
        this.id = PieceID.ROOK;
    }


    public void possibleMoves(Board b, List<Move> moveList) {
        checkLine(b, moveList, 0, 1);
        checkLine(b, moveList, 0, -1);
        checkLine(b, moveList, 1, 0);
        checkLine(b, moveList, -1, 0);
    }

    public void assignKingsideRook() {
        kingsideRook = true;
    }

}
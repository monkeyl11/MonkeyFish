import java.util.*;


class Bishop extends ChessPiece {

    public Bishop(byte currentSquare, Color pieceColor) {
        //add pawn to square currentSquare
        super(3, currentSquare, pieceColor);
        this.id = PieceID.BISHOP;
    }

    public void possibleMoves(Board b, List<Move> moveList) {
        checkLine(b, moveList, 1, 1);
        checkLine(b, moveList, -1, 1);
        checkLine(b, moveList, 1, -1);
        checkLine(b, moveList, -1, -1);
    }


}
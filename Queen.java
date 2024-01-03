import java.util.*;


class Queen extends ChessPiece {

    public Queen(byte currentSquare, Color pieceColor) {
        //add pawn to square currentSquare
        super(9, currentSquare, pieceColor);
        id = PieceID.QUEEN;
    }

    public void legalMoves(Board b, List<Move> moveList) {
    }

}
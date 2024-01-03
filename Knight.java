import java.util.*;


class Knight extends ChessPiece {

    public Knight(byte currentSquare, Color pieceColor) {
        //add pawn to square currentSquare
        super(3, currentSquare, pieceColor);
        id = PieceID.KNIGHT;
    }

    public void legalMoves(Board b, List<Move> moveList) {
    }

}
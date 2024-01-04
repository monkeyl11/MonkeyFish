import java.util.*;


class Queen extends ChessPiece {

    public Queen(byte currentSquare, Color pieceColor) {
        //add pawn to square currentSquare
        super(9, currentSquare, pieceColor);
        id = PieceID.QUEEN;
    }

    public void possibleMoves(Board b, List<Move> moveList) {
        checkLine(b, moveList, 1, 1);
        checkLine(b, moveList, -1, 1);
        checkLine(b, moveList, 1, -1);
        checkLine(b, moveList, -1, -1);
        checkLine(b, moveList, 0, 1);
        checkLine(b, moveList, 0, -1);
        checkLine(b, moveList, 1, 0);
        checkLine(b, moveList, -1, 0);
    }

}
import java.util.*;

import Enums.Color;
import Enums.PieceID;


class Queen extends ChessPiece {

    public Queen(byte currentSquare, Color pieceColor) {
        //add pawn to square currentSquare
        super(9, currentSquare, pieceColor);
        this.id = PieceID.QUEEN;
    }

    public void possibleMoves(Board b, List<Move> moveList, OppPieceInfo pieceInfo) {
        checkLine(b, moveList, 1, 1, pieceInfo);
        checkLine(b, moveList, -1, 1, pieceInfo);
        checkLine(b, moveList, 1, -1, pieceInfo);
        checkLine(b, moveList, -1, -1, pieceInfo);
        checkLine(b, moveList, 0, 1, pieceInfo);
        checkLine(b, moveList, 0, -1, pieceInfo);
        checkLine(b, moveList, 1, 0, pieceInfo);
        checkLine(b, moveList, -1, 0, pieceInfo);
    }

}
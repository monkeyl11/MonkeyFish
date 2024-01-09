import java.util.*;

class Rook extends ChessPiece {
    public boolean kingsideRook;
    public boolean queensideRook;

    public Rook(byte currentSquare, Color pieceColor) {
        //add pawn to square currentSquare
        super(5, currentSquare, pieceColor);
        this.kingsideRook = false;
        this.queensideRook = false;
        this.id = PieceID.ROOK;
    }


    public void possibleMoves(Board b, List<Move> moveList, OppPieceInfo pieceInfo) {
        checkLine(b, moveList, 0, 1, pieceInfo);
        checkLine(b, moveList, 0, -1, pieceInfo);
        checkLine(b, moveList, 1, 0, pieceInfo);
        checkLine(b, moveList, -1, 0, pieceInfo);
    }

    public void assignKingsideRook() {
        kingsideRook = true;
    }

    public void assignQueensideRook() {
        queensideRook = true;
    }

}
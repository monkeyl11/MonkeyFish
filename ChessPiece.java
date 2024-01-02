import java.util.*;

abstract class ChessPiece {
    public PieceID id;
    public double pieceMaterialValue;
    protected byte currentSquare;

    public ChessPiece(double pieceMaterialValue, byte currentSquare, String pieceName) {
        this.pieceMaterialValue = pieceMaterialValue;
        this.currentSquare = currentSquare;
    }

    abstract List<Move> LegalMoves();

}
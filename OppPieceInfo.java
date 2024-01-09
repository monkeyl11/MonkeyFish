import java.util.*;

class OppPieceInfo {
    public boolean isChecking;

    public ChessPiece oppPiece; //opponent piece causing all this sh*t

    public ChessPiece pinnedPiece; //piece that is pinned to the king

    public static HashSet<Byte> hazardSquares; //squares this side targets

    public Pawn enPassantHazard; //opponent pawn that could be en passant-ed and will reveal this piece to the King

    public static King king; //The king (if targeted)

    public OppPieceInfo(ChessPiece oppPiece) {
        this.oppPiece = oppPiece;
        this.isChecking = false;
        this.pinnedPiece = null;
        this.enPassantHazard = null;
    }

    public void setChecking(King k) {
        this.isChecking = true;
        king = k;
    }

    public void addPinnedPiece(ChessPiece pinnedPiece) {
        this.pinnedPiece = pinnedPiece;
    }

    public void addHazardSquare(byte square) {
        hazardSquares.add(square);
    }

    public void setEnPassantHazard(Pawn p) {
        this.enPassantHazard = p;
    }

    public static void reset() {
        hazardSquares = new HashSet<>(64);
        king = null;
    }
}
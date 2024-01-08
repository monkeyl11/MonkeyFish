import java.util.*;

class OppPieceInfo {
    public boolean isChecking;

    public ChessPiece oppPiece; //opponent piece causing all this sh*t

    public ChessPiece pinnedPiece; //piece that is pinned to the king
    public int[] pinDirection; //direction of the piece pinned to the king

    public HashSet<Byte> hazardSquares; //squares the piece targets

    public Pawn enPassantHazard; //opponent pawn that could be en passant-ed and will reveal this piece to the King

    public OppPieceInfo(ChessPiece oppPiece) {
        this.oppPiece = oppPiece;
        this.isChecking = false;
        this.pinnedPiece = null;
        this.pinDirection = null;
        this.hazardSquares = new HashSet<>();
        this.enPassantHazard = null;
    }

    public void setChecking() {
        this.isChecking = true;
    }

    public void addPinnedPiece(ChessPiece pinnedPiece, int fileDirection, int rankDirection) {
        this.pinnedPiece = pinnedPiece;
        this.pinDirection = new int[]{fileDirection, rankDirection};
    }

    public void addHazardSquare(byte square) {
        this.hazardSquares.add(square);
    }

    public void setEnPassantHazard(Pawn p) {
        this.enPassantHazard = p;
    }
}
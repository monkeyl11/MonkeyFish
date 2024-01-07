import java.util.*;

class OppPieceInfo {
    public boolean isChecking;

    public ChessPiece oppPiece; //opponent piece causing all this sh*t

    public ChessPiece pinnedPiece; //piece that is pinned to the king
    public int[] pinDirection; //direction of the piece pinned to the king

    public HashSet<Byte> hazardSquares; //squares the piece targets

    public boolean enPassantHazard; //opponent pawn that could be en passant-ed

    public OppPieceInfo(ChessPiece oppPiece) {
        this.oppPiece = oppPiece;
    }

    public void addPinnedPiece(ChessPiece pinnedPiece, int fileDirection, int rankDirection) {
        this.pinnedPiece = pinnedPiece;
        this.pinDirection = new int[]{fileDirection, rankDirection};
    }

    public void addHazardSquare(byte square) {
        hazardSquares.add(square);
    }

    public void setEnPassantHazard() {
        enPassantHazard = true;
    }
}
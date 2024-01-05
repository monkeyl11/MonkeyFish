import java.util.*;


class King extends ChessPiece {

    public King(byte currentSquare, Color pieceColor) {
        //add pawn to square currentSquare
        super(1000, currentSquare, pieceColor);
        this.id = PieceID.KING;
    }

    public void possibleMoves(Board b, List<Move> moveList) {
        //DOES NOT ADD CASTLING, this is done in Position.java, which is also the only place
        //this function will ever be called from
        int pieceRank = this.currentSquare & bitmaskRank;
        int pieceFile = (this.currentSquare & bitmaskFile) >> 3;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (withinBoard(pieceFile + i, pieceRank + j)) {
                    byte targetSquare = (byte)(((pieceFile + i) << 3) + (pieceRank + j));
                    ChessPiece capturedPiece = b.getPieceFromSquare(targetSquare);
                    if (capturedPiece == null || capturedPiece.pieceColor != this.pieceColor) {
                        moveList.add(new Move(this, capturedPiece, targetSquare, this.pieceColor));
                    }
                }
            }
        }
    }

    public void movePiece(byte newSquare) {
        super.movePiece(newSquare);
    }


}
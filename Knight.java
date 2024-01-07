import java.util.*;


class Knight extends ChessPiece {
    private final static int[][] knightMoves = 
    {{1,2}, {1,-2}, {2,1}, {2,-1}, {-1,2}, {-1,-2}, {-2,1}, {-2,-1}};

    public Knight(byte currentSquare, Color pieceColor) {
        //add pawn to square currentSquare
        super(3, currentSquare, pieceColor);
        this.id = PieceID.KNIGHT;
    }

    public void possibleMoves(Board b, List<Move> moveList, OppPieceInfo pieceInfo) {
        int pieceRank = this.currentSquare & bitmaskRank;
        int pieceFile = (this.currentSquare & bitmaskFile) >> 3;
        for (int[] possibleMove: knightMoves) {
            if (withinBoard(pieceFile + possibleMove[0], pieceRank + possibleMove[1])) {
                byte targetSquare = (byte)(((pieceFile + possibleMove[0]) << 3) + (pieceRank + possibleMove[1]));
                ChessPiece capturedPiece = b.getPieceFromSquare(targetSquare);
                if (capturedPiece == null || capturedPiece.pieceColor != this.pieceColor) {
                    moveList.add(new Move(this, capturedPiece, targetSquare, this.pieceColor));
                }
            }
        }

        
    }

}
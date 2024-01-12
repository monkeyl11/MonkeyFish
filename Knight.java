import java.util.*;


class Knight extends ChessPiece {
    private final static int[][] knightMoves = 
    {{1,2}, {1,-2}, {2,1}, {2,-1}, {-1,2}, {-1,-2}, {-2,1}, {-2,-1}};

    public Knight(byte currentSquare, Color pieceColor) {
        //add pawn to square currentSquare
        super(3.2, currentSquare, pieceColor);
        this.id = PieceID.KNIGHT;
    }

    public void possibleMoves(Board b, List<Move> moveList, OppPieceInfo pieceInfo) {
        int pieceRank = this.currentSquare & bitmaskRank;
        int pieceFile = (this.currentSquare & bitmaskFile) >> 3;
        for (int[] possibleMove: knightMoves) {
            if (withinBoard(pieceFile + possibleMove[0], pieceRank + possibleMove[1])) {
                byte targetSquare = (byte)(((pieceFile + possibleMove[0]) << 3) + (pieceRank + possibleMove[1]));
                ChessPiece capturedPiece = b.getPieceFromSquare(targetSquare);
                if (capturedPiece == null) {
                    moveList.add(new Move(this, capturedPiece, targetSquare, this.pieceColor));
                }
                else if (capturedPiece.pieceColor != this.pieceColor) {
                    moveList.add(new Move(this, capturedPiece, targetSquare, this.pieceColor));
                    if (capturedPiece.id == PieceID.KING) {
                        if (pieceInfo != null)
                            pieceInfo.setChecking();
                        else
                            System.out.println("Illegal Position! - " + b);
                    }
                }
                if (pieceInfo != null) {
                    pieceInfo.addHazardSquare(targetSquare);
                }
            }
        }

        
    }

}
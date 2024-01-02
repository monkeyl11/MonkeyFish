class Move {
    public static final int LAST_RANK_WHITE = 7;
    public static final int LAST_RANK_BLACK = 0;

    public boolean whiteToMove; //whos turn it is

    public boolean isKingsideCastle = false;
    public boolean isQueensideCastle = false;
    public PieceID isPromotion = PieceID.NONE;
    public ChessPiece capturedPiece = null;
    public byte startSquare;
    public byte endSquare;
    
    //Non-castling
    public Move(Board b, byte startSquare, byte endSquare, boolean whiteToMove, 
                PieceID promotionPiece, boolean isEnPassant) {
        this.whiteToMove = whiteToMove;
        this.startSquare = startSquare;
        this.endSquare = endSquare;
        ChessPiece targetPiece = b.getPieceFromSquare(startSquare);
        //Check if castling
        if (targetPiece.id == PieceID.PAWN) {
            if ((BoardMethods.getRank(endSquare) == LAST_RANK_WHITE && whiteToMove) ||
                 BoardMethods.getRank(endSquare) == LAST_RANK_BLACK && !whiteToMove) {
                isPromotion = promotionPiece;
            }
        }
        if (!isEnPassant) {
            capturedPiece = b.getPieceFromSquare(endSquare);
        }
        else {
            capturedPiece = b.getPieceFromSquare((byte)(endSquare - 1));
        }   
    }

    

    //Castling
    public Move(boolean whiteToMove, boolean isKingsideCastle, boolean isQueensideCastle) {
        this.whiteToMove = whiteToMove;
        this.isKingsideCastle = isKingsideCastle;
        this.isQueensideCastle = isQueensideCastle;
    }

    public String toString() {
        return null;
    }
}
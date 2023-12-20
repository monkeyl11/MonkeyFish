class Board {
    private static final byte bitmaskRank = 0b00000111;
    private static final byte bitmaskFile = 0b00111000;

    private boolean whiteToMove;
    private ChessPiece[][] board;

    public Board() {
        board = new ChessPiece[8][8];
        
    }

    public ChessPiece getPieceFromSquare(byte square, Board b) {
        return board[][];
    }
}
class Board {
    private static final byte bitmaskRank = 0b00000111;
    private static final byte bitmaskFile = 0b00111000;

    public ChessPiece[][] board;

    public Board() {
        board = new ChessPiece[8][8];
    }

    public void setUpBoard() {
        board[0][0] = new Rook((byte)0, Color.WHITE);
        board[1][0] = new Knight((byte)8, Color.WHITE);
        board[4][5] = new Bishop((byte)37, Color.WHITE);
        board[3][0] = new Queen((byte)24, Color.WHITE);
        board[4][0] = new King((byte)32, Color.WHITE);
        board[5][0] = new Bishop((byte)40, Color.WHITE);
        board[6][0] = new Knight((byte)48, Color.WHITE);
        board[7][0] = new Rook((byte)56, Color.WHITE);

        board[0][7] = new Rook((byte)7, Color.BLACK);
        board[1][7] = new Knight((byte)15, Color.BLACK);
        board[2][7] = new Bishop((byte)23, Color.BLACK);
        board[3][7] = new Queen((byte)31, Color.BLACK);
        board[4][7] = new King((byte)39, Color.BLACK);
        board[5][7] = new Bishop((byte)47, Color.BLACK);
        board[6][7] = new Knight((byte)55, Color.BLACK);
        board[7][7] = new Rook((byte)63, Color.BLACK);

        for (int i = 0; i < 8; i++) {
            board[i][1] = new Pawn((byte)(1 + 8 * i), Color.WHITE);
        }

        for (int i = 0; i < 8; i++) {
            board[i][6] = new Pawn((byte)(6 + 8 * i), Color.BLACK);
        }
    }

    public void makeMove(Move m) {
        if (m.isKingsideCastle) {
            if (m.color == Color.WHITE) {
                movePiece((byte)00100000, (byte)00111000);
            }
            else {
                movePiece((byte)00100111, (byte)00111111);
            }
        }
        if (m.isQueensideCastle) {

        }
        movePiece(m.startSquare, m.endSquare);
        //only use case is en passant really
        board[(m.capturedPiece.currentSquare & bitmaskFile) >> 3][m.capturedPiece.currentSquare & bitmaskRank] = null;
    }

    public void movePiece(byte start, byte end) {
        ChessPiece p = board[(start & bitmaskFile) >> 3][start & bitmaskRank];
        if (p == null) {
            System.out.println("error moving piece, no piece on square stupid");
            return;
        }
        board[(end & bitmaskFile) >> 3][end & bitmaskRank] = p;
        board[(start & bitmaskFile) >> 3][start & bitmaskRank] = null;
    }


    //be careful, undoing any move that isnt the last move may throw exceptions
    public void undoMove(Move m) {

    }

    public ChessPiece getPieceFromSquare(byte square) { 
        return board[(square & bitmaskFile) >> 3][square & bitmaskRank];
    }

    public String toString() {
        return BoardMethods.boardToString(this);
    }
}
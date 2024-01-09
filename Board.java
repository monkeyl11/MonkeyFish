class Board {
    private static final byte bitmaskRank = 0b00000111;
    private static final byte bitmaskFile = 0b00111000;
    private static final int BOARD_LENGTH = 8;

    public ChessPiece[][] board;

    public Board() {
        board = new ChessPiece[8][8];
    }

    public void setUpBoard() {
        board[0][0] = new Rook((byte)0, Color.WHITE);
        board[1][0] = new Knight((byte)8, Color.WHITE);
        board[2][0] = new Bishop((byte)16, Color.WHITE);
        board[3][0] = new Queen((byte)24, Color.WHITE);
        board[4][0] = new King((byte)32, Color.WHITE);
        board[5][0] = new Bishop((byte)40, Color.WHITE);
        board[6][0] = new Knight((byte)48, Color.WHITE);
        board[7][0] = new Rook((byte)56, Color.WHITE);
        ((Rook)board[7][0]).assignKingsideRook();
        ((Rook)board[0][0]).assignQueensideRook();

        board[0][7] = new Rook((byte)7, Color.BLACK);
        board[1][7] = new Knight((byte)15, Color.BLACK);
        board[2][7] = new Bishop((byte)23, Color.BLACK);
        board[3][7] = new Queen((byte)31, Color.BLACK);
        board[4][7] = new King((byte)39, Color.BLACK);
        board[5][7] = new Bishop((byte)47, Color.BLACK);
        board[6][7] = new Knight((byte)55, Color.BLACK);
        board[7][7] = new Rook((byte)63, Color.BLACK);
        ((Rook)board[7][7]).assignKingsideRook();
        ((Rook)board[0][7]).assignQueensideRook();


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
                movePiece((byte)0b00100000, (byte)0b00110000);
                movePiece((byte)0b00111000, (byte)0b00101000);
            }
            else {
                movePiece((byte)0b00100111, (byte)0b00110111);
                movePiece((byte)0b00111111, (byte)0b00101111);
            }
            return;
        }
        else if (m.isQueensideCastle) {
            if (m.color == Color.WHITE) {
                movePiece((byte)0b00100000, (byte)0b00010000);
                movePiece((byte)0b00000000, (byte)0b00011000);
            }
            else {
                movePiece((byte)0b00100111, (byte)0b00010111);
                movePiece((byte)0b00000111, (byte)0b00011111);
            }
            return;
        }
        if (m.capturedPiece != null) {
            board[(m.capturedPiece.currentSquare & bitmaskFile) >> 3][m.capturedPiece.currentSquare & bitmaskRank] = null;
        }
        movePiece(m.startSquare, m.endSquare);
    }

    //be careful, undoing any move that isnt the last move may throw exceptions
    public void undoMove(Move m) {
        if (m.isKingsideCastle) {
            if (m.color == Color.WHITE) {
                movePiece((byte)0b00110000, (byte)0b00100000);
                movePiece((byte)0b00101000, (byte)0b00111000);
            }
            else {
                movePiece((byte)0b00110111, (byte)0b00100111);
                movePiece((byte)0b00101111, (byte)0b00111111);
            }
            return;
        }
        else if (m.isQueensideCastle) {
            if (m.color == Color.WHITE) {
                movePiece((byte)0b00010000, (byte)0b00100000);
                movePiece((byte)0b00011000, (byte)0b00000000);
            }
            else {
                movePiece((byte)0b00010111, (byte)0b00100111);
                movePiece((byte)0b00011111, (byte)0b00000111);
            }
            return;
        }
        movePiece(m.endSquare, m.startSquare);
        if (m.capturedPiece != null) {
            board[(m.capturedPiece.currentSquare & bitmaskFile) >> 3][m.capturedPiece.currentSquare & bitmaskRank] = m.capturedPiece;
        }
    }

    public void movePiece(byte start, byte end) {
        ChessPiece p = board[(start & bitmaskFile) >> 3][start & bitmaskRank];
        if (p == null) {
            System.out.println("error moving piece, no piece on square stupid");
            System.out.println(BoardMethods.squareToString(start));
            return;
        }
        board[(end & bitmaskFile) >> 3][end & bitmaskRank] = p;
        board[(start & bitmaskFile) >> 3][start & bitmaskRank] = null;
        p.movePiece(end);
    }

    public ChessPiece placePiece(PieceID piece, byte square, Color c) {
        ChessPiece p = null;
        if (getPieceFromSquare(square) != null) {
            System.out.println("Warning: replacing piece already on "  + 
            BoardMethods.squareToString(square) + " with " + piece);
            System.out.println(this);
        }
        if (piece == PieceID.PAWN) {
            p = new Pawn(square, c);
        }
        else if (piece == PieceID.KNIGHT) {
            p = new Knight(square, c);
        }
        else if (piece == PieceID.BISHOP) {
            p = new Bishop(square, c);
        }
        else if (piece == PieceID.ROOK) {
            p = new Rook(square, c);
        }
        else if (piece == PieceID.QUEEN) {
            p = new Queen(square, c);
        }
        else if (piece == PieceID.KING) {
            p = new King(square, c);
        }
        board[(square & bitmaskFile) >> 3][square & bitmaskRank] = p;
        return p;
    }

    public void placePiece(ChessPiece p, byte square) {
        board[(square & bitmaskFile) >> 3][square & bitmaskRank] = p;
    }

    public void removePiece(byte square) {
        board[(square & bitmaskFile) >> 3][square & bitmaskRank] = null;
    }

    public ChessPiece getPieceFromSquare(byte square) { 
        return board[(square & bitmaskFile) >> 3][square & bitmaskRank];
    }

    //Debugging use only
    public ChessPiece getPieceFromSquare(String square) {
        return getPieceFromSquare(BoardMethods.stringToSquare(square));
    }

    //Only accounts for basic piece properties (location, color, type)
    //Does not account for special piece properties belonging to pawn, rook, king
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Board)) {
            return false;
        }
        Board b = (Board)o;
        for (byte i = 0; i < board.length * board.length; i++) {
            ChessPiece thisPiece = this.getPieceFromSquare(i);
            ChessPiece otherPiece = b.getPieceFromSquare(i);
            if (thisPiece != null) {
                if (!thisPiece.equals(otherPiece)) {
                    System.out.println("CONFLICT SQUARE: " + BoardMethods.squareToString(i));
                    System.out.println(b);
                    System.out.println(this);
                    return false;
                }

            }
            else {
                if (otherPiece != null) {
                    System.out.println(BoardMethods.squareToString(i));
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return BoardMethods.boardToString(this);
    }
}
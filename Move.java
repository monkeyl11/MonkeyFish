
class Move {
    public static final int LAST_RANK_WHITE = 7;
    public static final int LAST_RANK_BLACK = 0;

    public Color color; //whos turn it is

    public boolean isKingsideCastle = false;
    public boolean isQueensideCastle = false;
    public PieceID promotionPiece = PieceID.NONE;
    public ChessPiece currentPiece = null;
    public ChessPiece capturedPiece = null;
    public byte startSquare;
    public byte endSquare;
    
    public Move(ChessPiece currentPiece, ChessPiece capturedPiece, 
                byte endSquare, Color color, 
                PieceID promotionPiece, boolean isCheck) {
        this.color = color;
        this.startSquare = currentPiece.currentSquare;
        this.currentPiece = currentPiece;
        this.endSquare = endSquare;
        //Check if castling
        this.promotionPiece = promotionPiece;
        this.capturedPiece = capturedPiece;
    }

    //Non-castling
    public Move(ChessPiece currentPiece, ChessPiece capturedPiece, 
                byte endSquare, Color color, 
                PieceID promotionPiece) {
        this.color = color;
        this.startSquare = currentPiece.currentSquare;
        this.currentPiece = currentPiece;
        this.endSquare = endSquare;
        //Check if castling
        this.promotionPiece = promotionPiece;
        this.capturedPiece = capturedPiece;
    }

    public Move(ChessPiece currentPiece, ChessPiece capturedPiece, 
                byte endSquare, Color color) {
        this(currentPiece, capturedPiece, endSquare, color, PieceID.NONE);
    }

    //Castling
    public Move(Color color, boolean isKingsideCastle, boolean isQueensideCastle, ChessPiece king) {
        this.color = color;
        this.isKingsideCastle = isKingsideCastle;
        this.isQueensideCastle = isQueensideCastle;
        this.currentPiece = king;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Move)) {
            return false;
        }
        Move m = (Move)o;
        if (this.color == m.color) {
            if (m.isKingsideCastle == true && this.isKingsideCastle == true)
                return true;
            else if (m.isQueensideCastle == true && this.isQueensideCastle == true)
                return true;
            else {
                if (m.promotionPiece == this.promotionPiece
                    && m.currentPiece.equals(this.currentPiece)
                    && m.startSquare == this.startSquare && m.endSquare == this.endSquare) {
                    if (m.capturedPiece == null) {
                        return this.capturedPiece == null;
                    }
                    else {
                        return m.capturedPiece.equals(this.capturedPiece);
                    }
                }
            return false;
            }
        }
        else {
            return false;
        }  
    }

    @Override
    public int hashCode() {
        //random function
        return (color == Color.WHITE ? 1 : -1) * (endSquare * 100 + startSquare) * currentPiece.id.ordinal();
    }


    //Output moves the way stockfish does
    public String toStringSF() {
        return BoardMethods.squareToString(startSquare) + "" + BoardMethods.squareToString(endSquare);
    }

    @Override
    public String toString() {
        if (isKingsideCastle) {
            return "O-O";
        }
        else if (isQueensideCastle) {
            return "O-O-O";
        }
        String currentPiece = "";
        if (this.currentPiece.id == PieceID.KNIGHT) {
            currentPiece = "N";
        }
        else if (this.currentPiece.id == PieceID.BISHOP) {
            currentPiece = "B";
        }
        else if (this.currentPiece.id == PieceID.ROOK) {
            currentPiece = "R";
        }
        else if (this.currentPiece.id == PieceID.QUEEN) {
            currentPiece = "Q";
        }
        else if (this.currentPiece.id == PieceID.KING) {
            currentPiece = "K";
        }
        String promotionAddOn = "";
        if (promotionPiece == PieceID.KNIGHT) {
            promotionAddOn = " = N";
        }
        else if (promotionPiece == PieceID.BISHOP) {
            promotionAddOn = " = B";
        }
        else if (promotionPiece == PieceID.ROOK) {
            promotionAddOn = " = R";
        }
        else if (promotionPiece == PieceID.QUEEN) {
            promotionAddOn = " = Q";
        }
        if (capturedPiece != null) {
            return currentPiece + BoardMethods.squareToString(startSquare) + " x " + 
                    BoardMethods.squareToString(endSquare) + promotionAddOn;
        }
        return currentPiece + BoardMethods.squareToString(startSquare) + 
                " - " + BoardMethods.squareToString(endSquare) + promotionAddOn;
    }
}
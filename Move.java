import java.util.*;

class Move {
    public static final int LAST_RANK_WHITE = 7;
    public static final int LAST_RANK_BLACK = 0;

    public Color color; //whos turn it is

    public boolean isKingsideCastle = false;
    public boolean isQueensideCastle = false;
    public PieceID promotionPiece = PieceID.NONE;
    public ChessPiece targetPiece = null;
    public ChessPiece capturedPiece = null;
    public byte startSquare;
    public byte endSquare;
    
    //Non-castling
    public Move(ChessPiece targetPiece, ChessPiece capturedPiece, 
                byte endSquare, Color color, 
                PieceID promotionPiece) {
        this.color = color;
        this.startSquare = targetPiece.currentSquare;
        this.targetPiece = targetPiece;
        this.endSquare = endSquare;
        //Check if castling
        this.promotionPiece = promotionPiece;
        this.capturedPiece = capturedPiece;
    }

    public Move(ChessPiece targetPiece, ChessPiece capturedPiece, 
                byte endSquare, Color color) {
        this(targetPiece, capturedPiece, endSquare, color, PieceID.NONE);
    }

    //Castling
    public Move(Color color, boolean isKingsideCastle, boolean isQueensideCastle) {
        this.color = color;
        this.isKingsideCastle = isKingsideCastle;
        this.isQueensideCastle = isQueensideCastle;
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
                return m.promotionPiece == this.promotionPiece
                        && m.targetPiece == this.targetPiece
                        && m.capturedPiece == this.capturedPiece
                        && m.startSquare == this.startSquare && m.endSquare == this.endSquare;
            }
        }
        else {
            return false;
        }  
    }

    @Override
    public int hashCode() {
        //random function
        return (color == Color.WHITE ? 1 : -1) * (endSquare * 100 + startSquare) * targetPiece.id.ordinal();
    }

    public String toString() {
        if (isKingsideCastle) {
            return "O-O";
        }
        else if (isQueensideCastle) {
            return "O-O-O";
        }
        String targetPiece = "";
        if (this.targetPiece.id == PieceID.KNIGHT) {
            targetPiece = "N";
        }
        else if (this.targetPiece.id == PieceID.BISHOP) {
            targetPiece = "B";
        }
        else if (this.targetPiece.id == PieceID.ROOK) {
            targetPiece = "R";
        }
        else if (this.targetPiece.id == PieceID.QUEEN) {
            targetPiece = "Q";
        }
        else if (this.targetPiece.id == PieceID.KING) {
            targetPiece = "K";
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
            return targetPiece + BoardMethods.squareToString(startSquare) + " x " + 
                    BoardMethods.squareToString(endSquare) + promotionAddOn;
        }
        return targetPiece + BoardMethods.squareToString(startSquare) + 
                " - " + BoardMethods.squareToString(endSquare) + promotionAddOn;
    }
}
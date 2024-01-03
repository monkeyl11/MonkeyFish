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

    public String toString() {
        if (isKingsideCastle) {
            return "O-O";
        }
        else if (isQueensideCastle) {
            return "O-O-O";
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
            return BoardMethods.squareToString(startSquare) + " x " + BoardMethods.squareToString(endSquare) + promotionAddOn;
        }
        return BoardMethods.squareToString(startSquare) + " - " + BoardMethods.squareToString(endSquare) + promotionAddOn;
    }
}
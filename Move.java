import java.util.*;

class Move {
    public static final int LAST_RANK_WHITE = 7;
    public static final int LAST_RANK_BLACK = 0;

    public Color color; //whos turn it is

    public boolean isKingsideCastle = false;
    public boolean isQueensideCastle = false;
    public PieceID isPromotion = PieceID.NONE;
    public ChessPiece capturedPiece = null;
    public byte startSquare;
    public byte endSquare;
    
    //Non-castling
    public Move(Board b, byte startSquare, byte endSquare, Color color, 
                PieceID promotionPiece, boolean isEnPassant) {
        this.color = color;
        this.startSquare = startSquare;
        this.endSquare = endSquare;
        ChessPiece targetPiece = b.getPieceFromSquare(startSquare);
        //Check if castling
        if (targetPiece.id == PieceID.PAWN) {
            if ((BoardMethods.getRank(endSquare) == LAST_RANK_WHITE && color == Color.WHITE) ||
                 BoardMethods.getRank(endSquare) == LAST_RANK_BLACK && color == Color.BLACK) {
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

    public Move(Board b, byte startSquare, byte endSquare, Color color) {
        this(b, startSquare, endSquare, color, PieceID.NONE, false);
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
        if (capturedPiece != null) {
            return BoardMethods.squareToString(startSquare) + " x " + BoardMethods.squareToString(endSquare);
        }
        return BoardMethods.squareToString(startSquare) + " - " + BoardMethods.squareToString(endSquare);
    }
}
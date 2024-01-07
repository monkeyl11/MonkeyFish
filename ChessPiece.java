import java.util.*;

abstract class ChessPiece {
    public PieceID id;
    public double pieceMaterialValue;
    public Color pieceColor;
    protected byte currentSquare;
    protected List<Byte>[] movesForSquare;
    private static int specialIDCounter;
    public int specialID;

    protected static final byte bitmaskRank = 0b00000111;
    protected static final byte bitmaskFile = 0b00111000;

    public ChessPiece(double pieceMaterialValue, byte currentSquare, Color pieceColor) {
        this.pieceMaterialValue = pieceMaterialValue;
        this.currentSquare = currentSquare;
        this.pieceColor = pieceColor;
        this.specialID = specialIDCounter;
        specialIDCounter++;
    }

    //undoMove parameter here for consistency's sake for movePiece method calls between different pieces
    //Rook, King, and Pawn all require this parameter
    public void movePiece(byte newSquare) {
        this.currentSquare = newSquare;
    }

    protected boolean withinBoard(int rank, int file) {
        return rank < 8 && rank >= 0 && file < 8 && file >= 0;
    }

    //For Bishop, Rook, Queen
    protected void checkLine(Board b, List<Move> moveList, int fileIncrement, int rankIncrement) {
        int file = BoardMethods.getFile(currentSquare);
        int rank = BoardMethods.getRank(currentSquare);
        while (withinBoard(file += fileIncrement, rank += rankIncrement)) {
            ChessPiece p = b.board[file][rank];
            if (p != null) {
                if (p.pieceColor != this.pieceColor) {
                    moveList.add(new Move(this, p, (byte)((file << 3) + rank), this.pieceColor));
                }
                break;
            }
            else {
                moveList.add(new Move(this, null, (byte)((file << 3) + rank), this.pieceColor));
            }
        }
    }

    abstract void possibleMoves(Board b, List<Move> moveList, OppPieceInfo pieceInfo);

    public char toChar() {
        char ret = ' ';
        if(this.id == PieceID.PAWN) {
            ret = 'p';
        }
        else if (this.id == PieceID.KNIGHT) {
            ret = 'n';
        }
        else if (this.id == PieceID.BISHOP) {
            ret = 'b';
        }
        else if (this.id == PieceID.ROOK) {
            ret = 'r';
        }
        else if (this.id == PieceID.QUEEN) {
            ret = 'q';
        }
        else if (this.id == PieceID.KING) {
            ret = 'k';
        }
        if (this.pieceColor == Color.WHITE && ret != ' ') {
            ret = Character.toUpperCase(ret);
        }
        return ret;
    }    

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ChessPiece)) {
            return false;
        }
        ChessPiece c = (ChessPiece)o;
        return c.id == this.id && c.pieceColor == this.pieceColor && c.specialID == this.specialID;
    }

    @Override
    public int hashCode() {
        //random function
        return specialID;
    }

    //Mostly for debugging
    public String toString() {
        return pieceColor + " " + id + "-" + BoardMethods.squareToString(currentSquare);
        //return "" + specialID; //Used for debugging sets
    }

    public static void resetIDCounter() {
        specialIDCounter = 0;
    }


}
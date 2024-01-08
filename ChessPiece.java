import java.util.*;

abstract class ChessPiece {

    public PieceID id;
    public double pieceMaterialValue;
    public Color pieceColor;
    protected byte currentSquare;
    protected List<Byte>[] movesForSquare;
    private static int specialIDCounter;
    private int specialID;

    protected static final byte bitmaskRank = 0b00000111;
    protected static final byte bitmaskFile = 0b00111000;
    private static final int END_SEARCH = 2;

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
    protected void checkLine(Board b, List<Move> moveList, int fileIncrement, int rankIncrement, 
                            OppPieceInfo pieceInfo) {
        int file = BoardMethods.getFile(currentSquare);
        int rank = BoardMethods.getRank(currentSquare);
        int stopNum = 0; //when stopNum = 1, stop adding moves, when = 2, exit
        ChessPiece potentialPinnedPiece = null;
        Pawn potentialEPHazard = null;
        while (withinBoard(file += fileIncrement, rank += rankIncrement)) {
            ChessPiece p = b.board[file][rank];
            if (p != null) {
                if (p.pieceColor != this.pieceColor) {
                    if (stopNum == 0) {
                        if (p.id == PieceID.KING) {
                            if (pieceInfo != null) {
                                pieceInfo.setChecking((King)p);
                                //immediately end search line
                                stopNum++;
                            }
                            else {
                                System.out.println("Illegal Position! - " + b);
                            }
                        }
                        else {
                            potentialPinnedPiece = p;
                        }
                        moveList.add(new Move(this, p, (byte)((file << 3) + rank), this.pieceColor));
                    }
                    else if (stopNum == 1) {
                        if (p.id == PieceID.KING) {
                            if (potentialPinnedPiece != null) {
                                pieceInfo.addPinnedPiece(potentialPinnedPiece, fileIncrement, rankIncrement);
                            }
                            else if (potentialEPHazard != null) {
                                pieceInfo.setEnPassantHazard(potentialEPHazard);
                            }
                        }
                    }
                }
                else {
                    if (pieceInfo != null && stopNum == 0) {
                        pieceInfo.addHazardSquare((byte)((file << 3) + rank));
                    }
                    if (p.id == PieceID.PAWN && ((Pawn)p).canEnPassant()) {
                        potentialEPHazard = (Pawn)p;
                    }
                    else {
                        stopNum++; //end the search
                    }
                }
                if (pieceInfo == null)
                    stopNum += END_SEARCH;
                else
                    stopNum++;
                if (stopNum >= END_SEARCH)
                    break;
            }
            else {
                moveList.add(new Move(this, null, (byte)((file << 3) + rank), this.pieceColor));
            }
            if (pieceInfo != null && stopNum == 0) {
                pieceInfo.addHazardSquare((byte)((file << 3) + rank));
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

    // protected void pieceInfoAddSquare(byte square, OppPieceInfo o) {
    //     if (o != null)
    //         o.addHazardSquare(square);
    // }

    // protected void pieceInfoAddPin(ChessPiece pinnedPiece, int fileDirection, int rankDirection, 
    //                                 OppPieceInfo o) {
    //     if (o != null)
    //         o.addPinnedPiece(pinnedPiece, fileDirection, rankDirection);
    // }

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
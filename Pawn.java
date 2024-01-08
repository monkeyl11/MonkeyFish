import java.util.*;

class Pawn extends ChessPiece {
    //private int enPassantTurn = 0; //tracks what turn pawn can be en-passanted, 0 means none
    private final static int FIRST_RANK_WHITE = 1;
    private final static int LAST_RANK_WHITE = 7;
    private final static int EN_PASSANT_RANK_WHITE = 4;
    private final static int FIRST_RANK_BLACK = 6;
    private final static int LAST_RANK_BLACK = 0;
    private final static int EN_PASSANT_RANK_BLACK = 3;
    private final static int INITIAL_PAWN_STEP = 2;
    private boolean enPassant;

    public int enPassantTurn = -1;

    public Pawn(byte currentSquare, Color pieceColor) {
        //add pawn to square currentSquare
        super(1, currentSquare, pieceColor);
        this.id = PieceID.PAWN;
        this.enPassant = false;
    }

    //Method not in use as it cannot account for en passant
    public void possibleMoves(Board b, List<Move> moveList, OppPieceInfo pieceInfo) {
        int pieceRank = this.currentSquare & bitmaskRank;
        int pieceFile = (this.currentSquare & bitmaskFile) >> 3;
        if (this.pieceColor == Color.WHITE) {
            //forward moving pawns
            if (b.board[pieceFile][pieceRank + 1] == null) {
                byte targetSquare = (byte)(currentSquare + 1);
                if (pieceRank + 1 == LAST_RANK_WHITE) {
                    addPromotions(targetSquare, moveList, null);
                }
                else {
                    moveList.add(new Move(this, null, targetSquare, this.pieceColor));
                    if (pieceRank == FIRST_RANK_WHITE && b.board[pieceFile][pieceRank + INITIAL_PAWN_STEP] == null) {
                        moveList.add(new Move(this, null, (byte)(targetSquare + 1), this.pieceColor));
                    }
                }
            }
            //capture right/left
            int boardLength = 8;
            for (int i = -1; i <= 1; i += 2) {
                if (pieceFile + i >= 0 && pieceFile + i < boardLength) {
                    byte targetSquare = (byte)(currentSquare + 1 + i * boardLength);
                    if (b.board[pieceFile + i][pieceRank + 1] != null) {
                        if (b.board[pieceFile + i][pieceRank + 1].pieceColor == Color.BLACK) {
                            ChessPiece capturedPiece = b.getPieceFromSquare(targetSquare);
                            if (capturedPiece.id == PieceID.KING) {
                                if (pieceInfo != null)
                                    pieceInfo.setChecking((King)capturedPiece);
                                else
                                    System.out.println("Illegal Position! - " + b);
                            }
                            if (pieceRank + 1 == LAST_RANK_WHITE) {
                                addPromotions(targetSquare, moveList, capturedPiece);
                            }
                            else {
                                moveList.add(new Move(this, capturedPiece, targetSquare, this.pieceColor));
                            }
                        }
                    }
                    //en passant
                    else if (pieceRank == EN_PASSANT_RANK_WHITE && b.board[pieceFile + i][pieceRank] != null
                        && b.board[pieceFile + i][pieceRank].pieceColor == Color.BLACK
                        && b.board[pieceFile + i][pieceRank].id == PieceID.PAWN) {
                        Pawn p = (Pawn)b.board[pieceFile + i][pieceRank];
                        if (p.canEnPassant())
                            moveList.add(new Move(this, p, targetSquare, this.pieceColor));
                    }
                    if (pieceInfo != null) {
                        pieceInfo.addHazardSquare(targetSquare);
                    }
                }
            }
        }
        else {
            //BLACK
            //forward moving pawns
            if (b.board[pieceFile][pieceRank - 1] == null) {
                byte targetSquare = (byte)(currentSquare - 1);
                if (pieceRank - 1 == LAST_RANK_BLACK) {
                    addPromotions(targetSquare, moveList, null);
                }
                else {
                    moveList.add(new Move(this, null, targetSquare, this.pieceColor));
                    if (pieceRank == FIRST_RANK_BLACK && b.board[pieceFile][pieceRank - INITIAL_PAWN_STEP] == null) {
                        moveList.add(new Move(this, null, (byte)(targetSquare - 1), this.pieceColor));
                    }
                }
            }
            //capture right/left
            int boardLength = 8;
            for (int i = -1; i <= 1; i += 2) {
                if (pieceFile + i >= 0 && pieceFile + i < boardLength) {
                    byte targetSquare = (byte)(currentSquare - 1 + i * boardLength);
                    if (b.board[pieceFile + i][pieceRank - 1] != null) {
                        if (b.board[pieceFile + i][pieceRank - 1].pieceColor == Color.WHITE) {
                            ChessPiece capturedPiece = b.getPieceFromSquare(targetSquare);
                            if (capturedPiece.id == PieceID.KING) {
                                if (pieceInfo != null)
                                    pieceInfo.setChecking((King)capturedPiece);
                                else
                                    System.out.println("Illegal Position! - " + b);
                            }
                            if (pieceRank - 1 == LAST_RANK_BLACK) {
                                addPromotions(targetSquare, moveList, capturedPiece);
                            }
                            else {
                                moveList.add(new Move(this, capturedPiece, targetSquare, this.pieceColor));
                            }
                        }
                    }
                    //en passant
                    else if (pieceRank == EN_PASSANT_RANK_BLACK && b.board[pieceFile + i][pieceRank] != null
                        && b.board[pieceFile + i][pieceRank].pieceColor == Color.WHITE
                        && b.board[pieceFile + i][pieceRank].id == PieceID.PAWN) {
                        Pawn p = (Pawn)b.board[pieceFile + i][pieceRank];
                        if (p.canEnPassant())
                            moveList.add(new Move(this, p, targetSquare, this.pieceColor));
                    }
                    if (pieceInfo != null) {
                        pieceInfo.addHazardSquare(targetSquare);
                    }
                }
            }
        }
    }

    public void addPromotions(byte endSquare, List<Move> moveList, ChessPiece capturedPiece) {
        moveList.add(new Move(this, capturedPiece, endSquare, this.pieceColor, PieceID.KNIGHT));
        moveList.add(new Move(this, capturedPiece, endSquare, this.pieceColor, PieceID.BISHOP));
        moveList.add(new Move(this, capturedPiece, endSquare, this.pieceColor, PieceID.ROOK));
        moveList.add(new Move(this, capturedPiece, endSquare, this.pieceColor, PieceID.QUEEN));
    }

    @Override
    public void movePiece(byte newSquare) {
        int rankDiff = BoardMethods.getRank(newSquare) - BoardMethods.getRank(this.currentSquare);
        super.movePiece(newSquare);
        //do this to avoid setting en passant flag on undoMove()
        if (rankDiff == INITIAL_PAWN_STEP) {
            this.enPassant = this.pieceColor == Color.WHITE ? true : false;
        }
        else if (rankDiff == -INITIAL_PAWN_STEP) {
            this.enPassant = this.pieceColor == Color.BLACK ? true : false;
        }
    }

    public void setEnPassant(boolean b) {
        this.enPassant = b;
    }

    public boolean canEnPassant() {
        return enPassant;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Pawn)) {
            return false;
        }
        Pawn p = (Pawn)o;
        return super.equals(o) && p.enPassant == this.enPassant;
    }
}
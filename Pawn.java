import java.util.*;

class Pawn extends ChessPiece {
    private int enPassantTurn = 0; //tracks what turn pawn can be en-passanted, 0 means none
    private final static int FIRST_RANK_WHITE = 1;
    private final static int LAST_RANK_WHITE = 7;
    private final static int EN_PASSANT_RANK_WHITE = 4;
    private final static int FIRST_RANK_BLACK = 6;
    private final static int LAST_RANK_BLACK = 0;
    private final static int EN_PASSANT_RANK_BLACK = 3;

    public Pawn(byte currentSquare, Color pieceColor) {
        //add pawn to square currentSquare
        super(1, currentSquare, pieceColor);
        id = PieceID.PAWN;
    }

    //Method not in use as it cannot account for en passant
    public void legalMoves(Board b, List<Move> moveList) {
        System.out.println("wrong legalmoves method");
        int x = 0/0; //lmao throw an exception pls
    };

    public void legalMoves(Board b, List<Move> moveList, int turn) {
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
                    if (pieceRank == FIRST_RANK_WHITE && b.board[pieceFile][pieceRank + 2] == null) {
                        moveList.add(new Move(this, null, (byte)(targetSquare + 1), this.pieceColor));
                        enPassantTurn = turn + 1; //next turn the pawn can be taken en passant
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
                        if (p.canEnPassant(turn))
                            moveList.add(new Move(this, p, targetSquare, this.pieceColor));
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
                    if (pieceRank == FIRST_RANK_BLACK && b.board[pieceFile][pieceRank - 2] == null) {
                        moveList.add(new Move(this, null, (byte)(targetSquare - 1), this.pieceColor));
                        enPassantTurn = turn + 1; //next turn the pawn can be taken en passant
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
                        if (p.canEnPassant(turn))
                            moveList.add(new Move(this, p, targetSquare, this.pieceColor));
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

    public boolean canEnPassant(int currentTurn) {
        return currentTurn == enPassantTurn;
    }
}
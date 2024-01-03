import java.util.*;

abstract class ChessPiece {
    public PieceID id;
    public double pieceMaterialValue;
    public Color pieceColor;
    protected byte currentSquare;
    protected List<Byte>[] movesForSquare;

    protected static final byte bitmaskRank = 0b00000111;
    protected static final byte bitmaskFile = 0b00111000;

    public ChessPiece(double pieceMaterialValue, byte currentSquare, Color pieceColor) {
        this.pieceMaterialValue = pieceMaterialValue;
        this.currentSquare = currentSquare;
        this.pieceColor = pieceColor;
    }

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
            System.out.println(BoardMethods.squareToString((byte)(8 * file + rank)));
            ChessPiece p = b.board[file][rank];
            if (p != null) {
                if (p.pieceColor != this.pieceColor) {
                    moveList.add(new Move(b , this.currentSquare, (byte)((file << 3) + rank), this.pieceColor));
                }
                break;
            }
            else {
                moveList.add(new Move(b , this.currentSquare, (byte)((file << 3) + rank), this.pieceColor));
            }
        }
    }

    abstract void legalMoves(Board b, List<Move> moveList);


}
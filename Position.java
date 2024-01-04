import java.util.*;

class Position {
    private static final byte WHITE_KING_SQUARE = 0b00100000;
    private static final byte WHITE_K_ROOK_SQUARE = 0b00111000;
    private static final byte WHITE_Q_ROOK_SQUARE = 0b00000000;

    private static final byte BLACK_KING_SQUARE = 0b00100111;
    private static final byte BLACK_K_ROOK_SQUARE = 0b00111111;
    private static final byte BLACK_Q_ROOK_SQUARE = 0b00000111;

    private static final int NEXT_FILE = 8;
    private static final int PREV_FILE = -8;


    public Board b;
    public Color sideToMove;
    private HashSet<ChessPiece> whitePieces;
    private HashSet<ChessPiece> blackPieces;
    //private HashMap<Position, Integer> prevPositions; //tracking three-fold
    private int turn; //track 50-move rule (100 turn), also en passant

    private int lastCapture;

    public Position(Board b, Color sideToMove, boolean autoSetBoard, int turn) {
        if (b.equals(new Board()) && autoSetBoard) {
            b.setUpBoard();
        }
        this.b = b;
        this.sideToMove = sideToMove;
        this.turn = turn;
        whitePieces = new HashSet<>();
        blackPieces = new HashSet<>();
        setUpPieceSets();
    }
    
    public Position(boolean setUpBoard) {
        this(new Board(), Color.WHITE, setUpBoard, 1);
    }

    public void setUpPieceSets() {
        ChessPiece p;
        for (byte i = 0; i < b.board.length * b.board.length; i++) {
            p = b.getPieceFromSquare(i);
            if (p != null) {
                if (p.pieceColor == Color.WHITE) {
                    whitePieces.add(p);
                }
                else {
                    blackPieces.add(p);
                }
            }
        }
    }

    //Takes care of castling and eliminates moves that put King in check
    public List<Move> legalMoves(Color side, boolean ignoreChecks) {
        ArrayList<Move> allMoves = new ArrayList<>();
        HashSet<ChessPiece> playerPieces = side == Color.WHITE ? whitePieces : blackPieces;
        for (ChessPiece p: playerPieces) {
            p.possibleMoves(this.b, allMoves);
        }
        addCastlingMoves(side, allMoves);
        if (!ignoreChecks) {
            //do later, remove moves that put the King in check, then return a different list/set
            return allMoves;

        }
        else {
            return allMoves;
        }
    }

    public List<Move> legalMoves() {
        return legalMoves(this.sideToMove, false);
    }

    public void addCastlingMoves(Color side, List<Move> moveList) {
        ChessPiece king, kRook, qRook;
        if (side == Color.WHITE) {
            king = b.getPieceFromSquare(WHITE_KING_SQUARE);
            kRook = b.getPieceFromSquare(WHITE_K_ROOK_SQUARE);
            qRook = b.getPieceFromSquare(WHITE_Q_ROOK_SQUARE);
        }
        else {
            king = b.getPieceFromSquare(BLACK_KING_SQUARE);
            kRook = b.getPieceFromSquare(BLACK_K_ROOK_SQUARE);
            qRook = b.getPieceFromSquare(BLACK_Q_ROOK_SQUARE);
        }
        //kingside castle
        if (king.id == PieceID.KING && kRook.id == PieceID.ROOK
            && king.pieceColor == side && kRook.pieceColor == side
            && b.getPieceFromSquare((byte)(king.currentSquare + NEXT_FILE)) == null
            && b.getPieceFromSquare((byte)(king.currentSquare + NEXT_FILE * 2)) == null) {   
            moveList.add(new Move(side, true, false));
        }
        //queenside castle
        if (king.id == PieceID.KING && kRook.id == PieceID.ROOK
            && king.pieceColor == side && kRook.pieceColor == side
            && b.getPieceFromSquare((byte)(king.currentSquare + PREV_FILE)) == null
            && b.getPieceFromSquare((byte)(king.currentSquare + PREV_FILE * 2)) == null
            && b.getPieceFromSquare((byte)(king.currentSquare + PREV_FILE * 3)) == null) {   
            moveList.add(new Move(side, false, true));
        }
    }

    public void makeMove(Move m) {
        b.makeMove(m);
        if (m.capturedPiece != null) {
            lastCapture = turn;
            if (sideToMove == Color.WHITE) {
                if (!blackPieces.remove(m.capturedPiece)) {
                    System.out.println("Error in piece removal");
                }
            }
            else {
                if (!whitePieces.remove(m.capturedPiece)) {
                    System.out.println("Error in piece removal");
                }
            }
        }
        turn++;
        sideToMove = sideToMove == Color.WHITE ? Color.BLACK : Color.WHITE;
    }

    //Should be used only for detecting "same" positions in three-fold repetition
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Position)) {
            return false;
        }
        Position p = (Position)o;
        return p.b.equals(this.b) && this.sideToMove == p.sideToMove
                && p.legalMoves().equals(this.legalMoves());
    }

    public String toString() {
        return b.toString() + "\n--" + this.sideToMove + " to move--";
    }
}
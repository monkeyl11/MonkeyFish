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

    private static final int EN_PASSANT_TARGET_RANK_W = 2;
    private static final int EN_PASSANT_TARGET_RANK_B = 5;

    private static final int ASCII_FILE_CONVERSION_DIFF = 97;
    private static final int ASCII_RANK_CONVERSION_DIFF = 49;



    public Board b;
    public Color activeColor;
    private HashSet<ChessPiece> whitePieces;
    private HashSet<ChessPiece> blackPieces;
    int[][] castlingRights; //0 for castling allowed, >0 = no castling, {{WK, WQ},{BK, BQ}}
    private Stack<Pawn> enPassantHistory; //for undoing moves
    private Stack<Integer> halfMoveHistory; //keeping track of halfmove counts

    private Stack<Move> prevMoves;


    //private HashMap<Position, Integer> prevPositions; //tracking three-fold
    private int turn; //track 50-move rule (100 turn), also en passant

    private int halfmoveClock = 0;

    public Position(Board b, Color activeColor, boolean autoSetBoard, int turn, int[][] castlingRights) {
        if (b.equals(new Board()) && autoSetBoard) {
            b.setUpBoard();
        }
        this.b = b;
        this.activeColor = activeColor;
        this.turn = turn;
        this.castlingRights = castlingRights;
        this.prevMoves = new Stack<>();
        this.enPassantHistory = new Stack<>();
        this.halfMoveHistory = new Stack<>();
        setUpPieceSets();
    }
    
    public Position(boolean setUpBoard) {
        this(new Board(), Color.WHITE, setUpBoard, 1, new int[][]{{0, 0}, {0, 0}});
    }

    //for duplicating a position object
    public Position(Position p) {
        //To be implemented if necessary
        throw new IllegalArgumentException("Constructor not implemented, do not use");
    }

    public Position(String fen) {
        String[] fields = fen.split(" ");
        
        //Field 1: Board setup
        b = new Board();
        String[] ranks = fields[0].split("/");
        if (ranks.length != 8) {
            throw new IllegalArgumentException("FEN Error: Board Setup");
        }
        for (int rank = 0; rank < b.board.length; rank++) {
            int file = 0;
            for (char s: ranks[b.board.length - rank - 1].toCharArray()) {
                int val = Character.getNumericValue(s);
                if (val > 9) {
                    //A piece must be placed
                    Color color = Character.isUpperCase(s) ? Color.WHITE : Color.BLACK;
                    s = Character.toLowerCase(s);
                    byte square = (byte)((file << 3) + rank);
                    b.placePiece(identifyPiece(s), square, color);
                    file++;
                }
                else {
                    //skip n squares
                    file += val;
                }
            }
        }

        setUpPieceSets();

        //Field 2: Active Color
        if (fields[1].length() != 1) {
            throw new IllegalArgumentException("FEN Error: Active Color, invalid field length");
        }
        if (fields[1].charAt(0) == 'w') {
            this.activeColor = Color.WHITE;
        }
        else if (fields[1].charAt(0) == 'b'){
            this.activeColor = Color.BLACK;
        }
        else {
            throw new IllegalArgumentException("FEN Error: Active Color, invalid color");
        }

        //Field 3: Castling Rights
        castlingRights = new int[][]{{1, 1}, {1, 1}};
        for (char s: fields[2].toCharArray()) {
            if (s == '-') {
                //Nobody castles
                break;
            }
            else if (s == 'Q') {
                castlingRights[0][1] = 0;
            }
            else if (s == 'K') {
                castlingRights[0][0] = 0;
                ChessPiece p = b.getPieceFromSquare((byte)0b00111000);
                if (p != null && p instanceof Rook) {
                    ((Rook)p).assignKingsideRook();
                }
                else {
                    throw new IllegalArgumentException("FEN Error: Castling Rights, Kingside White");
                }
            }
            else if (s == 'q') {
                castlingRights[1][1] = 0;
            }
            else if (s == 'k') {
                castlingRights[1][0] = 0;
                ChessPiece p = b.getPieceFromSquare((byte)0b00111111);
                if (p != null && p instanceof Rook) {
                    ((Rook)p).assignKingsideRook();
                }
                else {
                    throw new IllegalArgumentException("FEN Error: Castling Rights, Kingside Black");
                }
            }
            else {
                throw new IllegalArgumentException("FEN Error: Castling Rights, Illegal Character");
            }
        }

        //Field 4: En Passant Targets
        enPassantHistory = new Stack<>();
        Pawn enPassantPawn = null;
        if (!(fields[3].charAt(0) == '-')) {
            byte square = BoardMethods.stringToSquare(fields[3]);
            if (BoardMethods.getRank(square) == EN_PASSANT_TARGET_RANK_B) {
                //Black pawn target
                ChessPiece p = b.getPieceFromSquare((byte)(square - 1));
                if (p!= null && (p instanceof Pawn) && p.pieceColor == Color.BLACK) {
                    ((Pawn)p).setEnPassant(true);
                    enPassantPawn = (Pawn)p;
                }
                else {
                    throw new IllegalArgumentException("FEN Error: En Passant Targets, Invalid E/P piece");
                }
            }
            else if (BoardMethods.getRank(square) == EN_PASSANT_TARGET_RANK_W) {
                //Black pawn target
                ChessPiece p = b.getPieceFromSquare((byte)(square + 1));
                if ((p instanceof Pawn) && p.pieceColor == Color.WHITE) {
                    ((Pawn)p).setEnPassant(true);
                }
                else {
                    throw new IllegalArgumentException("FEN Error: En Passant Targets, Invalid E/P piece");
                }
            }
            else {
                throw new IllegalArgumentException("FEN Error: Invalid En Passant Target Rank");
            }
        }
        

        //Field 5: Halfmove Clock, Full move number
        this.halfMoveHistory = new Stack<>();
        try {
            halfmoveClock = Integer.parseInt(fields[4]);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("FEN Error: Invalid Halfmove Clock Number");
        }

        //Field 6:Fullmove number
        try {
            turn = (Integer.parseInt(fields[5]) - 1) * 2 + (activeColor == Color.WHITE ? 1 : 0);
            if (enPassantPawn != null) {
                enPassantHistory.add(enPassantPawn);
            }
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("FEN Error: Invalid Fullmove Number");
        }
    }

    public void setUpPieceSets() {
        whitePieces = new HashSet<>();
        blackPieces = new HashSet<>();
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
        //EXTREMELY inefficient, DO NOT use with engine, this is only for the player
        if (!ignoreChecks) {
            //do later, remove moves that put the King in check, then return a different list/set
            return allMoves;

        }
        else {
            return allMoves;
        }
    }

    public List<Move> legalMoves() {
        return legalMoves(this.activeColor, false);
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
        if (king == null)
            return;
        //kingside castle
        int crIndex = side == Color.WHITE ? 0 : 1;
        //System.out.println("Side castling rights: " + Arrays.toString(castlingRights[crIndex]));
        if (kRook != null && king.id == PieceID.KING && kRook.id == PieceID.ROOK
            && king.pieceColor == side && kRook.pieceColor == side
            && castlingRights[crIndex][0] == 0
            && b.getPieceFromSquare((byte)(king.currentSquare + NEXT_FILE)) == null
            && b.getPieceFromSquare((byte)(king.currentSquare + NEXT_FILE * 2)) == null) { 
            moveList.add(new Move(side, true, false));
        }
        //queenside castle
        if (qRook != null && king.id == PieceID.KING && qRook.id == PieceID.ROOK
            && king.pieceColor == side && qRook.pieceColor == side
            && b.getPieceFromSquare((byte)(king.currentSquare + PREV_FILE)) == null
            && castlingRights[crIndex][1] == 0
            && b.getPieceFromSquare((byte)(king.currentSquare + PREV_FILE * 2)) == null
            && b.getPieceFromSquare((byte)(king.currentSquare + PREV_FILE * 3)) == null) {   
            moveList.add(new Move(side, false, true));
        }
    }

    public void makeMove(Move m) {
        //TO DO: get rid of en passant for the other side
        boolean resetHMClock = false;
        b.makeMove(m);
        //Set opposing pawns' en passant variable to false
        if (!enPassantHistory.empty()) {
            Pawn lastPawn = enPassantHistory.peek();
            if (lastPawn.enPassantTurn == this.turn - 1) {
                lastPawn.setEnPassant(false);
            }
        }
        //Remove captured pieces
        if (m.capturedPiece != null) {
            halfMoveHistory.add(halfmoveClock);
            halfmoveClock = 0;
            resetHMClock = true;
            HashSet<ChessPiece> s = this.activeColor == Color.WHITE ? blackPieces : whitePieces;
            if (!s.remove(m.capturedPiece)) {
                System.out.println("Error in piece removal");                
            }
        }
        int crIndex = activeColor == Color.WHITE ? 0 : 1;
        if (m.isKingsideCastle || m.isQueensideCastle) {
            castlingRights[crIndex][0]++; castlingRights[crIndex][1]++; //update both indices when castled
        }
        else {
            if (m.currentPiece.id == PieceID.PAWN) {
                //update halfmove clock for 50-move rule
                if (!resetHMClock) {
                    halfMoveHistory.add(halfmoveClock);
                    halfmoveClock = 0;
                }
                resetHMClock = true;
                //pawn promotion
                if (m.promotionPiece != PieceID.NONE) {
                    HashSet<ChessPiece> s = this.activeColor == Color.WHITE ? whitePieces : blackPieces;
                    s.remove(m.currentPiece);
                    b.removePiece(m.endSquare);
                    placePiece(m.promotionPiece, m.endSquare, m.color);
                }
                //Pawn moves forward two squares, en passant-able
                if (Math.abs(m.endSquare - m.startSquare) == 2) {
                    Pawn p = (Pawn)(m.currentPiece);
                    p.enPassantTurn = this.turn;
                    enPassantHistory.add(p);
                }
            }
            else if (m.currentPiece.id == PieceID.KING) {
                //update castling rights
                castlingRights[crIndex][0]++; castlingRights[crIndex][1]++;
            }
            else if (m.currentPiece.id == PieceID.ROOK) {
                //update castling rights
                if (((Rook)m.currentPiece).kingsideRook) {
                    castlingRights[crIndex][0]++;
                }
                else {
                    castlingRights[crIndex][1]++;
                }
            }
        }
        if (!resetHMClock) {
            halfmoveClock++;
        }
        prevMoves.add(m);
        turn++;
        activeColor = activeColor == Color.WHITE ? Color.BLACK : Color.WHITE;
    }

    public boolean makeMove(String s) {
        Move m = algebraicNotationToMove(s);
        if (m == null) {
            System.out.println("Error making move, please see above message for cause");
            return false;
        }
        else {
            makeMove(m);
            return true;
        }
    }

    public void undoMove() {
        activeColor = activeColor == Color.WHITE ? Color.BLACK : Color.WHITE;
        turn--;
        if (prevMoves.isEmpty()) {
            System.out.println("Undo move FAIL: no previous move");
            return;
        }
        Move m = prevMoves.pop();
        //System.out.println("UNDOING MOVE: " + m);
        //take care of en passant
        if (!enPassantHistory.isEmpty()) {
            //Dont need to check for pawns to set en passant back to false
            //That is taken care of in Pawn.java (due to bad program structuring oops)
            Pawn p = enPassantHistory.peek();
            if (p.enPassantTurn == this.turn) {
                enPassantHistory.pop().setEnPassant(false);
                if (!enPassantHistory.isEmpty()) {
                    p = enPassantHistory.peek();
                }
            }
            if (p.enPassantTurn == this.turn - 1) {
                p.setEnPassant(true);
            }
        }
        //take care of captures
        HashSet<ChessPiece> s = this.activeColor == Color.WHITE ? blackPieces : whitePieces;
        if (m.capturedPiece != null)
            s.add(m.capturedPiece);
        //take care of move-specific possible rules
        int crIndex = this.activeColor == Color.WHITE ? 0 : 1;
        if (m.isKingsideCastle || m.isQueensideCastle) {
            castlingRights[crIndex][0]--; castlingRights[crIndex][1]--;
        }
        else {
            if (m.currentPiece.id == PieceID.PAWN) {
                //pawn promotion
                if (m.promotionPiece != PieceID.NONE) {
                    s = this.activeColor == Color.WHITE ? whitePieces : blackPieces;
                    s.remove(b.getPieceFromSquare(m.endSquare));
                    b.removePiece(m.endSquare);
                    placePiece(m.currentPiece);
                }
            }
            else if (m.currentPiece.id == PieceID.KING) {
                //update castling rights
                castlingRights[crIndex][0]--; castlingRights[crIndex][1]--;
            }
            else if (m.currentPiece.id == PieceID.ROOK) {
                //update castling rights
                if (((Rook)m.currentPiece).kingsideRook) {
                    castlingRights[crIndex][0]--;
                }
                else {
                    castlingRights[crIndex][1]--;
                }
            }
        }
        //undo move on board
        b.undoMove(m);
        //update halfmove clock
        if (halfmoveClock == 0) {
            halfmoveClock = halfMoveHistory.pop();
        }
        else {
            halfmoveClock--;
        }
    }

    private Move algebraicNotationToMove(String s) {
        List<Move> legalMoves = legalMoves();
        s = s.replace("#", ""); //checkmate
        s = s.replace("+", ""); //checks
        //castling
        if (s.toLowerCase().equals("o-o")) {
            for (Move m: legalMoves) {
                if (m.isKingsideCastle) {
                    return m;
                }
            }
            System.out.println("Castling is not on option");
            return null;
        }
        else if (s.toLowerCase().equals("o-o-o")) {
            for (Move m: legalMoves) {
                if (m.isQueensideCastle) {
                    return m;
                }
            }
            System.out.println("Castling is not on option");
            return null;            
        }
        //non-castling
        //strip out all unnecessary characters
        PieceID promotionPiece = PieceID.NONE;
        if (s.contains("=")) { //promotions
            if (s.indexOf("=") == s.length() - 2) {
                promotionPiece = identifyPiece(Character.toUpperCase(s.charAt(s.length() - 1)));
                if (promotionPiece == PieceID.NONE) {
                    System.out.println("Invalid promotion piece");
                    return null;
                }
                s = s.substring(0, s.length() - 2);
            }
            else {
                System.out.println("Invalid promotion notation");
                return null;
            }
        }
        byte endSquare;
        try {
            endSquare = BoardMethods.stringToSquare(s.substring(s.length() - 2, s.length()));
        }
        catch (NumberFormatException e) {
            System.out.println("Invalid move square");
            return null;
        }
        s = s.substring(0, s.length() - 2);
        PieceID currentPiece;
        int pawnFile = -1;
        if (s.length() == 0) {
            currentPiece = PieceID.PAWN;
        }
        else {
            currentPiece = identifyPiece(s.charAt(0));
            if (currentPiece == PieceID.NONE && s.contains("x")) {
                currentPiece = PieceID.PAWN;
                pawnFile = s.charAt(0) - ASCII_FILE_CONVERSION_DIFF;
                if (pawnFile < 0 || pawnFile >= b.board.length) {
                    System.out.println("Invalid move: no such file for pawn");
                    return null;
                }
            }
            else {
                s = s.substring(1, s.length());
            }
        }
        s = s.replace("x", ""); //captures
        //filter by piece, promotion, end square
        ArrayList<Move> possibleMoves = new ArrayList<>();
        for (Move m: legalMoves) {
            //dont add castling to possibleMove list, as it is already checked above
            if (m.currentPiece == null) {
                continue;
            }
            if (m.endSquare == endSquare && m.currentPiece.id == currentPiece 
                && m.promotionPiece == promotionPiece
                && (pawnFile == -1 || pawnFile == BoardMethods.getFile(m.currentPiece.currentSquare))) {
                possibleMoves.add(m);
                }
        }
        if (possibleMoves.isEmpty()) {
            System.out.println("Not a legal move");
            return null;
        }
        else if (possibleMoves.size() == 1) {
            return possibleMoves.get(0);
        }
        //Disambiguating Moves
        else {
            //assume file disambiguation first (standard practice)
            if (s.length() == 1) {
                int check = s.charAt(0);
                ArrayList<Move> possibleMovesTwo = new ArrayList<>();
                if (check - ASCII_FILE_CONVERSION_DIFF >= 0 && check - ASCII_FILE_CONVERSION_DIFF < 8) {
                    int file = s.charAt(0) - ASCII_FILE_CONVERSION_DIFF;
                    for (Move m: possibleMoves) {
                        if (BoardMethods.getFile((byte)(m.currentPiece.currentSquare)) == file) {
                            possibleMovesTwo.add(m);
                        }
                    }
                }
                else if (check - ASCII_RANK_CONVERSION_DIFF >= 0 && check - ASCII_RANK_CONVERSION_DIFF < 8) {
                    int rank = s.charAt(0) - ASCII_RANK_CONVERSION_DIFF;
                    for (Move m: possibleMoves) {
                        if (BoardMethods.getRank((byte)(m.currentPiece.currentSquare)) == rank) {
                            possibleMovesTwo.add(m);
                        }
                    }
                }
                else {
                    System.out.println("Error with move disambiguation");
                    return null;
                }

                if (possibleMovesTwo.isEmpty()) {
                    System.out.println("Could not find given piece on given rank/file");
                    return null;
                }
                else if (possibleMovesTwo.size() == 1) {
                    return possibleMovesTwo.get(0);
                }
                else {
                    System.out.println("Multiple possible moves given your notation, exact square required");
                    return null;
                }

            }
            else if (s.length() == 2) {
                byte square = 0;
                try {
                    square = BoardMethods.stringToSquare(s.substring(0, 2));
                }
                catch (Exception e) {
                    System.out.println("Invalid disambiguation square");
                    return null;
                }
                for (Move m: possibleMoves) {
                    if (m.currentPiece.currentSquare == square) {
                        return m;
                    }
                }
                System.out.println("Could not find given piece on given square");
                return null;
            }
            else {
                if (s.length() == 0) {
                    System.out.println("Multiple possible moves given your notation, disambiguation required");
                }
                else {
                    System.out.println("Unknown notation error");
                }
                return null;
            }
        }
    }

    public PieceID identifyPiece(char c) {
        if (c == 'P'){
            return PieceID.PAWN;
        }
        else if (c == 'N') {
            return PieceID.KNIGHT;
        }
        else if (c == 'B') {
            return PieceID.BISHOP;
        }
        else if (c == 'R') {
            return PieceID.ROOK;
        }
        else if (c == 'Q') {
            return PieceID.QUEEN;
        }
        else if (c == 'K') {
            return PieceID.KING;
        }
        else {
            return PieceID.NONE;
        }
    }

    public void placePiece(PieceID piece, byte square, Color c) {
        HashSet<ChessPiece> s = c == Color.WHITE ? whitePieces : blackPieces;
        s.add(this.b.placePiece(piece, square, c));
    }

    public void placePiece(ChessPiece p) {
        HashSet<ChessPiece> s = p.pieceColor == Color.WHITE ? whitePieces : blackPieces;
        s.add(p);
        b.placePiece(p, p.currentSquare);
    }

    //equals() method but takes into account ALL class members, not just those needed by three-fold
    //mainly used for debugging undoMove()
    public boolean exactlyEquals(Position p) {
        if (p == this)
            return true;
        //System.out.println(this.whitePieces);
        // try {
        //     System.out.println("CORRECT: " + ((Pawn)(p.b.getPieceFromSquare("b7"))).canEnPassant());
        //     System.out.println("AFTER UNDO: " + ((Pawn)(this.b.getPieceFromSquare("b7"))).canEnPassant());
        // } catch (Exception e) {
        //     // TODO: handle exception
        // }


        return p.b.equals(this.b) && p.activeColor == this.activeColor 
                && p.whitePieces.equals(this.whitePieces)
                && p.blackPieces.equals(this.blackPieces)
                && Arrays.equals(this.castlingRights[0], p.castlingRights[0])
                && Arrays.equals(this.castlingRights[1], p.castlingRights[1])
                && p.enPassantHistory.equals(this.enPassantHistory)
                && p.halfMoveHistory.equals(this.halfMoveHistory)
                && p.prevMoves.equals(this.prevMoves)
                && p.turn == this.turn
                && p.halfmoveClock == this.halfmoveClock;
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
        return p.b.equals(this.b) && this.activeColor == p.activeColor
                && p.legalMoves().equals(this.legalMoves());
    }

    public String toString() {
        return b.toString() + "\n--" + this.activeColor + " to move--\n";
    }
}
import java.util.*;


class Position {
    //Using binary for now because im too lazy to use another enum, will change later
    //Also it looks cool :pleading_emoji:
    private static final byte WK_SQUARE = 0b00100000;
    private static final byte WK_ROOK_SQUARE = 0b00111000;
    private static final byte WQ_ROOK_SQUARE = 0b00000000;
    private static final byte[] WK_CASTLING_SQUARES = {0b00101000, 0b00110000};
    private static final byte[] WQ_CASTLING_SQUARES = {0b00010000, 0b00011000};

    private static final byte BK_SQUARE = 0b00100111;
    private static final byte BK_ROOK_SQUARE = 0b00111111;
    private static final byte BQ_ROOK_SQUARE = 0b00000111;
    //private static final byte[] BK_CASTLING_SQUARES = {0b00101111, 0b00110111};
    //private static final byte[] BQ_CASTLING_SQUARES = {0b00010111, 0b00011111};

    private static final int NEXT_FILE = 8;
    private static final int PREV_FILE = -8;

    private static final int EN_PASSANT_TARGET_RANK_W = 2;
    private static final int EN_PASSANT_TARGET_RANK_B = 5;

    private static final int ASCII_FILE_CONVERSION_DIFF = 97;
    private static final int ASCII_RANK_CONVERSION_DIFF = 49;

    private static final byte BOARD_END_INDEX = 7;

    private static final byte bitmaskRank = 0b00000111;
    private static final byte bitmaskFile = 0b00111000;


    public Board b;
    public Color activeColor;
    public HashSet<ChessPiece> whitePieces;
    public HashSet<ChessPiece> blackPieces;
    public PositionStatus positionStatus;
    private int[][] castlingRights; //0 for castling allowed, >0 = no castling, {{WK, WQ},{BK, BQ}}
    private Stack<Pawn> enPassantHistory; //for undoing moves
    private Stack<Integer> halfMoveHistory; //keeping track of halfmove counts

    public Stack<Move> prevMoves;


    //private HashMap<Position, Integer> prevPositions; //tracking three-fold
    public int turn; //track 50-move rule (100 turn), also en passant
    private int halfmoveClock = 0;
    private boolean gameOver = false;

    private long TTHash; //Stores the hash for the current position

    public Position(Board b, Color activeColor, boolean autoSetBoard, int turn, int[][] castlingRights) {
        if (b.equals(new Board()) && autoSetBoard) {
            b.setUpBoard();
        }
        this.b = b;
        this.activeColor = activeColor;
        this.turn = turn;
        this.positionStatus = new PositionStatus();
        positionStatus.turn = activeColor;
        this.castlingRights = castlingRights;
        this.prevMoves = new Stack<>();
        this.enPassantHistory = new Stack<>();
        this.halfMoveHistory = new Stack<>();
        setUpPieceSets();
        initTTHash();
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
        prevMoves = new Stack<>();
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
                    s = Character.toUpperCase(s);
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
        this.positionStatus = new PositionStatus();
        positionStatus.turn = this.activeColor;

        //Field 3: Castling Rights
        castlingRights = new int[][]{{1, 1}, {1, 1}};
        ChessPiece kR = b.getPieceFromSquare((byte)0b00111000);
        if (kR != null && kR instanceof Rook && kR.pieceColor == Color.WHITE) {
            ((Rook)kR).assignKingsideRook();
        }
        kR = b.getPieceFromSquare((byte)0b00111111);
        if (kR != null && kR instanceof Rook && kR.pieceColor == Color.BLACK) {
            ((Rook)kR).assignKingsideRook();
        }
        ChessPiece qR = b.getPieceFromSquare((byte)0b00000000);
        if (qR != null && qR instanceof Rook && qR.pieceColor == Color.WHITE) {
            ((Rook)qR).assignQueensideRook();
        }
        qR = b.getPieceFromSquare((byte)0b00000111);
        if (qR != null && qR instanceof Rook && qR.pieceColor == Color.BLACK) {
            ((Rook)qR).assignQueensideRook();
        }
        for (char s: fields[2].toCharArray()) {
            if (s == '-') {
                //Nobody castles
                break;
            }
            else if (s == 'Q') {
                castlingRights[0][1] = 0;
                ChessPiece p = b.getPieceFromSquare((byte)0b00000000);
                if (p == null || !(p instanceof Rook)) {
                    throw new IllegalArgumentException("FEN Error: Castling Rights, Queenside White");
                }
            }
            else if (s == 'K') {
                castlingRights[0][0] = 0;
                ChessPiece p = b.getPieceFromSquare((byte)0b00111000);
                if (p == null || !(p instanceof Rook)) {
                    throw new IllegalArgumentException("FEN Error: Castling Rights, Kingside White");
                }
            }
            else if (s == 'q') {
                castlingRights[1][1] = 0;
                ChessPiece p = b.getPieceFromSquare((byte)0b00000111);
                if (p == null || !(p instanceof Rook)) {
                    throw new IllegalArgumentException("FEN Error: Castling Rights, Queenside White");
                }
            }
            else if (s == 'k') {
                castlingRights[1][0] = 0;
                ChessPiece p = b.getPieceFromSquare((byte)0b00111111);
                if (p == null || !(p instanceof Rook)) {
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
                //White pawn target
                ChessPiece p = b.getPieceFromSquare((byte)(square + 1));
                if ((p instanceof Pawn) && p.pieceColor == Color.WHITE) {
                    ((Pawn)p).setEnPassant(true);
                    enPassantPawn = (Pawn)p;
                }
                else {
                    throw new IllegalArgumentException("FEN Error: En Passant Targets, Invalid E/P piece");
                }
            }
            else {
                throw new IllegalArgumentException("FEN Error: Invalid En Passant Target Rank");
            }
        }
        

        //Field 5: Halfmove Clock
        this.halfMoveHistory = new Stack<>();
        try {
            halfmoveClock = Integer.parseInt(fields[4]);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("FEN Error: Invalid Halfmove Clock Number");
        }

        //Field 6:Fullmove number
        try {
            turn = (Integer.parseInt(fields[5]) - 1) * 2 + 1 + (activeColor == Color.WHITE ? 0 : 1);
            if (enPassantPawn != null) {
                enPassantPawn.enPassantTurn = turn - 1;
                enPassantHistory.add(enPassantPawn);
            }
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("FEN Error: Invalid Fullmove Number");
        }
        initTTHash();
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
        ArrayList<Move> allMoves = new ArrayList<>(64);
        HashSet<ChessPiece> playerPieces = side == Color.WHITE ? whitePieces : blackPieces;
        for (ChessPiece p: playerPieces) {
            p.possibleMoves(this.b, allMoves, null);
        }
        addCastlingMoves(side, allMoves);
        if (!ignoreChecks) {
            List<Move> m = checkRemove(allMoves);
            return m;
            //do later, remove moves that put the King in check, then return a different list/set
            //return checkRemove(allMoves);
        }
        else {
            return allMoves;
        }
    }

    public List<Move> legalMoves() {
        return legalMoves(this.activeColor, false);
    }

    private List<Move> checkRemove(List<Move> possibleMoves) {
        ArrayList<Move> moveList = new ArrayList<>(64);
        /* NEED to do:
        1. Remove King moves that go to a square in check
        2. Remove moves from pieces pinned to the king that put the King in check
            -YOU CAN EN PASSANT INTO CHECK
        3. Remove moves that do not move the King out of check (when in check)
            -Remove the checking piece, block the checking piece, move the King out of check */
        
        ArrayList<Move> oppMoveList = new ArrayList<>(64);
        OppPieceInfo.reset();
        List<OppPieceInfo> oppPieceInfo = kingSafetyInfo(oppMoveList);
        HashSet<Byte> invalidSquares = OppPieceInfo.hazardSquares;
        ArrayList<ChessPiece> checkingPieces = new ArrayList<>(2);
        ArrayList<ChessPiece> pinnedPieces = new ArrayList<>(4);
        ArrayList<ChessPiece> pinners = new ArrayList<>(4);
        
        Pawn enPassantHazard = null;
        ChessPiece EPCheckingPiece = null;
        for (OppPieceInfo piece: oppPieceInfo) {
            if (piece.isChecking) {
                checkingPieces.add(piece.oppPiece);
            }
            if (piece.pinnedPiece != null) {
                pinnedPieces.add(piece.pinnedPiece);
                pinners.add(piece.oppPiece);
            }
            if (piece.enPassantHazard != null) {
                enPassantHazard = piece.enPassantHazard;
                EPCheckingPiece = piece.oppPiece;
            }
        }
        int pinnedPieceIndex;
        PieceID currentPieceID;
        byte kingSquare = getKing().currentSquare;
        if (!checkingPieces.isEmpty()) {
            positionStatus.inCheck = true;
        }
        for (Move m: possibleMoves) {
            currentPieceID = m.currentPiece.id;
            byte isBlack = m.color == Color.WHITE ? 0 : BOARD_END_INDEX;
            //In check
            if (!checkingPieces.isEmpty()) {
                if (currentPieceID == PieceID.KING) {
                    if (invalidSquares.contains(m.endSquare))
                        continue;
                }
                else if (checkingPieces.size() > 1) {
                    continue;
                }
                else {
                    //only one checking piece
                    ChessPiece checkingPiece = checkingPieces.get(0);
                    if (m.capturedPiece != checkingPieces.get(0)) {
                        //move did not capture checking piece
                        if (checkingPiece.id == PieceID.KNIGHT
                            || checkingPiece.id == PieceID.PAWN) {
                            continue;
                        }
                        else {
                            if (!collinear(checkingPiece.currentSquare, m.endSquare, kingSquare)) {
                                continue;
                            }
                        }
                        
                    }
                }
            }
            if (m.isKingsideCastle) {
                if (!checkingPieces.isEmpty() || invalidSquares.contains((byte)(WK_CASTLING_SQUARES[0] + isBlack)) 
                    || invalidSquares.contains((byte)(WK_CASTLING_SQUARES[1] + isBlack))) {
                    continue;
                }
            }
            else if (m.isQueensideCastle) {
                if (!checkingPieces.isEmpty() || invalidSquares.contains((byte)(WQ_CASTLING_SQUARES[0] + isBlack)) 
                    || invalidSquares.contains((byte)(WQ_CASTLING_SQUARES[1] + isBlack))) {
                    continue;
                }
            }
            else {
                //Not castling
                if (currentPieceID == PieceID.KING) {
                    if (invalidSquares.contains(m.endSquare))
                        continue;
                }
                else {
                    if (currentPieceID == PieceID.PAWN) {
                        if (m.capturedPiece != null && m.capturedPiece.equals(enPassantHazard)
                            && (m.startSquare & bitmaskRank) == (m.capturedPiece.currentSquare & bitmaskRank)) {
                            //VERY RARE CASE TEST
                            if (!collinear(EPCheckingPiece.currentSquare, m.endSquare, kingSquare)) {
                                continue;
                            }
                        }
                    }
                    pinnedPieceIndex = pinnedPieces.indexOf(m.currentPiece);
                    if (pinnedPieceIndex != -1) {
                        if (currentPieceID == PieceID.KNIGHT) {
                            continue;
                        }
                        else {
                            ChessPiece pinner = pinners.get(pinnedPieceIndex);
                            if (!collinear(pinner.currentSquare, m.endSquare, kingSquare)) {
                                if (m.capturedPiece != null) {
                                    if (!m.capturedPiece.equals(pinner)) {
                                        continue;
                                    }
                                }
                                else {
                                    continue;
                                }
                            }

                        }
                    }
                }
            }
            moveList.add(m);
        }
        //checkmate or stalemate
        if (moveList.isEmpty()) {
            if (checkingPieces.size() > 0) {
                positionStatus.status = PositionStatus.Status.CHECKMATE;
            }
            else {
                positionStatus.status = PositionStatus.Status.STALEMATE;
            }
            gameOver = true;
        }
        else {
            positionStatus.status = PositionStatus.Status.ONGOING;
        }

        return moveList;
    }

    private boolean collinear(byte aByte, byte bByte, byte cByte) {
        double[] a = {(aByte & bitmaskFile) >> 3, aByte & bitmaskRank};
        double[] b = {(bByte & bitmaskFile) >> 3, bByte & bitmaskRank};
        double[] c = {(cByte & bitmaskFile) >> 3, cByte & bitmaskRank};
        double acDist = (a[0] - c[0]) * (a[0] - c[0]) + (a[1] - c[1]) * (a[1] - c[1]);
        //System.out.println((c[1] - b[1])/(c[0] - b[0]) == (c[1] - c[1])/(a[0] - a[0]));
        //System.out.println("abdist " + ((a[0] - b[0]) * (a[0] - b[0]) + (a[1] - b[1]) * (a[1] - b[1])));
        //System.out.println("bcdist " + ((c[0] - b[0]) * (c[0] - b[0]) + (c[1] - b[1]) * (c[1] - b[1])));

        return Math.abs((b[1] - a[1]) * (c[0] - a[0]) - (b[0] - a[0]) * (c[1] - a[1])) < 0.001
         && (a[0] - b[0]) * (a[0] - b[0]) + (a[1] - b[1]) * (a[1] - b[1]) < acDist
         && (c[0] - b[0]) * (c[0] - b[0]) + (c[1] - b[1]) * (c[1] - b[1]) < acDist;
    }

    // private int getDirection(int fileDiff, int rankDiff) {
    //     rankDiff = rankDiff == 0 ? 0 : rankDiff / Math.abs(rankDiff);
    //     fileDiff = fileDiff == 0 ? 0 : fileDiff / Math.abs(fileDiff);
    //     return fileDiff * 10 + rankDiff;
    // }

    private ChessPiece getKing() {
        HashSet<ChessPiece> pieces = this.activeColor == Color.WHITE ? whitePieces : blackPieces;
        for (ChessPiece p: pieces) {
            if (p.id == PieceID.KING)
                return p;
        }
        return null;
    }

    //passing in the move list just in case it's needed, since we get it anyway as it is a requirement
    //when calling possibleMoves()
    private List<OppPieceInfo> kingSafetyInfo(List<Move> moveList) {
        ArrayList<OppPieceInfo> pieceInfoList = new ArrayList<>(16);
        HashSet<ChessPiece> playerPieces = this.activeColor == Color.WHITE ? blackPieces : whitePieces;
        for (ChessPiece p: playerPieces) {
            OppPieceInfo pieceInfo = new OppPieceInfo(p);
            p.possibleMoves(this.b, moveList, pieceInfo);
            pieceInfoList.add(pieceInfo);
        }
        return pieceInfoList;
    }

    public void addCastlingMoves(Color side, List<Move> moveList) {
        ChessPiece king, kRook, qRook;
        if (side == Color.WHITE) {
            king = b.getPieceFromSquare(WK_SQUARE);
            kRook = b.getPieceFromSquare(WK_ROOK_SQUARE);
            qRook = b.getPieceFromSquare(WQ_ROOK_SQUARE);
        }
        else {
            king = b.getPieceFromSquare(BK_SQUARE);
            kRook = b.getPieceFromSquare(BK_ROOK_SQUARE);
            qRook = b.getPieceFromSquare(BQ_ROOK_SQUARE);
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
            moveList.add(new Move(side, true, false, king));
        }
        //queenside castle
        if (qRook != null && king.id == PieceID.KING && qRook.id == PieceID.ROOK
            && king.pieceColor == side && qRook.pieceColor == side
            && b.getPieceFromSquare((byte)(king.currentSquare + PREV_FILE)) == null
            && castlingRights[crIndex][1] == 0
            && b.getPieceFromSquare((byte)(king.currentSquare + PREV_FILE * 2)) == null
            && b.getPieceFromSquare((byte)(king.currentSquare + PREV_FILE * 3)) == null) {   
            moveList.add(new Move(side, false, true, king));
        }
    }

    public void makeMove(Move m) {
        if (gameOver) {
            throw new IllegalArgumentException("Move cannot be made when game has ended");
        }
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
            if (m.capturedPiece.id == PieceID.ROOK) {
                int crIndex = m.capturedPiece.pieceColor == Color.WHITE ? 0 : 1;
                if (((Rook)m.capturedPiece).kingsideRook) {
                    castlingRights[crIndex][0]++;
                }
                else if (((Rook)m.capturedPiece).queensideRook){
                    castlingRights[crIndex][1]++;
                }
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
                else if (((Rook)m.currentPiece).queensideRook){
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
        positionStatus.nextMove();
        updateTTHash(m, false);
    }

    public boolean makeMove(String s) {
        Move m = algebraicNotationToMove(s);
        if (m == null) {
            System.out.println(this.toFEN() + "\nIllegal Move: " + s);
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
        if (m.capturedPiece != null) {
            s.add(m.capturedPiece);
            if (m.capturedPiece.id == PieceID.ROOK) {
                int crIndex = m.capturedPiece.pieceColor == Color.WHITE ? 0 : 1;
                if (((Rook)m.capturedPiece).kingsideRook) {
                    castlingRights[crIndex][0]--;
                }
                else if (((Rook)m.capturedPiece).queensideRook){
                    castlingRights[crIndex][1]--;
                }
            }
        }
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
                else if (((Rook)m.currentPiece).queensideRook){
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
        positionStatus.prevMove();
        gameOver = false;
        updateTTHash(m, true);
    }

    public Move algebraicNotationToMove(String s) {
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
            System.out.println("Castling is not an option");
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

    private void checkBasicDraws() {
        //TO DO: THREEFOLD REPETITION
        //FIFTY MOVE RULE
        if (halfmoveClock == 100) {
            positionStatus.status = PositionStatus.Status.FIFTY_MOVE;
            gameOver = true;
        }
        //INSUFFICIENT MATERIAL, does not include 2-bishop of same color draw
        else if (blackPieces.size() + whitePieces.size() == 3) {
            for (ChessPiece p: blackPieces) {
                if (p.id == PieceID.BISHOP || p.id == PieceID.KNIGHT) {
                    positionStatus.status = PositionStatus.Status.INSUFFICIENT_MATERIAL;
                    gameOver = true;
                }
            }
            for (ChessPiece p: whitePieces) {
                if (p.id == PieceID.BISHOP || p.id == PieceID.KNIGHT) {
                    positionStatus.status = PositionStatus.Status.INSUFFICIENT_MATERIAL;
                    gameOver = true;
                }
            }
        }
    }

    //returns true if game over
    public double positionStatus() {
        if (positionStatus.status == PositionStatus.Status.UNKNOWN) {
            throw new IllegalArgumentException("Cannot call function without first calling legalMoves()");
        }
        else if (positionStatus.status == PositionStatus.Status.ONGOING) {
            return 0;
        }
        else if (positionStatus.status == PositionStatus.Status.STALEMATE
                || positionStatus.status == PositionStatus.Status.FIFTY_MOVE
                || positionStatus.status == PositionStatus.Status.REPETITION) {
                    return 0.5;
                }
        else {
            //game over, details of the position status are set at the end of legalMoves()
            return 1;
        }
    }

    public boolean isDrawn() {
        checkBasicDraws();
        if (gameOver)
            return true;
        return false;
    }

    //mainly for debugging
    public boolean inCheck() {
        if (positionStatus.status == PositionStatus.Status.UNKNOWN) {
            throw new IllegalArgumentException("Cannot run inCheck() without running legalMoves() first");
        }
        return positionStatus.inCheck();
    }

    public boolean isCheckmate() {
        if (positionStatus.status == PositionStatus.Status.UNKNOWN) {
            throw new IllegalArgumentException("Cannot run isCheckmate() without running legalMoves() first");
        }
        return positionStatus.status == PositionStatus.Status.CHECKMATE;
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

    private void initTTHash() {
        long currPosRand = 0;
        long castlingRightsRand = 0;
        long enPassantRand = 0;
        long sideToMove = 0;


        for (ChessPiece p: whitePieces) {
            currPosRand ^= pieceHash(p.id, p.currentSquare, Color.WHITE);
        }
        for (ChessPiece p: blackPieces) {
            currPosRand ^= pieceHash(p.id, p.currentSquare, Color.BLACK);
        }
        //wipe lower eight bits, which are meant to represent other values, (0-2 en passant file, 3-6 castling rights, 7 side to move)
        currPosRand &= (~0b11111111); 
    

        //deal with en passant pawns
        if (!enPassantHistory.empty()) {
            if (enPassantHistory.peek().enPassantTurn == turn - 1) {
                enPassantRand = (long)BoardMethods.getFile(enPassantHistory.peek().currentSquare);
            }
        }

        //deal with castling rights
        castlingRightsRand += (castlingRights[0][0] == 0 ? 0 : 1) << 3;
        castlingRightsRand += (castlingRights[0][1] == 0 ? 0 : 1) << 4;
        castlingRightsRand += (castlingRights[1][0] == 0 ? 0 : 1) << 5;
        castlingRightsRand += (castlingRights[1][1] == 0 ? 0 : 1) << 6;

        sideToMove = activeColor == Color.WHITE ? (1 << 7) : 0;

        TTHash = currPosRand ^ castlingRightsRand ^ enPassantRand ^ sideToMove;

    }

    private void updateTTHash(Move m, boolean undo) {
        long castlingRightsRand = 0;
        long enPassantRand = 0;
        long sideToMove = 0;

        ChessPiece captured = m.capturedPiece;
        ChessPiece moved = m.currentPiece;

        TTHash ^= pieceHash(moved.id, m.startSquare, moved.pieceColor);
        TTHash ^= pieceHash(moved.id, m.endSquare, moved.pieceColor);
        if (m.capturedPiece != null)
            TTHash ^= pieceHash(captured.id, captured.currentSquare, captured.pieceColor);

        byte bIndexing = 0;
        if (m.color == Color.BLACK) {
            bIndexing = 0b111;
        }
        //Need to move the rook too
        if (m.isKingsideCastle) {
            TTHash ^= pieceHash(PieceID.ROOK, (byte)(WK_ROOK_SQUARE + bIndexing), moved.pieceColor);
            TTHash ^= pieceHash(PieceID.ROOK, (byte)(WK_ROOK_SQUARE + PREV_FILE * 2 + bIndexing), moved.pieceColor);
        }
        else if (m.isQueensideCastle) {
            TTHash ^= pieceHash(PieceID.ROOK, (byte)(WQ_ROOK_SQUARE + bIndexing), moved.pieceColor);
            TTHash ^= pieceHash(PieceID.ROOK, (byte)(WK_ROOK_SQUARE + NEXT_FILE * 3 + bIndexing), moved.pieceColor);
        }

        TTHash &= (~0b11111111); //wipe lower eight bits, see above init method for reason

        //deal with en passant pawns
        if (!enPassantHistory.empty()) {
            if (enPassantHistory.peek().enPassantTurn == turn - 1) {
                enPassantRand = (long)BoardMethods.getFile(enPassantHistory.peek().currentSquare);
            }
        }

        //deal with castling rights
        castlingRightsRand += (castlingRights[0][0] == 0 ? 0 : 1) << 3;
        castlingRightsRand += (castlingRights[0][1] == 0 ? 0 : 1) << 4;
        castlingRightsRand += (castlingRights[1][0] == 0 ? 0 : 1) << 5;
        castlingRightsRand += (castlingRights[1][1] == 0 ? 0 : 1) << 6;

        sideToMove = activeColor == Color.WHITE ? (1 << 7) : 0;

        TTHash ^= (castlingRightsRand ^ enPassantRand ^ sideToMove);
    }

    private long pieceHash(PieceID pid, byte square, Color color) {
        return (new Random((pid.ordinal() << 9) + (color.ordinal() << 6) + square)).nextLong();
    }

    public String toFEN() {
        StringBuilder fen = new StringBuilder();

        for (int i = BOARD_END_INDEX; i >= 0; i--) {
            int emptyCount = 0;
            for (int j = 0; j < 8; j++) {
                ChessPiece p = b.getPieceFromSquare((byte)((j << 3) + i));
                if (p == null) {
                    emptyCount++;
                    if (j == BOARD_END_INDEX) {
                        //end of rank
                        fen.append(emptyCount);
                    }
                }
                else {
                    if (emptyCount != 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    fen.append(p.toChar());
                }
            }
            if (i != 0) {
                fen.append("/");
            }
        }
        fen.append(" ");

        fen.append(this.activeColor == Color.WHITE ? 'w' : 'b');
        fen.append(" ");

        StringBuilder cR = new StringBuilder();
        if (castlingRights[0][0] == 0) 
            cR.append('K');
        if (castlingRights[0][1] == 0) 
            cR.append('Q');
        if (castlingRights[1][0] == 0) 
            cR.append('k');
        if (castlingRights[1][1] == 0) 
            cR.append('q');
        if (cR.isEmpty()) 
            cR.append('-');
        fen.append(cR);
        fen.append(" ");

        if (!enPassantHistory.isEmpty()) {
            Pawn lastEnPassant = enPassantHistory.peek();
            if (lastEnPassant.enPassantTurn == turn - 1) {
                if (lastEnPassant.pieceColor == Color.WHITE) {
                    fen.append(BoardMethods.squareToString((byte)(lastEnPassant.currentSquare - 1)));
                }
                else {
                    fen.append(BoardMethods.squareToString((byte)(lastEnPassant.currentSquare + 1)));
                }
            }
            else {
                fen.append("-");
            }
        }
        else {
            fen.append("-");
        }
        fen.append(" ");
        

        fen.append(halfmoveClock);
        fen.append(" ");

        fen.append((turn - (activeColor == Color.WHITE ? 0 : 1)) / 2 + 1);

        return fen.toString();
    }

    //equals() method but takes into account ALL class members, not just those needed by three-fold
    //mainly used for debugging undoMove()
    public boolean exactlyEquals(Position p) {
        if (p == this)
            return true;

        return p.b.equals(this.b) && p.activeColor == this.activeColor 
                && p.whitePieces.equals(this.whitePieces)
                && p.blackPieces.equals(this.blackPieces)
                && Arrays.equals(this.castlingRights[0], p.castlingRights[0])
                && Arrays.equals(this.castlingRights[1], p.castlingRights[1])
                && p.enPassantHistory.equals(this.enPassantHistory)
                && p.halfMoveHistory.equals(this.halfMoveHistory)
                && p.prevMoves.equals(this.prevMoves)
                && p.turn == this.turn
                && p.TTHash == this.TTHash
                && p.halfmoveClock == this.halfmoveClock;
    }

    //Strictly for debugging FEN Generation
    public boolean FENEquals(Position p) {
        if (p == this)
            return true;
        return p.b.equals(this.b) && p.activeColor == this.activeColor 
                && ((this.castlingRights[0][0] > 0 && p.castlingRights[0][0] > 0) || (this.castlingRights[0][0] == 0 && p.castlingRights[0][0] == 0))
                && ((this.castlingRights[1][0] > 0 && p.castlingRights[1][0] > 0) || (this.castlingRights[1][0] == 0 && p.castlingRights[1][0] == 0))
                && ((this.castlingRights[0][1] > 0 && p.castlingRights[0][1] > 0) || (this.castlingRights[0][1] == 0 && p.castlingRights[0][1] == 0))
                && ((this.castlingRights[1][1] > 0 && p.castlingRights[1][1] > 0) || (this.castlingRights[1][1] == 0 && p.castlingRights[1][1] == 0))
                && p.turn == this.turn
                && p.halfmoveClock == this.halfmoveClock;
    }

    public long getTTHash() {
        return TTHash;
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
        return p.TTHash == this.TTHash;
    }

    @Override
    public String toString() {
        return b + "\n--" + this.activeColor + " to move--\n";
    }


    class PositionStatus{
        public Color turn;
        public Status status;
        public boolean inCheck;
    
        public PositionStatus() {
            status = Status.UNKNOWN;
        }
    
        public void nextMove() {
            turn = turn == Color.WHITE ? Color.BLACK : Color.WHITE;
            status = Status.UNKNOWN;
            inCheck = false;
        }
    
        public void prevMove() {
            turn = turn == Color.WHITE ? Color.BLACK : Color.WHITE;
            status = Status.ONGOING;
            inCheck = false;
        }

        public boolean inCheck() {
            return inCheck;
        }
    
        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Position)) {
                return false;
            }
            PositionStatus p = (PositionStatus)o;
            return p.status == this.status && p.turn == this.turn;
        }

        @Override
        public int hashCode() {
        //random function
            return 0;
        }

        @Override
        public String toString() {
            if (status == Status.STALEMATE)
                return this.turn + " " + "STALEMATED";
            else if (status == Status.CHECKMATE)
                return this.turn + " " + "CHECKMATED";
            else {
                if (status == Status.UNKNOWN || status == Status.ONGOING) {
                    return status.toString();
                }
                else {
                    return "DRAWN BY " + status.toString();
                }
            }
        }
    
        public enum Status {
            UNKNOWN,
            ONGOING,
            STALEMATE,
            INSUFFICIENT_MATERIAL,
            FIFTY_MOVE,
            REPETITION,
            //AGREEMENT,
            CHECKMATE,
            //RESIGN,
            //TIMEOUT
        }
    }
    
    
    
}
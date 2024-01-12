//Taken from https://www.chessprogramming.org/Simplified_Evaluation_Function

final class EvalTables {

    private static final byte bitmaskRank = 0b00000111;
    private static final byte bitmaskFile = 0b00111000;

    public static final int[] PAWN_TABLE =  
    {0,  0,  0,  0,  0,  0,  0,  0,
    50, 50, 50, 50, 50, 50, 50, 50,
    10, 10, 20, 30, 30, 20, 10, 10,
    5,  5, 10, 25, 25, 10,  5,  5,
    0,  0,  0, 20, 20,  0,  0,  0,
    5, -5,-10,  0,  0,-10, -5,  5,
    5, 10, 10,-20,-20, 10, 10,  5,
    0,  0,  0,  0,  0,  0,  0,  0};

    public static final int[] KNIGHT_TABLE = 
    {-50,-40,-30,-30,-30,-30,-40,-50,
    -40,-20,  0,  0,  0,  0,-20,-40,
    -30,  0, 10, 15, 15, 10,  0,-30,
    -30,  5, 15, 20, 20, 15,  5,-30,
    -30,  0, 15, 20, 20, 15,  0,-30,
    -30,  5, 10, 15, 15, 10,  5,-30,
    -40,-20,  0,  5,  5,  0,-20,-40,
    -50,-40,-30,-30,-30,-30,-40,-50};

    public static final int[] BISHOP_TABLE = 
    {-20,-10,-10,-10,-10,-10,-10,-20,
    -10,  0,  0,  0,  0,  0,  0,-10,
    -10,  0,  5, 10, 10,  5,  0,-10,
    -10,  5,  5, 10, 10,  5,  5,-10,
    -10,  0, 10, 10, 10, 10,  0,-10,
    -10, 10, 10, 10, 10, 10, 10,-10,
    -10,  5,  0,  0,  0,  0,  5,-10,
    -20,-10,-10,-10,-10,-10,-10,-20};

    public static final int[] ROOK_TABLE = 
    {0,  0,  0,  0,  0,  0,  0,  0,
    10, 20, 20, 20, 20, 20, 20,  10,
    0,  0,  0,  0,  0,  0,  0, 0,
    -10,  0,  0,  0,  0,  0,  0, -10,
    -10,  0,  0,  0,  0,  0,  0, -10,
    -10,  0,  0,  0,  0,  0,  0, -10,
    -10,  0,  0,  0,  0,  0,  0, -10,
    0,  0,  3,  5,  5,  3,  0,  0};

    public static final int[] QUEEN_TABLE = 
    {0,  0,  0,  0,  0,  0,  0,  0,
    5, 10, 10, 10, 10, 10, 10,  5,
    -5,  0,  0,  0,  0,  0,  0, -5,
    -5,  0,  0,  0,  0,  0,  0, -5,
    -5,  0,  0,  0,  0,  0,  0, -5,
    -5,  0,  0,  0,  0,  0,  0, -5,
    -5,  0,  0,  0,  0,  0,  0, -5,
    0,  0,  0,  5,  5,  0,  0,  0};

    public static final int[] KING_MG_TABLE =
    {-30,-40,-40,-50,-50,-40,-40,-30,
    -30,-40,-40,-50,-50,-40,-40,-30,
    -30,-40,-40,-50,-50,-40,-40,-30,
    -30,-40,-40,-50,-50,-40,-40,-30,
    -20,-30,-30,-40,-40,-30,-30,-20,
    -10,-20,-20,-20,-20,-20,-20,-10,
    20, 20,  0,  0,  0,  0, 20, 20,
    40, 50, 10,  0,  0, 10, 50, 40};

    public static final int[] KING_EG_TABLE = 
    {-50,-40,-30,-20,-20,-30,-40,-50,
    -30,-20,-10,  0,  0,-10,-20,-30,
    -30,-10, 20, 30, 30, 20,-10,-30,
    -30,-10, 30, 40, 40, 30,-10,-30,
    -30,-10, 30, 40, 40, 30,-10,-30,
    -30,-10, 20, 30, 30, 20,-10,-30,
    -30,-30,  0,  0,  0,  0,-30,-30,
    -50,-30,-30,-30,-30,-30,-30,-50};

    //For square conversion into evaluation tables
    public static byte tableConvWhite(byte currentSquare) {
        return (byte)(((currentSquare & bitmaskFile) >> 3) + 
            ((7 - (currentSquare & bitmaskRank)) << 3));
    }

    //For square conversion into evaluation tables
    public static byte tableConvBlack(byte currentSquare) {
        return (byte)(((currentSquare & bitmaskFile) >> 3) + 
            ((currentSquare & bitmaskRank) << 3));
    }
}
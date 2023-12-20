import java.util.*;
//Mainly for debugging I think
class BoardMethods {
    //being unnecessarily fancy here
    private static final String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private static final byte bitmaskRank = 0b00000111;
    private static final byte bitmaskFile = 0b00111000;

    public static String squareToString(byte square) {
        return letters[(square & bitmaskFile) >> 3] + (int)(square & bitmaskRank);
    }


}
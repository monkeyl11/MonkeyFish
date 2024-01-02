import java.util.*;
//Mainly for debugging I think
class BoardMethods {
    //being unnecessarily fancy here
    private static final String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private static final byte bitmaskRank = 0b00000111;
    private static final byte bitmaskFile = 0b00111000;

    public static String squareToString(byte square) {
        return letters[(square & bitmaskFile) >> 3] + ((int)(square & bitmaskRank) + 1);
    }

    public static int getFile(byte square) {
        return (int)(square & bitmaskFile) >> 3;
    }

    public static int getRank(byte square) {
        return (int)(square & bitmaskRank);
    }

    public static int[] byteToArray(byte square) {
        int[] ret = {getFile(square), getRank(square)};
        return ret;
    }

    public static byte arrayToByte(int[] arr) {
        if (arr.length != 2) {
            System.out.println("bad input");
        }
        return (byte)(arr[0] * 8 + arr[1]);
    }




}
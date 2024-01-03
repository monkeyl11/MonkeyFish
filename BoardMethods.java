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

    public static String boardToString(Board b) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            int[] square = byteToArray((byte)i);
            ChessPiece p = b.board[square[1]][square[0]];
            if (p == null) {
                s.append("-- ");
            }
            else {
                String color = p.pieceColor == Color.WHITE ? "W" : "B";
                if (p.id == PieceID.PAWN) {
                    s.append(color + "P ");
                }
                else if (p.id == PieceID.KNIGHT) {
                    s.append(color + "N ");
                }
                else if (p.id == PieceID.BISHOP) {
                    s.append(color + "B ");
                }
                else if (p.id == PieceID.ROOK) {
                    s.append(color + "R ");
                }
                else if (p.id == PieceID.QUEEN) {
                    s.append(color + "Q ");
                }
                else if (p.id == PieceID.KING) {
                    s.append(color + "K ");
                }
            }
            
            if (i % 8 == 7) {
                s.append("\n");
            }
        }
        return s.toString();
    }


}
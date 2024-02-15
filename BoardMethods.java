import java.util.*;

import Enums.Color;
import Enums.PieceID;
//Mainly for debugging I think
class BoardMethods {
    //being unnecessarily fancy here
    private static final String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private static final byte bitmaskRank = 0b00000111;
    private static final byte bitmaskFile = 0b00111000;

    public static String squareToString(byte square) {
        return letters[(square & bitmaskFile) >> 3] + ((int)(square & bitmaskRank) + 1);
    }

    public static byte stringToSquare(String s) {
        if (s.length() != 2) {
            throw new IllegalArgumentException("Invalid string length");
        }
        int file = Arrays.asList(letters).indexOf(s.substring(0, 1));
        return (byte)((file << 3) + Integer.parseInt(s.substring(1, 2)) - 1);
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

    public static boolean checkBoardConsistency(Board b) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (b.board[i][j] != null) {
                    if (i * 8 + j != b.board[i][j].currentSquare) {
                        System.out.println("Board inconsistency at " + squareToString((byte)(i * 8 + j)));
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static String boardToString(Board b) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            int[] square = byteToArray((byte)i);
            ChessPiece p = b.board[square[1]][b.board.length - square[0] - 1];
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
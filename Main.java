import java.util.*;

class Main {
    public static void main(String[] args) {
        // for (byte i = 0; i < 64; i++) {
        //     System.out.println(BoardMethods.squareToString(i));
        //     int[] a = BoardMethods.byteToArray(i);
        //     System.out.println(Arrays.toString(a));
        //     //System.out.println(BoardMethods.arrayToByte(a));
        // }
        Board b = new Board();
        b.setUpBoard();
        System.out.println(b);
        ChessPiece p = b.board[4][5];
        System.out.println(p.pieceColor);
        System.out.println(p.id);
        ArrayList<Move> moveList = new ArrayList<Move>();
        p.legalMoves(b, moveList);
        System.out.println(moveList);
    }
}
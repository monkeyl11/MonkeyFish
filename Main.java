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
        BoardMethods.checkBoardConsistency(b);
        System.out.println(b);
        Pawn p = (Pawn)b.board[4][1];
        System.out.println(p.id);
        ArrayList<Move> moveList = new ArrayList<>();
        p.possibleMoves(b, moveList);
        System.out.println(moveList);
        b.makeMove(moveList.get(1));
        BoardMethods.checkBoardConsistency(b);

        moveList.clear();
        Pawn p2 = (Pawn)b.board[3][6];
        p2.possibleMoves(b, moveList);
        System.out.println(b);
        b.makeMove(moveList.get(1));
        System.out.println(moveList);

        moveList.clear();
        p.possibleMoves(b, moveList);
        System.out.println(b);
        b.makeMove(moveList.get(1));
        System.out.println(moveList);

        moveList.clear();
        Pawn p3 = (Pawn)b.board[2][6];
        p3.possibleMoves(b, moveList);
        System.out.println(b);
        b.makeMove(moveList.get(1));
        System.out.println(moveList);

        moveList.clear();
        p.possibleMoves(b, moveList);
        System.out.println(b);
        b.makeMove(moveList.get(0));
        System.out.println(moveList);

        moveList.clear();
        Pawn p4 = (Pawn)b.board[4][6];
        p4.possibleMoves(b, moveList);
        System.out.println(b);
        b.makeMove(moveList.get(1));
        System.out.println(moveList);

        moveList.clear();
        p.possibleMoves(b, moveList);
        System.out.println(b);
        b.makeMove(moveList.get(0));
        System.out.println(moveList);

        moveList.clear();
        Queen q = (Queen)b.board[3][7];
        q.possibleMoves(b, moveList);
        System.out.println(b);
        b.makeMove(moveList.get(3));
        System.out.println(moveList);

        moveList.clear();
        p.possibleMoves(b, moveList);
        System.out.println(b);
        b.makeMove(moveList.get(11));
        System.out.println(moveList);

        moveList.clear();
        King k = (King)b.board[4][0];
        k.possibleMoves(b, moveList);
        System.out.println(b);
        //b.makeMove(moveList.get(11));
        System.out.println(moveList);
        BoardMethods.checkBoardConsistency(b);
    }
}
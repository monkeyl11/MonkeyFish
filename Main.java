import java.util.*;

class Main {
    public static void main(String[] args) {
        Position p = new Position(true);
        moves(0, true, p);
        moves(0, false, p);
        System.out.println(p);
    }

    public static void moves(int index, boolean makeMove, Position p) {
        List<Move> legalMoves = null;
        legalMoves = p.legalMoves();
        if (!makeMove)
            System.out.println(legalMoves);
        if (makeMove) {
            p.makeMove(legalMoves.get(index));
        }
    }
}
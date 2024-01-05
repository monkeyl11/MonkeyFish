import java.util.*;

class Main {
    public static void main(String[] args) {
        Position p = new Position(true);
        Scanner s = new Scanner(System.in);
        while (true) {
            System.out.println(p);
            while (true) {
                System.out.println("Enter move: ");
                String response = s.nextLine();
                if (p.makeMove(response)) {
                    break;
                }
            }

        }
    }

    public static void moves(int index, boolean makeMove, Position p) {
        List<Move> legalMoves = null;
        legalMoves = p.legalMoves();
        if (!makeMove) {
            System.out.println(legalMoves);
            System.out.println(legalMoves.size());
        }
        if (makeMove) {
            p.makeMove(legalMoves.get(index));
        }
    }
}
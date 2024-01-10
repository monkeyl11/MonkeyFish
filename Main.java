import java.util.*;
import java.io.File;
import java.io.FileNotFoundException; 

import java.io.FileReader;

import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
import org.json.simple.parser.*;

class Main {
    private static Stopwatch stopwatch = new Stopwatch();
    private static int totalCasesFailed = 0;
    private static int totalCases = 0;
    
    public static void main(String[] args){
        // try {
        //     testSpecialPositions();
        // }
        // catch (Exception e) {
        //     System.out.println(e);
        // }
        //Position p = new Position("8/7p/p5pb/4k3/P1pPn3/8/P5PP/1rB2RK1 b - d3 0 28");
        //perft(2, p, true);
        //perft(6, "8/7p/p5pb/4k3/P1pPn3/8/P5PP/1rB2RK1 b - d3 0 28", true);

        //75s
        //perftTestShallow();
        //2524s
        perftTestDeep();
        
        // Position p = new Position("8/7p/p5pb/4k3/P1pPn3/8/P5PP/1rB2RK1 b - - 0 28");
        // p.makeMove("Kd5");
        // for (Move m : p.legalMoves()) {
        //     System.out.println(m.toString());
        // }
        //System.out.println("size: " + p.legalMoves().size());
        //testCase("rnbqkb1r/pppppppp/8/8/4n3/3P4/PPPKPPPP/RNBQ1BNR w kq - 0 1", "a3");
        //testFENs();
        //runPlayerGames("./testcase_games/TorreRepetto.pgn", false, -1);
        //testAll();
        //testUndo(-1);
        //testAllUndo();
        
    }

    public static void testCase(String fen, String move) {
        Position p = new Position(fen);
        //p.makeMove(move);
        System.out.println(p.legalMoves());
        p.makeMove(move);
        System.out.println("Position: \n" + p);

    }

    public static void testFENs() {
        Scanner s = new Scanner(System.in);
        while(true) {
            System.out.println("Enter FEN: ");
            Position p = new Position(s.nextLine());
            System.out.println(p.legalMoves());
            if (p.positionStatus()) {
                System.out.println(p.positionStatus);
            }
        }
    }

    public static long perft(int depth, String fen, boolean debug) {
        Position p = new Position(fen);
        return perft(depth, p, debug);
    }

    //perft
    public static long perft(int depth, Position p, boolean debug) {
        stopwatch.reset();
        stopwatch.start();
        long[] stats = {0, 0, 0}; //total positions, checkmates found
        long[] prevStats = {0, 0, 0};
        List<Move> moves = p.legalMoves();
        //System.out.println("mlL " + moves.size());
        for (Move m: moves) {
            p.makeMove(m);
            testTreeHelper(p, depth - 2, stats);
            p.undoMove();
            if (debug) {
                System.out.println("MOVE " + m.toStringSF());
                System.out.println("Leaf nodes generated: " + (stats[0] - prevStats[0]));
                //System.out.println("Checkmates seen: " + (stats[1] - prevStats[1]));
                //System.out.println("Checks seen: " + (stats[2] - prevStats[2]));
            }

            prevStats = Arrays.copyOf(stats, stats.length);
        }
        stopwatch.stop();
        if (debug) {
            System.out.println("---TOTAL STATS---");
            System.out.println("Legal moves from given position: " + moves.size());
            System.out.println("Leaf nodes generated: " + stats[0]);
            System.out.println("Checkmates seen: " + stats[1]);
            System.out.println("Checks seen: " + stats[2]);
            System.out.println("Total time taken to search " + p.toFEN() + " with depth " + depth + ": " + stopwatch.time());
        }
        
        return stats[0];
    }

    private static void testTreeHelper(Position p, int depth, long[] stats) {
        List<Move> legalMoves = p.legalMoves();
        if (depth == 0)
            stats[0] += legalMoves.size();
        if (p.positionStatus() || depth <= 0) {
            if (p.isCheckmate())
                stats[1]++;
            if (p.inCheck())
                stats[2]++;
            return;
        }
        for (Move m: legalMoves) {
            try {
                p.makeMove(m);
            }
            catch (Exception e) {
                System.out.println(p);
                throw e;
            }
            testTreeHelper(p, depth - 1, stats);
            p.undoMove();
        }
    }

    //Courtesy of https://github.com/schnitzi
    public static void testSpecialPositions() throws Exception{
        boolean passedAll = true;
        File[] files = new File("./testcase_positions").listFiles();
        for (File f: files) {
            Object obj = new JSONParser().parse(new FileReader(f));
            JSONObject jo = (JSONObject) obj;
            ArrayList<JSONObject> testCases = (ArrayList)jo.get("testCases");
            for (JSONObject testcase: testCases) {
                //System.out.println(JSONObj.toString());
                HashMap startingInfo = (HashMap)testcase.get("start");
                Position p = new Position((String)(startingInfo.get("fen")));
                List<Move> generatedMoves = p.legalMoves();
                List<Move> expectedMoves = new ArrayList<>(64);
                List<String> expectedMovesAlgebraic = new ArrayList<>();
                List<String> expectedFENs = new ArrayList<>(256);
                ArrayList<JSONObject> moves = (ArrayList<JSONObject>)testcase.get("expected");
                for (JSONObject m: moves) {
                    expectedMovesAlgebraic.add((String)(m.get("move")));
                    expectedMoves.add(p.algebraicNotationToMove((String)(m.get("move"))));
                    expectedFENs.add((String)(m.get("fen")));
                }
                boolean testcasePassed = true;
                for (Move m: generatedMoves) {
                    if (!expectedMoves.contains(m)) {
                        System.out.println("ERROR: Extra move generated- " + m);
                        testcasePassed = false;
                        passedAll = false;
                    }
                }
                for (Move m: expectedMoves) {
                    if (!generatedMoves.contains(m)) {
                        System.out.println("ERROR: Move missing- " + m);
                        testcasePassed = false;
                        passedAll = false;
                    }
                }
                if (!testcasePassed) {
                    System.out.println("TESTCASE FAILED- " + startingInfo.get("fen"));
                }
                //Testing FEN generation
                if (testcasePassed) {
                    for (int i = 0; i < expectedMoves.size(); i++) {
                        p.makeMove(expectedMovesAlgebraic.get(i));
                        if (!(p.toFEN().equals(expectedFENs.get(i)))) {
                            System.out.println(p);
                            System.out.println("Incorrect: " + Arrays.toString(p.toFEN().toCharArray()));
                            System.out.println("Expected:  " + Arrays.toString(expectedFENs.get(i).toCharArray()));
                            System.out.println("Original FEN: " + startingInfo.get("fen"));
                            System.out.println("Move: " + expectedMovesAlgebraic.get(i));
                            throw new IllegalArgumentException();
                        }
                        p.undoMove();
                    }
                }
            }

        }
        if (passedAll) {
            System.out.println("PASSED ALL SPECIAL TEST CASES");
        }
    }

    //plays out a game from start, returns number of moves
    public static int playOutGame(Position p, List<String> game, int numMoves, boolean debug) {
        totalCases++;
        numMoves = numMoves < 0 ? Integer.MAX_VALUE : numMoves;
        int i = 0;
        for (String move: game) {
            if (i >= numMoves) {
                return numMoves;
            }
            try {
                if (!p.makeMove(move)) {
                    totalCasesFailed++;
                    if (debug) {
                        System.out.println("GAME: " + game);
                        System.out.println(move + " FAILED\n" + p);
                        System.out.println("LEGAL MOVES GIVEN: " + p.legalMoves());
                        System.out.println("FEN: " + p.toFEN());
                        //throw new IllegalArgumentException("whatever");
                    }
                    break;
                }

            }
            catch (Exception e) {
                System.out.println("EXCEPTION!!!");
                System.out.println("GAME: " + game);
                System.out.println(move + " FAILED\n" + p);
                System.out.println("LEGAL MOVES GIVEN: " + p.legalMoves());
                throw e;
            }
            i++;
        }
        return i;
    }

    //test
    public static void runPlayerGames(String s, boolean debug, int numGames) {
        numGames = numGames < 0 ? Integer.MAX_VALUE : numGames;
        Position p;
        PGNParser parser = new PGNParser(s);
        List<String> game;
        int testcase = 0;
        s = s.replace(".\\testcase_games\\", "");
        s = s.replace(".pgn", "");
        System.out.println("PLAYING OUT " + s);
        while(true && testcase < numGames) {
            p = new Position(true);
            testcase++;
            //System.out.println("TESTCASE " + testcase);
            game = parser.nextGame();
            if (game == null) {
                break;
            }
            //System.out.println(game);
            stopwatch.start();
            playOutGame(p, game, -1, debug);
            stopwatch.stop();
        }
    }

    //Runs through ~400k games
    public static void testAll() {
        File[] files = new File("./testcase_games").listFiles();
        for (File f: files) {
            runPlayerGames(f.toString(), false, -1);
        }
        System.out.println(stopwatch);
        System.out.println("Cases failed: " + totalCasesFailed + " / " + totalCases);
        resetCases();
    }

    public static void testAllUndo() {
        File[] files = new File("./testcase_games").listFiles();
        for (File f: files) {
            testUndo(f.toString(),  -1);
        }
    }

    public static void testUndo(String s, int numTests) {
        Position p = null;
        Position pComp = null;
        PGNParser parser = new PGNParser(s);
        s = s.replace(".\\testcase_games\\", "");
        s = s.replace(".pgn", "");
        System.out.println("PLAYING OUT " + s);
        List<String> game;
        int testcase = 0;
        int i = 0;
        numTests = numTests < 0 ? Integer.MAX_VALUE : numTests;
        while(true && i < numTests) {
            ChessPiece.resetIDCounter();
            p = new Position(true);
            testcase++;
            game = parser.nextGame();
            if (game == null) {
                break;
            }
            //System.out.println(game);

            int moves = playOutGame(p, game, -1, false);
            //System.out.println(p.b);
            //System.out.println(pComp.b);
            for (int j = 1; j <= moves; j++) {
                p.undoMove();
                ChessPiece.resetIDCounter();
                pComp = new Position(true);
                playOutGame(pComp, game, moves - j, false);
                if (!p.exactlyEquals(pComp)) {
                    System.out.println("FAILED");
                    System.out.println(moves - j);
                    throw new IllegalArgumentException();
                }
            }
            i++;
        }
    }

    public static void perftTest(String fileName, String testcaseName){
        boolean passedAll = true;
        File f = new File(fileName);
        Scanner scan;
        long totalMovesExamined = 0;
        try {
            scan = new Scanner(f);
        }
        catch (FileNotFoundException e) {
            System.out.println("Test case cannot be found");
            return;
        }
        Stopwatch s = new Stopwatch();
        s.start();
        while(scan.hasNextLine()) {
            String fen = scan.nextLine();
            String[] params = scan.nextLine().split(" ");
            long expectedVal = Long.parseLong(params[1]);
            long actualVal = perft(Integer.parseInt(params[0]), fen, false);
            if (actualVal != expectedVal) {
                System.out.println("perft for " + fen + " failed at depth " + params[0]);
                System.out.println("EXPECTED: " + expectedVal);
                System.out.println("ACTUAL: " + actualVal);
                passedAll = false;
            }
            totalMovesExamined += actualVal;
        }
        s.stop();
        if (passedAll) {
            System.out.println(testcaseName + " PASSED");
            System.out.println("Total time taken: " + s.time());
            System.out.println("Total moves examined: " + totalMovesExamined);
        }
        scan.close();
    }

    public static void perftTestDeep() {
        perftTest("./testcase_perft/testcaseDeep", "PERFT DEEP");
    }

    public static void perftTestShallow() {
        perftTest("./testcase_perft/testcaseShallow", "PERFT SHALLOW");
    }

    public static void resetCases() {
        totalCasesFailed = 0;
        totalCases = 0;
    }
}
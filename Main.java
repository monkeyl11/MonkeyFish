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

    protected static final byte bitmaskRank = 0b00000111;
    protected static final byte bitmaskFile = 0b00111000;
    
    public static void main(String[] args){
        PeSTO.initTables();
        // System.out.println(new Position("r1b1kbnr/2p2ppp/p1p5/3p4/3P2q1/2N1P1P1/PPP2P1P/R1BQ1RK1 w kq - 3 10").getTTHash());
        // Position p = new Position("r1b1kbnr/2pp1ppp/p1p2q2/4N3/8/2N1P3/PPPP1PPP/R1BQK2R b KQkq - 0 6");
        // Node<String> root = new Node<>();
        // root.children = new ArrayList<>();
        //System.out.println((p.activeColor == Color.WHITE ? 1 : -1) * Evaluate.quiesce(-Double.MAX_VALUE, Double.MAX_VALUE, p, root));
        //System.out.println(Evaluate.evaluatePosition(p));

        

        // System.out.println(Long.toBinaryString(p.getTTHash()));
        // p.makeMove("e4");
        // System.out.println(Long.toBinaryString(p.getTTHash()));
        // p.undoMove();
        // System.out.println(Long.toBinaryString(p.getTTHash()));
        


        // try {
        //     testSpecialPositions();
        // }
        // catch (Exception e) {
        //     System.out.println(e);
        // }

        //engineCompare(null, true);

        //perft(2, p, true);
        //perft(6, "8/7p/p5pb/4k3/P1pPn3/8/P5PP/1rB2RK1 b - d3 0 28", true);

        //75s
        //perftTestShallow();
        //2524s
        //perftTestDeep();

        // Position p = new Position("rnbqkbnr/2pp1p1p/1p2p3/p5p1/P1PP4/4P3/1P3PPP/RNBQKBNR w KQkq g6 0 5");
        // Move[] bestMove = new Move[1];
        // Node<String> root = new Node<String>();
        // root.children = new ArrayList<>();
        // stopwatch.start();
        // double e = EvaluateOld.evaluatePosition(p);
        // double evaluation = EvaluateOld.evalNodeCount(p, 1000000.0, -Double.MAX_VALUE, Double.MAX_VALUE, bestMove, 0, e, 0);
        
        // System.out.println("EVALUATION: " + EvaluateOld.formatEval(evaluation));

        // stopwatch.stop();
        // System.out.println("TOTAL TIME: " + stopwatch.time());
        // //System.out.println("TIME SPENT GENERATING MOVES: " + EvaluateOld.s.time());
        // //System.out.println("TIME SPENT MAKING MOVES: " + EvaluateOld.s2.time());
        // System.out.println("TIME SPENT EVALUATING POSITIONS: " + EvaluateOld.s3.time());
        // System.out.println("TOTAL LEAF NODES EVALUATED: " + EvaluateOld.total_nodes);
        // System.out.println("MAX DEPTH SEARCHED: " + EvaluateOld.maxDepth);
        // System.out.println(bestMove[0]);

        // stopwatch.reset();

        // Position p2 = new Position("r1b1kbnr/2p2ppp/p1p2q2/3pp3/8/2N1PNP1/PPPP1P1P/R1BQK2R w KQkq d6 0 7");
        // Move[] bestMove2 = new Move[1];
        // Node<String> root2 = new Node<String>();
        // root2.children = new ArrayList<>();
        // double evaluation2 = 0;
        // double nodeDepth = 1000;
        // while (stopwatch.time() < 3) {
        //     root2 = new Node<String>();
        //     root2.children = new ArrayList<>();
        //     stopwatch.start();
        //     evaluation2 = Evaluate.evalNodeCountDebug(p2, nodeDepth, -Double.MAX_VALUE, Double.MAX_VALUE, bestMove2, 0, 0, null, root2);
        //     nodeDepth *= 10;
        //     stopwatch.stop();
        //     System.out.println("ATTEMPTING " + nodeDepth);
        // }
        
        // System.out.println("EVALUATION: " + Evaluate.formatEval(evaluation2));

        // System.out.println("TOTAL TIME: " + stopwatch.time());
        // //System.out.println("TIME SPENT GENERATING MOVES: " + Evaluate.s.time());
        // //System.out.println("TIME SPENT MAKING MOVES: " + Evaluate.s2.time());
        // System.out.println("CALLS TO QUIESCE: " + Evaluate.quiesce_calls);
        // System.out.println("TIME SPENT EVALUATING POSITIONS: " + Evaluate.s3.time());
        // System.out.println("TOTAL LEAF NODES EVALUATED: " + Evaluate.total_nodes);
        // System.out.println("MAX DEPTH SEARCHED: " + Evaluate.maxDepth);

        // System.out.println("Transposition Table Size: " + Evaluate.qTable.size);
        // System.out.println("Transposition Table Unwanted Collisions: " + Evaluate.qTable.unwantedCollisions);
        // System.out.println("Transposition Table Hits: " + Evaluate.qTable.hits);

        // System.out.println(bestMove2[0]);

        //Position p = new Position("r1b2rk1/1ppp1ppp/p7/3nP3/P1Qn4/2P2NPB/R3PK1P/5R2 b - - 1 20");
        //System.out.println(Evaluate.evaluatePosition(p));

        // double whiteWins = 0; double blackWins = 0;
        // for (int i = 0; i < 500; i++) {
        //     int result = engineCompare(null, true);
        //     if (result == 1)
        //         whiteWins++;
        //     else if (result == -1)
        //         blackWins++;
        //     else
        //     {
        //         whiteWins += 0.5; blackWins += 0.5;
        //     }
        //     System.out.println("GAME SCORE: " + whiteWins + " - " + blackWins);
        // }




       playEngine(null, false, false);

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

    public static int engineCompare(String fen, boolean oldEngineIsWhite) {
        Evaluate.resetTable();
        Position p = null;
        if (fen == null) {
            p = new Position(true);
        }
        else {
            p = new Position(fen);
        }
        boolean oldEngineTurn = p.activeColor == Color.WHITE ? oldEngineIsWhite : !oldEngineIsWhite;
        while(true) {
            if (p.isDrawn())
                break;
            p.legalMoves();
            if (p.positionStatus() != 0) {
                break;
            }
            double baseLineEval = 1000.0;
            Stopwatch s = new Stopwatch();
            Move[] move = new Move[1];
            double eval = 0;

            while (s.time() < 0.1) {
                s.start();
                if (oldEngineTurn)
                    eval = EvaluateOld.evalNodeCount(p, baseLineEval, -Double.MAX_VALUE, Double.MAX_VALUE, move, 0, 0, null);
                else
                    eval = Evaluate.evalNodeCount(p, baseLineEval, -Double.MAX_VALUE, Double.MAX_VALUE, move, 0, 0, null);
                s.stop();
                baseLineEval *= (6 + Math.random() * 8.0);
                //System.out.println(baseLineEval);
            }
            if (oldEngineTurn) 
                System.out.println("Old Engine evaluation: " + Evaluate.formatEval(eval));
            else
                System.out.println("Engine evaluation: " + Evaluate.formatEval(eval));
            //System.out.println(p.toFEN());
            System.out.println(p.toFEN());
            System.out.println("Move played: " + move[0]);
            //System.out.println("Eval num used: " + baseLineEval);
            //System.out.println(p);
            p.makeMove(move[0]);
            oldEngineTurn = !oldEngineTurn;
        }
        if (p.positionStatus() == 1) {
            return (p.activeColor == Color.WHITE ? -1 : 1);
        }
        else {
            return 0;
        }
    }

    public static void playEngine(String fen, boolean userIsWhite, boolean enginePlay) {
        Position p = null;
        if (fen == null) {
            p = new Position(true);
        }
        else {
            p = new Position(fen);
        }
        boolean userTurn = p.activeColor == Color.WHITE ? userIsWhite : !userIsWhite;
        Scanner input = new Scanner(System.in);
        String userResponse;
        while(true) {
            p.isDrawn();
            List<Move> legalMoves = p.legalMoves();
            if (p.positionStatus() != 0) {
                break;
            }
            double baseLineEval = 10000.0;
            if (userTurn && !enginePlay) {
                while (true) {
                    System.out.print("Make move: ");
                    userResponse = input.nextLine();
                    if (userResponse.equals("undo")) {
                        p.undoMove();
                        p.undoMove();
                        break;
                    }
                    try {
                        if (p.makeMove(userResponse)) {
                            break;
                        }
                    }
                    catch (Exception e){};
                }
                if (!userResponse.equals("undo"))
                    userTurn = false;
            }
            else {
                Stopwatch s = new Stopwatch();
                Move[] move = new Move[1];
                double e = Evaluate.evaluatePosition(p);
                double eval = 0;

                Node<String> root = null;
                while (s.time() < 3.5) {
                    root = new Node<>();
                    root.children = new ArrayList<Node<String>>();
                    s.start();
                    eval = Evaluate.evalNodeCount(p, baseLineEval, -Double.MAX_VALUE, Double.MAX_VALUE, move, 0, 0, null);
                    s.stop();
                    baseLineEval *= 10;
                }
                long prevSize = Evaluate.tTable.size;
                System.out.println("TABLE SIZE: " + Evaluate.tTable.size);
                System.out.println("Engine evaluation: " + Evaluate.formatEval(eval));
                System.out.println("Engine plays " + move[0]);
                System.out.println("Eval nodenum used: " + baseLineEval);
                System.out.println((prevSize - Evaluate.tTable.size) + " moves cleared from the table");
                //System.out.println("Eval num used: " + baseLineEval);
                //System.out.println(p);
                p.makeMove(move[0]);
                userTurn = true;
            }
        }
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
            if (p.positionStatus() != 0) {
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
        if (p.positionStatus() != 0 || depth <= 0) {
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
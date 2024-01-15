import java.util.*;

//could be optimized
//please pray for no index collisions
//implements a basic hash table/map, does not increase in size
class TranspositionTable {
    private final static int TT_DEFAULT_SIZE = 33554432;
    //used for debugging
    public long size = 0;
    public int hits = 0;
    public int unwantedCollisions = 0;
    private long maxSize;
    private int cutoff = 0;

    //size of table
    private int tableSize;

    private TTEntry[] savedPositions;

    public TranspositionTable() {
        savedPositions = new TTEntry[TT_DEFAULT_SIZE];
        tableSize = TT_DEFAULT_SIZE;
        maxSize = TT_DEFAULT_SIZE * 2;
    }

    public TranspositionTable(int pow) {
        savedPositions = new TTEntry[(int)Math.pow(2, pow)];
        tableSize = (int)Math.pow(2, pow);
        maxSize = tableSize * 2;
    };

    public void addEntry(Position p, double evaluation, double depth, int moveNum) {
        size++;
        long positionHash = new Random(p.getTTHash()).nextLong();
        TTEntry newEntry = new TTEntry(evaluation, depth, positionHash, moveNum);
        int index = (int)Math.abs(positionHash % tableSize);
        if (savedPositions[index] == null) {
            savedPositions[index] = newEntry;
        }
        else {
            //Simple linked collision resolution/replacement
            boolean replacedEntry = false;
            TTEntry prevEntry = null;
            TTEntry currEntry = savedPositions[index];
            while (currEntry != null) {
                if (currEntry.signature == positionHash) {
                    size--;
                    replacedEntry = true;
                    if (currEntry.depth <= depth) {
                        currEntry.depth = depth;
                        currEntry.evaluation = evaluation;
                    }
                    currEntry.moveNum = Math.max(currEntry.moveNum, moveNum);
                    break;
                }
                prevEntry = currEntry;
                currEntry = currEntry.next;
            }
            if (!replacedEntry) {
                unwantedCollisions++;
                prevEntry.next = newEntry;
            }
        }
        if (size > maxSize) {
            cutoff++;
            cleanTable(cutoff);
        }
    }

    //Removes all table entries that are <= cutoff
    public void cleanTable(int cutoffMove) {
        for (int i = 0; i < tableSize; i++) {
            TTEntry prev = null;
            TTEntry curr = savedPositions[i];
            while (curr != null) {
                if (curr.moveNum <= cutoffMove) {
                    TTEntry temp = curr.next;
                    removeEntry(i, prev, curr);
                    curr = temp;
                }
                else {
                    prev = curr;
                    curr = curr.next;
                }

            }
        }
    }

    private void removeEntry(int entryIndex, TTEntry prev, TTEntry toRemove) {
        size--;
        if (prev == null) {
            savedPositions[entryIndex] = toRemove.next;
        }
        else {
            prev.next = toRemove.next;
        }
    };

    //returns Double.MAX_VALUE if no entry found
    public double getEntry(Position p, double minAcceptableDepth, int moveNum) {
        long positionHash = new Random(p.getTTHash()).nextLong();
        int index = (int)Math.abs(positionHash % tableSize);
        if (savedPositions[index] == null) {
            return Double.MAX_VALUE;
        }
        else {
            TTEntry currEntry = savedPositions[index];
            while (currEntry != null) {
                if (currEntry.signature == positionHash) {
                    //Checkmate
                    if (Math.abs(currEntry.evaluation) == Evaluate.WIN_EVAL) {
                        currEntry.moveNum = Math.max(moveNum, currEntry.moveNum);
                        return currEntry.evaluation;
                    }
                    if (currEntry.depth >= minAcceptableDepth) {
                        hits++;
                        currEntry.moveNum = Math.max(moveNum, currEntry.moveNum);
                        return currEntry.evaluation;
                    }
                    return Double.MAX_VALUE;
                }
                currEntry = currEntry.next;
            }
            return Double.MAX_VALUE;
        }
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < savedPositions.length; i++) {
            if (savedPositions[i] != null) {
                TTEntry curr = savedPositions[i];
                while (curr != null) {
                    s.append(curr.toString() + " -> ");
                    curr = curr.next;
                }
                s.append("\n");
            }
        }
        return s.toString();
    }





    class TTEntry {
        public double evaluation;
        public double depth;
        public long signature; //resolving index collisions
        //pray for no key collisions :pray:
        public TTEntry next;
        public int moveNum; //for cleaning up

        public TTEntry(double evaluation, double depth, long signature, int moveNum) {
            this.evaluation = evaluation;
            this.depth = depth;
            this.signature = signature;
            this.moveNum = moveNum;
            this.next = null;
        }

        public void setNext(TTEntry next) {
            this.next = next;
        }

        public String toString() {
            return "E: " + evaluation + "     D: " + depth + "     M: " + moveNum;
        }

    }

}
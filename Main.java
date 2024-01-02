import java.util.Arrays;

class Main {
    public static void main(String[] args) {
        for (byte i = 0; i < 64; i++) {
            System.out.println(BoardMethods.squareToString(i));
            int[] a = BoardMethods.byteToArray(i);
            System.out.println(Arrays.toString(a));
            System.out.println(BoardMethods.arrayToByte(a));
        }
    }
}
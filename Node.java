import java.util.*;
//taken from https://stackoverflow.com/questions/3522454/how-to-implement-a-tree-data-structure-in-java
public class Node<T> {
    public T data;
    //public Node<T> parent;
    public List<Node<T>> children;
}
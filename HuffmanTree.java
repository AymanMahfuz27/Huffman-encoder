/*  Student information for assignment:
 *
 *  On our honor, Mahir Kaya and Ayman Mahfuz, this programming assignment is our own work
 *  and we have not provided this code to any other student.
 *
 *  Number of slip days used:
 *
 *  Student 1 (Student whose Canvas account is being used)
 *  UTEID: mk45397
 *  email address: mahirgs2005@gmail.com
 *  Grader name: Namish
 *
 *  Student 2
 *  UTEID: aam7544
 *  email address: aymanmahfuz27@utexas.edu
 *
 */

import java.util.ArrayList;

public class HuffmanTree implements Comparable<HuffmanTree>{
    private TreeNode root;

    private final static int INTERNAL_NODE_CONSTANT = -10;

    /**
     * Construct a leaf node with the given value and frequency
     * @param value, leaf node's value
     * @param frequency, leaf node's frequency
     */
    public HuffmanTree(int value, int frequency) {
        root = new TreeNode(value, frequency);
    }

    /**
     * Construct a new Huffman Tree with the given subtrees
     * @param leftTree, the subtree that will be added to the left
     * @param rightTree, the subtree that will be added to the right
     */
    public HuffmanTree(HuffmanTree leftTree, HuffmanTree rightTree) {
        root = new TreeNode(leftTree.root,
                INTERNAL_NODE_CONSTANT,
                rightTree.root);
    }
    /**
     * @return the frequency of root
     */
    public int getFreqOfRoot() {
        return root.getFrequency();
    }

    /**
     * @return the value of root
     */
    public int getValue() {
        return root.getValue();
    }

    /**
     * @return the root of this tree
     */
    public TreeNode getRoot() {
        return root;
    }

    /**
     * Get the new values from the old values
     * @return a String list with new values
     */
    public String[] getNewValues() {
        String[] result = new String[IHuffConstants.ALPH_SIZE + 1];
        String newValue = "";
        traverseTree(root, result, newValue);
        return result;
    }

    /**
     * Traverses the tree in-order and updates the values
     * @param currentNode, the node we are currently at
     * @param result, array that stores the new values
     * @param value, the new value so far
     */
    private void traverseTree(TreeNode currentNode, String[] result, String value) {
        if (currentNode.isLeaf()) {
            result[currentNode.getValue()] = value;
        } else {
            traverseTree(currentNode.getLeft(), result, value + "0");
            traverseTree(currentNode.getRight(), result, value + "1");
        }
    }


    /**
     * Compares two Huffman trees based on their root nodes' frequencies
     * @param other the object to be compared.
     * @return int representing the difference between the two objects
     */
    public int compareTo(HuffmanTree other) {
        return root.getFrequency() - other.getFreqOfRoot() ;
    }

    public static HuffmanTree getFinalTree(int[] frequencies) {
        //ADDS THE FREQUENCIES TO THE PRIORITY QUEUE
        PriorityQueue<HuffmanTree> queue = new PriorityQueue<>();
        for (int i = 0; i < frequencies.length; i++) {
            int freq = frequencies[i];
            if (freq > 0) {
                HuffmanTree tree = new HuffmanTree(i, freq);
                queue.enqueue(tree);
            }
        }

        //Loops until only one element is left and constructs the final tree
        while(queue.size() > 1) {
            HuffmanTree first = queue.dequeue();
            HuffmanTree second = queue.dequeue();
            HuffmanTree resultingTree = new HuffmanTree(first, second);
            queue.enqueue(resultingTree);

        }
        //GET THE FINAL HUFFMAN TREE
        return queue.dequeue();
    }

    /**
     * Prints a vertical representation of this tree.
     * The tree has been rotated counter clockwise 90
     * degrees. The root is on the left. Each node is printed
     * out on its own row. A node's children will not necessarily
     * be at the rows directly above and below a row. They will
     * be indented three spaces from the parent. Nodes indented the
     * same amount are at the same depth.
     * <br>pre: none
     */
    public void printTree() {
        printTree(root, "");
    }



    //DELETE AFTER CONFIRMING IT WORKS
    private void printTree(TreeNode n, String spaces) {
        if(n != null){
            printTree(n.getRight(), spaces + "  ");
            if (n.getValue() == -10) {
                System.out.println(spaces + n.getFrequency());
            } else {
                System.out.println(spaces + "(" + (char) n.getValue() + " - " + n.getFrequency() + ")");
            }
            printTree(n.getLeft(), spaces + "  ");
        }
    }



}

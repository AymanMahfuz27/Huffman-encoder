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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Decoder {
    private static final int binaryLengthForTreeFormat = 9;

    private BitInputStream bitIn;
    private BitOutputStream bitOut;
    private IHuffViewer myViewer;

    private String[] newValues;

    public Decoder(InputStream in, OutputStream out, IHuffViewer viewer) {
        bitIn = new BitInputStream(in);
        bitOut = new BitOutputStream(out);
        myViewer = viewer;
    }

    public int decompress() throws IOException {
        int magic = bitIn.readBits(IHuffConstants.BITS_PER_INT);
        int sum = 0;
        // Read the magic number
        if (magic != IHuffConstants.MAGIC_NUMBER) {
            throw new IOException("Error reading the compressed file. The file did not start " +
                    "with the magic number");
        }
        int headerFormat = bitIn.readBits(IHuffConstants.BITS_PER_INT);
        if (headerFormat == IHuffConstants.STORE_COUNTS) {
            readSCF();
        } else if (headerFormat == IHuffConstants.STORE_TREE) {
            readSTF();
            System.out.println("HERE");
        } else {
            throw new IOException("Unexpected end for format header");
        }
        System.out.println("ENDED METHOD");
        return sum;
    }

    private void readSTF() throws IOException {
        int size = bitIn.readBits(IHuffConstants.BITS_PER_INT);
        if (size == -1) {
            throw new IOException("Unexpected end of file.");
        }
        StringBuilder tree = new StringBuilder();
        int count = 0;
        int currentBit = bitIn.readBits(1);
        while (count < size) {
            if (currentBit == -1) {
                throw new IOException("Unexpected end of file for reading the tree code.");
            }
            tree.append(currentBit);
            currentBit = bitIn.readBits(1);
            count++;
        }
        HuffmanTree finalTree = new HuffmanTree(0, 0);
        String treeCode = tree.toString();
        treeRecursiveHelper(treeCode, new int[] { 0 }, finalTree.getRoot(), finalTree);
        finalTree.printTree();
        newValues = finalTree.getNewValues();

    }

    private void treeRecursiveHelper(String treeCode, int[] index, TreeNode currentNode, HuffmanTree finalTree) {

    }

    private void readSCF () throws IOException{
            int[] frequencies = new int[IHuffConstants.ALPH_SIZE + 1];
            int index = 0;
            int frequency = 0;
            while (index < IHuffConstants.ALPH_SIZE) {
                frequency = bitIn.readBits(IHuffConstants.BITS_PER_INT);
                if (frequency == -1) {
                    throw new IOException("UNEXPECTED END OF FILE");
                }
                frequencies[index] = frequency;
                index++;
            }
            frequencies[frequencies.length - 1]++; //For PEOF
            newValues = HuffmanTree.getFinalTree(frequencies).getNewValues();
        }

}

private void decodeAndWriteData(BitInputStream in, OutputStream out, HuffmanTree tree) throws IOException{
    HuffmanTree.Node currentNode = tree.getRoot();
    boolean endOfFile = false;

    while (!endOfFile){
        int bit=in.readBits(1);
        currentNode = bit == 0 ? currentNode.getLeft : currentNode.getRight();

        if (currentNode.isLeaf()){
            if (currentNode.getValue() == IHuffConstants.PSEUDO_EOF) {
                endOfFile = true; // Reached end of compressed data
            } else {
                out.write(currentNode.getValue()); // Write the byte value
                currentNode = tree.getRoot(); // Reset to start from root for next character
            }
    }
}
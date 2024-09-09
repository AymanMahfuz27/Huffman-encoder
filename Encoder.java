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

//imports
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

public class Encoder {

    private static final int NUM_CHARS = 257; //256 chars + pseudo_eof
    private static final int binaryLengthForTreeFormat = 9;
    private int[] frequencies;
    private int headerFormat;
    private String[] newValues;
    private int originalNumberOfBits;
    private int newNumberOfBits;
    private HuffmanTree finalTree;
    private IHuffViewer myViewer;


    /**
     * Constructor for the encoder class
     * @param format, the header format
     * @param in, the input stream from which we will read the file
     */
    public Encoder(int format, InputStream in, IHuffViewer view) throws IOException{
        frequencies = getFrequencies(in);
        finalTree = HuffmanTree.getFinalTree(frequencies);
        newValues = finalTree.getNewValues();
        headerFormat = format;
        newNumberOfBits = getHowManyBitsWillBeWritten();
        myViewer = view;
    }

    /**
     * Get the frequency of each character in the given file
     * @param in, the input stream from which we will read the file
     * @return an array of ints that has the corresponding frequencies of each character
     */
    public int[] getFrequencies(InputStream in) throws IOException{
        //Wraps InputStream in BitInputStream
        BitInputStream reader = new BitInputStream(in);
        int[] frequencies = new int[NUM_CHARS];
        int currentRead = reader.readBits(IHuffConstants.BITS_PER_WORD);

        //UPDATE THE FREQUENCIES AND THE ORIGINAL SIZE INSTANCE VARIABLE
        while (currentRead != -1) {
            frequencies[currentRead]++;
            currentRead = reader.readBits(IHuffConstants.BITS_PER_WORD);
            originalNumberOfBits += IHuffConstants.BITS_PER_WORD;
        }
        frequencies[NUM_CHARS - 1]++;
        return frequencies;
    }

    /**
     * @return the final Huffman Tree that has the new values
     */


    /**
     * @return number of bits that will be written in the new file
     */
    public int getNewNumberOfBits() {
        return newNumberOfBits;
    }

    /**
     * @return number of bits that was written in the new file
     */
    public int getOriginalNumberOfBits() {
        return originalNumberOfBits;
    }

    /**
     * Before compressing the file, determines how many bits will have to be written to the new file
     * @return the number of bits that will be written to the file
     */
    public int getHowManyBitsWillBeWritten() throws IOException{
        int sum = 0;
        sum += 2 * IHuffConstants.BITS_PER_INT; //Number of bits for magic number

        // and the header format
        sum += howManyBitsForHeader(); //Number of bits for the header

        //Number of bits for the actual data and PEOF
        for (int i = 0; i < frequencies.length; i++) {
            if (frequencies[i] != 0) {
                sum += frequencies[i] * newValues[i].length();
            }
        }


        return sum;
    }

    /**
     * @return the number of bits that will be written to the compressed file for the encoder
     */
    private int howManyBitsForHeader() {
        int sum = 0;
        if (headerFormat == IHuffConstants.STORE_TREE) {
            //This is the size of the tree that will be written before the actual tree is written
            sum += IHuffConstants.BITS_PER_INT;
            //Get the number of bits the tree code will generate
            sum += getSTFCode().length();
        } else if (headerFormat == IHuffConstants.STORE_COUNTS) {
            //The number of bits written for the counts will be the number of elements in the
            //alphabet size times the BITS_PER_INT constant
            sum += IHuffConstants.ALPH_SIZE * IHuffConstants.BITS_PER_INT;
        }
        return sum;
    }

    /**
     * Compress the file using Huffman Coding
     * @param in, input stream from which we will read the original file
     * @param out, output stream from which we will write the new compressed file
     * @param force, boolean that indicates if the compression should take place based on the
     *               difference between the number of bits between the compressed file and
     *               the original file
     */
    public void compressFile(InputStream in, OutputStream out, boolean force)
            throws IOException {
        if (force && newNumberOfBits - originalNumberOfBits > 0) {
            myViewer.showError("Compressed file is larger than the actual file.");
            throw new IOException("COMPRESSION RESULTS IN A LARGER FILE");
        }
        BitInputStream bitIn = new BitInputStream(in);
        BitOutputStream bitOut = new BitOutputStream(out);

        //Write the magic number constant - STEP 1
        bitOut.writeBits(IHuffConstants.BITS_PER_INT, IHuffConstants.MAGIC_NUMBER);


        //Writes the header - STEP 2 & 3
        bitOut.writeBits(IHuffConstants.BITS_PER_INT, headerFormat);
        if (headerFormat == IHuffConstants.STORE_COUNTS) {
            writeSCF(bitOut);
        }
        if (headerFormat == IHuffConstants.STORE_TREE) {
            writeSTF(bitOut);
        }

        //Writes the content - STEP 4

        int bit;
        while ((bit = bitIn.readBits(IHuffConstants.BITS_PER_WORD)) != -1) {
            String code = newValues[bit];
            for (int i = 0; i < code.length(); i++) {
                bitOut.writeBits(1, code.charAt(i) == '0' ? 0 : 1);
            }
        }

        // Write the PSEUDO_EOF code - STEP 5
        String eofCode = newValues[IHuffConstants.PSEUDO_EOF];
        for (char c : eofCode.toCharArray()) {
            bitOut.writeBits(1, c == '0' ? 0 : 1);
        }
        bitIn.close();
        bitOut.close();
    }

    /**
     * Write the standard count format to the file
     * @param out, the output stream to which we will write the counts
     */
    private void writeSCF(BitOutputStream out) {
        for (int k = 0; k < IHuffConstants.ALPH_SIZE; k++) {
            out.writeBits(IHuffConstants.BITS_PER_INT, frequencies[k]);
        }
    }

    /**
     * Write the String version of the Standard Tree Format code for the header
     */
    private void writeSTF(BitOutputStream out) {
        String treeCode = getSTFCode();
        out.writeBits(IHuffConstants.BITS_PER_INT, treeCode.length());
        for (char i : treeCode.toCharArray()) {
            if (i == '1') {
                out.writeBits(1, 1);
            } else {
                out.writeBits(1, 0);
            }
        }
    }

    /**
     * @return the String version of the Standard Tree Format code for the header
     */
    private String getSTFCode() {
        StringBuilder sb = new StringBuilder();
        serializeNode(finalTree.getRoot(), sb);
        return sb.toString();
    }

    /**
     * Get the traversal code for the tree
     * @param node, the node we are currently at
     * @param sb, the string builder in which we will store the tree code
     */
    private void serializeNode(TreeNode node, StringBuilder sb) {
        if (node.isLeaf()) {
            sb.append("1");
            //append the value stored in the leaf
            String name = Integer.toBinaryString(node.getValue());
            if (name.length() < binaryLengthForTreeFormat) {
                name = "0".repeat(binaryLengthForTreeFormat - name.length()) + name;
            }
            sb.append(name);
        } else {
            sb.append("0");
            serializeNode(node.getLeft(), sb);
            serializeNode(node.getRight(), sb);
        }
    }
}


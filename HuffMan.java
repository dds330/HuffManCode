import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;

public class HuffMan {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(System.in);

        ArrayList<Pair> pairs = new ArrayList<>();

        System.out.println("Enter the file name to read from: ");
        String fileName = in.nextLine();

        File file = new File(fileName);
        Scanner inputFile = new Scanner(file);

        //reading each line of the file and creating pairs for each character and probability
        while(inputFile.hasNextLine()){
            String line = inputFile.nextLine();
            String[] lineVals = line.split("\\s+");
            char value = lineVals[0].charAt(0);
            double prob = Double.parseDouble(lineVals[1]);

            pairs.add(new Pair(value, prob));
        }

        inputFile.close();

        BinaryTree<Pair> huffmanTree = createHuffmanTree(pairs);
        String[] huffmanCodes = findEncoding(huffmanTree);

        System.out.println("Enter a line of text (uppercase letters only):");
        String input = in.nextLine();
        ArrayList<Character> encodedText = encode(input, huffmanCodes);
        String decodedText = decodeText(encodedText, huffmanTree);

        System.out.println("Here's the encoded line: ");
        for(int i = 0; i < encodedText.size(); i++){
            System.out.print(encodedText.get(i));
        }
        System.out.println();
        System.out.println("The decoded line is: " + decodedText);
    }

    /*
     * building the huffman tree using priority queue
     * returns a binary tree of type Pair
     */
    private static BinaryTree<Pair> createHuffmanTree(ArrayList<Pair> pairs){
        PriorityQueue<BinaryTree<Pair>> queue =
                new PriorityQueue<>((a, b) -> Double.compare(a.getData().getProb(), b.getData().getProb()));

        //creating a new binary tree for each character (pair)
        for(Pair pair: pairs){
            BinaryTree<Pair> tree = new BinaryTree<>();
            tree.makeRoot(pair);
            queue.offer(tree);
        }

        while(queue.size() > 1){
            //removes the two trees with the least weight
            BinaryTree<Pair> A = queue.poll();
            BinaryTree<Pair> B = queue.poll();
            BinaryTree<Pair> P = new BinaryTree<>();

            //makes a parent node for the two objects which is the sum of weights of the two
            P.makeRoot(new Pair('\0', A.getData().getProb() + B.getData().getProb()));
            P.attachLeft(A);
            P.attachRight(B);

            //adds the parent node to the queue so that it is compared for next iterations
            queue.offer(P);
        }
        return queue.poll();
    }

    private static String[] findEncoding(BinaryTree<Pair> bt){
        String[] result = new String[26];
        findEncoding(bt, result, "");
        return result;
    }

    private static void findEncoding(BinaryTree<Pair> bt, String[] a, String prefix){
        if(bt.getLeft() == null && bt.getRight() == null){
            a[bt.getData().getValue() - 65] = prefix;
        }
        else{
            findEncoding(bt.getLeft(), a, prefix + "0");
            findEncoding(bt.getRight(), a, prefix + "1");
        }
    }

    private static ArrayList<Character> encode(String text, String[] huffmanCodes){
        ArrayList<Character> encodedText = new ArrayList<>();
        for(char c : text.toCharArray()){
            //if it's a space, simply add it
            if(c == ' '){
                encodedText.add(c);
            }
            //else convert it to huffman code
            else{
                String code = huffmanCodes[ c - 65];
                for(char bit: code.toCharArray()){
                    encodedText.add(bit);
                }
            }
        }
        return encodedText;
    }

    private static String decodeText(ArrayList<Character> encodedText, BinaryTree<Pair> huffmanTree){
        StringBuilder decodedText = new StringBuilder();
        BinaryTree<Pair> current = huffmanTree;
        for(char bit: encodedText){
            if(bit == '0' && current != null){
                current = current.getLeft();
            }
            else if(bit == '1' && current != null){
                current = current.getRight();
            }
            //if it's a space, add space in the decoded text too
           else{
                decodedText.append(' ');
                current = huffmanTree;
                continue;
            }
            if(current != null && current.getLeft() == null && current.getRight() == null){
                decodedText.append(current.getData().getValue());
                current = huffmanTree;
            }
        }
         if(current != null){
             decodedText.append(current.getData().getValue());
         }
         decodedText.deleteCharAt(decodedText.length() - 1);

        return decodedText.toString();
    }
}

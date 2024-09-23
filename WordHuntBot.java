import java.util.*;
import java.io.*;

class WordHuntBot {
    private static final int[] ROW_DIRECTIONS = {-1, -1, -1, 0, 0, 1, 1, 1};
    private static final int[] COL_DIRECTIONS = { -1, 0, 1, -1, 1, -1, 0, 1};
    private static Trie wordTrie;

    public static void main (String[] args) throws IOException {
        wordTrie = new Trie();
        loadWords("wordhuntwords.txt");
        char[][] board = buildBoard();
        List<String> words = findWords(board);
        for (String word : words) {
            System.out.println(word);
        }
    }

    public static void loadWords(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String word;
            while ((word = br.readLine()) != null) {
                wordTrie.insert(word.trim().toLowerCase());
            }
        }
    }

    // ask user to input letters from board, format into 2D array
    public static char[][] buildBoard() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter letters from the board in lowercase:");
        String letters = "";
    
        while (letters.length() != 25) {
            letters = scan.nextLine();
        }
    
        char[][] board = new char[5][5];
        for (int i = 0; i < 25; i++) {
            board[i / 5][i % 5] = letters.charAt(i);
        }

        scan.close();
        return board;
    }

    // DFS function to find all valid words on inputted board
    private static List<String> findWords(char[][] board) {
        Set<String> validWords = new HashSet<>();
        boolean[][] visited = new boolean[5][5];

        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                dfs(board, visited, "", r, c, validWords);
            }
        }

        List<String> sortedWords = new ArrayList<>(validWords);
        sortedWords.sort((a, b) -> b.length() - a.length());

        return sortedWords;
    }

    // method for DFS
    private static void dfs(char[][] board, boolean[][] visited, String currentWord, int row, int col, Set<String> validWords) {
        if (row < 0 || col < 0 || row >= 5 || col >= 5 || visited[row][col]) {
            return;
        }

        currentWord += board[row][col];

        if (!wordTrie.startsWith(currentWord)) {
            return;
        }

        if (currentWord.length() >= 3 && wordTrie.search(currentWord)) {
            validWords.add(currentWord);
        }

        visited[row][col] = true;

        for (int i = 0; i < 8; i++) {
            int newRow = row + ROW_DIRECTIONS[i];
            int newCol = col + COL_DIRECTIONS[i];
            dfs(board, visited, currentWord, newRow, newCol, validWords);
        }
        visited[row][col] = false;
    }
}

class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            if (!node.containsKey(c)) {
                node.put(c, new TrieNode());
            }
            node = node.get(c);
        }
        node.setEnd();
    }

    public boolean search(String word) {
        TrieNode node = searchPrefix(word);
            return node != null && node.isEnd();
    }

    public boolean startsWith(String prefix) {
        return searchPrefix(prefix) != null;
    }

    private TrieNode searchPrefix(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            if (node.containsKey(c)) {
                node = node.get(c);
            } else {
                return null;
            }
        }
        return node;
    }
 }

 class TrieNode {
    private TrieNode[] links;
    private final int R = 26;
    private boolean isEnd;

    public TrieNode() {
        links = new TrieNode[R];
    }

    public boolean containsKey(char ch) {
        return links[ch - 'a'] != null;
    }

    public TrieNode get(char ch) {
        return links[ch - 'a'];
    }

    public void put(char ch, TrieNode node) {
        links[ch - 'a'] = node;
    }

    public void setEnd() {
        isEnd = true;
    }

    public boolean isEnd() {
        return isEnd;
    }
 }

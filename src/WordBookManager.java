import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

public class WordBookManager extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;//序列化属性
    private JTextField wordField, meaningField;
    private JButton addButton, modifyButton, deleteButton, searchButton, browseButton,getRandomButton;
    private JTextArea wordArea;
    JTextArea textArea = new JTextArea();
    private ArrayList<Word> wordList;

    public static void main(String[] args) {
        //WordBookManager win=new WordBookManager();
        //win.setTitle("英文单词簿");
        SwingUtilities.invokeLater(new Runnable() {//在 EDT 中创建和显示 GUI
            public void run() {
                new WordBookManager();

            }
        });
    }

    public WordBookManager() { // 初始化单词列表
        super("英文单词簿");
        wordList = new ArrayList<Word>();//泛型集合 ArrayList
        loadWordsFromFile(); // 从文件中加载单词
        createGUI();// 创建界面
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setVisible(true);
        System.out.println("wordList:");//控制台打印 wordList 的内容，调试用
        for (Word w : wordList) {
            System.out.println(w.getWord() + ", " + w.getMeaning());
        }
    }

    private void createGUI() { // 创建输入面板


        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.PAGE_AXIS));
        wordField = new JTextField(10);
        meaningField = new JTextField(10);
        addButton = new JButton("添加单词");
        modifyButton = new JButton("修改单词");
        deleteButton = new JButton("删除单词");
        searchButton = new JButton("查询一个单词");
        browseButton = new JButton("浏览全部单词");
        getRandomButton = new JButton("随机查看单词");
        addButton.addActionListener(this);
        modifyButton.addActionListener(this);
        deleteButton.addActionListener(this);
        searchButton.addActionListener(this);
        browseButton.addActionListener(this);
        getRandomButton.addActionListener(this);
        inputPanel.add(new JLabel("英文单词"));
        inputPanel.add(wordField);
        inputPanel.add(new JLabel("中文解释"));
        inputPanel.add(meaningField);
        inputPanel.add(addButton);
        inputPanel.add(modifyButton);
        inputPanel.add(deleteButton);
        inputPanel.add(searchButton);
        inputPanel.add(browseButton);
        inputPanel.add(getRandomButton);

        // 创建单词显示区域
        wordArea = new JTextArea();
        wordArea.setEditable(false);

        // 将输入面板和单词显示区域添加到窗口中
        getContentPane().add(inputPanel, BorderLayout.WEST);
        getContentPane().add(new JScrollPane(wordArea), BorderLayout.CENTER);

    }
    private void loadWordsFromFile() {//将程序联系到文件
        try {
            Scanner scanner = new Scanner(new File("wordbook.txt"));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.length() > 0) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        String word = parts[0].trim();
                        String meaning = parts[1].trim();
                        wordList.add(new Word(word, meaning));
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            // 如果文件不存在则创建一个空文件
            try {
                FileWriter writer = new FileWriter(new File("wordbook.txt"));
                writer.write("");
                writer.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void saveWordsToFile() {//将单词列表中的所有单词写入到文件
        try {
            FileWriter writer = new FileWriter(new File("wordbook.txt"));
            for (Word word : wordList) {
                writer.write(word.getWord() + "," + word.getMeaning() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   /* private void addWord() { // 获取用户输入的单词和解释
        String word = wordField.getText().trim();
        String meaning = meaningField.getText().trim();
        if (word.length() > 0 && meaning.length() > 0) { // 添加单词到列表中
            wordList.add(new Word(word, meaning)); // 将单词列表保存到文件中
            saveWordsToFile();// 在单词显示区域显示添加成功的消息

            wordArea.setText("添加成功");
        } else {
            wordArea.setText("请输入单词和解释");// 在单词显示区域显示提示信息
        }
    }*/
   private void addWord() {
       String word = wordField.getText().trim();
       String meaning = meaningField.getText().trim();
       if (word.length() > 0 && meaning.length() > 0) {
           boolean found = false; // 查找单词列表中是否存在要添加的单词
           for (Word w : wordList) {
               if (w.getWord().equals(word)) {
                   wordArea.setText("已有此单词");// 如果单词存在，在单词显示区域显示提示信息
                   found = true;
                   break;
               }
           }
           if (!found) { // 如果单词不存在，则添加单词到列表中
               wordList.add(new Word(word, meaning));
               saveWordsToFile();
               wordArea.setText("添加成功");// 在单词显示区域显示添加成功的消息
           }
       } else {
           wordArea.setText("请输入单词和解释");// 在单词显示区域显示提示信息
       }
   }
    private void modifyWord() {//修改单词
        String word = wordField.getText().trim();
        String meaning = meaningField.getText().trim();
        if (word.length() > 0 && meaning.length() > 0) { // 查找单词列表中是否存在要修改的单词
            boolean found = false;
            for (Word w : wordList) {
                if (w.getWord().equals(word)) {// 修改单词的解释
                    w.setMeaning(meaning);// 将单词列表保存到文件中
                    found = true;
                    break;
                }
            }
            if (found) {
                saveWordsToFile();// 在单词显示区域显示修改成功的消息
                wordArea.setText("修改成功");
            } else {
                wordArea.setText("单词不存在");
            } // 在单词显示区域显示提示信息
        } else {
            wordArea.setText("请输入单词和解释");
        }
    }

    private void deleteWord() {
        String word = wordField.getText().trim(); // 获取用户输入的单词
        if (word.length() > 0) { // 查找单词列表中是否存在要删除的单词
            boolean removed = false;
            Iterator<Word> iter = wordList.iterator();
            while (iter.hasNext()) {
                Word w = iter.next();
                if (w.getWord().equals(word)) {// 从单词列表中删除该单词
                    iter.remove();
                    removed = true;
                    break;
                }
            }
            if (removed) {
                saveWordsToFile();
                wordArea.setText("删除成功");// 在单词显示区域显示删除成功的消息
            } else {
                wordArea.setText("单词不存在");
            }
        } else {
            wordArea.setText("请输入单词");// 在单词显示区域显示提示信息
        }
    }

    private void searchWord() {//查找单词
        String word = wordField.getText().trim(); // 查找单词列表中是否存在要查询的单词
        if (word.length() > 0) {
            boolean found = false;
            for (Word w : wordList) {
                if (w.getWord().equals(word)) { // 在单词显示区域显示该单词的解释
                    wordArea.setText(w.getWord() + " " + w.getMeaning());
                    found = true;
                    break;
                }
            } // 如果单词不存在则在单词显示区域显示提示信息
            if (!found) {
                wordArea.setText("单词不存在");
            }
        } else {
            wordArea.setText("请输入单词");
        }
    }

    private void browseWords() {// 在单词显示区域显示所有单词和解释
        StringBuilder sb = new StringBuilder();
        for (Word w : wordList) {
            sb.append(w.getWord() + " " + w.getMeaning() + "\n");
        }
        wordArea.setText(sb.toString());
    }

    private void getRandomWord() {//随机生成一个单词
        Random rand = new Random();
        int index = rand.nextInt(wordList.size());
        Word randomWord = wordList.get(index);
        wordArea.setText(randomWord.getWord() + "  " + randomWord.getMeaning());
    }

    @Override
    public void actionPerformed(ActionEvent e) {//监视按钮选择
        if (e.getSource() == addButton) {
            addWord();
        } else if (e.getSource() == modifyButton) {
            modifyWord();
        } else if (e.getSource() == deleteButton) {
            deleteWord();
        } else if (e.getSource() == searchButton) {
            searchWord();
        } else if (e.getSource() == browseButton) {
            browseWords();
        } else if (e.getSource() == getRandomButton) {
            getRandomWord();
        }
    }

    private class Word {
        private String word;
        private String meaning;

        public Word(String word, String meaning) {
            this.word = word;
            this.meaning = meaning;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public String getMeaning() {
            return meaning;
        }

        public void setMeaning(String meaning) {
            this.meaning = meaning;
        }
    }
}
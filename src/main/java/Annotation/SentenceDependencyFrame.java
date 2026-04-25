package Annotation;

import AnnotatedSentence.AnnotatedCorpus;
import AnnotatedSentence.AnnotatedSentence;
import AnnotatedSentence.AnnotatedWord;
import DataCollector.DataCollector;
import DataCollector.ParseTree.TreeEditorPanel;
import DataCollector.Sentence.SentenceAnnotatorFrame;
import DataCollector.Sentence.SentenceAnnotatorPanel;
import DependencyError.DependencyError;
import DependencyError.DependencyErrorType;
import DependencyParser.Universal.UniversalDependencyRelation;
import Util.DrawingButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class SentenceDependencyFrame extends SentenceAnnotatorFrame {

    static final protected String DELETEWORD = "deleteword";
    JList<String> errorList;
    JScrollPane scrollPane;
    private final JCheckBox autoDependencyDetectionOption;
    private final HashMap<String, ArrayList<AnnotatedWord>> mappedWords = new HashMap<>();
    private final HashMap<String, ArrayList<AnnotatedSentence>> mappedSentences = new HashMap<>();

    @Override
    public SentenceAnnotatorPanel generatePanel(String currentPath, String rawFileName) {
        return new SentenceDependencyPanel(currentPath, rawFileName, mappedWords, mappedSentences, scrollPane);
    }

    public SentenceDependencyFrame() {
        super();
        autoDependencyDetectionOption = new JCheckBox("Auto Dependency Detection", false);
        toolBar.add(autoDependencyDetectionOption);
        JPanel errorPanel = new JPanel(new BorderLayout(50, 0));
        errorList = new JList<>();
        scrollPane = new JScrollPane(errorList);
        errorPanel.add(scrollPane);
        bottom.add(errorPanel, BorderLayout.SOUTH);
        scrollPane.setPreferredSize(new Dimension(200, 100));
        scrollPane.setVisible(false);
        JButton button = new DrawingButton(DataCollector.class, this, "delete", DELETEWORD, "Delete Word");
        button.setVisible(true);
        toolBar.add(button);
        projectPane.addChangeListener(e -> {
            if (projectPane.getTabCount() > 0) {
                showErrors((SentenceDependencyPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView());
            }
        });
        String subFolder = "false";
        Properties properties1 = new Properties();
        try {
            properties1.load(Files.newInputStream(new File("config.properties").toPath()));
            subFolder = properties1.getProperty("subFolder");
        } catch (IOException ignored) {
        }
        AnnotatedCorpus annotatedCorpus = readCorpus(subFolder);
        for (int i = 0; i < annotatedCorpus.sentenceCount(); i++) {
            AnnotatedSentence sentence = (AnnotatedSentence) annotatedCorpus.getSentence(i);
            for (int j = 0; j < sentence.wordCount(); j++) {
                AnnotatedWord word = (AnnotatedWord) sentence.getWord(j);
                UniversalDependencyRelation universalDependencyRelation = word.getUniversalDependency();
                if (word.getName() != null && universalDependencyRelation != null) {
                    ArrayList<AnnotatedWord> annotatedWords;
                    if (mappedWords.containsKey(word.getName())) {
                        annotatedWords = mappedWords.get(word.getName());
                    } else {
                        annotatedWords = new ArrayList<>();
                    }
                    annotatedWords.add(word);
                    mappedWords.put(word.getName(), annotatedWords);
                    ArrayList<AnnotatedSentence> annotatedSentences;
                    if (mappedSentences.containsKey(word.getName())) {
                        annotatedSentences = mappedSentences.get(word.getName());
                    } else {
                        annotatedSentences = new ArrayList<>();
                    }
                    annotatedSentences.add(sentence);
                    mappedSentences.put(word.getName(), annotatedSentences);
                }
            }
        }
        JMenuItem itemShowUnannotated = addMenuItem(projectMenu, "Show Unannotated Files", KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK));
        itemShowUnannotated.addActionListener(e -> {
            int count = 0;
            String result = JOptionPane.showInputDialog(null, "How many sentences you want to see:", "",
                    JOptionPane.PLAIN_MESSAGE);
            int numberOfSentences = Integer.parseInt(result);
            for (int i = 0; i < annotatedCorpus.sentenceCount(); i++) {
                AnnotatedSentence sentence = (AnnotatedSentence) annotatedCorpus.getSentence(i);
                for (int j = 0; j < sentence.wordCount(); j++) {
                    AnnotatedWord word = (AnnotatedWord) sentence.getWord(j);
                    UniversalDependencyRelation universalDependencyRelation = word.getUniversalDependency();
                    if (word.getName() != null && universalDependencyRelation == null) {
                        SentenceAnnotatorPanel annotatorPanel = generatePanel(TreeEditorPanel.phrasePath, sentence.getFileName());
                        addPanelToFrame(annotatorPanel, sentence.getFileName());
                        count++;
                        if (count == numberOfSentences) {
                            return;
                        }
                        break;
                    }
                }
            }
        });
        JMenuItem itemViewAnnotated = addMenuItem(projectMenu, "View Annotations", KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        itemViewAnnotated.addActionListener(e -> new ViewSentenceDependencyAnnotationFrame(annotatedCorpus, this));
        JOptionPane.showMessageDialog(this, "Annotated corpus is loaded!", "Dependency Annotation", JOptionPane.INFORMATION_MESSAGE);
    }

    protected void deleteWord() {
        SentenceDependencyPanel current = (SentenceDependencyPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (current != null) {
            current.deleteWord();
        }
    }

    public static boolean checkDependencyWithToUniversalPosTag(String dependency, String universalPos, String fromPos) {
        if (dependency.equals("OBL") && universalPos.equals("NOUN")) {
            return !fromPos.equals("NOUN");
        }
        return true;
    }

    /**
     * Checks if there is an error with the dependency relation and the universal pos tag of the word.
     *
     * @param dependency   Dependency tag of the dependency relation.
     * @param universalPos Universal pos tag of the dependent word.
     * @return True if there is no error, false if there is an error.
     */
    public static boolean checkDependencyWithUniversalPosTag(String dependency, String universalPos) {
        if (dependency.equals("ADVMOD")) {
            if (!universalPos.equals("ADV") && !universalPos.equals("ADJ") && !universalPos.equals("CCONJ") &&
                    !universalPos.equals("DET") && !universalPos.equals("PART") && !universalPos.equals("SYM")) {
                return false;
            }
        }
        if (dependency.equals("AUX") && !universalPos.equals("AUX")) {
            return false;
        }
        if (dependency.equals("CASE")) {
            if (universalPos.equals("PROPN") || universalPos.equals("ADJ") || universalPos.equals("PRON") ||
                    universalPos.equals("DET") || universalPos.equals("NUM") || universalPos.equals("AUX")) {
                return false;
            }
        }
        if (dependency.equals("MARK") || dependency.equals("CC")) {
            if (universalPos.equals("NOUN") || universalPos.equals("PROPN") || universalPos.equals("ADJ") ||
                    universalPos.equals("PRON") || universalPos.equals("DET") || universalPos.equals("NUM") ||
                    universalPos.equals("VERB") || universalPos.equals("AUX") || universalPos.equals("INTJ")) {
                return false;
            }
        }
        if (dependency.equals("COP")) {
            if (!universalPos.equals("AUX") && !universalPos.equals("PRON") &&
                    !universalPos.equals("DET") && !universalPos.equals("SYM")) {
                return false;
            }
        }
        if (dependency.equals("DET")) {
            if (!universalPos.equals("DET") && !universalPos.equals("PRON")) {
                return false;
            }
        }
        if (dependency.equals("NUMMOD")) {
            if (!universalPos.equals("NUM") && !universalPos.equals("NOUN") && !universalPos.equals("SYM")) {
                return false;
            }
        }
        if (!dependency.equals("PUNCT") && universalPos.equals("PUNCT")) {
            return false;
        }
        return !dependency.equals("COMPOUND") || !universalPos.equals("AUX");
    }

    /**
     * Checks if there is a non-projectivity case between two words.
     *
     * @param from Index of the first word
     * @param to   Index of the second word
     * @return True if there are no outgoing arcs to out of the group specified with indexes (from, to), false
     * otherwise.
     */
    public boolean checkForNonProjectivityOfPunctuation(AnnotatedSentence sentence, int from, int to) {
        int min = Math.min(from, to);
        int max = Math.max(from, to);
        for (int i = 0; i < sentence.wordCount(); i++) {
            AnnotatedWord word = (AnnotatedWord) sentence.getWord(i);
            if (word.getUniversalDependency() != null) {
                int currentTo = word.getUniversalDependency().to();
                int currentFrom = i + 1;
                if (currentFrom > min && currentFrom < max && (currentTo < min || currentTo > max)) {
                    return false;
                }
                if (currentTo > min && currentTo < max && (currentFrom < min || currentFrom > max)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if there are multiple root words in the sentence or not.
     *
     * @return True if there are multiple roots, false otherwise.
     */
    public boolean checkMultipleRoots(AnnotatedSentence sentence) {
        int rootCount = 0;
        for (int i = 0; i < sentence.wordCount(); i++) {
            AnnotatedWord word = (AnnotatedWord) sentence.getWord(i);
            if (word.getUniversalDependency() != null && word.getUniversalDependency().toString().equals("ROOT")) {
                rootCount++;
            }
        }
        return rootCount <= 1;
    }

    /**
     * Checks if there are multiple subjects dependent to the root node.
     *
     * @return True if there are multiple subjects dependent to the root node. False otherwise.
     */
    public boolean checkMultipleSubjects(AnnotatedSentence sentence) {
        int subjectCount = 0;
        for (int i = 0; i < sentence.wordCount(); i++) {
            AnnotatedWord word = (AnnotatedWord) sentence.getWord(i);
            if (word.getUniversalDependency() != null && word.getUniversalDependency().toString().equals("NSUBJ") && word.getUniversalDependency().to() - 1 >= 0 && word.getUniversalDependency().to() - 1 < sentence.wordCount()) {
                AnnotatedWord toWord = (AnnotatedWord) sentence.getWord(word.getUniversalDependency().to() - 1);
                if (toWord.getUniversalDependency() != null && toWord.getUniversalDependency().toString().equals("ROOT")) {
                    subjectCount++;
                }
            }
        }
        return subjectCount <= 1;
    }

    /**
     * Checks the annotated sentence for dependency errors, and returns them as a list.
     *
     * @return An arraylist of dependency annotation errors.
     */
    public ArrayList<DependencyError> getDependencyErrors(AnnotatedSentence sentence) {
        ArrayList<DependencyError> errorList = new ArrayList<>();
        if (!checkMultipleRoots(sentence)) {
            errorList.add(new DependencyError(DependencyErrorType.MULTIPLE_ROOT,
                    0,
                    (AnnotatedWord) sentence.getWord(0),
                    "",
                    ""));
        }
        if (!checkMultipleSubjects(sentence)) {
            errorList.add(new DependencyError(DependencyErrorType.MULTIPLE_SUBJECTS,
                    0,
                    (AnnotatedWord) sentence.getWord(0),
                    "",
                    ""));
        }
        for (int i = 0; i < sentence.wordCount(); i++) {
            AnnotatedWord word = (AnnotatedWord) sentence.getWord(i);
            if (word.getUniversalDependencyPos() == null) {
                errorList.add(new DependencyError(DependencyErrorType.NO_MORPHOLOGICAL_ANALYSIS,
                        i + 1,
                        (AnnotatedWord) sentence.getWord(i),
                        "",
                        ""));
            }
            if (word.getUniversalDependency() != null) {
                int to = word.getUniversalDependency().to();
                int from = i + 1;
                String dependency = word.getUniversalDependency().toString();
                if (from == to) {
                    errorList.add(new DependencyError(DependencyErrorType.HEAD_EQUALS_ID,
                            from,
                            (AnnotatedWord) sentence.getWord(from - 1),
                            "",
                            ""));
                }
                if (dependency.equals("PUNCT") && !checkForNonProjectivityOfPunctuation(sentence, from, to)) {
                    errorList.add(new DependencyError(DependencyErrorType.PUNCTUATION_NOT_PROJECTIVE,
                            from,
                            (AnnotatedWord) sentence.getWord(from - 1),
                            "",
                            ""));
                }
                if (to > from && (dependency.equals("CONJ") || dependency.equals("GOESWITH") ||
                        dependency.equals("FIXED") || dependency.equals("FLAT") || dependency.equals("APPOS"))) {
                    errorList.add(new DependencyError(DependencyErrorType.GO_LEFT_TO_RIGHT,
                            from,
                            (AnnotatedWord) sentence.getWord(from - 1),
                            dependency,
                            ""));
                }
                if (from > to && from > to + 1 && dependency.equals("GOESWITH")) {
                    errorList.add(new DependencyError(DependencyErrorType.GAPS_IN_GOESWITH,
                            from,
                            (AnnotatedWord) sentence.getWord(from - 1),
                            "",
                            ""));
                }
                if (to > 0 && to <= sentence.wordCount() && ((AnnotatedWord) sentence.getWord(to - 1)).getUniversalDependencyPos() != null){
                    String universalPos = word.getUniversalDependencyPos();
                    String toPos = ((AnnotatedWord) sentence.getWord(to - 1)).getUniversalDependencyPos();
                    if (!checkDependencyWithToUniversalPosTag(dependency, universalPos, toPos)){
                        errorList.add(new DependencyError(DependencyErrorType.SHOULDNT_BE_OF_POS,
                                from,
                                (AnnotatedWord) sentence.getWord(from - 1),
                                dependency,
                                toPos));
                    }
                }
                if (word.getUniversalDependencyPos() != null) {
                    String universalPos = word.getUniversalDependencyPos();
                    if (!checkDependencyWithUniversalPosTag(dependency, universalPos)) {
                        errorList.add(new DependencyError(DependencyErrorType.SHOULDNT_BE_OF_POS,
                                from,
                                (AnnotatedWord) sentence.getWord(from - 1),
                                dependency,
                                universalPos));
                    }
                }
                if (to > 0 && to <= sentence.wordCount()) {
                    AnnotatedWord toWord = (AnnotatedWord) sentence.getWord(to - 1);
                    if (toWord.getUniversalDependency() != null) {
                        String toDependency = toWord.getUniversalDependency().toString();
                        if (toDependency.equals("AUX") || toDependency.equals("COP") || toDependency.equals("CC") ||
                                toDependency.equals("FIXED") || toDependency.equals("GOESWITH") || toDependency.equals("CASE") ||
                                toDependency.equals("MARK") || toDependency.equals("DET") || toDependency.equals("PUNCT")) {
                            errorList.add(new DependencyError(DependencyErrorType.NOT_EXPECTED_TO_HAVE_CHILDREN,
                                    from,
                                    (AnnotatedWord) sentence.getWord(from - 1),
                                    toDependency,
                                    ""));
                        }
                        if (dependency.equals("ORPHAN") && !toDependency.equals("CONJ")) {
                            errorList.add(new DependencyError(DependencyErrorType.PARENT_ORPHAN_SHOULD_BE_CONJ,
                                    from,
                                    (AnnotatedWord) sentence.getWord(from - 1),
                                    toDependency,
                                    ""));
                        }
                    }
                }
            } else {
                errorList.add(new DependencyError(DependencyErrorType.NO_DEPENDENCY,
                        i + 1,
                        (AnnotatedWord) sentence.getWord(i),
                        "",
                        ""));
            }
        }
        return errorList;
    }

    protected void showErrors(SentenceDependencyPanel current) {
        if (current != null) {
            ArrayList<DependencyError> errors = getDependencyErrors(current.sentence);
            if (!errors.isEmpty()) {
                DefaultListModel<String> listModel = new DefaultListModel<>();
                for (DependencyError dependencyError : errors) {
                    listModel.addElement(dependencyError.toString());
                }
                errorList.setModel(listModel);
                scrollPane.setVisible(true);
            } else {
                scrollPane.setVisible(false);
            }
        }
    }

    public void next(int count) {
        super.next(count);
        SentenceDependencyPanel current;
        current = (SentenceDependencyPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (autoDependencyDetectionOption.isSelected()) {
            current.autoDetect();
        }
    }

    public void previous(int count) {
        super.previous(count);
        SentenceDependencyPanel current;
        current = (SentenceDependencyPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        if (autoDependencyDetectionOption.isSelected()) {
            current.autoDetect();
        }
    }

    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        switch (e.getActionCommand()) {
            case DELETEWORD:
                deleteWord();
                break;
            case BACKWARD:
            case FORWARD:
            case FAST_BACKWARD:
            case FAST_FORWARD:
            case FAST_FAST_BACKWARD:
            case FAST_FAST_FORWARD:
                showErrors((SentenceDependencyPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView());
                break;
        }
    }

}

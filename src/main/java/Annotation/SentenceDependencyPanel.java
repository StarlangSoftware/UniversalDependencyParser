package Annotation;

import AnnotatedSentence.AnnotatedSentence;
import AnnotatedSentence.AnnotatedWord;
import AnnotatedSentence.AnnotatedCorpus;
import AnnotatedSentence.ViewLayerType;
import AutoProcessor.TurkishSentenceAutoDependency;
import DataCollector.Sentence.SentenceAnnotatorPanel;
import DataStructure.CounterHashMap;
import DependencyParser.Universal.UniversalDependencyRelation;
import DependencyParser.Universal.UniversalDependencyType;
import Dictionary.Word;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SentenceDependencyPanel extends SentenceAnnotatorPanel {

    private boolean dragged = false;
    private int dragX = -1, dragY = -1;
    private TurkishSentenceAutoDependency turkishSentenceAutoDependency;
    private HashMap<String, ArrayList<AnnotatedWord>> mappedWords;
    private HashMap<String, ArrayList<AnnotatedSentence>> mappedSentences;

    public SentenceDependencyPanel(String currentPath, String rawFileName, HashMap<String, ArrayList<AnnotatedWord>> mappedWords, HashMap<String, ArrayList<AnnotatedSentence>> mappedSentences) {
        super(currentPath, rawFileName, ViewLayerType.DEPENDENCY);
        this.mappedWords = mappedWords;
        this.mappedSentences = mappedSentences;
        turkishSentenceAutoDependency = new TurkishSentenceAutoDependency();
        list.setCellRenderer(new ListRenderer());
    }

    private class ListRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component cell = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            AnnotatedWord selectedWord = ((AnnotatedWord)sentence.getWord(selectedWordIndex));
            String examples = "<html>";
            int count = 0;
            if (mappedSentences.containsKey(selectedWord.getName())){
                for (AnnotatedSentence annotatedSentence : mappedSentences.get(selectedWord.getName())){
                    for (int i = 0; i < annotatedSentence.wordCount(); i++){
                        AnnotatedWord word = (AnnotatedWord) annotatedSentence.getWord(i);
                        if (word.getName().equals(selectedWord.getName())){
                            if (word.getUniversalDependency().toString().equals(value)){
                                examples += annotatedSentence.toDependencyString(i) + "<br>";
                                count++;
                            }
                        }
                    }
                    if (count >= 20){
                        break;
                    }
                }
            }
            examples += "</html>";
            ((JComponent) cell).setToolTipText(examples);
            return this;
        }
    }

    public void deleteWord(){
        if (selectedWordIndex != -1) {
            sentence.removeWord(selectedWordIndex);
            sentence.save();
            selectedWordIndex = -1;
            this.repaint();
        }
    }

    public void autoDetect(){
        turkishSentenceAutoDependency.autoDependency(sentence);
        sentence.save();
        this.repaint();
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        boolean errorMode = false;
        if (draggedWordIndex != -1) {
            UniversalDependencyRelation parentRelation = ((AnnotatedWord) sentence.getWord(draggedWordIndex)).getUniversalDependency();
            if (parentRelation != null &&
                    (parentRelation.toString().equals("PUNCT") || parentRelation.toString().equals("MARK") ||
                            parentRelation.toString().equals("CASE") || parentRelation.toString().equals("GOESWITH") ||
                            parentRelation.toString().equals("FIXED") || parentRelation.toString().equals("CC") ||
                            parentRelation.toString().equals("AUX") || parentRelation.toString().equals("COP"))){
                JOptionPane.showMessageDialog(this, parentRelation.toString() + " not expected to have children!", "Dependency Rule", JOptionPane.ERROR_MESSAGE);
                selectionMode = false;
                errorMode = true;
            } else {
                int selectedIndex = populateLeaf(sentence, selectedWordIndex);
                if (selectedIndex != -1){
                    list.setValueIsAdjusting(true);
                    list.setSelectedIndex(selectedIndex);
                }
                list.setVisible(true);
                pane.setVisible(true);
                pane.getVerticalScrollBar().setValue(0);
                pane.setBounds((((AnnotatedWord) sentence.getWord(selectedWordIndex)).getArea().x + ((AnnotatedWord) sentence.getWord(draggedWordIndex)).getArea().x) / 2, ((AnnotatedWord) sentence.getWord(selectedWordIndex)).getArea().y + 20, 120, 440);
            }
        }
        ((AnnotatedWord)sentence.getWord(selectedWordIndex)).setSelected(false);
        dragged = false;
        if (!errorMode){
            selectionMode = true;
        }
        dragX = -1;
        dragY = -1;
        this.repaint();
    }

    public void mouseMoved(MouseEvent e) {
        if (!selectionMode){
            for (int i = 0; i < sentence.wordCount(); i++){
                AnnotatedWord word = (AnnotatedWord) sentence.getWord(i);
                if (word.getArea().contains(e.getX(), e.getY())){
                    word.setSelected(true);
                    if (i != selectedWordIndex){
                        if (selectedWordIndex != -1){
                            ((AnnotatedWord)sentence.getWord(i)).setSelected(false);
                        }
                    }
                    selectedWordIndex = i;
                    clickedWord = ((AnnotatedWord)sentence.getWord(selectedWordIndex));
                    repaint();
                    return;
                }
            }
            if (selectedWordIndex != -1){
                ((AnnotatedWord)sentence.getWord(selectedWordIndex)).setSelected(false);
                selectedWordIndex = -1;
                repaint();
            }
        }
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        if (selectedWordIndex != -1 && mouseEvent.isControlDown()) {
            clickedWord = ((AnnotatedWord) sentence.getWord(selectedWordIndex));
            lastClickedWord = clickedWord;
            editText.setText(clickedWord.getName());
            editText.setBounds(clickedWord.getArea().x - 5, clickedWord.getArea().y + 20, 100, 30);
            editText.setVisible(true);
            pane.setVisible(false);
            editText.requestFocus();
        } else {
            if (selectedWordIndex != -1 && mouseEvent.isShiftDown()){
                ((AnnotatedWord)sentence.getWord(selectedWordIndex)).setSelected(true);
                this.repaint();
            } else {
                selectionMode = false;
                list.setVisible(false);
                pane.setVisible(false);
            }
        }
    }

    public void mouseDragged(MouseEvent e) {
        dragged = true;
        for (int i = 0; i < sentence.wordCount(); i++){
            AnnotatedWord word = (AnnotatedWord) sentence.getWord(i);
            if (word.getArea().contains(e.getX(), e.getY())){
                if (i != selectedWordIndex){
                    draggedWordIndex = i;
                    repaint();
                    return;
                }
            }
        }
        if (selectedWordIndex != -1){
            draggedWordIndex = -1;
            dragX = e.getX();
            dragY = e.getY();
            this.repaint();
        }
    }

    protected void paintComponent(Graphics g){
        int startX, startY;
        Point2D.Double pointCtrl1, pointCtrl2, pointStart, pointEnd;
        CubicCurve2D.Double cubicCurve;
        super.paintComponent(g);
        if (dragged && selectedWordIndex != -1){
            AnnotatedWord selectedWord = ((AnnotatedWord)sentence.getWord(selectedWordIndex));
            startX = selectedWord.getArea().x + selectedWord.getArea().width / 2;
            startY = selectedWord.getArea().y + 20;
            pointStart = new Point2D.Double(startX, startY);
            pointEnd = new Point2D.Double(dragX, dragY);
            if (dragY > startY){
                pointCtrl1 = new Point2D.Double(startX, (startY + dragY) / 2 + 40);
                pointCtrl2 = new Point2D.Double((startX + dragX) / 2, dragY + 50);
            } else {
                pointCtrl1 = new Point2D.Double((startX + dragX) / 2, startY + 30);
                pointCtrl2 = new Point2D.Double(dragX, (startY + dragY) / 2 + 40);
            }
            cubicCurve = new CubicCurve2D.Double(pointStart.x, pointStart.y, pointCtrl1.x, pointCtrl1.y, pointCtrl2.x, pointCtrl2.y, pointEnd.x, pointEnd.y);
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(Color.MAGENTA);
            g2.draw(cubicCurve);
        }
    }

    private UniversalDependencyType[] possibleValues(String word){
        if (!mappedWords.containsKey(word)){
            return UniversalDependencyType.values();
        }
        ArrayList<AnnotatedWord> words = mappedWords.get(word);
        CounterHashMap<UniversalDependencyType> counts = new CounterHashMap<>();
        for (UniversalDependencyType universalDependencyType : UniversalDependencyType.values()){
            counts.put(universalDependencyType);
        }
        for (AnnotatedWord annotatedWord : words){
            counts.put(UniversalDependencyRelation.getDependencyTag(annotatedWord.getUniversalDependency().toString()));
        }
        List<Map.Entry<UniversalDependencyType, Integer>> sortedCounts = counts.topN(counts.size());
        UniversalDependencyType[] result = new UniversalDependencyType[sortedCounts.size()];
        int i = 0;
        for (Map.Entry<UniversalDependencyType, Integer> entry : sortedCounts){
            result[i] = entry.getKey();
            i++;
        }
        return result;
    }

    public int populateLeaf(AnnotatedSentence sentence, int wordIndex){
        int numberOfValidItemsUntilNow = -1;
        int selectedIndex = -1;
        listModel.clear();
        AnnotatedWord selectedWord = ((AnnotatedWord)sentence.getWord(selectedWordIndex));
        UniversalDependencyType[] typeList = possibleValues(selectedWord.getName());
        for (int i = 0; i < typeList.length; i++){
            if (draggedWordIndex > selectedWordIndex){
                if (typeList[i].equals(UniversalDependencyType.FIXED) || typeList[i].equals(UniversalDependencyType.FLAT) ||
                        typeList[i].equals(UniversalDependencyType.CONJ) || typeList[i].equals(UniversalDependencyType.APPOS) ||
                        typeList[i].equals(UniversalDependencyType.GOESWITH)){
                    continue;
                }
            }
            if (draggedWordIndex + 1 < selectedWordIndex && typeList[i].equals(UniversalDependencyType.GOESWITH)){
                continue;
            }
            if (selectedWord.getUniversalDependencyPos() != null){
                String uvPos = selectedWord.getUniversalDependencyPos();
                String dependency = typeList[i].toString();
                if (uvPos != null && !AnnotatedSentence.checkDependencyWithUniversalPosTag(dependency, uvPos)){
                    continue;
                }
            }
            numberOfValidItemsUntilNow++;
            if (selectedWord.getUniversalDependency() != null && selectedWord.getUniversalDependency().toString().equalsIgnoreCase(typeList[i].toString().replace('_', ':'))){
                selectedIndex = numberOfValidItemsUntilNow;
            }
            listModel.addElement(typeList[i].toString());
        }
        return selectedIndex;
    }

}

package Annotation;

import AnnotatedSentence.AnnotatedSentence;
import AnnotatedSentence.AnnotatedWord;
import AnnotatedSentence.Language;
import AnnotatedSentence.ViewLayerType;
import AutoProcessor.TurkishSentenceAutoDependency;
import DataCollector.Sentence.SentenceAnnotatorPanel;
import DataStructure.CounterHashMap;
import DependencyParser.Universal.UniversalDependencyRelation;
import DependencyParser.Universal.UniversalDependencyType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

public class SentenceDependencyPanel extends SentenceAnnotatorPanel {

    private boolean dragged = false;
    private int dragX = -1, dragY = -1;

    private final JScrollPane scrollPane;
    private final TurkishSentenceAutoDependency turkishSentenceAutoDependency;
    private final HashMap<String, ArrayList<AnnotatedWord>> mappedWords;
    private final HashMap<String, ArrayList<AnnotatedSentence>> mappedSentences;

    public SentenceDependencyPanel(String currentPath, String rawFileName, HashMap<String, ArrayList<AnnotatedWord>> mappedWords, HashMap<String, ArrayList<AnnotatedSentence>> mappedSentences, JScrollPane scrollPane) {
        super(currentPath, rawFileName, ViewLayerType.DEPENDENCY);
        this.scrollPane = scrollPane;
        this.mappedWords = mappedWords;
        this.mappedSentences = mappedSentences;
        turkishSentenceAutoDependency = new TurkishSentenceAutoDependency();
        list.setCellRenderer(new ListRenderer());
    }

    @Override
    protected void setWordLayer() {
        String relation = ((String) list.getSelectedValue()).toLowerCase().replace('_', ':');
        if (relation.equals("root")){
            clickedWord.setUniversalDependency(0, relation);
        } else {
            if (relation.equals("none")){
                clickedWord.setUniversalDependency(-1, relation);
            } else {
                clickedWord.setUniversalDependency(draggedWordIndex + 1, relation);
            }
        }
        draggedWordIndex = -1;
        selectionMode = false;
        scrollPane.setVisible(true);
    }

    @Override
    protected void setBounds() {
        pane.setBounds((((AnnotatedWord) sentence.getWord(selectedWordIndex)).getArea().getX() + ((AnnotatedWord) sentence.getWord(draggedWordIndex)).getArea().getX()) / 2, ((AnnotatedWord) sentence.getWord(selectedWordIndex)).getArea().getY() + 20, 120, (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.4));
    }

    @Override
    protected void setLineSpace() {
        lineSpace = 80;
    }

    @Override
    protected void drawLayer(AnnotatedWord word, Graphics g, int currentLeft, int lineIndex, int wordIndex, int maxSize, ArrayList<Integer> wordSize, ArrayList<Integer> wordTotal) {
        Point2D.Double pointCtrl1, pointCtrl2, pointStart, pointEnd;
        CubicCurve2D.Double cubicCurve;
        if (word.getUniversalDependencyPos() != null){
            g.drawString(word.getUniversalDependencyPos(), currentLeft, (lineIndex + 1) * lineSpace + 30);
            if (word.getLanguage() != null && word.getLanguage().equals(Language.ENGLISH) && word.getMetamorphicParse() != null){
                g.setColor(Color.BLUE);
                g.drawString(word.getMetamorphicParse().getWord().getName(), currentLeft, (lineIndex + 1) * lineSpace + 60);
            }
            if (word.getParse() != null){
                g.setColor(Color.BLUE);
                ArrayList<String> features = word.getParse().getUniversalDependencyFeatures(word.getUniversalDependencyPos());
                for (int i = 0; i < features.size(); i++){
                    g.drawString(features.get(i), currentLeft, (lineIndex + 1) * lineSpace + 30 * (i + 2));
                }
            }
        }
        if (word.getUniversalDependency() != null){
            String correct = word.getUniversalDependency().toString().toLowerCase();
            if (word.getUniversalDependency().to() != 0){
                Color color = Color.BLACK;
                switch (correct){
                    case "acl":
                        color = new Color(255, 214, 0);
                        break;
                    case "advcl":
                        color = new Color(0, 121, 107);
                        break;
                    case "aux":
                        color = new Color(0, 0, 128);
                        break;
                    case "advmod":
                        color = new Color(30, 136, 229);
                        break;
                    case "amod":
                        color = new Color(183, 28, 28);
                        break;
                    case "det":
                        color = new Color(255, 128, 171);
                        break;
                    case "flat":
                        color = new Color(106, 27, 154);
                        break;
                    case "obj":
                        color = new Color(67, 160, 71);
                        break;
                    case "conj":
                        color = new Color(175, 180, 43);
                        break;
                    case "mark":
                        color = new Color(255, 111, 0);
                        break;
                    case "nmod":
                        color = new Color(255, 138, 101);
                        break;
                    case "nsubj":
                        color = new Color(179, 157, 219);
                        break;
                    case "obl":
                        color = new Color(135, 206, 250);
                        break;
                    case "compound":
                        color = new Color(84, 110, 122);
                        break;
                    case "cc":
                        color = new Color(121, 85, 72);
                        break;
                    case "ccomp":
                        color = new Color(205, 92, 92);
                        break;
                    case "case":
                        color = new Color(188, 143, 143);
                        break;
                    case "nummod":
                        color = new Color(143, 188, 143);
                        break;
                    case "xcomp":
                        color = new Color(210, 105, 30);
                        break;
                    case "parataxis":
                        color = new Color(92, 107, 192);
                        break;
                }
                g.setColor(color);
                int startX = currentLeft + maxSize / 2;
                int startY = lineIndex * lineSpace + 50;
                double height = Math.pow(Math.abs(word.getUniversalDependency().to() - 1 - wordIndex), 0.7);
                int toX = wordTotal.get(word.getUniversalDependency().to() - 1) + wordSize.get(word.getUniversalDependency().to() - 1) / 2 /*+ added*/;
                pointEnd = new Point2D.Double(startX + 5 * Math.signum(word.getUniversalDependency().to() - 1 - wordIndex), startY);
                pointStart = new Point2D.Double(toX, startY);
                int controlY = (int) (pointStart.y - 20 - 20 * height);
                g.drawString(correct, ((int) (pointStart.x + pointEnd.x) / 2) - g.getFontMetrics().stringWidth(correct) / 2, (int) (controlY + 30 + 4 * height));
                pointCtrl1 = new Point2D.Double(pointStart.x, controlY);
                pointCtrl2 = new Point2D.Double(pointEnd.x, controlY);
                cubicCurve = new CubicCurve2D.Double(pointStart.x, pointStart.y, pointCtrl1.x, pointCtrl1.y, pointCtrl2.x, pointCtrl2.y, pointEnd.x, pointEnd.y);
                Graphics2D g2 = (Graphics2D)g;
                g2.setColor(color);
                g2.draw(cubicCurve);
                g.drawOval((int) pointStart.x - 2, (int) pointStart.y - 2, 4, 4);
                g.drawLine((int) pointEnd.x, (int) pointEnd.y, (int) pointEnd.x - 5, (int) pointEnd.y - 5);
                g.drawLine((int) pointEnd.x, (int) pointEnd.y, (int) pointEnd.x + 5, (int) pointEnd.y - 5);
            } else {
                g.drawString("root", currentLeft + maxSize / 2 - g.getFontMetrics().stringWidth("root") / 2, lineIndex * lineSpace);
                g.setColor(Color.BLACK);
                g.drawLine(currentLeft + maxSize / 2, lineIndex * lineSpace + 50, currentLeft + maxSize / 2, lineIndex * lineSpace + 15);
            }
        }
    }

    @Override
    protected int getMaxLayerLength(AnnotatedWord word, Graphics g) {
        return g.getFontMetrics().stringWidth(word.getName());
    }

    private class ListRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component cell = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            AnnotatedWord selectedWord = ((AnnotatedWord)sentence.getWord(selectedWordIndex));
            StringBuilder examples = new StringBuilder("<html>");
            int count = 0;
            if (mappedSentences.containsKey(selectedWord.getName())){
                for (AnnotatedSentence annotatedSentence : mappedSentences.get(selectedWord.getName())){
                    for (int i = 0; i < annotatedSentence.wordCount(); i++){
                        AnnotatedWord word = (AnnotatedWord) annotatedSentence.getWord(i);
                        if (word.getName().equals(selectedWord.getName()) && word.getUniversalDependency() != null){
                            if (word.getUniversalDependency().toString().equals(value)){
                                examples.append(annotatedSentence.toDependencyString(i)).append("<br>");
                                count++;
                            }
                        }
                    }
                    if (count >= 20){
                        break;
                    }
                }
            }
            examples.append("</html>");
            ((JComponent) cell).setToolTipText(examples.toString());
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
                JOptionPane.showMessageDialog(this, parentRelation + " not expected to have children!", "Dependency Rule", JOptionPane.ERROR_MESSAGE);
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
                setBounds();
                scrollPane.setVisible(false);
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
            editText.setBounds(clickedWord.getArea().getX() - 5, clickedWord.getArea().getY() + 20, 100, 30);
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
            startX = selectedWord.getArea().getX() + selectedWord.getArea().getWidth() / 2;
            startY = selectedWord.getArea().getY() + 20;
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
        sortedCounts.sort((o1, o2) -> {
            if (o1.getValue() == 1 && o2.getValue() == 1){
                return String.CASE_INSENSITIVE_ORDER.compare(o1.getKey().toString(), o2.getKey().toString());
            } else {
                return o2.getValue() - o1.getValue();
            }
        });
        UniversalDependencyType[] result = new UniversalDependencyType[sortedCounts.size()];
        int i = 0;
        for (Map.Entry<UniversalDependencyType, Integer> entry : sortedCounts){
            result[i] = entry.getKey();
            i++;
        }
        return result;
    }

    public int populateLeaf(AnnotatedSentence sentence, int wordIndex){
        int numberOfValidItemsUntilNow = 0;
        int selectedIndex = -1;
        listModel.clear();
        listModel.addElement("NONE");
        AnnotatedWord selectedWord = ((AnnotatedWord)sentence.getWord(selectedWordIndex));
        UniversalDependencyType[] typeList = possibleValues(selectedWord.getName());
        for (UniversalDependencyType universalDependencyType : typeList) {
            if (draggedWordIndex > selectedWordIndex) {
                if (universalDependencyType.equals(UniversalDependencyType.FIXED) || universalDependencyType.equals(UniversalDependencyType.FLAT) ||
                        universalDependencyType.equals(UniversalDependencyType.CONJ) || universalDependencyType.equals(UniversalDependencyType.APPOS) ||
                        universalDependencyType.equals(UniversalDependencyType.GOESWITH)) {
                    continue;
                }
            }
            if (draggedWordIndex + 1 < selectedWordIndex && universalDependencyType.equals(UniversalDependencyType.GOESWITH)) {
                continue;
            }
            if (selectedWord.getUniversalDependencyPos() != null) {
                String uvPos = selectedWord.getUniversalDependencyPos();
                String dependency = universalDependencyType.toString();
                if (uvPos != null && !AnnotatedSentence.checkDependencyWithUniversalPosTag(dependency, uvPos)) {
                    continue;
                }
            }
            numberOfValidItemsUntilNow++;
            if (selectedWord.getUniversalDependency() != null && selectedWord.getUniversalDependency().toString().equalsIgnoreCase(universalDependencyType.toString().replace('_', ':'))) {
                selectedIndex = numberOfValidItemsUntilNow;
            }
            listModel.addElement(universalDependencyType.toString());
        }
        return selectedIndex;
    }

}

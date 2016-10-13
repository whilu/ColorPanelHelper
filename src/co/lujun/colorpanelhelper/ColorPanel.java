package co.lujun.colorpanelhelper;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import org.apache.http.util.TextUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016/10/11 下午9:48
 */
public class ColorPanel extends JFrame  {

    private JSlider sliderAlpha;
    private JPanel rootPanelContainer;
    private JButton btnOk;
    private JTextField textFieldHexColor;
    private JTextField textFieldA;
    private JTextField textFieldR;
    private JTextField textFieldB;
    private JTextField textFieldG;
    private JLabel labelAlpha;
    private JTextPane colorPreviewTextPane;

    private SimpleAttributeSet simpleAttributeSet;

    private boolean isHexColorStartWithSharp = false;

    public ColorPanel(Editor editor, String hexColor){
        super("ColorPanelHelper");
        setContentPane(rootPanelContainer);
        setPreferredSize(new Dimension(410, 160));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        initView(editor, hexColor);
        setVisible(true);
    }

    private void initView(Editor editor, String hexColor){
        sliderAlpha.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int alphaValue = sliderAlpha.getValue();
                String hexColor = textFieldHexColor.getText();
                if (hexColor.length() > 6){
                    hexColor = convertAlphaValue2HexStr(alphaValue) + hexColor.substring(hexColor.length() - 6);
                }else {
                    hexColor = convertAlphaValue2HexStr(alphaValue) + hexColor;
                }
                invalidateUI(alphaValue, hexColor, true);
            }
        });
        textFieldHexColor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changeHexColorField();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changeHexColorField();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changeHexColorField();
            }
        });
        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                injectHexColor(editor, textFieldHexColor.getText());
                dispose();
            }
        });

        simpleAttributeSet = new SimpleAttributeSet();

        if (TextUtils.isEmpty(hexColor)){
            return;
        }
        isHexColorStartWithSharp = hexColor.startsWith("#");
        hexColor = hexColor.replace("#", "");
        int length = hexColor.length();
        if (!isHexNumber(hexColor) || length < 6 || length > 8){
            return;
        }

        String alphaHex;
        if (length == 6){
            alphaHex = "FF";
        }else if (length == 7 && hexColor.charAt(0) == '0'){
            hexColor = "0" + hexColor;
            alphaHex = "00";
        }else {
            alphaHex = hexColor.substring(0, length - 6);
        }
        invalidateUI(convertHexStr2AlphaValue(alphaHex), hexColor, true);
    }

    private String convertAlphaValue2HexStr(int value){
        String result = Integer.toHexString((int) ((1 - (float) value / 100) * 255));
        return result.equals("0") ? result + "0" : result;
    }

    private int convertHexStr2AlphaValue(String hexStr){
        if (TextUtils.isEmpty(hexStr) || !isHexNumber(hexStr)){
            return 0;
        }
        int value = Integer.parseInt(hexStr, 16);
        return (int) ((1 - (float) value / 255) * 100);
    }

    private static boolean isHexNumber(String str){
        boolean result = true;
        for(int i=0; i < str.length(); i++){
            char c = str.charAt(i);
            if (c < '0' || c > 'f' || (c > '9' && c < 'A') || (c > 'F' && c < 'a')){
                result = false;
            }
        }
        return result;
    }

    private void changeHexColorField(){
        if (!textFieldHexColor.hasFocus()){
            return;
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String hexColor = textFieldHexColor.getText();
                int length = hexColor.length();
                if (!isHexNumber(hexColor) || length < 6 || length > 8){
                    return;
                }

                String alphaHexStr = hexColor.substring(0, length - 6);
                invalidateUI(convertHexStr2AlphaValue(alphaHexStr), hexColor, false);
            }
        };
        SwingUtilities.invokeLater(runnable);
    }

    private void invalidateUI(int alphaValue, String hexColor, boolean updateHexColorArea){
        if(TextUtils.isEmpty(hexColor) || hexColor.length() < 6 || hexColor.length() > 8 || !isHexNumber(hexColor)){
            return;
        }
        sliderAlpha.setValue(alphaValue);
        labelAlpha.setText(alphaValue + "%");
        if (updateHexColorArea) {
            textFieldHexColor.setText(hexColor);
        }
        int a = 255;
        String aStr = hexColor.substring(0, hexColor.length() - 6);
        if (!TextUtils.isEmpty(aStr)) {
            a = Integer.parseInt(aStr, 16);
        }
        int r = Integer.parseInt(hexColor.substring(hexColor.length() - 6, hexColor.length() - 4), 16);
        int g = Integer.parseInt(hexColor.substring(hexColor.length() - 4, hexColor.length() - 2), 16);
        int b = Integer.parseInt(hexColor.substring(hexColor.length() - 2, hexColor.length()), 16);
        textFieldA.setText(String.valueOf(a));
        textFieldR.setText(String.valueOf(r));
        textFieldG.setText(String.valueOf(g));
        textFieldB.setText(String.valueOf(b));

        StyleConstants.setForeground(simpleAttributeSet, new Color(r, g, b, a));
        StyleConstants.setBackground(simpleAttributeSet, Color.WHITE);
        StyleConstants.setUnderline(simpleAttributeSet, Boolean.TRUE);
        StyleConstants.setBold(simpleAttributeSet, true);

        colorPreviewTextPane.getStyledDocument().setCharacterAttributes(0, colorPreviewTextPane.getText().length(),
                simpleAttributeSet, false);
    }

    private void injectHexColor(Editor editor, String color){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                editor.getDocument().deleteString(editor.getSelectionModel().getSelectionStart(),
                        editor.getSelectionModel().getSelectionEnd());
                editor.getDocument().insertString(editor.getSelectionModel().getSelectionStart(),
                        isHexColorStartWithSharp ? "#" + color : color);
            }
        };
        WriteCommandAction.runWriteCommandAction(editor.getProject(), runnable);
    }
}

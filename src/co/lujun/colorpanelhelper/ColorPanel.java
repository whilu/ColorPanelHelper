package co.lujun.colorpanelhelper;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import org.apache.http.util.TextUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
                invalidateUI(alphaValue, hexColor);
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

        isHexColorStartWithSharp = hexColor.startsWith("#");
        hexColor = hexColor.replace("#", "");
        int length = hexColor.length();
        if (!isHexNumber(hexColor) || length < 6){
            return;
        }

        if (length >= 8){
            hexColor = hexColor.substring(length - 8);
        }else if (hexColor.charAt(0) == '0'){
            hexColor = "0" + hexColor;
        }else {
            hexColor = "F" + hexColor;
        }
        invalidateUI(convertHexStr2AlphaValue(hexColor.substring(0, 2)), hexColor);
    }

    private String convertAlphaValue2HexStr(int value){
        String result = Integer.toHexString((int) ((1 - (float) value / 100) * 255));
        return result.equals("0") ? result + "0" : result;
    }

    private int convertHexStr2AlphaValue(String hexStr){
        int value = Integer.parseInt(hexStr, 16);
        return (1 - value / 255) * 100;
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

    private void invalidateUI(int alphaValue, String hexColor){
        if(TextUtils.isEmpty(hexColor) || hexColor.length() < 8 || !isHexNumber(hexColor)){
            return;
        }
        sliderAlpha.setValue(alphaValue);
        labelAlpha.setText(alphaValue + "%");
        textFieldHexColor.setText(hexColor);
        textFieldA.setText(hexColor.substring(0, 2));
        textFieldR.setText(hexColor.substring(2, 4));
        textFieldG.setText(hexColor.substring(4, 6));
        textFieldB.setText(hexColor.substring(6, 8));
    }

    private void changeHexColorField(){
        String hexColor = textFieldHexColor.getText();
        int length = hexColor.length();
        if (!isHexNumber(hexColor) || length < 6){
            return;
        }

        String alphaHexStr;
        if (length < 8){
            alphaHexStr = hexColor.substring(0, length - 6);
        }else {
            alphaHexStr = hexColor.substring(length - 8, length - 6);
        }
        invalidateUI(convertHexStr2AlphaValue(alphaHexStr), hexColor.substring(length - 6));
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

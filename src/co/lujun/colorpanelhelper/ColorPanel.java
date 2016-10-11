package co.lujun.colorpanelhelper;

import com.intellij.openapi.editor.Editor;

import javax.swing.*;
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

    public ColorPanel(Editor editor, String hexColor){
        super("ColorPanelHelper");
        setContentPane(rootPanelContainer);
        setPreferredSize(new Dimension(530, 260));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        initView(editor, hexColor);
        setVisible(true);
    }

    private void initView(Editor editor, String hexColor){
        hexColor = hexColor.replace("#", "");
        int length = hexColor.length();
        if (!isHexNumber(hexColor) || length < 6){
            return;
        }
        String rgbColor = hexColor.substring(length - 6);
        String hexAlpha = "FF";
        if (length == 8){
            hexAlpha = hexColor.substring(0, 2);
        }
        invalidateUI((int) convertHexStr2AlphaPercent(hexAlpha), hexAlpha, rgbColor);
        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                injectHexColor(editor, textFieldHexColor.getText());
                dispose();
            }
        });
    }

    private String convertAlphaPercent2HexStr(float TPercent){
        return Float.toHexString((1 - TPercent) * 255);
    }

    private float convertHexStr2AlphaPercent(String hexStr){
        int value = Integer.parseInt(hexStr, 16);
        return 1 - value / 255;
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

    private void invalidateUI(int alphaPercent, String hexAlpha, String rgbColor){
        sliderAlpha.setValue(alphaPercent * 100);
        labelAlpha.setText(alphaPercent + "%");
        textFieldHexColor.setText(hexAlpha + rgbColor);
        textFieldA.setText(hexAlpha);
        textFieldR.setText(rgbColor.substring(0, 2));
        textFieldG.setText(rgbColor.substring(2, 4));
        textFieldB.setText(rgbColor.substring(4, 6));
    }

    private void injectHexColor(Editor editor, String color){

    }
}

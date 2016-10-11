package co.lujun.colorpanelhelper;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016/10/3 下午12:12
 */
public class ColorPanelAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        getHexColor(e);
    }

    private void getHexColor(AnActionEvent e){
        Editor editor = e.getData(PlatformDataKeys.EDITOR);

        String colorStr = null;
        if (editor != null){
            // Support selected text
            colorStr = editor.getSelectionModel().getSelectedText();

            // TODO support hover over the color text
        }
        showPanel(editor, colorStr);
    }

    private void showPanel(Editor editor, String hexColor){
        new ColorPanel(editor, hexColor);
    }
}

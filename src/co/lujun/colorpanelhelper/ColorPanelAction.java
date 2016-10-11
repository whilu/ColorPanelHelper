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
        showPanel(e);
    }

    private void showPanel(AnActionEvent e){
        Editor editor = e.getData(PlatformDataKeys.EDITOR);

        String colorHex;
        if (editor != null){
            // Support selected text
            colorHex = editor.getSelectionModel().getSelectedText();

            // TODO support hover over the color text
        }

    }
}

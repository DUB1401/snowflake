package snowflake.components.files.browser;

import snowflake.App;
import snowflake.common.FileSystem;
import snowflake.components.files.DndTransferData;
import snowflake.components.files.FileComponentHolder;
import snowflake.components.files.browser.folderview.FolderView;
import snowflake.components.files.browser.folderview.FolderViewEventListener;
import snowflake.components.newsession.SessionInfo;
import snowflake.utils.PathUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Map;

public abstract class AbstractFileBrowserView extends JPanel implements FolderViewEventListener {
    protected AddressBar addressBar;
    protected FolderView folderView;
    protected JRootPane rootPane;
    private AbstractAction upAction, reloadAction;
    protected String path;
    protected FileComponentHolder holder;

    protected PanelOrientation orientation;

    public enum PanelOrientation {
        Left, Right
    }

    public AbstractFileBrowserView(JRootPane rootPane, FileComponentHolder holder, PanelOrientation orientation) {
        super(new BorderLayout());
        this.orientation = orientation;
        this.rootPane = rootPane;
        this.holder = holder;
        JPanel toolBar = new JPanel(new BorderLayout());
        createAddressBar();
        addressBar.addActionListener(e -> {
            String text = addressBar.getText();
            System.out.println("Address changed: " + text + " old: " + this.path);
            if (PathUtils.isSamePath(this.path, text)) {
                System.out.println("Same text");
                return;
            }
            if (text != null && text.length() > 0) {
                addBack(this.path);
                render(text);
            }
        });
        Box smallToolbar = Box.createHorizontalBox();

        upAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                up();
            }
        };
        reloadAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reload();
            }
        };

        JButton btnBack = new JButton();
        btnBack.setFont(App.getFontAwesomeFont());
        btnBack.setText("\uf104");
        smallToolbar.add(btnBack);

        JButton btnNext = new JButton();
        btnNext.setFont(App.getFontAwesomeFont());
        btnNext.setText("\uf105");
        smallToolbar.add(btnNext);

        JButton btnHome = new JButton();
        btnHome.setFont(App.getFontAwesomeFont());
        btnHome.setText("\uf015");
        btnHome.addActionListener(e -> home());
        smallToolbar.add(btnHome);

        JButton btnUp = new JButton();
        btnUp.addActionListener(upAction);
        btnUp.setFont(App.getFontAwesomeFont());
        btnUp.setText("\uf062");
        smallToolbar.add(btnUp);

        JButton btnReload = new JButton();
        btnReload.addActionListener(reloadAction);
        btnReload.setFont(App.getFontAwesomeFont());
        btnReload.setText("\uf021");

        Box b2 = Box.createHorizontalBox();
        b2.add(btnReload);
        b2.setBorder(new EmptyBorder(3, 0, 3, 0));
        b2.add(btnReload);

        toolBar.add(smallToolbar, BorderLayout.WEST);
        toolBar.add(addressBar);
        toolBar.add(b2, BorderLayout.EAST);
        add(toolBar, BorderLayout.NORTH);
        folderView = new FolderView(this);
        add(folderView);

        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "up");
        this.getActionMap().put("up", upAction);
    }


    protected abstract void createAddressBar();

    public abstract String toString();

    public void close() {
//        if (fs != null) {
//            synchronized (fileViewMap) {
//                int c = fileViewMap.get(fs);
//                if (c > 1) {
//                    fileViewMap.put(fs, c - 1);
//                } else if (c == 1) {
//                    try {
//                        fs.close();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
    }

    public String getCurrentDirectory() {
        return this.path;
    }

    public abstract boolean handleDrop(DndTransferData transferData);

    protected abstract void up();

    protected abstract void home();

    public void reload() {
        this.render(this.path);
    }

    public PanelOrientation getOrientation() {
        return orientation;
    }
}
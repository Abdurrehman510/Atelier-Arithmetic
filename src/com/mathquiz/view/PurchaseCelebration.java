package com.mathquiz.view;

import com.mathquiz.config.AppConfig;
import com.mathquiz.config.AppTheme;
import com.mathquiz.view.tour.TourStep.MascotExpression;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;

/**
 * The "ta-da" after a Mascot Shop purchase (FEAT-13): a big, excited Archie wearing exactly what was just
 * bought — as clearly visible as in the guided tour — with the item's name and where the child will see
 * it. Replaces the plain text pop-up that only mentioned a small emoji. A modal dialog that Enter, Esc or
 * the button closes; colours follow the current light/dark theme and the text size follows the setting.
 */
final class PurchaseCelebration {

    /** Archie's size in pixels (the tour draws him at 80). He is artwork, not text, so the text-size setting
     *  does not enlarge him — otherwise the dialog would outgrow the smallest window at 150 %. */
    private static final int ARCHIE_HEIGHT = 150;
    private static final int ARCHIE_WIDTH = 127;   // his proportions (144 : 170 in ArchieView)

    private PurchaseCelebration() {
    }

    /** Builds the dialog without showing it (also used by the tests). */
    static JDialog create(Component parent, MascotShopPanel.ShopItem item) {
        Window owner = parent == null ? null : javax.swing.SwingUtilities.getWindowAncestor(parent);
        if (owner == null && parent instanceof Window) {
            owner = (Window) parent;
        }
        JDialog dialog = new JDialog(owner, "Item Unlocked!", java.awt.Dialog.ModalityType.APPLICATION_MODAL);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(AppTheme.getBgCard());
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getAccentGold(), 2),
                new EmptyBorder(18, 36, 18, 36)));

        ArchieView archie = ArchieView.wearing(item.id);
        archie.setPreferredSize(new Dimension(ARCHIE_WIDTH, ARCHIE_HEIGHT));
        archie.setExpression(MascotExpression.EXCITED);
        center(archie);
        content.add(archie);
        content.add(spacer(10));

        JLabel name = new JLabel(item.name, SwingConstants.CENTER);
        name.setFont(new Font("Serif", Font.BOLD, 24));
        name.setForeground(AppTheme.getTextDark());
        center(name);
        content.add(name);
        content.add(spacer(6));

        content.add(text("<b>Archie says:</b> \"Ta-da! How do I look?\"", 13, AppTheme.getTextDark()));
        content.add(spacer(8));
        content.add(text("You will see him wearing it on Home, in your quizzes and on your results.",
                11, AppTheme.getTextMuted()));
        content.add(spacer(18));

        JButton ok = new JButton("AWESOME!");
        ok.setFont(new Font("SansSerif", Font.BOLD, 13));
        ok.setFocusPainted(false);
        ok.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ok.setBorder(new EmptyBorder(11, 40, 11, 40));
        PrimaryButtonStyle.apply(ok);
        ok.addActionListener(e -> dialog.dispose());
        center(ok);
        content.add(ok);

        dialog.setContentPane(content);
        dialog.getRootPane().setDefaultButton(ok);                    // Enter closes it
        dialog.getRootPane().registerKeyboardAction(e -> dialog.dispose(),
                javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);                   // so does Esc
        dialog.setResizable(false);

        AppTheme.scaleComponentFont(content, AppConfig.getInstance().getFontSizeScale(), null);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        return dialog;
    }

    /** Shows the celebration and returns when the child closes it. */
    static void show(Component parent, MascotShopPanel.ShopItem item) {
        JDialog dialog = create(parent, item);
        dialog.setVisible(true);
    }

    private static JLabel text(String html, int size, java.awt.Color color) {
        JLabel label = new JLabel("<html><body style='width: 285px; text-align: center'>" + html + "</body></html>",
                SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, size));
        label.setForeground(color);
        center(label);
        return label;
    }

    private static Component spacer(int height) {
        return javax.swing.Box.createRigidArea(new Dimension(0, height));
    }

    private static void center(JComponent c) {
        c.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
}

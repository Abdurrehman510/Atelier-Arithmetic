package com.mathquiz.view;

import com.mathquiz.config.AppConfig;
import com.mathquiz.config.AppTheme;
import com.mathquiz.service.RewardService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Mascot Shop — Atelier Arithmetic Engagement System.
 *
 * A premium shop screen where children spend earned stars (⭐) to unlock
 * cosmetic accessories for Archie the Owl: hats, glasses, theme colors,
 * and honor badges. All items are purely cosmetic and do not affect gameplay.
 *
 * Items are persisted per-profile via AppConfig (encrypted). The equipped
 * accessory is displayed on the WelcomePanel mascot greeting area.
 */
public class MascotShopPanel extends JPanel {

    // ── Shop Item Definition ──────────────────────────────────────────────────

    public static class ShopItem {
        public final String id;
        public final String name;
        public final String emoji;
        public final String category;
        public final String description;
        public final int price;

        public ShopItem(String id, String name, String emoji, String category,
                        String description, int price) {
            this.id = id;
            this.name = name;
            this.emoji = emoji;
            this.category = category;
            this.description = description;
            this.price = price;
        }
    }

    // ── All shop items catalog ────────────────────────────────────────────────

    public static final List<ShopItem> ALL_ITEMS = new ArrayList<>();
    static {
        ALL_ITEMS.add(new ShopItem("hat_top",     "Top Hat",       "🎩", "Hats",    "A dapper classic topper!",          30));
        ALL_ITEMS.add(new ShopItem("hat_crown",   "Royal Crown",   "👑", "Hats",    "Fit for a math champion!",           50));
        ALL_ITEMS.add(new ShopItem("hat_wizard",  "Wizard Hat",    "🧙", "Hats",    "Channel your inner wizard!",         40));
        ALL_ITEMS.add(new ShopItem("hat_party",   "Party Hat",     "🎉", "Hats",    "Celebrate every quiz win!",          20));
        ALL_ITEMS.add(new ShopItem("glasses_nerd","Nerd Glasses",  "🤓", "Glasses", "Show off your smarts!",              20));
        ALL_ITEMS.add(new ShopItem("glasses_sun", "Cool Shades",   "😎", "Glasses", "Too cool for school!",               25));
        ALL_ITEMS.add(new ShopItem("glasses_star","Star Frames",   "⭐", "Glasses", "See the world in stars!",            35));
        ALL_ITEMS.add(new ShopItem("color_gold",  "Golden Archie", "✨", "Colors",  "Turn Archie into solid gold!",       60));
        ALL_ITEMS.add(new ShopItem("color_rainbow","Rainbow Glow", "🌈", "Colors",  "Shine in every color!",              80));
        ALL_ITEMS.add(new ShopItem("badge_scholar","Scholar Badge","🎓", "Badges",  "Earn the scholar's emblem!",         35));
        ALL_ITEMS.add(new ShopItem("badge_star",  "Star Champion", "🌟", "Badges",  "The top achiever's mark!",           45));
        ALL_ITEMS.add(new ShopItem("badge_heart", "Kind Heart",    "💖", "Badges",  "Show your love of learning!",        30));
    }

    // ── State ─────────────────────────────────────────────────────────────────

    private final QuizNavigator nav;
    private final RewardService rewardService;
    private final AppConfig config;

    // Header elements
    private JLabel titleLabel;
    private JLabel subTitleLabel;
    private JLabel balanceLabel;
    private JButton backButton;

    // Grid
    private JPanel gridPanel;
    private JScrollPane scrollPane;

    // ── Constructor ───────────────────────────────────────────────────────────

    public MascotShopPanel(QuizNavigator nav, RewardService rewardService) {
        this.nav = nav;
        this.rewardService = rewardService;
        this.config = AppConfig.getInstance();
        setLayout(new BorderLayout());
        build();
        applyTheme();
    }

    // ── Build ─────────────────────────────────────────────────────────────────

    private void build() {
        add(buildHeader(), BorderLayout.NORTH);

        gridPanel = new JPanel(new GridLayout(0, 3, 14, 14));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(new EmptyBorder(0, 30, 20, 30));

        scrollPane = new JScrollPane(gridPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        add(scrollPane, BorderLayout.CENTER);
        populateGrid();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(30, 40, 16, 40));

        // Left: title + subtitle
        JPanel leftCol = new JPanel();
        leftCol.setOpaque(false);
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));

        subTitleLabel = new JLabel("ARCHIE'S SHOP");
        subTitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        leftCol.add(subTitleLabel);

        titleLabel = new JLabel("Mascot Accessories 🦉");
        titleLabel.setFont(new Font("Serif", Font.PLAIN, 26));
        leftCol.add(titleLabel);

        JLabel hintLabel = new JLabel("Spend your ⭐ Stars to unlock cool looks for Archie!");
        hintLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        leftCol.add(Box.createVerticalStrut(4));
        leftCol.add(hintLabel);

        header.add(leftCol, BorderLayout.WEST);

        // Right: balance + back
        JPanel rightCol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightCol.setOpaque(false);

        balanceLabel = new JLabel("⭐ " + rewardService.getBalance() + " Stars");
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        rightCol.add(balanceLabel);

        backButton = new JButton("← Back");
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> nav.goToWelcome());
        rightCol.add(backButton);

        header.add(rightCol, BorderLayout.EAST);
        return header;
    }

    private void populateGrid() {
        gridPanel.removeAll();
        String equipped = rewardService.getEquippedItem();
        for (ShopItem item : ALL_ITEMS) {
            boolean owned = rewardService.isItemUnlocked(item.id);
            boolean equippedNow = item.id.equals(equipped);
            gridPanel.add(buildItemCard(item, owned, equippedNow));
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private JPanel buildItemCard(ShopItem item, boolean owned, boolean equippedNow) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        equippedNow ? AppTheme.getAccentGold() : AppTheme.getBorderClr(),
                        equippedNow ? 2 : 1),
                new EmptyBorder(16, 14, 16, 14)));
        card.setBackground(AppTheme.getBgCard());

        // Top: emoji icon
        JLabel emojiLabel = new JLabel(item.emoji, SwingConstants.CENTER);
        emojiLabel.setFont(new Font("Serif", Font.PLAIN, 34));
        card.add(emojiLabel, BorderLayout.NORTH);

        // Center: name + category + description
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel catLabel = new JLabel(item.category.toUpperCase());
        catLabel.setFont(new Font("SansSerif", Font.PLAIN, 9));
        catLabel.setForeground(AppTheme.getTextMuted());
        catLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPanel.add(catLabel);

        JLabel nameLabel = new JLabel(item.name);
        nameLabel.setFont(new Font("Serif", Font.BOLD, 14));
        nameLabel.setForeground(AppTheme.getTextDark());
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPanel.add(nameLabel);

        JLabel descLabel = new JLabel("<html><center>" + item.description + "</center></html>");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        descLabel.setForeground(AppTheme.getTextMuted());
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(descLabel);

        card.add(textPanel, BorderLayout.CENTER);

        // Bottom: price + action button
        JPanel bottomPanel = new JPanel(new BorderLayout(6, 0));
        bottomPanel.setOpaque(false);

        JLabel priceLabel = new JLabel("⭐ " + item.price);
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        priceLabel.setForeground(owned ? AppTheme.getTextMuted() : AppTheme.getAccentGold());
        bottomPanel.add(priceLabel, BorderLayout.WEST);

        JButton actionBtn;
        if (equippedNow) {
            actionBtn = makeActionBtn("✅ Equipped", new Color(34, 139, 34));
            actionBtn.addActionListener(e -> {
                rewardService.unequipItem();
                refresh();
            });
        } else if (owned) {
            actionBtn = makeActionBtn("Equip", AppTheme.getAccentGold());
            actionBtn.addActionListener(e -> {
                rewardService.equipItem(item.id);
                refresh();
            });
        } else {
            actionBtn = makeActionBtn("Buy " + item.price + "⭐", AppTheme.getAccentGold());
            actionBtn.addActionListener(e -> handlePurchase(item));
        }

        bottomPanel.add(actionBtn, BorderLayout.EAST);
        card.add(bottomPanel, BorderLayout.SOUTH);

        return card;
    }

    private JButton makeActionBtn(String text, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setForeground(fg);
        btn.setBackground(AppTheme.getBgPrimary());
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                new EmptyBorder(4, 10, 4, 10)));
        return btn;
    }

    private void handlePurchase(ShopItem item) {
        int balance = rewardService.getBalance();
        if (balance < item.price) {
            JOptionPane.showMessageDialog(this,
                    "You need " + item.price + " ⭐ stars to unlock this!\n" +
                    "You currently have " + balance + " ⭐ stars.\n\n" +
                    "Complete more quizzes to earn stars!",
                    "Not Enough Stars ⭐",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Unlock \"" + item.name + "\" for " + item.price + " ⭐ stars?",
                "Confirm Purchase",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean success = rewardService.spendStars(item.price);
        if (success) {
            rewardService.unlockItem(item.id);
            rewardService.equipItem(item.id);
            JOptionPane.showMessageDialog(this,
                    "🎉 " + item.emoji + " \"" + item.name + "\" unlocked and equipped!\n\n" +
                    "Archie is wearing it right now on the Home screen!",
                    "Item Unlocked!",
                    JOptionPane.INFORMATION_MESSAGE);
            refresh();
        }
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /** Refreshes the grid and balance label (call after earning/spending stars). */
    public void refresh() {
        balanceLabel.setText("⭐ " + rewardService.getBalance() + " Stars");
        populateGrid();
        applyTheme();
    }

    public void applyTheme() {
        setBackground(AppTheme.getBgPrimary());
        if (titleLabel != null) titleLabel.setForeground(AppTheme.getTextDark());
        if (subTitleLabel != null) subTitleLabel.setForeground(AppTheme.getTextMuted());
        if (balanceLabel != null) balanceLabel.setForeground(AppTheme.getAccentGold());
        if (backButton != null) {
            backButton.setBackground(AppTheme.getBgCard());
            backButton.setForeground(AppTheme.getTextMuted());
            backButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                    new EmptyBorder(6, 14, 6, 14)));
        }
        if (gridPanel != null) gridPanel.setBackground(AppTheme.getBgPrimary());
        if (scrollPane != null) scrollPane.getViewport().setBackground(AppTheme.getBgPrimary());
        revalidate();
        repaint();
    }
}

package com.mathquiz.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import com.mathquiz.config.AppTheme;
import com.mathquiz.config.AppConfig;
import com.mathquiz.service.AnalyticsService;
import com.mathquiz.service.SessionRepository;
import com.mathquiz.model.Question;
import com.mathquiz.service.SoundService;

/**
 * Redesigned Welcome / Configuration Dashboard panel.
 * Uses a modern two-column SaaS-style educational dashboard layout.
 */
public class WelcomePanel extends JPanel {

    // ── Design tokens ────────────────────────────────────────────────────────
    private static final Color BG_PRIMARY  = new Color(250, 249, 246);
    private static final Color BG_CARD     = Color.WHITE;
    private static final Color ACCENT_GOLD = new Color(184, 150, 110);
    private static final Color TEXT_DARK   = new Color(28, 25, 23);
    private static final Color TEXT_MUTED  = new Color(120, 113, 108);
    private static final Color BORDER_CLR  = new Color(230, 227, 220);

    // ── Tour-targetable components ────────────────────────────────────────────
    private JTextField       qCountField;
    private JComboBox<String> diffCombo;
    private JButton          startButton;

    // Preference buttons
    private JButton soundToggleBtn;
    private JButton themeToggleBtn;
    private JButton achievementsBtn;
    private JButton analyticsBtn;
    private JButton smartPracticeBtn;

    private JLabel subBrand;
    private JLabel titleLabel;
    private JLabel taglineLabel;
    private JLabel countLabel;
    private JLabel diffLabel;
    private JPanel configCardPanel;
    private JPanel topBarPanel;
    private JPanel calendarStripHolder;

    private JButton profileButton;
    private JButton scaleToggleBtn;

    private JButton customBuilderBtn;
    private JButton customLoadBtn;
    private JLabel customLabel;

    private final AppConfig config = AppConfig.getInstance();
    private final QuizNavigator nav;

    public WelcomePanel(QuizNavigator nav) {
        this.nav = nav;
        setBackground(BG_PRIMARY);
        setLayout(new BorderLayout());
        build();
    }

    // ── Public accessors for TourManager ─────────────────────────────────────
    public JTextField        getQuestionCountField() { return qCountField; }
    public JComboBox<String> getDifficultyCombo()    { return diffCombo;   }
    public JButton           getStartButton()        { return startButton; }

    public JButton getProfileButton() { return profileButton; }
    public JButton getScaleToggleBtn() { return scaleToggleBtn; }
    public JButton getThemeToggleBtn() { return themeToggleBtn; }
    public JButton getSoundToggleBtn() { return soundToggleBtn; }
    public JPanel getCalendarStripHolder() { return calendarStripHolder; }
    public JButton getCustomBuilderBtn() { return customBuilderBtn; }
    public JButton getCustomLoadBtn() { return customLoadBtn; }
    public JButton getAnalyticsBtn() { return analyticsBtn; }
    public JButton getSmartPracticeBtn() { return smartPracticeBtn; }
    public JButton getAchievementsBtn() { return achievementsBtn; }

    public int getQuestionCount() {
        try {
            int v = Integer.parseInt(qCountField.getText().trim());
            return (v > 0) ? v : 10;
        } catch (NumberFormatException e) {
            return 10;
        }
    }

    public String getDifficulty() {
        return (String) diffCombo.getSelectedItem();
    }

    // ── UI construction ───────────────────────────────────────────────────────
    private void build() {
        // Top preference bar
        topBarPanel = buildTopBar();
        add(topBarPanel, BorderLayout.NORTH);

        // Dashboard main grid (2-Column)
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(10, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Left Column (width ~58%)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.58;
        gbc.insets = new Insets(0, 0, 0, 20);
        mainContent.add(buildLeftColumn(), gbc);

        // Right Column (width ~42%)
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.42;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainContent.add(buildRightColumn(), gbc);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel buildLeftColumn() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        // Hero mascot header
        c.gridx = 0; c.gridy = 0;
        c.insets = new Insets(0, 0, 16, 0);
        panel.add(buildHeroHeader(), c);

        // Config card
        c.gridx = 0; c.gridy = 1;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 0, 0, 0);
        configCardPanel = buildConfigCard();
        panel.add(configCardPanel, c);

        return panel;
    }

    private JPanel buildHeroHeader() {
        JPanel panel = new JPanel(new BorderLayout(16, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(8, 10, 8, 10));

        // Mascot logo
        try {
            java.net.URL logoUrl = WelcomePanel.class.getResource("/com/mathquiz/resources/logo.png");
            if (logoUrl != null) {
                ImageIcon logoIcon = new ImageIcon(logoUrl);
                Image scaledImg = logoIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                JLabel logoLabel = new JLabel(new ImageIcon(scaledImg));
                panel.add(logoLabel, BorderLayout.WEST);
            }
        } catch (Exception e) {}

        // Titles
        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridx = 0;

        c.gridy = 0;
        subBrand = new JLabel("ATELIER ARITHMETIC");
        subBrand.setFont(new Font("Serif", Font.BOLD, 10));
        subBrand.setForeground(ACCENT_GOLD);
        textPanel.add(subBrand, c);

        c.gridy = 1;
        titleLabel = new JLabel("Royal Mathematics Quiz");
        titleLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        titleLabel.setForeground(TEXT_DARK);
        textPanel.add(titleLabel, c);

        c.gridy = 2;
        c.insets = new Insets(4, 0, 0, 0);
        taglineLabel = new JLabel("<html><body>🦉 <b>Archie says:</b> \"Ready for a math adventure? Configure below and select a category!\"</body></html>");
        taglineLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        taglineLabel.setForeground(TEXT_MUTED);
        textPanel.add(taglineLabel, c);

        panel.add(textPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildConfigCard() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BG_CARD);
        card.setOpaque(true);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                new EmptyBorder(22, 30, 22, 30)));

        GridBagConstraints c = new GridBagConstraints();
        c.fill   = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 10, 10, 10);

        // Row 0: Question count
        c.gridx = 0; c.gridy = 0;
        c.weightx = 0.4;
        countLabel = makeLabel("NUMBER OF QUESTIONS:");
        card.add(countLabel, c);

        c.gridx = 1;
        c.weightx = 0.6;
        qCountField = new JTextField("10", 8);
        styleTextField(qCountField);
        card.add(qCountField, c);

        // Row 1: Difficulty
        c.gridx = 0; c.gridy = 1;
        c.weightx = 0.4;
        diffLabel = makeLabel("DIFFICULTY LEVEL:");
        card.add(diffLabel, c);

        c.gridx = 1;
        c.weightx = 0.6;
        diffCombo = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        diffCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        card.add(diffCombo, c);

        // Row 2: Custom Quiz
        c.gridx = 0; c.gridy = 2;
        c.weightx = 0.4;
        customLabel = makeLabel("CUSTOM QUIZZES:");
        card.add(customLabel, c);

        c.gridx = 1;
        c.weightx = 0.6;
        JPanel customBtnsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        customBtnsPanel.setOpaque(false);

        customBuilderBtn = new JButton("🛠️ Quiz Builder");
        customBuilderBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        customBuilderBtn.setFocusPainted(false);
        customBuilderBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        customBuilderBtn.addActionListener(e -> nav.showQuizBuilder());
        customBtnsPanel.add(customBuilderBtn);

        customLoadBtn = new JButton("📂 Load Quiz");
        customLoadBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        customLoadBtn.setFocusPainted(false);
        customLoadBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        customLoadBtn.addActionListener(e -> handleLoadCustomQuiz(customLoadBtn));
        customBtnsPanel.add(customLoadBtn);

        card.add(customBtnsPanel, c);

        // Row 3: Start Button
        c.gridx = 0; c.gridy = 3;
        c.gridwidth = 2;
        c.insets = new Insets(24, 10, 4, 10);
        startButton = makePrimaryButton("CHOOSE CATEGORY  →");
        startButton.addActionListener(e -> handleStart());
        card.add(startButton, c);

        outer.add(card);
        return outer;
    }

    private JPanel buildRightColumn() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.gridx = 0;

        // Card 1: Daily challenge
        c.gridy = 0;
        c.weighty = 0.35;
        c.insets = new Insets(0, 0, 14, 0);
        panel.add(buildDailyChallengeCard(), c);

        // Card 2: Stats & Progress
        c.gridy = 1;
        c.weighty = 0.35;
        c.insets = new Insets(0, 0, 14, 0);
        panel.add(buildStatsCard(), c);

        // Card 3: Guide & smart practice
        c.gridy = 2;
        c.weighty = 0.30;
        c.insets = new Insets(0, 0, 0, 0);
        panel.add(buildGuideCard(), c);

        return panel;
    }

    private JPanel buildDailyChallengeCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BG_CARD);
        card.setOpaque(true);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                new EmptyBorder(12, 16, 12, 16)));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridx = 0;

        // Header Row: Title & Streak
        c.gridy = 0;
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);

        JLabel title = new JLabel("📅 Daily Quest");
        title.setFont(new Font("SansSerif", Font.BOLD, 13));
        title.setForeground(TEXT_DARK);
        headerRow.add(title, BorderLayout.WEST);

        SessionRepository repo = new SessionRepository();
        int streak = getCurrentStreak(repo);
        JLabel streakLabel = new JLabel("🔥 " + streak + " Day Streak");
        streakLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        streakLabel.setForeground(ACCENT_GOLD);
        headerRow.add(streakLabel, BorderLayout.EAST);
        card.add(headerRow, c);

        // Body Row: Calendar strip
        c.gridy = 1;
        c.insets = new Insets(10, 0, 2, 0);
        calendarStripHolder = new JPanel(new BorderLayout());
        calendarStripHolder.setOpaque(false);
        calendarStripHolder.add(buildCalendarStrip());
        card.add(calendarStripHolder, c);

        return card;
    }

    private JPanel buildStatsCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BG_CARD);
        card.setOpaque(true);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                new EmptyBorder(12, 16, 12, 16)));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridx = 0;

        // Header
        c.gridy = 0;
        JLabel title = new JLabel("📊 Performance & Badges");
        title.setFont(new Font("SansSerif", Font.BOLD, 13));
        title.setForeground(TEXT_DARK);
        card.add(title, c);

        // Body summary stats
        c.gridy = 1;
        c.insets = new Insets(8, 0, 10, 0);
        SessionRepository repo = new SessionRepository();
        int totalSess = repo.loadRaw().size();
        AnalyticsService analService = new AnalyticsService(repo);
        com.mathquiz.service.AchievementService achievementService = new com.mathquiz.service.AchievementService(repo, analService);
        long unlockedCount = achievementService.calculateAchievements().stream().filter(ach -> ach.unlocked).count();

        JLabel infoLabel = new JLabel("<html><body>💾 <b>" + totalSess + "</b> Sessions completed<br>🏆 <b>" + unlockedCount + " / 10</b> Badges unlocked</body></html>");
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        infoLabel.setForeground(TEXT_MUTED);
        card.add(infoLabel, c);

        // Actions
        c.gridy = 2;
        c.insets = new Insets(4, 0, 0, 0);
        JPanel actionsRow = new JPanel(new GridLayout(1, 2, 10, 0));
        actionsRow.setOpaque(false);

        analyticsBtn = new JButton("📊 Analytics");
        analyticsBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        analyticsBtn.setFocusPainted(false);
        analyticsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        analyticsBtn.addActionListener(e -> nav.showAnalytics());
        actionsRow.add(analyticsBtn);

        achievementsBtn = new JButton("🏆 Badges");
        achievementsBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        achievementsBtn.setFocusPainted(false);
        achievementsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        achievementsBtn.addActionListener(e -> nav.showAchievements());
        actionsRow.add(achievementsBtn);

        card.add(actionsRow, c);

        return card;
    }

    private JPanel buildGuideCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BG_CARD);
        card.setOpaque(true);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                new EmptyBorder(12, 16, 12, 16)));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridx = 0;

        // Header
        c.gridy = 0;
        JLabel title = new JLabel("🎯 Smart Practice & Guide");
        title.setFont(new Font("SansSerif", Font.BOLD, 13));
        title.setForeground(TEXT_DARK);
        card.add(title, c);

        // Body
        c.gridy = 1;
        c.insets = new Insets(8, 0, 10, 0);
        JLabel infoLabel = new JLabel("<html><body>Practice weak categories or take a guide tour.</body></html>");
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        infoLabel.setForeground(TEXT_MUTED);
        card.add(infoLabel, c);

        // Actions
        c.gridy = 2;
        c.insets = new Insets(4, 0, 0, 0);
        JPanel actionsRow = new JPanel(new GridLayout(1, 3, 6, 0));
        actionsRow.setOpaque(false);

        smartPracticeBtn = new JButton("🎯 Practice");
        smartPracticeBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        smartPracticeBtn.setFocusPainted(false);
        smartPracticeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        smartPracticeBtn.addActionListener(e -> nav.startSmartPractice());
        actionsRow.add(smartPracticeBtn);

        JButton tourBtn = new JButton("🦉 Tour");
        tourBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        tourBtn.setFocusPainted(false);
        tourBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tourBtn.addActionListener(e -> nav.launchTour());
        actionsRow.add(tourBtn);

        JButton guideBtn = new JButton("❓ Guide");
        guideBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        guideBtn.setFocusPainted(false);
        guideBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        guideBtn.addActionListener(e -> nav.showHelp("welcome"));
        actionsRow.add(guideBtn);

        card.add(actionsRow, c);

        return card;
    }

    private void handleLoadCustomQuiz(Component parent) {
        com.mathquiz.service.CustomQuizService service = new com.mathquiz.service.CustomQuizService();
        java.util.List<String> quizzes = service.getAvailableQuizzes();
        if (quizzes.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No custom quizzes found!\nCreate one using the Quiz Builder first. 🦉",
                    "No Quizzes", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(AppTheme.getBgCard());

        for (String qName : quizzes) {
            JMenuItem item = new JMenuItem("📂  " + qName);
            item.setFont(new Font("SansSerif", Font.PLAIN, 12));
            item.setBackground(AppTheme.getBgCard());
            item.setForeground(AppTheme.getTextDark());
            item.addActionListener(e -> {
                java.util.List<Question> questions = service.loadQuiz(qName);
                if (!questions.isEmpty()) {
                    nav.startCustomQuiz(qName, questions);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to load quiz or quiz is empty!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            menu.add(item);
        }

        menu.show(parent, 0, parent.getHeight());
    }

    private void handleStart() {
        String raw = qCountField.getText().trim();
        int qty;
        try {
            qty = Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a number for how many questions you want!",
                    "Oops!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (qty <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Please enter at least 1 question!",
                    "Oops!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        nav.goToCategories();
    }

    // ── Widget helpers ────────────────────────────────────────────────────────
    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(TEXT_MUTED);
        return lbl;
    }

    private void styleTextField(JTextField tf) {
        tf.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 195, 185), 1),
                new EmptyBorder(5, 10, 5, 10)));
    }

    private JButton makePrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(TEXT_DARK);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(12, 36, 12, 36));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // =========================================================================
    // Preference top bar and helpers
    // =========================================================================
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bar.setOpaque(false);

        profileButton = new JButton("👤 " + config.getCurrentProfile());
        styleToggleBtn(profileButton);
        profileButton.addActionListener(e -> showProfileMenu(profileButton));
        bar.add(profileButton);

        scaleToggleBtn = new JButton("🔍 Scale: " + (int)(config.getFontSizeScale() * 100) + "%");
        styleToggleBtn(scaleToggleBtn);
        scaleToggleBtn.addActionListener(e -> toggleScale());
        bar.add(scaleToggleBtn);

        soundToggleBtn = new JButton(getConfigSoundEmoji() + " Sound");
        styleToggleBtn(soundToggleBtn);
        soundToggleBtn.addActionListener(e -> showSoundMenu(soundToggleBtn));
        bar.add(soundToggleBtn);

        themeToggleBtn = new JButton(AppTheme.isDarkMode() ? "☀️ Light" : "🌙 Dark");
        styleToggleBtn(themeToggleBtn);
        themeToggleBtn.addActionListener(e -> toggleTheme());
        bar.add(themeToggleBtn);

        return bar;
    }

    private void toggleScale() {
        double current = config.getFontSizeScale();
        double next = 1.0;
        if (current == 1.0) next = 1.25;
        else if (current == 1.25) next = 1.5;
        config.setFontSizeScale(next);
        scaleToggleBtn.setText("🔍 Scale: " + (int)(next * 100) + "%");
        if (nav instanceof QuizFrame) {
            ((QuizFrame) nav).updateAllThemes();
        }
    }

    private void showProfileMenu(Component parent) {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(AppTheme.getBgCard());

        java.util.List<String> profiles = config.getProfiles();
        String current = config.getCurrentProfile();

        for (String p : profiles) {
            boolean isCurrent = p.equals(current);
            JMenuItem item = new JMenuItem((isCurrent ? "✓ " : "  ") + p);
            item.setFont(new Font("SansSerif", isCurrent ? Font.BOLD : Font.PLAIN, 12));
            item.setBackground(AppTheme.getBgCard());
            item.setForeground(AppTheme.getTextDark());
            item.addActionListener(e -> switchProfile(p));
            menu.add(item);
        }

        menu.addSeparator();

        JMenuItem addProfileItem = new JMenuItem("➕ Create Profile...");
        addProfileItem.setFont(new Font("SansSerif", Font.PLAIN, 12));
        addProfileItem.setBackground(AppTheme.getBgCard());
        addProfileItem.setForeground(AppTheme.getTextDark());
        addProfileItem.addActionListener(e -> createProfile());
        menu.add(addProfileItem);

        menu.show(parent, 0, parent.getHeight());
    }

    private void switchProfile(String profileName) {
        config.setCurrentProfile(profileName);
        profileButton.setText("👤 " + profileName);
        if (nav instanceof QuizFrame) {
            ((QuizFrame) nav).updateAllThemes();
        }
    }

    private void createProfile() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Create Profile 🦉", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setBackground(AppTheme.getBgPrimary());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
        mainPanel.setBackground(AppTheme.getBgPrimary());

        // Header Title
        JLabel title = new JLabel("Create Child Profile");
        title.setFont(new Font("Serif", Font.BOLD, 18));
        title.setForeground(AppTheme.getTextDark());
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(10));

        // Privacy Warning Block (COPPA/GDPR-K Compliance Notice)
        JLabel warning = new JLabel(
            "<html><body>" +
            "<b>🦉 Privacy First:</b> To protect child safety, please do not use your real " +
            "first or last name! Choose a fun nickname or generate a mascot name below." +
            "</body></html>"
        );
        warning.setFont(new Font("SansSerif", Font.PLAIN, 11));
        warning.setForeground(AppTheme.getTextMuted());
        warning.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
            new EmptyBorder(8, 8, 8, 8)
        ));
        warning.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(warning);
        mainPanel.add(Box.createVerticalStrut(14));

        // Input row
        JPanel inputRow = new JPanel(new BorderLayout(8, 0));
        inputRow.setOpaque(false);
        inputRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField nameField = new JTextField();
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        nameField.setBackground(AppTheme.getBgCard());
        nameField.setForeground(AppTheme.getTextDark());
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
            new EmptyBorder(6, 8, 6, 8)
        ));
        inputRow.add(nameField, BorderLayout.CENTER);

        JButton generateBtn = new JButton("🎭 Spark Mascot Name");
        generateBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        generateBtn.setBackground(AppTheme.getAccentGold());
        generateBtn.setForeground(AppTheme.getBgPrimary());
        generateBtn.setFocusPainted(false);
        generateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        generateBtn.addActionListener(e -> {
            String[] adjectives = {"Happy", "Clever", "Swift", "Bright", "Merry", "Kind", "Calm", "Brave", "Shiny", "Friendly", "Loyal", "Sparkly"};
            String[] animals = {"Owl", "Panda", "Cheetah", "Squirrel", "Dolphin", "Penguin", "Tiger", "Koala", "Otter", "Badger", "Fox", "Falcon"};
            Random rand = new Random();
            String randomName = adjectives[rand.nextInt(adjectives.length)] + " " + animals[rand.nextInt(animals.length)];
            nameField.setText(randomName);
        });
        inputRow.add(generateBtn, BorderLayout.EAST);
        mainPanel.add(inputRow);
        mainPanel.add(Box.createVerticalStrut(18));

        // Actions Row
        JPanel actionsRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsRow.setOpaque(false);
        actionsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton cancelBtn = new JButton("Cancel");
        styleToggleBtn(cancelBtn);
        cancelBtn.addActionListener(e -> dialog.dispose());
        actionsRow.add(cancelBtn);

        JButton createBtn = new JButton("Create Profile");
        createBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        createBtn.setBackground(AppTheme.getBgCard());
        createBtn.setForeground(new Color(34, 139, 34));
        createBtn.setFocusPainted(false);
        createBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
            new EmptyBorder(6, 16, 6, 16)
        ));
        createBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Profile name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!name.matches("[a-zA-Z0-9_ ]+")) {
                JOptionPane.showMessageDialog(dialog, "Profile name contains invalid characters!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (name.length() > 15) {
                JOptionPane.showMessageDialog(dialog, "Profile name must be 15 characters or less!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            config.addProfile(name);
            switchProfile(name);
            dialog.dispose();
        });
        actionsRow.add(createBtn);
        mainPanel.add(actionsRow);

        dialog.add(mainPanel);
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void styleToggleBtn(JButton btn) {
        btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btn.setBackground(AppTheme.getBgCard());
        btn.setForeground(AppTheme.getTextMuted());
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                new EmptyBorder(6, 12, 6, 12)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void toggleSound() {
        boolean current = config.isSoundEnabled();
        config.setSoundEnabled(!current);
        if (nav instanceof QuizFrame) {
            ((QuizFrame) nav).updateAllThemes();
        }
    }

    private void showSoundMenu(Component parent) {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(AppTheme.getBgCard());

        boolean soundOn = config.isSoundEnabled();
        JMenuItem muteItem = new JMenuItem((soundOn ? "🔊  Mute Sound" : "🔇  Unmute Sound"));
        muteItem.setFont(new Font("SansSerif", Font.PLAIN, 12));
        muteItem.setBackground(AppTheme.getBgCard());
        muteItem.setForeground(AppTheme.getTextDark());
        muteItem.addActionListener(e -> {
            toggleSound();
            com.mathquiz.service.SoundService snd = getSound();
            if (snd != null) snd.playClick();
        });
        menu.add(muteItem);

        menu.addSeparator();

        int currentVol = config.getSoundVolume();
        int[] vols = {100, 75, 50, 25};
        for (int v : vols) {
            boolean isCurrent = soundOn && (currentVol == v);
            JMenuItem volItem = new JMenuItem((isCurrent ? "✓ " : "  ") + v + "% Volume");
            volItem.setFont(new Font("SansSerif", isCurrent ? Font.BOLD : Font.PLAIN, 12));
            volItem.setBackground(AppTheme.getBgCard());
            volItem.setForeground(AppTheme.getTextDark());
            volItem.setEnabled(soundOn);
            volItem.addActionListener(e -> {
                config.setSoundVolume(v);
                com.mathquiz.service.SoundService snd = getSound();
                if (snd != null) {
                    snd.playClick();
                }
                if (nav instanceof QuizFrame) {
                    ((QuizFrame) nav).updateAllThemes();
                }
            });
            menu.add(volItem);
        }

        menu.show(parent, 0, parent.getHeight());
    }

    private com.mathquiz.service.SoundService getSound() {
        if (nav instanceof QuizFrame) {
            return ((QuizFrame) nav).getSoundService();
        }
        return null;
    }

    private String getConfigSoundEmoji() {
        return config.isSoundEnabled() ? "🔊" : "🔇";
    }

    private void toggleTheme() {
        boolean current = config.isDarkMode();
        config.setDarkMode(!current);
        if (nav instanceof QuizFrame) {
            ((QuizFrame) nav).updateAllThemes();
        }
    }

    private JPanel buildCalendarStrip() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        panel.setOpaque(false);

        SessionRepository repo = new SessionRepository();
        Set<String> playedDays = new HashSet<>();
        for (Map<String, Object> s : repo.loadRaw()) {
            String ts = (String) s.get("timestamp");
            if (ts != null && ts.length() >= 10) {
                playedDays.add(ts.substring(0, 10));
            }
        }

        JButton dailyBtn = new JButton("📅 Daily Challenge");
        styleToggleBtn(dailyBtn);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        String todayStr = sdf.format(new java.util.Date());
        if (config.getLastDailyChallengeDate().equals(todayStr)) {
            dailyBtn.setText("✅ Completed");
            dailyBtn.setEnabled(false);
        } else {
            dailyBtn.addActionListener(e -> nav.startDailyChallenge());
        }
        panel.add(dailyBtn);

        panel.add(Box.createHorizontalStrut(10));

        java.text.SimpleDateFormat daySdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.text.SimpleDateFormat nameSdf = new java.text.SimpleDateFormat("E");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_YEAR, -6);

        for (int i = 0; i < 7; i++) {
            String dateKey = daySdf.format(cal.getTime());
            String dayName = nameSdf.format(cal.getTime()).substring(0, 1);
            boolean played = playedDays.contains(dateKey);

            JPanel dayUnit = new JPanel(new BorderLayout(0, 2));
            dayUnit.setOpaque(false);

            JLabel nameLbl = new JLabel(dayName, SwingConstants.CENTER);
            nameLbl.setFont(new Font("SansSerif", Font.PLAIN, 9));
            nameLbl.setForeground(AppTheme.getTextMuted());
            dayUnit.add(nameLbl, BorderLayout.NORTH);

            JLabel circle = new JLabel(played ? "●" : "○", SwingConstants.CENTER);
            circle.setFont(new Font("SansSerif", Font.BOLD, 14));
            circle.setForeground(played ? AppTheme.getAccentGold() : AppTheme.getBorderClr());
            dayUnit.add(circle, BorderLayout.CENTER);

            panel.add(dayUnit);
            cal.add(java.util.Calendar.DAY_OF_YEAR, 1);
        }

        return panel;
    }

    private int getCurrentStreak(SessionRepository repo) {
        Set<String> days = new TreeSet<>();
        for (Map<String, Object> s : repo.loadRaw()) {
            String ts = (String) s.get("timestamp");
            if (ts != null && ts.length() >= 10) {
                days.add(ts.substring(0, 10));
            }
        }
        if (days.isEmpty()) return 0;

        java.util.List<String> sorted = new ArrayList<>(days);
        Collections.sort(sorted);

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new java.util.Date());
        int lastIndex = sorted.size() - 1;
        String lastPlayed = sorted.get(lastIndex);

        long diffFromToday = dayDiff(lastPlayed, today);
        if (diffFromToday > 1) return 0;

        int streak = 1;
        for (int i = lastIndex; i > 0; i--) {
            if (dayDiff(sorted.get(i - 1), sorted.get(i)) == 1) {
                streak++;
            } else {
                break;
            }
        }
        return streak;
    }

    private static long dayDiff(String d1, String d2) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            long ms1 = sdf.parse(d1).getTime();
            long ms2 = sdf.parse(d2).getTime();
            return Math.abs(ms2 - ms1) / 86_400_000L;
        } catch (Exception e) {
            return 999;
        }
    }

    public void focusDefaultField() {
        SwingUtilities.invokeLater(() -> {
            if (qCountField != null) {
                qCountField.requestFocusInWindow();
                qCountField.selectAll();
            }
        });
    }

    public void applyTheme() {
        setBackground(AppTheme.getBgPrimary());
        recolorTree(this);
    }

    private void recolorTree(Container parent) {
        for (Component c : parent.getComponents()) {
            if (c instanceof JPanel) {
                JPanel p = (JPanel) c;
                if (p.isOpaque() && p != this && p != topBarPanel) {
                    p.setBackground(AppTheme.getBgCard());
                    p.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                            p.getBorder() != null && p.getBorder() instanceof javax.swing.border.CompoundBorder
                                    ? ((javax.swing.border.CompoundBorder)p.getBorder()).getInsideBorder()
                                    : new EmptyBorder(16, 16, 16, 16)));
                } else {
                    p.setBackground(AppTheme.getBgPrimary());
                }
                recolorTree(p);
            } else if (c instanceof JLabel) {
                JLabel lbl = (JLabel) c;
                if (lbl == subBrand) {
                    lbl.setForeground(AppTheme.getAccentGold());
                } else if (lbl == titleLabel) {
                    lbl.setForeground(AppTheme.getTextDark());
                } else {
                    lbl.setForeground(AppTheme.getTextMuted());
                }
            } else if (c instanceof JButton) {
                JButton btn = (JButton) c;
                if (btn == startButton) {
                    btn.setBackground(AppTheme.getTextDark());
                    btn.setForeground(AppTheme.getBgCard());
                } else {
                    btn.setBackground(AppTheme.getBgCard());
                    btn.setForeground(AppTheme.getTextMuted());
                    btn.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                            new EmptyBorder(6, 12, 6, 12)));
                }
            } else if (c instanceof JComboBox) {
                JComboBox<?> cb = (JComboBox<?>) c;
                cb.setBackground(AppTheme.getBgCard());
                cb.setForeground(AppTheme.getTextDark());
            } else if (c instanceof JTextField) {
                JTextField tf = (JTextField) c;
                tf.setBackground(AppTheme.getBgCard());
                tf.setForeground(AppTheme.getTextDark());
                tf.setCaretColor(AppTheme.getTextDark());
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                        new EmptyBorder(5, 10, 5, 10)));
            }
        }
    }
}

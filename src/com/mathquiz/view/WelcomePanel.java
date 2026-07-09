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


/**
 * Welcome / Configuration screen.
 * Lets the child set the number of questions and difficulty before choosing a category.
 * Exposes named component accessors so TourManager can target them for spotlighting.
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

    // Phase 3 components
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
    private JPanel brandHeaderPanel;
    private JPanel footerPanel;
    private JPanel calendarStripHolder;

    // Phase 4 components
    private JButton profileButton;
    private JButton scaleToggleBtn;

    // Phase 5 components
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


    /** Returns the current question count, defaulting to 10 on parse error. */
    public int getQuestionCount() {
        try {
            int v = Integer.parseInt(qCountField.getText().trim());
            return (v > 0) ? v : 10;
        } catch (NumberFormatException e) {
            return 10;
        }
    }

    /** Returns the selected difficulty string. */
    public String getDifficulty() {
        return (String) diffCombo.getSelectedItem();
    }

    // ── UI construction ───────────────────────────────────────────────────────

    private void build() {
        JPanel headerContainer = new JPanel(new BorderLayout());
        headerContainer.setOpaque(false);
        topBarPanel = buildTopBar();
        brandHeaderPanel = buildBrandHeader();
        headerContainer.add(topBarPanel, BorderLayout.NORTH);
        headerContainer.add(brandHeaderPanel, BorderLayout.CENTER);

        add(headerContainer, BorderLayout.NORTH);
        configCardPanel = buildConfigCard();
        add(configCardPanel,  BorderLayout.CENTER);
        footerPanel = buildFooter();
        add(footerPanel,      BorderLayout.SOUTH);
    }


    private JPanel buildBrandHeader() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // Brand Logo
        int gridRow = 0;
        try {
            java.net.URL logoUrl = WelcomePanel.class.getResource("/com/mathquiz/resources/logo.png");
            if (logoUrl != null) {
                ImageIcon logoIcon = new ImageIcon(logoUrl);
                Image scaledImg = logoIcon.getImage().getScaledInstance(75, 75, Image.SCALE_SMOOTH);
                JLabel logoLabel = new JLabel(new ImageIcon(scaledImg));
                gbc.gridy = gridRow++;
                gbc.insets = new Insets(0, 0, 10, 0);
                panel.add(logoLabel, gbc);
            }
        } catch (Exception e) {
            System.err.println("Could not load logo in WelcomePanel: " + e.getMessage());
        }

        // Sub-brand line
        gbc.gridy = gridRow++;
        gbc.insets = new Insets(0, 0, 0, 0);
        subBrand = new JLabel("ATELIER ARITHMETIC");
        subBrand.setFont(new Font("Serif", Font.PLAIN, 11));
        subBrand.setForeground(ACCENT_GOLD);
        panel.add(subBrand, gbc);

        // Main title
        gbc.gridy = gridRow++;
        titleLabel = new JLabel("Royal Mathematics Quiz");
        titleLabel.setFont(new Font("Serif", Font.PLAIN, 34));
        titleLabel.setForeground(TEXT_DARK);
        panel.add(titleLabel, gbc);

        // Tagline
        gbc.gridy = gridRow++;
        taglineLabel = new JLabel("Test your arithmetic skills with bespoke challenges!");
        taglineLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        taglineLabel.setForeground(TEXT_MUTED);
        panel.add(taglineLabel, gbc);

        // Streak Calendar Visualizer
        gbc.gridy = gridRow++;
        gbc.insets = new Insets(15, 0, 0, 0);
        calendarStripHolder = new JPanel(new BorderLayout());
        calendarStripHolder.setOpaque(false);
        calendarStripHolder.add(buildCalendarStrip());
        panel.add(calendarStripHolder, gbc);

        return panel;
    }


    private JPanel buildConfigCard() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                new EmptyBorder(18, 30, 18, 30)));

        GridBagConstraints c = new GridBagConstraints();
        c.fill   = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 10, 6, 10);

        // ── Row 0: Question count ─────────────────────────────────────────────
        c.gridx = 0; c.gridy = 0;
        countLabel = makeLabel("NUMBER OF QUESTIONS:");
        card.add(countLabel, c);

        c.gridx = 1;
        qCountField = new JTextField("10", 8);
        styleTextField(qCountField);
        card.add(qCountField, c);

        // ── Row 1: Difficulty ─────────────────────────────────────────────────
        c.gridx = 0; c.gridy = 1;
        diffLabel = makeLabel("DIFFICULTY LEVEL:");
        card.add(diffLabel, c);


        c.gridx = 1;
        diffCombo = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        diffCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        card.add(diffCombo, c);

        // ── Row 2: Custom Quiz ────────────────────────────────────────────────
        c.gridx = 0; c.gridy = 2;
        customLabel = makeLabel("CUSTOM QUIZZES:");
        card.add(customLabel, c);

        c.gridx = 1;
        JPanel customBtnsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        customBtnsPanel.setOpaque(false);

        customBuilderBtn = new JButton("🛠️ Quiz Builder");
        customBuilderBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        customBuilderBtn.setBackground(AppTheme.getBgCard());
        customBuilderBtn.setForeground(AppTheme.getTextMuted());
        customBuilderBtn.setFocusPainted(false);
        customBuilderBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                new EmptyBorder(6, 12, 6, 12)));
        customBuilderBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        customBuilderBtn.addActionListener(e -> nav.showQuizBuilder());
        customBtnsPanel.add(customBuilderBtn);

        customLoadBtn = new JButton("📂 Load Quiz");
        customLoadBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        customLoadBtn.setBackground(AppTheme.getBgCard());
        customLoadBtn.setForeground(AppTheme.getTextMuted());
        customLoadBtn.setFocusPainted(false);
        customLoadBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                new EmptyBorder(6, 12, 6, 12)));
        customLoadBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        customLoadBtn.addActionListener(e -> handleLoadCustomQuiz(customLoadBtn));
        customBtnsPanel.add(customLoadBtn);

        card.add(customBtnsPanel, c);

        // ── Row 3: Start Button ───────────────────────────────────────────────
        c.gridx = 0; c.gridy = 3;
        c.gridwidth = 2;
        c.insets = new Insets(14, 10, 4, 10);
        startButton = makePrimaryButton("CHOOSE CATEGORY  →");
        startButton.addActionListener(e -> handleStart());
        card.add(startButton, c);

        outer.add(card);
        return outer;
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

    private JPanel buildFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(8, 10, 20, 10));

        // Help button ❓
        JButton helpBtn = makeIconButton("❓ Guide");
        helpBtn.addActionListener(e -> nav.showHelp("welcome"));
        panel.add(helpBtn);

        // Tour replay button 🦉
        JButton tourBtn = makeIconButton("🦉 Tour");
        tourBtn.addActionListener(e -> nav.launchTour());
        panel.add(tourBtn);

        // Analytics button 📊
        analyticsBtn = makeIconButton("📊 Analytics");
        analyticsBtn.addActionListener(e -> nav.showAnalytics());
        panel.add(analyticsBtn);

        // Smart Practice button 🎯
        smartPracticeBtn = makeIconButton("🎯 Practice");
        smartPracticeBtn.addActionListener(e -> nav.startSmartPractice());
        panel.add(smartPracticeBtn);

        // Achievements button 🏆
        achievementsBtn = makeIconButton("🏆 Badges");
        achievementsBtn.addActionListener(e -> nav.showAchievements());
        panel.add(achievementsBtn);

        return panel;
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

    private JButton makeIconButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setBackground(BG_CARD);
        btn.setForeground(TEXT_MUTED);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                new EmptyBorder(10, 18, 10, 18)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // =========================================================================
    // Phase 3 Features
    // =========================================================================

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bar.setOpaque(false);

        // Profile Switcher
        profileButton = new JButton("👤 " + config.getCurrentProfile());
        styleToggleBtn(profileButton);
        profileButton.addActionListener(e -> showProfileMenu(profileButton));
        bar.add(profileButton);

        // Font Scale Toggle
        scaleToggleBtn = new JButton("🔍 Scale: " + (int)(config.getFontSizeScale() * 100) + "%");
        styleToggleBtn(scaleToggleBtn);
        scaleToggleBtn.addActionListener(e -> toggleScale());
        bar.add(scaleToggleBtn);

        // Sound Toggle
        soundToggleBtn = new JButton(getConfigSoundEmoji() + " Sound");
        styleToggleBtn(soundToggleBtn);
        soundToggleBtn.addActionListener(e -> showSoundMenu(soundToggleBtn));
        bar.add(soundToggleBtn);

        // Theme Toggle
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
        String name = JOptionPane.showInputDialog(
                this,
                "Enter new profile name: 🦉",
                "Create Profile",
                JOptionPane.QUESTION_MESSAGE
        );
        if (name != null) {
            name = name.trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Profile name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!name.matches("[a-zA-Z0-9_ ]+")) {
                JOptionPane.showMessageDialog(this, "Profile name contains invalid characters!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (name.length() > 15) {
                JOptionPane.showMessageDialog(this, "Profile name must be 15 characters or less!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            config.addProfile(name);
            switchProfile(name);
        }
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
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
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
            dailyBtn.setText("✅ Daily Completed");
            dailyBtn.setEnabled(false);
        } else {
            dailyBtn.addActionListener(e -> nav.startDailyChallenge());
        }
        panel.add(dailyBtn);

        panel.add(Box.createHorizontalStrut(15));

        int currentStreak = getCurrentStreak(repo);
        JLabel flameLabel = new JLabel("🔥 " + currentStreak + " Day Streak");
        flameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        flameLabel.setForeground(AppTheme.getAccentGold());
        panel.add(flameLabel);

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
        if (subBrand != null) subBrand.setForeground(AppTheme.getAccentGold());
        if (titleLabel != null) titleLabel.setForeground(AppTheme.getTextDark());
        if (taglineLabel != null) taglineLabel.setForeground(AppTheme.getTextMuted());
        if (countLabel != null) countLabel.setForeground(AppTheme.getTextMuted());
        if (diffLabel != null) diffLabel.setForeground(AppTheme.getTextMuted());

        if (qCountField != null) {
            qCountField.setBackground(AppTheme.getBgCard());
            qCountField.setForeground(AppTheme.getTextDark());
            qCountField.setCaretColor(AppTheme.getTextDark());
            qCountField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                    new EmptyBorder(5, 10, 5, 10)));
        }

        if (diffCombo != null) {
            diffCombo.setBackground(AppTheme.getBgCard());
            diffCombo.setForeground(AppTheme.getTextDark());
        }

        if (startButton != null) {
            startButton.setBackground(AppTheme.getTextDark());
            startButton.setForeground(AppTheme.getBgCard());
        }

        if (soundToggleBtn != null) {
            String volStr = config.isSoundEnabled() ? " " + config.getSoundVolume() + "%" : " Muted";
            soundToggleBtn.setText(getConfigSoundEmoji() + volStr);
            styleToggleBtn(soundToggleBtn);
        }
        if (themeToggleBtn != null) {
            themeToggleBtn.setText(AppTheme.isDarkMode() ? "☀️ Light" : "🌙 Dark");
            styleToggleBtn(themeToggleBtn);
        }

        if (profileButton != null) {
            profileButton.setText("👤 " + config.getCurrentProfile());
            styleToggleBtn(profileButton);
        }
        if (scaleToggleBtn != null) {
            scaleToggleBtn.setText("🔍 Scale: " + (int)(config.getFontSizeScale() * 100) + "%");
            styleToggleBtn(scaleToggleBtn);
        }

        if (customLabel != null) customLabel.setForeground(AppTheme.getTextMuted());
        if (customBuilderBtn != null) {
            customBuilderBtn.setBackground(AppTheme.getBgCard());
            customBuilderBtn.setForeground(AppTheme.getTextMuted());
            customBuilderBtn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                    new EmptyBorder(6, 12, 6, 12)));
        }
        if (customLoadBtn != null) {
            customLoadBtn.setBackground(AppTheme.getBgCard());
            customLoadBtn.setForeground(AppTheme.getTextMuted());
            customLoadBtn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                    new EmptyBorder(6, 12, 6, 12)));
        }

        if (calendarStripHolder != null) {
            calendarStripHolder.removeAll();
            calendarStripHolder.add(buildCalendarStrip());
            calendarStripHolder.revalidate();
            calendarStripHolder.repaint();
        }

        if (configCardPanel != null && configCardPanel.getComponentCount() > 0 && configCardPanel.getComponent(0) instanceof JPanel) {
            JPanel card = (JPanel) configCardPanel.getComponent(0);
            card.setBackground(AppTheme.getBgCard());
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                    new EmptyBorder(30, 40, 30, 40)));
        }

        // Apply style to footer buttons recursively
        if (footerPanel != null) {
            for (Component c : footerPanel.getComponents()) {
                if (c instanceof JButton) {
                    JButton btn = (JButton) c;
                    if (btn != startButton) {
                        btn.setBackground(AppTheme.getBgCard());
                        btn.setForeground(AppTheme.getTextMuted());
                        btn.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                                new EmptyBorder(10, 18, 10, 18)));
                    }
                }
            }
        }
    }
}


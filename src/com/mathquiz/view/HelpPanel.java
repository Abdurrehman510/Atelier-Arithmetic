package com.mathquiz.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import com.mathquiz.config.AppTheme;


/**
 * Persistent Help & Guide panel accessible via the ❓ button on every screen.
 *
 * Organised into 8 child-friendly sections covering every major feature.
 * Uses large readable text, emoji headers, and plain language suitable for
 * children aged 8–14.
 *
 * The panel remembers which screen the user came from so the Back button
 * returns them to the correct place.
 */
public class HelpPanel extends JPanel {

    // ── Design tokens ────────────────────────────────────────────────────────
    private static final Color BG_PRIMARY  = new Color(250, 249, 246);
    private static final Color BG_CARD     = Color.WHITE;
    private static final Color ACCENT_GOLD = new Color(184, 150, 110);
    private static final Color TEXT_DARK   = new Color(28, 25, 23);
    private static final Color TEXT_MUTED  = new Color(120, 113, 108);
    private static final Color BORDER_CLR  = new Color(230, 227, 220);
    private static final Color SECTION_BG  = new Color(253, 251, 247);

    private final QuizNavigator nav;
    private       String        returnScreen = "welcome";

    public HelpPanel(QuizNavigator nav) {
        this.nav = nav;
        setBackground(BG_PRIMARY);
        setLayout(new BorderLayout());
        build();
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /** Sets the screen to return to when the user presses Back. */
    public void setReturnScreen(String screen) {
        this.returnScreen = screen;
    }

    // ── UI construction ───────────────────────────────────────────────────────

    private void build() {
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(20, 30, 10, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.CENTER;

        // Logo
        int row = 0;
        try {
            java.net.URL logoUrl = HelpPanel.class.getResource("/com/mathquiz/resources/logo.png");
            if (logoUrl != null) {
                ImageIcon logoIcon = new ImageIcon(logoUrl);
                Image scaledImg = logoIcon.getImage().getScaledInstance(55, 55, Image.SCALE_SMOOTH);
                JLabel logoLabel = new JLabel(new ImageIcon(scaledImg));
                gbc.gridy = row++;
                gbc.insets = new Insets(0, 0, 8, 0);
                p.add(logoLabel, gbc);
            }
        } catch (Exception e) {
            System.err.println("Could not load logo in HelpPanel: " + e.getMessage());
        }

        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 0, 0);
        JLabel title = new JLabel("🦉 Help & Guide");
        title.setFont(new Font("Serif", Font.PLAIN, 30));
        title.setForeground(TEXT_DARK);
        p.add(title, gbc);

        gbc.gridy = row++;
        JLabel sub = new JLabel("Everything you need to know about Atelier Arithmetic!");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(TEXT_MUTED);
        p.add(sub, gbc);

        return p;
    }

    private JScrollPane buildContent() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(10, 30, 20, 30));

        content.add(buildSection("📖 How to Start a Quiz",
                "1. On the Home screen, choose HOW MANY questions you want (try 5 or 10 to start).\n" +
                "2. Pick a DIFFICULTY: Easy is great for beginners, Hard is for champions!\n" +
                "3. Click 'CHOOSE CATEGORY' and then pick what type of math you want to practise.\n" +
                "4. Answer each question by typing a number and pressing ENTER or the Submit button.\n" +
                "5. After all questions, you'll see your score and grade!"));

        content.add(Box.createVerticalStrut(12));

        content.add(buildSection("➕ Understanding the Categories",
                "ADDITION — Add numbers together to get the sum. Example: 34 + 57 = 91\n" +
                "DIFFERENCE — Subtract one number from another. Example: 85 - 32 = 53\n" +
                "MULTIPLICATION — Multiply numbers to get the product. Example: 12 × 7 = 84\n" +
                "DIVISION — Divide numbers perfectly (no remainders!). Example: 48 / 6 = 8\n" +
                "MIXED — A random mix of all four types! Great for all-round practice.\n" +
                "SPECIAL — Tricky compound expressions using brackets. Example: (6 + 4) × (9 - 3) = 60"));

        content.add(Box.createVerticalStrut(12));

        content.add(buildSection("⭐ What Do the Grades Mean?",
                "A++ (95–100%) → Outstanding — True Mastery! 🏆\n" +
                "A+  (85–94%)  → Excellent — Impressive Skills! 🌟\n" +
                "A   (78–84%)  → Very Good — Strong Performance! 💪\n" +
                "B+  (65–77%)  → Good — Solid Foundation! 👍\n" +
                "B   (53–64%)  → Progressing — Keep Practising! 📈\n" +
                "C+  (40–52%)  → Developing — You're Learning! 🔄\n" +
                "C   (33–39%)  → Getting Started — Stay Curious! 🌱\n" +
                "D   (0–32%)   → Room to Grow — Try an Easier Level! 🚀"));

        content.add(Box.createVerticalStrut(12));

        content.add(buildSection("⏱ About the Timer",
                "Each question shows a timer counting up in seconds (⏱ 0s, ⏱ 1s…).\n" +
                "There is NO time limit — it's just there to help you track how fast you're getting!\n" +
                "As you practise more, you'll notice your time getting shorter. That means you're improving!"));

        content.add(Box.createVerticalStrut(12));

        content.add(buildSection("📋 Reviewing Your Answers",
                "After finishing a quiz, click the '📋 Review Answers' button on the results screen.\n" +
                "You'll see a table of every question with:\n" +
                "  - Your answer\n" +
                "  - The correct answer\n" +
                "  - Whether you were right (✅) or wrong (❌)\n" +
                "  - How long you took\n" +
                "Green rows = correct. Red rows = incorrect. Learn from your mistakes!"));

        content.add(Box.createVerticalStrut(12));

        content.add(buildSection("💾 Your Progress is Saved!",
                "Every quiz session is automatically saved to your computer!\n" +
                "This means in future updates of the app, you'll be able to see your full history,\n" +
                "track your improvement over time, and find out which areas need more practice.\n" +
                "Your data is stored privately on your own computer only."));

        content.add(Box.createVerticalStrut(12));

        content.add(buildSection("🎮 Tips to Score Higher",
                "✅ Start on Easy and master it before moving to Medium or Hard.\n" +
                "✅ Use the MIXED category to practise all math types.\n" +
                "✅ Review your wrong answers after each session — that's where learning happens!\n" +
                "✅ Set a goal: beat your previous score each time you play!\n" +
                "✅ Practise a little every day — even 5 questions a day makes a big difference!\n" +
                "✅ For SPECIAL questions, solve the brackets first (BODMAS rule)."));

        content.add(Box.createVerticalStrut(12));

        content.add(buildSection("⚙️ Settings & Customization",
                "• DARK MODE: click the Moon icon on the Home screen to toggle professional zinc dark mode.\n" +
                "• FONT SCALING: click the Scale button to enlarge text sizing recursively to 125% or 150%.\n" +
                "• PROFILE SWITCHER: select your profile name in the dropdown to keep your data isolated.\n" +
                "• SOUND EFFECTS: toggle arpeggio sound chimes and warning buzzer sounds off or on."));

        content.add(Box.createVerticalStrut(12));

        content.add(buildSection("🔥 Weekly Streaks & Daily Challenges",
                "• WEEKLY CALENDAR: see checkmarks for each day of the past 7 days you completed math practice.\n" +
                "• PRACTICE STREAK: complete at least one quiz daily to build your flame streak level.\n" +
                "• DAILY CHALLENGE: a unique daily quiz of 10 medium questions. Prevents double-completion daily."));

        content.add(Box.createVerticalStrut(12));

        content.add(buildSection("📊 Analytics, Smart Practice & Quiz Builder",
                "• CHARTS: line charts show accuracy trends; hexagonal radar charts show category strengths.\n" +
                "• SMART PRACTICE: click Practice on the Home screen to play a quiz custom-designed to address your weakest topic.\n" +
                "• QUIZ BUILDER: Parents & Teachers can build custom math quiz sets and load them via the dropdown."));

        content.add(Box.createVerticalStrut(12));

        content.add(buildSection("🎨 About Atelier Arithmetic & Archie",
                "Atelier Arithmetic is a premium math quiz and intelligence learning studio built for children.\n\n" +
                "Archie the Owl is your mathematics guide! He will help you during the interactive tour, " +
                "provide hints during calculations, and offer helpful learning suggestions on the results dashboard.\n\n" +
                "Version: 1.0.0\n" +
                "Brand and mascot designs by Atelier Arithmetic Studio. All rights reserved."));

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setViewportBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(BG_PRIMARY);
        return scroll;
    }

    private JPanel buildSection(String title, String body) {
        JPanel section = new JPanel(new BorderLayout(0, 8));
        section.setBackground(SECTION_BG);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                new EmptyBorder(16, 20, 16, 20)));
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLbl.setForeground(new Color(120, 90, 50));
        section.add(titleLbl, BorderLayout.NORTH);

        // Multi-line body with line breaks preserved and emojis wrapped for Segoe UI Emoji
        String bodyWrapped = wrapEmojis(body.replace("\n", "<br>"));
        String html = "<html><body style='font-family:SansSerif;font-size:12px;color:#555;'>"
                + bodyWrapped
                + "</body></html>";
        JLabel bodyLbl = new JLabel(html);
        bodyLbl.setForeground(TEXT_MUTED);
        bodyLbl.setBorder(new EmptyBorder(4, 0, 0, 0));
        section.add(bodyLbl, BorderLayout.CENTER);

        return section;
    }

    private String wrapEmojis(String text) {
        if (text == null) return "";
        // Matches basic emoji/symbol ranges and SMP surrogate pairs (like U+1F300 to U+1F9FF)
        return text.replaceAll("([\u2600-\u27BF]|[\ud83c-\udbff][\udc00-\udfff])", "<font face='Segoe UI Emoji'>$1</font>");
    }

    private JPanel buildFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 20, 28, 20));

        JButton backBtn = new JButton("← Back");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        backBtn.setBackground(new Color(28, 25, 23));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorder(new EmptyBorder(10, 28, 10, 28));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> navigateBack());
        panel.add(backBtn);

        JButton tourBtn = new JButton("🦉 Replay Tour");
        tourBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tourBtn.setBackground(BG_PRIMARY);
        tourBtn.setForeground(TEXT_MUTED);
        tourBtn.setFocusPainted(false);
        tourBtn.setBorderPainted(false);
        tourBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tourBtn.addActionListener(e -> nav.launchTour());
        panel.add(tourBtn);

        return panel;
    }

    private void navigateBack() {
        switch (returnScreen) {
            case "categories": nav.goToCategories(); break;
            case "game":       /* can't cleanly return to mid-game, go home */ nav.goToWelcome(); break;
            case "results":    nav.finishQuiz(null); break;
            default:           nav.goToWelcome();
        }
    }
    public void applyTheme() {
        setBackground(AppTheme.getBgPrimary());
        recolorTree(this);
    }

    private void recolorTree(Container parent) {
        for (Component c : parent.getComponents()) {
            if (c instanceof JPanel) {
                JPanel p = (JPanel) c;
                if (p.isOpaque() && p != this) {
                    p.setBackground(AppTheme.getBgCard());
                    p.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                            new EmptyBorder(12, 16, 12, 16)));
                } else {
                    p.setBackground(AppTheme.getBgPrimary());
                }
                recolorTree(p);
            } else if (c instanceof JLabel) {
                JLabel lbl = (JLabel) c;
                if (lbl.getFont().getSize() > 20) {
                    lbl.setForeground(AppTheme.getTextDark());
                } else if (lbl.getFont().getSize() > 11) {
                    lbl.setForeground(AppTheme.getAccentGold());
                } else {
                    if (lbl.getText() != null && lbl.getText().startsWith("<html>")) {
                        String colorHex = AppTheme.isDarkMode() ? "#aaa" : "#555";
                        String textClean = lbl.getText().replaceAll("color:#[0-9a-fA-F]+", "color:" + colorHex);
                        lbl.setText(textClean);
                    }
                    lbl.setForeground(AppTheme.getTextMuted());
                }
            } else if (c instanceof JButton) {
                JButton btn = (JButton) c;
                if (btn.getText().contains("Back")) {
                    btn.setBackground(AppTheme.getTextDark());
                    btn.setForeground(AppTheme.getBgCard());
                } else {
                    btn.setBackground(AppTheme.getBgPrimary());
                    btn.setForeground(AppTheme.getTextMuted());
                }
            } else if (c instanceof JScrollPane) {
                JScrollPane s = (JScrollPane) c;
                s.getViewport().setBackground(AppTheme.getBgPrimary());
                Component view = s.getViewport().getView();
                if (view instanceof Container) {
                    recolorTree((Container) view);
                }
            }
        }
    }
}


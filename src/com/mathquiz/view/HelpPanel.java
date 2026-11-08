package com.mathquiz.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import com.mathquiz.config.AppTheme;


/**
 * Help & Guide panel opened from the ❓ Guide buttons on the Home, Category, Game and Results screens.
 *
 * Organised into 13 child-friendly sections covering every major feature.
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
                "1. On the Home screen, pick HOW MANY questions (try 5 or 10).\n" +
                "2. Pick a DIFFICULTY: Easy for beginners, Hard for champions!\n" +
                "3. Click 'CHOOSE CATEGORY', then pick a type of math.\n" +
                "4. Type a whole number, then press ENTER or click Submit.\n" +
                "5. After the last question, you'll see your score and grade!\n" +
                "Want to stop early? Press ESC during a quiz and choose Yes.\n" +
                "A quiz you leave early is not saved."));

        content.add(Box.createVerticalStrut(12));

        content.add(buildSection("➕ Understanding the Categories",
                "ADDITION — Add numbers to get the sum. Example: 34 + 57 = 91\n" +
                "DIFFERENCE — Subtract to find the gap. Example: 85 - 32 = 53\n" +
                "MULTIPLICATION — Multiply to get the product. Example: 12 * 7 = 84\n" +
                "DIVISION — Divide perfectly (no remainders!). Example: 48 / 6 = 8\n" +
                "MIXED — A random mix of the four types above!\n" +
                "SPECIAL — Compound expressions with brackets.\n" +
                "&nbsp;&nbsp;Example: (6 + 4) * (9 - 3) = 60\n" +
                "FRACTIONS — Parts of numbers, percentages and ratios.\n" +
                "&nbsp;&nbsp;Example: What is 3/4 of 20? = 15\n" +
                "PATTERNS — Find the missing number in a sequence.\n" +
                "&nbsp;&nbsp;Example: Pattern: 2, 4, 6, 8, _ = 10\n" +
                "ALGEBRA — Find the mystery number x.\n" +
                "&nbsp;&nbsp;Example: Solve for x: x + 5 = 12 means x = 7\n" +
                "MEASUREMENT — Convert units; find areas, perimeters and volumes.\n" +
                "&nbsp;&nbsp;Example: Convert 3 meters to centimeters = 300\n" +
                "In the game, * means multiply and / means divide.\n" +
                "Every answer is a whole number."));

        content.add(Box.createVerticalStrut(12));

        content.add(buildSection("💡 Hints, Level Prompts & Shortcuts",
                "• Stuck? Click 💡 Hint (or press F1 or Ctrl+H).\n" +
                "&nbsp;&nbsp;Hints are free!\n" +
                "• After 5 right answers in a row, Archie may offer the next level.\n" +
                "&nbsp;&nbsp;After 3 wrong answers in a row, he may offer an easier level.\n" +
                "&nbsp;&nbsp;It is always your choice.\n" +
                "• Type whole numbers only. Anything else just lets you try again.\n" +
                "• ENTER sends your answer. ESC asks if you want to leave the quiz.\n" +
                "• Category screen: arrow keys to move, ENTER or SPACE to start."));

        content.add(Box.createVerticalStrut(12));

        content.add(buildSection("⭐ What Do the Grades Mean?",
                "A++ (95–100%) → Outstanding — True Mastery! 🏆\n" +
                "A+  (85–94%)  → Excellent — Impressive Skills! 🌟\n" +
                "A   (78–84%)  → Very Good — Strong Performance! 💪\n" +
                "B+  (65–77%)  → Good — Solid Foundation! 👍\n" +
                "B   (53–64%)  → Progressing — Keep Practicing! 📈\n" +
                "C+  (40–52%)  → Developing — You're Learning! 🔄\n" +
                "C   (33–39%)  → Getting Started — Stay Curious! 🌱\n" +
                "D   (below 33%) → Room to Grow — Try an Easier Level! 🚀"));

        content.add(Box.createVerticalStrut(12));

        content.add(buildSection("⏱ About the Timer",
                "Each question shows a timer counting up in seconds (⏱ 0s, ⏱ 1s…).\n" +
                "There is NO time limit — it just shows how fast you're getting!\n" +
                "As you practice, your times get shorter — you're improving!"));

        content.add(Box.createVerticalStrut(12));

        content.add(buildSection("📋 Reviewing Your Answers",
                "After a quiz, click '📋 Review Answers' on the results screen.\n" +
                "You'll see a table of every question with:\n" +
                "  - Your answer\n" +
                "  - The correct answer\n" +
                "  - Whether you were right (✅) or wrong (❌)\n" +
                "  - How long you took\n" +
                "Green rows = correct. Red rows = incorrect. Learn from mistakes!"));

        content.add(Box.createVerticalStrut(12));

        content.add(buildSection("🛍️ Stars, Badges & Archie's Shop",
                "• Every finished quiz earns stars: 5 for finishing and 1 for each\n" +
                "&nbsp;&nbsp;right answer, plus bonus stars for great scores, streaks and\n" +
                "&nbsp;&nbsp;the Daily Challenge.\n" +
                "• Spend stars in Archie's Shop (Home screen → Open Mascot Shop)\n" +
                "&nbsp;&nbsp;on hats, glasses, colors and badges for Archie.\n" +
                "&nbsp;&nbsp;A new item goes on Archie right away.\n" +
                "• 10 BADGES unlock by themselves as you play: your first quiz,\n" +
                "&nbsp;&nbsp;a perfect score, an answer in under 2 seconds, 10 quizzes,\n" +
                "&nbsp;&nbsp;90% or more in Addition, Difference, Multiplication or Division,\n" +
                "&nbsp;&nbsp;80% or more on a Hard Special quiz, and a 3-day streak.\n" +
                "• Stars are kept for the whole computer. Each profile has\n" +
                "&nbsp;&nbsp;its own quiz history, badges and Archie accessories."));

        content.add(Box.createVerticalStrut(12));

        content.add(buildSection("💾 Your Progress is Saved!",
                "Every finished quiz is saved on this computer automatically.\n" +
                "Each profile keeps its own history. Grown-ups can open\n" +
                "Analytics on the Home screen (after a math puzzle) to see\n" +
                "accuracy, speed, streaks and the topics that need practice.\n" +
                "Your data stays on this computer unless a report is exported."));

        content.add(Box.createVerticalStrut(12));

        content.add(buildSection("🎮 Tips to Score Higher",
                "✅ Start on Easy and master it before moving to Medium or Hard.\n" +
                "✅ Use MIXED to practice all four basic operations.\n" +
                "✅ Review your wrong answers — that's where learning happens!\n" +
                "✅ Set a goal: beat your previous score each time you play!\n" +
                "✅ Practice a little every day — even 5 questions helps!\n" +
                "✅ For SPECIAL questions, solve the brackets first (BODMAS rule).\n" +
                "✅ Try Smart Practice: Archie picks where you can improve the most."));

        content.add(Box.createVerticalStrut(12));

        content.add(buildSection("⚙️ Settings & Customization",
                "• DARK MODE: click the Moon button on the Home screen\n" +
                "&nbsp;&nbsp;(it becomes a Sun to switch back).\n" +
                "• TEXT SIZE: the Scale button cycles 100%, 125% (start) and 150%.\n" +
                "• PROFILES: click your profile name to switch or create one.\n" +
                "&nbsp;&nbsp;Use a nickname (or Spark Mascot Name), never your real name!\n" +
                "• SOUND: the Sound button opens a menu to mute or pick a volume\n" +
                "&nbsp;&nbsp;(100%, 75%, 50% or 25%). A grown-up math puzzle comes first."));

        content.add(Box.createVerticalStrut(12));

        content.add(buildSection("🔥 Weekly Streaks & Daily Challenges",
                "• WEEKLY CALENDAR: on the Home screen, a filled dot ● marks\n" +
                "&nbsp;&nbsp;each of the last 7 days you finished a quiz.\n" +
                "• PRACTICE STREAK: finish a quiz daily to grow your 🔥.\n" +
                "&nbsp;&nbsp;Missing a whole day starts it over.\n" +
                "• DAILY CHALLENGE: everyone gets the same 10 Medium questions\n" +
                "&nbsp;&nbsp;each day, with bonus stars. When you finish, the Home button\n" +
                "&nbsp;&nbsp;says Completed until tomorrow."));

        content.add(Box.createVerticalStrut(12));

        content.add(buildSection("📊 Analytics, Smart Practice & Quiz Builder",
                "• ANALYTICS (for grown-ups): an accuracy line chart, a 10-sided\n" +
                "&nbsp;&nbsp;radar chart of each category, a 28-day practice map, strengths,\n" +
                "&nbsp;&nbsp;weaknesses and recent quizzes. Reset History clears the current\n" +
                "&nbsp;&nbsp;profile's quiz history, stars and Archie accessories.\n" +
                "• SMART PRACTICE: click Practice on the Home screen to play 10\n" +
                "&nbsp;&nbsp;questions on the topic and level where you can improve most.\n" +
                "• QUIZ BUILDER (for parents and teachers): write your own\n" +
                "&nbsp;&nbsp;questions and answers, then play them with Load Quiz on Home.\n" +
                "• Analytics, Quiz Builder and Sound ask for a math puzzle first."));

        content.add(Box.createVerticalStrut(12));

        content.add(buildSection("🎨 About Atelier Arithmetic & Archie",
                "Atelier Arithmetic is a math quiz studio built for children.\n\n" +
                "Archie the Owl is your mathematics guide! He shows you around\n" +
                "in the interactive tour, greets you on the Home screen, and\n" +
                "shares learning tips on your results and analytics screens.\n\n" +
                "Version: 1.0.0\n" +
                "Brand and mascot designs by Atelier Arithmetic Studio.\n" +
                "All rights reserved."));

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
        String html = "<html><body style='font-family:SansSerif;font-size:12px;color:#555;width:440px;'>"
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


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.time.LocalDateTime;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class FurnitureUI extends JFrame {

    private java.util.List<Product> cart = new java.util.ArrayList<>();
    private JPanel productsSection;
    private JPanel howItWorksSection;

    // ---------------- Product model ----------------
    private static class Product {
        String name;
        String price;
        String imgName;
        public Product(String name, String price, String imgName){
            this.name = name;
            this.price = price;
            this.imgName = imgName;
        }
    }

    // ---------------- Styling fields (Session 8 minimal requested) ----------------
    private final Font uiFont = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font uiFontBold = new Font("Segoe UI", Font.BOLD, 17);
    private final Color primaryBlue = new Color(30, 50, 80); // dark blue
    private final Color primaryBlueHover = new Color(18, 35, 65);

    public FurnitureUI() {
        setTitle("Furniture");
        setSize(1200, 1600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Apply global font
        UIManager.put("Label.font", uiFont);
        UIManager.put("Button.font", uiFont);
        UIManager.put("TextField.font", uiFont);

        // Navbar fixe
        JPanel navbar = buildNavbar();
        add(navbar, BorderLayout.NORTH);

        // Contenu scrollable
        JScrollPane scrollPane = new JScrollPane(buildMainPanelWithoutNavbar());
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    // -------------------- helper: style a button --------------------
    private void styleButton(JButton btn) {
        btn.setBackground(primaryBlue);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(uiFont);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setOpaque(true);
        // Rounded effect with empty border is fine; for true rounded you'd need custom painting.
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(primaryBlueHover);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(primaryBlue);
            }
        });
    }

    // -------------------- IMAGE LOADER --------------------
    private ImageIcon loadImage(String fileName, int w, int h) {
        try {
            java.net.URL imgURL = getClass().getResource("/images/" + fileName);
            if (imgURL == null) {
                // placeholder if image missing
                BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = bi.createGraphics();
                g2.setPaint(Color.LIGHT_GRAY);
                g2.fillRect(0,0,w,h);
                g2.dispose();
                return new ImageIcon(bi);
            }
            ImageIcon icon = new ImageIcon(imgURL);
            Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = bi.createGraphics();
            g2.setPaint(Color.LIGHT_GRAY);
            g2.fillRect(0,0,w,h);
            g2.dispose();
            return new ImageIcon(bi);
        }
    }

    // -------------------- MAIN PANEL BUILD --------------------
    private JPanel buildMainPanelWithoutNavbar() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(Color.WHITE);

        // on commence par le hero section
        main.add(buildHeroSection());
        main.add(Box.createVerticalStrut(20));
        main.add(buildInspirationSection());
        main.add(Box.createVerticalStrut(20));
        main.add(buildBrowseRangeSection());
        main.add(Box.createVerticalStrut(20));
        main.add(buildProductsSection());
        main.add(Box.createVerticalStrut(20));
        main.add(buildHowItWorksSection());
        main.add(Box.createVerticalStrut(20));
        main.add(buildMailingListSection());
        main.add(Box.createVerticalStrut(20));
        main.add(buildFooterSection());

        return main;
    }

    // -------------------- NAVBAR --------------------
    private JPanel buildNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setPreferredSize(new Dimension(1200, 70));
        navbar.setBackground(new Color(255, 255, 255, 200));
        navbar.setBorder(new EmptyBorder(10, 40, 10, 40));

        JLabel brand = new JLabel("Furniture");
        brand.setFont(uiFontBold);
        brand.setForeground(new Color(30, 50, 80));

        JPanel menu = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0));
        menu.setOpaque(false);
        String[] items = {"Home",  "Products", "Services","Contact"};
        for (String item : items) {
            JLabel lbl = new JLabel(item);
            lbl.setFont(uiFont);
            lbl.setForeground(new Color(30, 50, 80));
            lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Improved listener: use scrollTo when possible and call contact form
            lbl.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    switch(item){
                        case "Home":
                            scrollTo(0);
                            break;
                        case "Products":
                            if (productsSection != null) scrollTo(productsSection.getY());
                            break;
                        case "Services":
                            if (howItWorksSection != null) scrollTo(howItWorksSection.getY());
                            break;
                        case "Contact":
                            openContactForm();
                            break;
                    }
                }
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    lbl.setForeground(primaryBlueHover);
                }
                public void mouseExited(java.awt.event.MouseEvent e) {
                    lbl.setForeground(new Color(30, 50, 80));
                }
            });

            menu.add(lbl);
        }

        JButton cartBtn = new JButton("Cart");
        styleButton(cartBtn);
        cartBtn.setFocusPainted(false);
        cartBtn.addActionListener(e -> openCartWindow());

        menu.add(cartBtn);

        navbar.add(brand, BorderLayout.WEST);
        navbar.add(menu, BorderLayout.EAST);

        return navbar;
    }

    private void scrollTo(int y){
        // find the scroll pane in the content and set scrollbar value
        for (Component c : getContentPane().getComponents()) {
            if (c instanceof JScrollPane) {
                JScrollBar bar = ((JScrollPane)c).getVerticalScrollBar();
                bar.setValue(y);
                return;
            }
        }
        // fallback (old approach)
        try {
            JScrollPane sp = (JScrollPane)getContentPane().getComponent(0);
            sp.getVerticalScrollBar().setValue(y);
        } catch (Exception ignored) {}
    }

    // -------------------- HERO --------------------
    private JPanel buildHeroSection() {
        JPanel hero = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(loadImage("hero.jpg", 1200, 500).getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        hero.setPreferredSize(new Dimension(1200, 500));
        hero.setLayout(new GridBagLayout());

        JPanel overlay = new JPanel();
        overlay.setBackground(new Color(0, 0, 0, 120));
        overlay.setLayout(new BoxLayout(overlay, BoxLayout.Y_AXIS));
        overlay.setBorder(new EmptyBorder(50, 50, 50, 50));

        JLabel title = new JLabel("Modern Furniture for Your Home");
        title.setFont(new Font("SansSerif", Font.BOLD, 40));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Discover unique designs and comfortable styles");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 20));
        subtitle.setForeground(Color.WHITE);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton shopBtn = new JButton("Shop Now");
        styleButton(shopBtn);
        shopBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        shopBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        shopBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        shopBtn.addActionListener(e -> {
            if (productsSection != null) scrollTo(productsSection.getY());
        });

        overlay.add(title);
        overlay.add(Box.createVerticalStrut(15));
        overlay.add(subtitle);
        overlay.add(Box.createVerticalStrut(25));
        overlay.add(shopBtn);

        hero.add(overlay);
        return hero;
    }

    // INSPIRATION COLLECTION
    private JPanel buildInspirationSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBorder(new EmptyBorder(60, 40, 60, 40));
        section.setBackground(Color.WHITE);

        JLabel title = new JLabel("Inspiration Collection", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel grid = new JPanel(new GridLayout(2, 3, 30, 30));
        grid.setBackground(Color.WHITE);
        grid.setBorder(new EmptyBorder(40, 0, 0, 0));

        grid.add(inspirationCard("img1.jpg", "Scandinavian Living Room"));
        grid.add(inspirationCard("img2.jpg", "Cozy Bedroom"));
        grid.add(inspirationCard("img3.jpg", "Minimalist Workspace"));
        grid.add(inspirationCard("hero.jpg", "Elegant Dining Area"));
        grid.add(inspirationCard("lamp.jpg", "Outdoor Patio Style"));
        grid.add(inspirationCard("lounge.jpg", "Modern Reading Nook"));

        section.add(title);
        section.add(grid);

        return section;
    }

    private JPanel inspirationCard(String imgName, String caption) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel img = new JLabel(loadImage(imgName, 350, 200));
        img.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lbl = new JLabel(caption, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lbl.setBorder(new EmptyBorder(10, 5, 5, 5));

        card.add(img, BorderLayout.CENTER);
        card.add(lbl, BorderLayout.SOUTH);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createLineBorder(new Color(30, 50, 80), 2));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
            }
        });

        return card;
    }

    //  BROWSE THE RANGE 
    private JPanel buildBrowseRangeSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBorder(new EmptyBorder(60, 40, 60, 40));
        section.setBackground(new Color(245, 245, 245));

        JLabel title = new JLabel("Browse Our Range", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel grid = new JPanel(new GridLayout(1, 4, 30, 0));
        grid.setBackground(new Color(245, 245, 245));
        grid.setBorder(new EmptyBorder(40, 0, 0, 0));

        grid.add(rangeCard("armchair.jpg", "Chairs"));
        grid.add(rangeCard("nightstand.jpg", "Tables"));
        grid.add(rangeCard("lamp.jpg", "Lamps"));
        grid.add(rangeCard("bedroom.jpg", "Bedroom"));

        section.add(title);
        section.add(grid);

        return section;
    }

    private JPanel rangeCard(String imgName, String category) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel img = new JLabel(loadImage(imgName, 200, 150));
        img.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbl = new JLabel(category, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(10, 0, 10, 0));

        card.add(img);
        card.add(lbl);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createLineBorder(new Color(30, 50, 80), 2));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            }
        });

        return card;
    }

    //  OUR PRODUCTS
    private JPanel buildProductsSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBorder(new EmptyBorder(60, 40, 60, 40));
        section.setBackground(Color.WHITE);

        productsSection = section; // pour scroll et Shop Now

        JLabel title = new JLabel("Our Products", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel grid = new JPanel(new GridLayout(2, 3, 30, 30));
        grid.setBackground(Color.WHITE);
        grid.setBorder(new EmptyBorder(40, 0, 0, 0));

        grid.add(styledProductBox("armchair.jpg", "Bouclé Wooden Armchair", "15000 DA"));
        grid.add(styledProductBox("lamp.jpg", "Woven Table Lamp", "4000 DA"));
        grid.add(styledProductBox("bedroom2.jpg", "Minimalist Bedroom", "65000 DA"));
        grid.add(styledProductBox("mirror.jpg", "LED Curve Mirror", "9000 DA"));
        grid.add(styledProductBox("lounge.jpg", "Scandinavian Lounge Chair", "55000 DA"));
        grid.add(styledProductBox("bedroom.jpg", "Cozy Bedroom Set", "320000 DA"));

        JButton viewAll = new JButton("VIEW ALL");
        styleButton(viewAll);
        viewAll.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewAll.setFont(new Font("Segoe UI", Font.BOLD, 14));
        viewAll.setFocusPainted(false);
        viewAll.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        section.add(title);
        section.add(grid);
        section.add(Box.createVerticalStrut(30));
        section.add(viewAll);

        return section;
    }

    private JPanel styledProductBox(String imgName, String name, String price) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        box.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        box.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                box.setBorder(BorderFactory.createLineBorder(new Color(30, 50, 80), 2));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                box.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
            }
        });

        JLabel img = new JLabel(loadImage(imgName, 220, 180));
        img.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblName = new JLabel(name, SwingConstants.CENTER);
        lblName.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblName.setBorder(new EmptyBorder(10, 0, 5, 0));

        JLabel lblPrice = new JLabel(price, SwingConstants.CENTER);
        lblPrice.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblPrice.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPrice.setForeground(new Color(30, 50, 80));

        JButton addBtn = new JButton("Add to Cart");
        styleButton(addBtn);
        addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addBtn.addActionListener(ev -> {
            cart.add(new Product(name, price, imgName));
            JOptionPane.showMessageDialog(this, name + " added to cart!");
        });

        box.add(img);
        box.add(lblName);
        box.add(lblPrice);
        box.add(Box.createVerticalStrut(5));
        box.add(addBtn);

        return box;
    }

    // HOW IT WORKS
    private JPanel buildHowItWorksSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBorder(new EmptyBorder(60, 40, 60, 40));
        section.setBackground(Color.WHITE);

        howItWorksSection = section; // pour scroll

        JLabel title = new JLabel("How It Works", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel grid = new JPanel(new GridLayout(1, 3, 30, 20));
        grid.setBackground(Color.WHITE);
        grid.setBorder(new EmptyBorder(40, 0, 0, 0));

        grid.add(workStepCard("purchase.jpg", "1. Purchase Securely", "Shop with confidence through our secure payment system."));
        grid.add(workStepCard("warehouse.jpg", "2. Ships From Warehouse", "We carefully pack and ship your order directly from our warehouse."));
        grid.add(workStepCard("style.jpg", "3. Style Your Room", "Add your personal touch and enjoy furniture that transforms your space."));

        section.add(title);
        section.add(grid);

        return section;
    }

    private JPanel workStepCard(String imgName, String stepTitle, String desc) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        box.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        box.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                box.setBorder(BorderFactory.createLineBorder(new Color(30, 50, 80), 2));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                box.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
            }
        });

        JLabel img = new JLabel(loadImage(imgName, 180, 150));
        img.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel(stepTitle, SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(new EmptyBorder(10, 0, 5, 0));

        JLabel lblDesc = new JLabel("<html><center>" + desc + "</center></html>");
        lblDesc.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(img);
        box.add(lblTitle);
        box.add(lblDesc);

        return box;
    }
    // MAILING LIST
    private JPanel buildMailingListSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBorder(new EmptyBorder(60, 40, 60, 40));
        section.setBackground(new Color(237, 244, 252));

        JLabel title = new JLabel("Join Our Mailing List", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Sign up to receive inspiration, product updates, and special offers.", SwingConstants.CENTER);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 16));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        inputPanel.setBackground(new Color(237, 244, 252));
        JTextField emailField = new JTextField(30);
        emailField.setFont(uiFont);
        JButton submitBtn = new JButton("Submit");
        styleButton(submitBtn);

        inputPanel.add(emailField);
        inputPanel.add(submitBtn);

        submitBtn.addActionListener(e -> {
            String em = emailField.getText().trim();
            if (em.isEmpty() || !em.matches("^.+@.+\\..+$")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid email.");
            } else {
                JOptionPane.showMessageDialog(this, "Thanks for subscribing!");
                emailField.setText("");
            }
        });

        section.add(title);
        section.add(Box.createVerticalStrut(15));
        section.add(sub);
        section.add(Box.createVerticalStrut(25));
        section.add(inputPanel);

        return section;
    }

    // FOOTER
    private JPanel buildFooterSection() {
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBorder(new EmptyBorder(40, 40, 40, 40));
        footer.setBackground(new Color(30, 50, 80));

        JLabel follow = new JLabel("Follow Us", SwingConstants.CENTER);
        follow.setFont(new Font("SansSerif", Font.BOLD, 18));
        follow.setForeground(Color.WHITE);
        follow.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel icons = new JPanel(new FlowLayout());
        icons.setBackground(new Color(30, 50, 80));
        icons.add(new JLabel("📘"));
        icons.add(new JLabel("📸"));
        icons.add(new JLabel("🐦"));

        footer.add(follow);
        footer.add(Box.createVerticalStrut(15));
        footer.add(icons);

        return footer;
    }

    //  CONTACT FORM
    private void openContactForm(){
        JFrame contact = new JFrame("Contact Us");
        contact.setSize(420,360);
        contact.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("📨 Contact Us");
        title.setFont(uiFontBold);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField name = new JTextField();
        JTextField email = new JTextField();
        JTextField subject = new JTextField();
        JTextArea message = new JTextArea(5, 20);
        message.setLineWrap(true);
        message.setWrapStyleWord(true);

        name.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        email.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        subject.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        message.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        panel.add(title);
        panel.add(Box.createVerticalStrut(12));

        panel.add(new JLabel("Name:"));
        panel.add(name);
        panel.add(Box.createVerticalStrut(8));

        panel.add(new JLabel("Email:"));
        panel.add(email);
        panel.add(Box.createVerticalStrut(8));

        panel.add(new JLabel("Subject:"));
        panel.add(subject);
        panel.add(Box.createVerticalStrut(8));

        panel.add(new JLabel("Message:"));
        panel.add(new JScrollPane(message));
        panel.add(Box.createVerticalStrut(12));

        JButton send = new JButton("Send Message");
        styleButton(send);
        send.setAlignmentX(Component.CENTER_ALIGNMENT);
        send.addActionListener(e -> {
            if (name.getText().trim().isEmpty() ||
                email.getText().trim().isEmpty() ||
                message.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(contact, "Please fill in name, email and message.");
                return;
            }
            if (!email.getText().matches("^.+@.+\\..+$")) {
                JOptionPane.showMessageDialog(contact, "Please enter a valid email address.");
                return;
            }
            JOptionPane.showMessageDialog(contact, "Thank you, " + name.getText() + "!");
            contact.dispose();
        });

        panel.add(send);

        contact.add(panel);
        contact.setVisible(true);
    }

    //CART 
    private void openCartWindow() {
        JFrame cartFrame = new JFrame("Your Cart");
        cartFrame.setSize(450, 550);
        cartFrame.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        int total = 0;
        for (int i = 0; i < cart.size(); i++) {
            Product p = cart.get(i);

            JPanel itemPanel = new JPanel(new BorderLayout());
            itemPanel.setBackground(Color.decode("#F5F5F5"));
            itemPanel.setBorder(BorderFactory.createLineBorder(Color.decode("#CCCCCC"), 1));
            itemPanel.setMaximumSize(new Dimension(400, 80));
            itemPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel nameLabel = new JLabel(p.name + " - " + p.price);
            nameLabel.setFont(uiFont);
            nameLabel.setBorder(new EmptyBorder(5, 10, 5, 10));

            JButton removeBtn = new JButton("Remove");
            styleButton(removeBtn);
            removeBtn.setBackground(new Color(200, 60, 60)); // red-ish remove
            removeBtn.addActionListener(e -> {
                cart.remove(p);
                cartFrame.dispose();
                openCartWindow();
            });

            itemPanel.add(nameLabel, BorderLayout.CENTER);
            itemPanel.add(removeBtn, BorderLayout.EAST);

            panel.add(itemPanel);
            panel.add(Box.createVerticalStrut(10));

            String priceNum = p.price.replaceAll("[^0-9]", "");
            total += Integer.parseInt(priceNum);
        }

        JLabel totalLbl = new JLabel("Total: " + total + " DA");
        totalLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLbl.setForeground(new Color(30, 50, 80));
        totalLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        totalLbl.setBorder(new EmptyBorder(10,0,10,0));

        panel.add(totalLbl);

        // Checkout button 
        JButton checkoutBtn = new JButton("Checkout");
        styleButton(checkoutBtn);

        int finalTotal = total; // capture current total
        checkoutBtn.addActionListener(ev -> {
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(cartFrame, "Your cart is empty.");
                return;
            }
            // open prettier checkout form
            openCheckoutForm(finalTotal, cartFrame);
        });

        panel.add(Box.createVerticalStrut(10));
        panel.add(checkoutBtn);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        cartFrame.add(scrollPane);
        cartFrame.setVisible(true);
    }

    // Session 6: Checkout Form 
    private void openCheckoutForm(int totalAmount, JFrame parentFrame) {

        JFrame checkout = new JFrame("Checkout");
        checkout.setSize(420, 480);
        checkout.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JLabel t = new JLabel("Complete your order");
        t.setFont(uiFontBold);
        t.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField addressField = new JTextField();

        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        phoneField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        addressField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        panel.add(t);
        panel.add(Box.createVerticalStrut(12));

        panel.add(new JLabel("Full Name"));
        panel.add(nameField);
        panel.add(Box.createVerticalStrut(8));

        panel.add(new JLabel("Phone Number"));
        panel.add(phoneField);
        panel.add(Box.createVerticalStrut(8));

        panel.add(new JLabel("Email"));
        panel.add(emailField);
        panel.add(Box.createVerticalStrut(8));

        panel.add(new JLabel("Shipping Address"));
        panel.add(addressField);
        panel.add(Box.createVerticalStrut(12));

        JLabel totalLabel = new JLabel("Order total: " + totalAmount + " DA");
        totalLabel.setFont(uiFontBold);
        totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(totalLabel);
        panel.add(Box.createVerticalStrut(12));

        JButton confirmBtn = new JButton("Confirm Order");
        styleButton(confirmBtn);
        confirmBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        confirmBtn.addActionListener(e -> {

            if (nameField.getText().trim().isEmpty()
                    || phoneField.getText().trim().isEmpty()
                    || emailField.getText().trim().isEmpty()
                    || addressField.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(checkout, "Please fill in all fields.");
                return;
            }

            if (!phoneField.getText().matches("[0-9]+")) {
                JOptionPane.showMessageDialog(checkout, "Phone number must contain digits only.");
                return;
            }

            if (!emailField.getText().matches("^.+@.+\\..+$")) {
                JOptionPane.showMessageDialog(checkout, "Invalid email address.");
                return;
            }

            saveOrderToFile(totalAmount);

            JOptionPane.showMessageDialog(checkout,
                    "Order Confirmed!\nThank you " + nameField.getText() + " ♥");

            cart.clear();
            checkout.dispose();
            parentFrame.dispose();
        });

        panel.add(confirmBtn);

        checkout.add(panel);
        checkout.setVisible(true);
    }

    //  Session 7: Save order to file
    private void saveOrderToFile(long total) {
        try (FileWriter fw = new FileWriter("orders.txt", true)) {
            fw.write("----- NEW ORDER -----\n");
            fw.write("Date: " + LocalDateTime.now() + "\n");
            for (Product p : cart) {
                fw.write(p.name + " - " + p.price + "\n");
            }
            fw.write("Total: " + total + " DA\n");
            fw.write("---------------------\n\n");
            fw.flush();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving order: " + e.getMessage());
        }
    }

    // main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FurnitureUI().setVisible(true));
    }
}

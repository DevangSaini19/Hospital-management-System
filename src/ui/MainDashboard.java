package ui;

import dao.AppointmentDAO;
import dao.PatientDAO;
import dao.DoctorDAO;
import dao.BillDAO;
import models.Appointment;
import models.Patient;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class MainDashboard extends JFrame {

    private PatientDAO patientDAO = new PatientDAO();
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private DoctorDAO doctorDAO = new DoctorDAO();
    private BillDAO billDAO = new BillDAO();
    private JLabel lblPatients, lblAppointments, lblDoctors, lblBills;
    private JLabel statusLabel;
    private JPanel allPatientsPanel;
    private JPanel allAppointmentsPanel;
    private JPanel contentArea;
    private BillingForm billingForm;
    private DoctorForm doctorForm;
    
    // Bombay Hospital Color Scheme
    private static final Color MAROON = new Color(139, 0, 0);      // #8B0000 - Deep Maroon
    private static final Color LIGHT_MAROON = new Color(180, 30, 30); // Hover effect
    private static final Color WHITE_BG = new Color(255, 255, 255);
    private static final Color LIGHT_GRAY = new Color(245, 248, 252);
    private static final Color NAVY = new Color(25, 50, 100);

    public MainDashboard() {
        setTitle("Hospital Management System — Bombay Hospital");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(WHITE_BG);

        // Professional Header Panel with Logo Area
        JPanel header = createHeaderPanel();
        add(header, BorderLayout.NORTH);

        // Professional Sidebar Navigation
        JPanel sidebar = createSidebarPanel();

        // Content Area with CardLayout
        contentArea = new JPanel(new CardLayout());
        contentArea.setBackground(LIGHT_GRAY);

        // Dashboard Stats Panel (lightweight, loads first)
        JPanel dashPanel = createDashboardPanel();
        contentArea.add(dashPanel, "📋 Dashboard");
        
        // Add Patient Form (lightweight)
        AddPatientForm addPatientForm = new AddPatientForm(patientDAO, this);
        contentArea.add(addPatientForm, "➕ Add Patient");
        
        // Add placeholder panels for All Patients and Appointments (will be populated in background)
        allPatientsPanel = createLoadingPanel("Loading Patients...");
        contentArea.add(allPatientsPanel, "👥 All Patients");
        
        allAppointmentsPanel = createLoadingPanel("Loading Appointments...");
        contentArea.add(allAppointmentsPanel, "📅 Appointments");
        
        // Book Appointment Form
        AppointmentFormWithEvents appointmentForm = new AppointmentFormWithEvents(appointmentDAO, patientDAO, this);
        contentArea.add(appointmentForm, "🕐 Book Appointment");
        
        // Doctor Management Form
        doctorForm = new DoctorForm(doctorDAO, this);
        contentArea.add(doctorForm, "👨‍⚕️ Doctor Management");
        
        // Billing Form
        billingForm = new BillingForm(billDAO, patientDAO, doctorDAO, this);
        contentArea.add(billingForm, "💳 Billing");

        add(sidebar, BorderLayout.WEST);
        add(contentArea, BorderLayout.CENTER);

        // Status Bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBackground(new Color(240, 240, 245));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));
        statusLabel = new JLabel("  ✅ Connected to MySQL Database | Total Patients: " +
                                         patientDAO.getTotalPatientCount() +
                                         " | Total Appointments: " +
                                         appointmentDAO.getTotalAppointmentCount());
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(NAVY);
        statusBar.add(statusLabel);
        add(statusBar, BorderLayout.SOUTH);

        setVisible(true);
        
        // Load heavy panels in background thread to prevent UI lag
        loadDataInBackground();
    }
    
    /**
     * Load heavy data panels in background thread
     */
    private void loadDataInBackground() {
        new Thread(() -> {
            try {
                // Load all patients panel
                JPanel patientsPanel = createAllPatientsPanel();
                SwingUtilities.invokeLater(() -> {
                    contentArea.remove(allPatientsPanel);
                    allPatientsPanel = patientsPanel;
                    contentArea.add(patientsPanel, "👥 All Patients");
                    contentArea.revalidate();
                    contentArea.repaint();
                });
                
                // Load all appointments panel
                JPanel appointmentsPanel = createAllAppointmentsPanel();
                SwingUtilities.invokeLater(() -> {
                    contentArea.remove(allAppointmentsPanel);
                    allAppointmentsPanel = appointmentsPanel;
                    contentArea.add(appointmentsPanel, "📅 Appointments");
                    contentArea.revalidate();
                    contentArea.repaint();
                });
            } catch (Exception e) {
                System.err.println("Error loading data in background: " + e.getMessage());
            }
        }).start();
    }
    
    /**
     * Create a loading placeholder panel
     */
    private JPanel createLoadingPanel(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_GRAY);
        JLabel loadingLabel = new JLabel(message, SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        loadingLabel.setForeground(NAVY);
        panel.add(loadingLabel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Create logo label with image loading and fallback
     * Loads bombayhospital.png from resources folder
     * Falls back to red circular placeholder if image not found
     */
    private JLabel createLogoLabel() {
        JLabel logoLabel = new JLabel();
        logoLabel.setPreferredSize(new Dimension(65, 65));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setVerticalAlignment(SwingConstants.CENTER);

        // Try to load the image from resources folder
        try {
            String resourcePath = System.getProperty("user.dir") + "/resources/bombayhospital.png";
            java.io.File imageFile = new java.io.File(resourcePath);
            
            if (imageFile.exists()) {
                ImageIcon icon = new ImageIcon(resourcePath);
                // Scale the image to 65x65
                Image scaledImage = icon.getImage().getScaledInstance(65, 65, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(scaledImage));
                System.out.println("✓ Logo image loaded from: " + resourcePath);
            } else {
                // Fallback: red circular placeholder
                System.out.println("⚠️  Logo image not found at: " + resourcePath);
                createFallbackLogo(logoLabel);
            }
        } catch (Exception e) {
            System.err.println("Error loading logo image: " + e.getMessage());
            createFallbackLogo(logoLabel);
        }

        return logoLabel;
    }

    /**
     * Create fallback logo if image not found
     */
    private void createFallbackLogo(JLabel logoLabel) {
        // Create a red circular placeholder with custom painting
        logoLabel.setText(null);
        logoLabel.setIcon(new javax.swing.Icon() {
            @Override
            public int getIconWidth() {
                return 65;
            }

            @Override
            public int getIconHeight() {
                return 65;
            }

            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw red circle background
                g2d.setColor(MAROON);
                g2d.fillOval(x, y, 65, 65);
                
                // Draw white border
                g2d.setColor(WHITE_BG);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(x, y, 64, 64);
                
                // Draw hospital emoji in center
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 40));
                g2d.drawString("🏥", x + 10, y + 50);
            }
        });
    }

    /**
     * Create professional header panel with hospital logo and branding
     */
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(MAROON);
        header.setPreferredSize(new Dimension(0, 80));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(100, 0, 0)));

        // Left side: Logo and Hospital Name
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        logoPanel.setOpaque(false);

        // Load actual logo image with fallback
        JLabel logoLabel = createLogoLabel();
        
        JLabel hospitalName = new JLabel("Bombay Hospital");
        hospitalName.setFont(new Font("Georgia", Font.BOLD, 26));
        hospitalName.setForeground(WHITE_BG);

        JLabel hospitalSubtitle = new JLabel("Jaipur");
        hospitalSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hospitalSubtitle.setForeground(new Color(220, 220, 220));

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);
        namePanel.add(hospitalName);
        namePanel.add(hospitalSubtitle);

        logoPanel.add(logoLabel);
        logoPanel.add(namePanel);

        // Right side: Tagline
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 25));
        rightPanel.setOpaque(false);
        JLabel tagline = new JLabel("Est. 1950 • Quality Healthcare Excellence");
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        tagline.setForeground(new Color(220, 220, 220));
        rightPanel.add(tagline);

        header.add(logoPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    /**
     * Create professional sidebar with navigation buttons
     */
    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(MAROON);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(100, 0, 0)));

        // Navigation items
        String[] menuItems = {"📋 Dashboard", "➕ Add Patient", "👥 All Patients", "📅 Appointments", "🕐 Book Appointment", "👨‍⚕️ Doctor Management", "💳 Billing"};

        for (String item : menuItems) {
            JButton btn = new JButton(item);
            btn.setMaximumSize(new Dimension(220, 55));
            btn.setMinimumSize(new Dimension(220, 55));
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            btn.setForeground(WHITE_BG);
            btn.setBackground(MAROON);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

            // Professional hover effect
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    btn.setBackground(LIGHT_MAROON);
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    btn.setBackground(MAROON);
                }
            });

            btn.addActionListener(e -> {
                CardLayout cl = (CardLayout) contentArea.getLayout();
                cl.show(contentArea, item);
                
                // Refresh BillingForm when Billing tab is selected
                if (item.equals("💳 Billing") && billingForm != null) {
                    billingForm.refreshDropdowns();
                }
            });

            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(2));
        }

        // Add vertical glue at bottom
        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    /**
     * Create professional dashboard with stats cards
     */
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_GRAY);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Title
        JLabel dashboardTitle = new JLabel("Dashboard Overview");
        dashboardTitle.setFont(new Font("Georgia", Font.BOLD, 24));
        dashboardTitle.setForeground(MAROON);
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        titlePanel.add(dashboardTitle);
        panel.add(titlePanel, BorderLayout.NORTH);

        // Stats Cards Container
        JPanel cardsContainer = new JPanel(new GridLayout(1, 4, 25, 0));
        cardsContainer.setOpaque(false);
        cardsContainer.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        lblPatients = createStatCard("👥 Total Patients", String.valueOf(patientDAO.getTotalPatientCount()), 
                                     new Color(41, 128, 185), cardsContainer);    // Professional Blue
        lblAppointments = createStatCard("📅 Today's Appointments", String.valueOf(appointmentDAO.getTotalAppointmentCount()), 
                                         new Color(230, 126, 34), cardsContainer); // Professional Orange
        lblDoctors = createStatCard("👨‍⚕️ Total Doctors", String.valueOf(doctorDAO.getTotalDoctorCount()), 
                                   new Color(46, 139, 87), cardsContainer);      // Professional Green
        lblBills = createStatCard("💳 Total Bills", String.valueOf(billDAO.getTotalBillsCount()), 
                                  new Color(155, 89, 182), cardsContainer);      // Professional Purple

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(cardsContainer, BorderLayout.NORTH);
        centerPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Create professional stat card with colored top border
     */
    private JLabel createStatCard(String title, String value, Color borderColor, JPanel container) {
        JPanel card = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded rectangle border
                int width = getWidth() - 1;
                int height = getHeight() - 1;
                g2d.setColor(new Color(200, 200, 200));
                g2d.drawRoundRect(0, 0, width, height, 8, 8);
                
                // Draw colored top border
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawLine(0, 0, getWidth(), 0);
            }
        };
        card.setBackground(WHITE_BG);
        card.setOpaque(true);
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        card.setPreferredSize(new Dimension(250, 130));

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(NAVY);

        // Value
        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("Georgia", Font.BOLD, 42));
        valLabel.setForeground(borderColor);
        valLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valLabel, BorderLayout.CENTER);
        container.add(card);

        return valLabel;
    }

    /**
     * Create all patients panel with professional table styling
     */
    private JPanel createAllPatientsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WHITE_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Heading
        JLabel heading = new JLabel("   All Registered Patients");
        heading.setFont(new Font("Georgia", Font.BOLD, 18));
        heading.setForeground(MAROON);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));
        heading.setOpaque(true);
        heading.setBackground(LIGHT_GRAY);

        JPanel headingPanel = new JPanel(new BorderLayout());
        headingPanel.setBackground(LIGHT_GRAY);
        headingPanel.add(heading, BorderLayout.WEST);
        panel.add(headingPanel, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Name", "Age", "Gender", "Blood", "Phone", "Status"};
        LinkedList<Patient> patientList = patientDAO.getAllPatients();
        patientList.sort((p1, p2) -> Integer.compare(p1.getPatientId(), p2.getPatientId()));
        Object[][] data = patientList.stream().map(p -> new Object[]{
            p.getPatientId(), p.getFullName(), p.getAge(),
            p.getGender(), p.getBloodGroup(), p.getPhone(), p.getStatus()
        }).toArray(Object[][]::new);

        JTable table = new JTable(data, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(28);
        table.setGridColor(new Color(220, 220, 220));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(MAROON);
        table.getTableHeader().setForeground(WHITE_BG);
        table.getTableHeader().setPreferredSize(new Dimension(0, 35));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Create all appointments panel with professional table styling
     */
    private JPanel createAllAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WHITE_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Heading
        JLabel heading = new JLabel("   All Appointments");
        heading.setFont(new Font("Georgia", Font.BOLD, 18));
        heading.setForeground(MAROON);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));
        heading.setOpaque(true);
        heading.setBackground(LIGHT_GRAY);

        JPanel headingPanel = new JPanel(new BorderLayout());
        headingPanel.setBackground(LIGHT_GRAY);
        headingPanel.add(heading, BorderLayout.WEST);
        panel.add(headingPanel, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Patient", "Doctor", "Date", "Time", "Department", "Status", "Token"};
        ArrayList<Appointment> appointmentList = appointmentDAO.getAllAppointments();

        Object[][] data = appointmentList.stream().map(a -> new Object[]{
            a.getAppointmentId(),
            a.getPatientName(),
            a.getDoctorName(),
            a.getAppointmentDate(),
            a.getAppointmentTime(),
            a.getDepartment(),
            a.getStatus(),
            a.getTokenNumber()
        }).toArray(Object[][]::new);

        JTable table = new JTable(data, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(28);
        table.setGridColor(new Color(220, 220, 220));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(MAROON);
        table.getTableHeader().setForeground(WHITE_BG);
        table.getTableHeader().setPreferredSize(new Dimension(0, 35));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Update patient count display
     */
    public void updatePatientCount() {
        if (lblPatients != null) {
            lblPatients.setText(String.valueOf(patientDAO.getTotalPatientCount()));
        }
        if (statusLabel != null) {
            statusLabel.setText("  ✅ Connected to MySQL Database | Total Patients: " +
                              patientDAO.getTotalPatientCount() +
                              " | Total Appointments: " +
                              appointmentDAO.getTotalAppointmentCount());
        }
        refreshAllPatientsPanel();
        revalidate();
        repaint();
    }

    /**
     * Refresh dashboard with real-time updates
     */
    public void refreshDashboard() {
        if (lblPatients != null) {
            lblPatients.setText(String.valueOf(patientDAO.getTotalPatientCount()));
        }
        if (lblAppointments != null) {
            lblAppointments.setText(String.valueOf(appointmentDAO.getTotalAppointmentCount()));
        }
        if (lblDoctors != null) {
            lblDoctors.setText(String.valueOf(appointmentDAO.getTotalDoctorCount()));
        }
        if (statusLabel != null) {
            statusLabel.setText("  ✅ Connected to MySQL Database | Total Patients: " +
                              patientDAO.getTotalPatientCount() +
                              " | Total Appointments: " +
                              appointmentDAO.getTotalAppointmentCount());
        }
        
        // Refresh tables in background to prevent lag
        new Thread(() -> {
            refreshAllPatientsPanel();
            refreshAllAppointmentsPanel();
        }).start();
    }

    /**
     * Refresh patients panel with latest data
     */
    private void refreshAllPatientsPanel() {
        if (allPatientsPanel != null && contentArea != null) {
            try {
                JPanel newPanel = createAllPatientsPanel();
                SwingUtilities.invokeLater(() -> {
                    contentArea.remove(allPatientsPanel);
                    allPatientsPanel = newPanel;
                    contentArea.add(newPanel, "👥 All Patients");
                    contentArea.revalidate();
                    contentArea.repaint();
                    System.out.println("✓ Patients panel refreshed");
                });
            } catch (Exception e) {
                System.err.println("Error refreshing patients panel: " + e.getMessage());
            }
        }
    }

    /**
     * Refresh appointments panel with latest data
     */
    private void refreshAllAppointmentsPanel() {
        if (allAppointmentsPanel != null && contentArea != null) {
            try {
                JPanel newPanel = createAllAppointmentsPanel();
                SwingUtilities.invokeLater(() -> {
                    contentArea.remove(allAppointmentsPanel);
                    allAppointmentsPanel = newPanel;
                    contentArea.add(newPanel, "📅 Appointments");
                    contentArea.revalidate();
                    contentArea.repaint();
                    System.out.println("✓ Appointments panel refreshed");
                });
            } catch (Exception e) {
                System.err.println("Error refreshing appointments panel: " + e.getMessage());
            }
        }
    }
}

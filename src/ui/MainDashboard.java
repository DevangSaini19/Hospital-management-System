package ui;

import dao.AppointmentDAO;
import dao.PatientDAO;
import models.Patient;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class MainDashboard extends JFrame {

    private PatientDAO patientDAO = new PatientDAO();
    private AppointmentDAO appointmentDAO = new AppointmentDAO();

    public MainDashboard() {
        setTitle("Hospital Management System — Bombay Hospital");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 102, 153));
        header.setPreferredSize(new Dimension(0, 70));

        JLabel title = new JLabel("  🏥 Hospital Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Bombay Hospital, Jaipur  ");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(200, 235, 255));
        subtitle.setHorizontalAlignment(SwingConstants.RIGHT);

        header.add(title, BorderLayout.WEST);
        header.add(subtitle, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(20, 40, 70));
        sidebar.setPreferredSize(new Dimension(200, 0));

        String[] menuItems = {"📋 Dashboard", "➕ Add Patient", "📅 Appointment", "👥 All Patients"};
        JPanel contentArea = new JPanel(new CardLayout());

        for (String item : menuItems) {
            JButton btn = new JButton(item);
            btn.setMaximumSize(new Dimension(200, 50));
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(20, 40, 70));
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setHorizontalAlignment(SwingConstants.LEFT);

            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    btn.setBackground(new Color(0, 102, 153));
                }
                public void mouseExited(java.awt.event.MouseEvent e) {
                    btn.setBackground(new Color(20, 40, 70));
                }
            });

            btn.addActionListener(e -> {
                CardLayout cl = (CardLayout) contentArea.getLayout();
                cl.show(contentArea, item);
            });

            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(2));
        }

        // Dashboard Stats Panel
        JPanel dashPanel = createDashboardPanel();
        AddPatientForm addPatientForm = new AddPatientForm(patientDAO, this);
        AppointmentForm appointmentForm = new AppointmentForm(appointmentDAO, patientDAO, this);
        JPanel allPatientsPanel = createAllPatientsPanel();

        contentArea.add(dashPanel, "📋 Dashboard");
        contentArea.add(addPatientForm, "➕ Add Patient");
        contentArea.add(appointmentForm, "📅 Appointment");
        contentArea.add(allPatientsPanel, "👥 All Patients");

        add(sidebar, BorderLayout.WEST);
        add(contentArea, BorderLayout.CENTER);

        // Status Bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBackground(new Color(240, 240, 240));
        JLabel statusLabel = new JLabel("  ✅ Connected to MySQL Database | Total Patients: " +
                                         patientDAO.getTotalPatientCount() +
                                         " | Total Appointments: " +
                                         appointmentDAO.getTotalAppointmentCount());
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusBar.add(statusLabel);
        add(statusBar, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 248, 252));

        // Summary cards
        JPanel cards = new JPanel(new GridLayout(1, 3, 20, 0));
        cards.setOpaque(false);
        cards.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        cards.add(createStatCard("Total Patients", String.valueOf(patientDAO.getTotalPatientCount()), new Color(0, 122, 204)));
        cards.add(createStatCard("Appointments", String.valueOf(appointmentDAO.getTotalAppointmentCount()), new Color(0, 153, 102)));
        cards.add(createStatCard("Doctors", String.valueOf(appointmentDAO.getTotalDoctorCount()), new Color(204, 102, 0)));

        panel.add(cards);
        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(200, 120));

        JLabel valLabel = new JLabel(value, SwingConstants.CENTER);
        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));
        valLabel.setForeground(Color.WHITE);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(220, 240, 255));

        card.add(valLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createAllPatientsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = {"ID", "Name", "Age", "Gender", "Blood", "Phone", "Status"};
        LinkedList<Patient> patientList = patientDAO.getAllPatients();
        // Sort by patient ID in ascending order
        patientList.sort((p1, p2) -> Integer.compare(p1.getPatientId(), p2.getPatientId()));
        Object[][] data = patientList.stream().map(p -> new Object[]{
            p.getPatientId(), p.getFullName(), p.getAge(),
            p.getGender(), p.getBloodGroup(), p.getPhone(), p.getStatus()
        }).toArray(Object[][]::new);

        // Create table with non-editable cells
        JTable table = new JTable(data, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(0, 102, 153));
        table.getTableHeader().setForeground(Color.WHITE);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JLabel heading = new JLabel("  All Registered Patients", SwingConstants.LEFT);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 16));
        heading.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(heading, BorderLayout.NORTH);
        return panel;
    }

    public void updatePatientCount() {
        // Refresh without recreating the whole window
        dispose();
        new MainDashboard();
    }

    public void refreshDashboard() {
        // Call this after adding patient/appointment to refresh counts
        dispose();
        new MainDashboard();
    }
}

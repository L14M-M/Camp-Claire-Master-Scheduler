package com.campclaire.campscheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a graphical user interface (GUI) for managing and scheduling classes. 
 * Users can input class details, view existing classes, and save or load class configurations to and from a JSON file.
 * This class extends {@link JFrame} to create the main window of the application.
 */
public class ClassInputGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField titleField;
    private JTextField restrictedPeriodsField;
    private JCheckBox doublePeriodCheckBox;
    private JCheckBox isRequiredCheckBox;
    private JCheckBox is10PlusCheckBox;
    private JCheckBox mustBeConsecutiveCheckBox;
    private JCheckBox requiresSwimLevelCheckBox;
    private JTextField singlePeriodCutoffField;
    private ArrayList<ClassClass> classList;
    private DefaultTableModel tableModel;
    private JTable classTable;

    /**
     * Constructs a new ClassInputGUI and initializes the GUI components.
     * The window allows the user to input class details, add or remove classes,
     * and save or load classes to/from a JSON file.
     */
    public ClassInputGUI() {
        classList = new ArrayList<>();
        setTitle("Class Input Manager");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 10, 5, 10);

        // Title input
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel titleLabel = new JLabel("Title:");
        titleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                titleLabel.setToolTipText("Enter the class title here (ex. Camp Craft, Birchcraft, Waterfront etc.)");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                titleLabel.setToolTipText(null);
            }
        });
        inputPanel.add(titleLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        titleField = new JTextField(20);
        inputPanel.add(titleField, gbc);

        // Restricted Periods input
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel restrictedPeriodsLabel = new JLabel("Restricted Periods:");
        restrictedPeriodsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                restrictedPeriodsLabel.setToolTipText(
                        "Enter restricted periods separated by commas. (ex. Swim Lessons is 2,3 [can't be first])");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                restrictedPeriodsLabel.setToolTipText(null);
            }
        });
        inputPanel.add(restrictedPeriodsLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        restrictedPeriodsField = new JTextField(20);
        inputPanel.add(restrictedPeriodsField, gbc);

        // Double Period checkbox
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel doublePeriodLabel = new JLabel("Double Period:");
        doublePeriodLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                doublePeriodLabel.setToolTipText("Select if class must span two periods. (ex. Sailing)");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                doublePeriodLabel.setToolTipText(null);
            }
        });
        inputPanel.add(doublePeriodLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        doublePeriodCheckBox = new JCheckBox();
        inputPanel.add(doublePeriodCheckBox, gbc);

        // Is Required checkbox
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel requiredLabel = new JLabel("Is Required: ");
        requiredLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                requiredLabel.setToolTipText("Select if class is required by some campers. (ex. Swim Lessons)");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                requiredLabel.setToolTipText(null);
            }
        });
        inputPanel.add(requiredLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        isRequiredCheckBox = new JCheckBox();
        inputPanel.add(isRequiredCheckBox, gbc);

        // Is 10 Plus checkbox
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel plusLabel = new JLabel("Is 10 Plus:");
        plusLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                plusLabel.setToolTipText("Select if class is 10 plus. (ex. Archery)");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                plusLabel.setToolTipText(null);
            }
        });
        inputPanel.add(plusLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        is10PlusCheckBox = new JCheckBox();
        inputPanel.add(is10PlusCheckBox, gbc);

        // Must Be Consecutive checkbox
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel consecutiveLabel = new JLabel("Must Be Consecutive:");
        consecutiveLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                consecutiveLabel.setToolTipText(
                        "Select if class must have consecutive periods if the class has 2+. (ex. Camp Craft)");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                consecutiveLabel.setToolTipText(null);
            }
        });
        inputPanel.add(consecutiveLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 5;
        mustBeConsecutiveCheckBox = new JCheckBox();
        inputPanel.add(mustBeConsecutiveCheckBox, gbc);

        // Requires Swim Level checkbox
        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel swimLevelLabel = new JLabel("Requires Swim Level:");
        swimLevelLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                swimLevelLabel.setToolTipText("Select if class requires swim level. (ex. Sailing)");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                swimLevelLabel.setToolTipText(null);
            }
        });
        inputPanel.add(swimLevelLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 6;
        requiresSwimLevelCheckBox = new JCheckBox();
        inputPanel.add(requiresSwimLevelCheckBox, gbc);

        // Single Period Cutoff input
        gbc.gridx = 0;
        gbc.gridy = 7;
        JLabel singlePeriodCutoffLabel = new JLabel("Single Period Cutoff:");
        singlePeriodCutoffLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                singlePeriodCutoffLabel.setToolTipText("Ideal number of campers per period for this class.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                singlePeriodCutoffLabel.setToolTipText(null);
            }
        });
        inputPanel.add(singlePeriodCutoffLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 7;
        singlePeriodCutoffField = new JTextField(20);
        inputPanel.add(singlePeriodCutoffField, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        // Add Class button
        gbc.gridx = 0;
        gbc.gridy = 8;
        JButton addButton = new JButton("Add Class");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addClass();
            }
        });
        inputPanel.add(addButton, gbc);

        // Remove Selected Class button
        gbc.gridx = 1;
        gbc.gridy = 8;
        JButton removeButton = new JButton("Remove Selected Class");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeSelectedClass();
            }
        });
        inputPanel.add(removeButton, gbc);

        // Save Classes button
        gbc.gridx = 0;
        gbc.gridy = 9;
        JButton saveButton = new JButton("Save Classes");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveToJson();
            }
        });
        inputPanel.add(saveButton, gbc);

        // Load Classes button
        gbc.gridx = 1;
        gbc.gridy = 9;
        JButton loadButton = new JButton("Load Classes");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                readFromJsonAndUpdateGUI();
            }
        });
        inputPanel.add(loadButton, gbc);

        // Add input panel to the left side of the BorderLayout
        add(inputPanel, BorderLayout.WEST);

        // Initialize and set up the table to display class details
        String[] columnNames = { "Title", "Restricted Periods", "Double Period", "Is Required", "Is 10 Plus",
                "Must Be Consecutive", "Requires Swim Level", "Single Period Cutoff", "Concurrent Restrictions" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8; // Only the Concurrent Restrictions column is editable
            }
        };

        classTable = new JTable(tableModel) {
            private static final long serialVersionUID = 1L;

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 8) {
                    return List.class; // Column 8 (Concurrent Restrictions) will display lists
                }
                return super.getColumnClass(column);
            }
        };

        TableColumn concurrentRestrictionsColumn = classTable.getColumnModel().getColumn(8);
        CheckBoxListRenderer checkBox = new CheckBoxListRenderer();
        concurrentRestrictionsColumn.setCellRenderer(checkBox);
        concurrentRestrictionsColumn.setCellEditor(new CheckBoxListEditor(classList, this));

        TableColumnModel columnModel = classTable.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            TableColumn column = columnModel.getColumn(i);
            column.setPreferredWidth(1000); // Set preferred width in pixels
        }

        JScrollPane scrollPane = new JScrollPane(classTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        add(scrollPane, BorderLayout.EAST);

        // Pack the frame to fit its components
        pack();

        // Center the frame on the screen
        setLocationRelativeTo(null);

        // Set visibility after all components are added
        setVisible(true);
    }

    /**
     * Adds a new class to the class list and updates the GUI table with the class details.
     * Validates the user input before adding the class.
     */
    private void addClass() {
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Class title is required!");
            return;
        } else if (singlePeriodCutoffField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Single period cutoff is required!");
            return;
        }

        String title = titleField.getText().trim();
        int[] restrictedPeriods = new int[0];

        if (!restrictedPeriodsField.getText().trim().isEmpty()) {
            String[] periods = restrictedPeriodsField.getText().trim().split(",");
            restrictedPeriods = new int[periods.length];
            for (int i = 0; i < periods.length; i++) {
                try {
                    restrictedPeriods[i] = Integer.parseInt(periods[i].trim());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid input for restricted periods. Please enter numbers separated by commas.");
                    return;
                }
            }
            if (doublePeriodCheckBox.isSelected() && restrictedPeriods.length < 2) {
                JOptionPane.showMessageDialog(this, "Must select two restricted periods for double period class.");
                return;
            }
        }

        boolean doublePeriod = doublePeriodCheckBox.isSelected();
        boolean isRequired = isRequiredCheckBox.isSelected();
        boolean is10Plus = is10PlusCheckBox.isSelected();
        boolean mustBeConsecutive = mustBeConsecutiveCheckBox.isSelected();
        boolean requiresSwimLevel = requiresSwimLevelCheckBox.isSelected();
        int singlePeriodCutoff;
        try {
            singlePeriodCutoff = Integer.parseInt(singlePeriodCutoffField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input for single period cutoff. Please enter a number.");
            return;
        }

        ClassClass newClass = new ClassClass(title, restrictedPeriods, doublePeriod, isRequired, is10Plus,
                mustBeConsecutive, requiresSwimLevel, singlePeriodCutoff);
        classList.add(newClass); // Add to classList first

        // Add class details to table
        List<String> emptyList = new ArrayList<>();
        tableModel
                .addRow(new Object[] { newClass.getTitle(),
                        restrictedPeriods.length > 0 ? restrictedPeriodsToString(newClass.getRestrictedPeriods())
                                : "None",
                        newClass.isDoublePeriod() ? "✓" : "", newClass.isRequired() ? "✓" : "",
                        newClass.is10Plus() ? "✓" : "", newClass.mustBeConsecutive() ? "✓" : "",
                        newClass.requiresSwimLevel() ? "✓" : "", newClass.getSinglePeriodCutoff(), emptyList });

        // Update dropdown list in the table
        TableColumn concurrentRestrictionsColumn = classTable.getColumnModel().getColumn(8);
        concurrentRestrictionsColumn.setCellEditor(new CheckBoxListEditor(classList, this));

        clearInputFields();
    }

    /**
     * Updates the concurrent restrictions for all classes based on the user's selections.
     * Clears existing restrictions and applies new ones.
     */
    void updateConcurrentRestrictions() {
        // Clear existing restrictions
        for (ClassClass class_ : classList) {
            class_.clearRestrictedConcurrentClasses();
        }

        // Update with new selections
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            @SuppressWarnings("unchecked")
            List<String> selectedItems = (List<String>) tableModel.getValueAt(i, 8);
            if (selectedItems != null && !selectedItems.isEmpty()) {
                String className = (String) tableModel.getValueAt(i, 0);
                ClassClass currentClass = classList.stream().filter(c -> c.getTitle().equals(className)).findFirst()
                        .orElse(null);

                if (currentClass != null) {
                    for (String selected : selectedItems) {
                        ClassClass selectedClass = classList.stream().filter(c -> c.getTitle().equals(selected))
                                .findFirst().orElse(null);

                        if (selectedClass != null) {
                            currentClass.addRestrictedConcurrentClass(selectedClass);
                        }
                    }
                }
            }
        }
    }

    /**
     * Reads class data from a JSON file and updates the GUI table with the loaded data.
     * Clears any existing class data before loading.
     */
    private void readFromJsonAndUpdateGUI() {
        ObjectMapper mapper = new ObjectMapper();
        File jsonFile = new File("classes.json");

        try {
            // Read JSON file and map to ArrayList of ClassClass objects
            CollectionType listType = TypeFactory.defaultInstance().constructCollectionType(ArrayList.class,
                    ClassClass.class);
            ArrayList<ClassClass> loadedClassList = mapper.readValue(jsonFile, listType);

            // Clear existing data
            classList.clear();
            tableModel.setRowCount(0);

            // Add loaded classes to classList and tableModel
            for (ClassClass loadedClass : loadedClassList) {
                classList.add(loadedClass);

                // Add row to tableModel
                List<String> emptyList = new ArrayList<>();
                tableModel.addRow(new Object[] { loadedClass.getTitle(),
                        loadedClass.getRestrictedPeriods().length > 0
                                ? restrictedPeriodsToString(loadedClass.getRestrictedPeriods())
                                : "None",
                        loadedClass.isDoublePeriod() ? "✓" : "", loadedClass.isRequired() ? "✓" : "",
                        loadedClass.is10Plus() ? "✓" : "", loadedClass.mustBeConsecutive() ? "✓" : "",
                        loadedClass.requiresSwimLevel() ? "✓" : "", loadedClass.getSinglePeriodCutoff(), emptyList });
            }
            // Update dropdown list in the table
            TableColumn concurrentRestrictionsColumn = classTable.getColumnModel().getColumn(8);
            concurrentRestrictionsColumn.setCellEditor(new CheckBoxListEditor(classList, this));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading from JSON. File not found!");
        }
    }

    /**
     * Removes the selected class from the class list and updates the GUI table.
     * Displays a message to confirm the removal.
     */
    private void removeSelectedClass() {
        int selectedRow = classTable.getSelectedRow();
        if (selectedRow != -1) {
            String selectedTitle = (String) tableModel.getValueAt(selectedRow, 0);
            classList.removeIf(c -> c.getTitle().equals(selectedTitle));
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Class removed: " + selectedTitle);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a class to remove.");
        }
    }

    /**
     * Clears all input fields in the GUI after adding a class or after clearing.
     */
    private void clearInputFields() {
        titleField.setText("");
        restrictedPeriodsField.setText("");
        doublePeriodCheckBox.setSelected(false);
        isRequiredCheckBox.setSelected(false);
        is10PlusCheckBox.setSelected(false);
        mustBeConsecutiveCheckBox.setSelected(false);
        requiresSwimLevelCheckBox.setSelected(false);
    }

    /**
     * Converts an array of restricted periods into a comma-separated string.
     *
     * @param restrictedPeriods an array of integers representing the restricted periods
     * @return a comma-separated string of restricted periods
     */
    private String restrictedPeriodsToString(int[] restrictedPeriods) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < restrictedPeriods.length; i++) {
            sb.append(restrictedPeriods[i]);
            if (i < restrictedPeriods.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     * Saves the current list of classes to a JSON file.
     * The JSON file is formatted for readability with indentation.
     */
    private void saveToJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            mapper.writeValue(new File("classes.json"), classList);
            JOptionPane.showMessageDialog(this, "Classes saved!");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving.");
        }
    }

    /**
     * The main method to launch the application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClassInputGUI());
    }
}

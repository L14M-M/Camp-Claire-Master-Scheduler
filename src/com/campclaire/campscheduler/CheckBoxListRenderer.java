package com.campclaire.campscheduler;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.List;
import java.util.ArrayList;

/**
 * A renderer for displaying a list of checkboxes in a JTable cell. It shows
 * the selected items as a comma-separated string.
 */
class CheckBoxListRenderer extends JLabel implements TableCellRenderer {
    private static final long serialVersionUID = 1L;

    /**
     * Returns the component used for drawing the cell. This method is used to
     * configure the renderer appropriately before drawing.
     *
     * @param table      the JTable that is asking the renderer to draw
     * @param value      the value of the cell to be rendered
     * @param isSelected true if the cell is to be rendered with the selection highlighted
     * @param hasFocus   if true, render cell appropriately
     * @param row        the row index of the cell being drawn
     * @param column     the column index of the cell being drawn
     * @return the component used for drawing the cell
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }
        if (value instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> restrictions = (List<String>) value;
            setText(String.join(", ", restrictions));
        } else {
            setText("");
        }
        return this;
    }
}

/**
 * An editor for handling a list of checkboxes in a JTable cell. It provides a
 * button to display a popup menu with checkboxes for selection.
 */
class CheckBoxListEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
    private static final long serialVersionUID = 1L;
    private final JPopupMenu popupMenu;
    private final JPanel panel;
    private final List<JCheckBox> checkBoxes;
    private List<String> selectedItems;
    private JButton editorButton;
    private ClassInputGUI parent;

    /**
     * Constructs a CheckBoxListEditor with the specified items and parent GUI.
     *
     * @param items  the list of ClassClass items to display as checkboxes
     * @param parent the parent GUI component to notify of updates
     */
    public CheckBoxListEditor(List<ClassClass> items, ClassInputGUI parent) {
        this.parent = parent;
        panel = new JPanel(new GridLayout(items.size(), 1));
        checkBoxes = new ArrayList<>();
        selectedItems = new ArrayList<>();
        popupMenu = new JPopupMenu();

        for (ClassClass item : items) {
            JCheckBox checkBox = new JCheckBox(item.getTitle());
            checkBox.addActionListener(this);
            checkBoxes.add(checkBox);
            panel.add(checkBox);
        }

        popupMenu.add(panel);

        editorButton = new JButton("Select");
        editorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popupMenu.show(editorButton, 0, editorButton.getHeight());
            }
        });
    }

    /**
     * Returns the current value in the editor, which is the list of selected
     * items as strings.
     *
     * @return the current value in the editor
     */
    @Override
    public Object getCellEditorValue() {
        selectedItems.clear();
        for (JCheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                selectedItems.add(checkBox.getText());
            }
        }
        parent.updateConcurrentRestrictions(); // Notify parent GUI to update
        return selectedItems;
    }

    /**
     * Sets up and returns the editor component for editing the cell value.
     *
     * @param table      the JTable that is asking the editor to edit
     * @param value      the value of the cell to be edited
     * @param isSelected true if the cell is to be rendered with the selection highlighted
     * @param row        the row index of the cell being edited
     * @param column     the column index of the cell being edited
     * @return the component for editing
     */
    @SuppressWarnings("unchecked")
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value instanceof List) {
            selectedItems = (List<String>) value;
        } else {
            selectedItems = new ArrayList<>();
        }

        for (JCheckBox checkBox : checkBoxes) {
            checkBox.setSelected(selectedItems.contains(checkBox.getText()));
        }

        return editorButton;
    }

    /**
     * Determines if the cell is editable. Returns true for all cells.
     *
     * @param anEvent the event that triggered the edit
     * @return true if the cell is editable, false otherwise
     */
    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    /**
     * Handles action events triggered by the checkboxes, stopping editing
     * when an action occurs.
     *
     * @param e the action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        fireEditingStopped();
    }
}

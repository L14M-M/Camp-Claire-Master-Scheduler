package com.campclaire.campscheduler;

import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * The ScheduleDriver class represents the main GUI application for managing
 * camp schedules. It provides functionality for adding, removing, and managing
 * campers and their class schedules.
 */
public class ScheduleDriver extends JFrame {
	private static final long serialVersionUID = 1L;
	private static ArrayList<ClassClass> classList;
	private ArrayList<Camper> camperRoster;
	private ArrayList<JTextField> classRankInputs;
	private HashMap<ClassClass, JTextField> textFieldMap;
	private JPanel infoPanel;
	private JTextField nameField, ageField, swimField;
	private JProgressBar progressBar;
	private DefaultTableModel camperTableModel;
	private JTable camperTable;
	private DefaultTableModel scheduleTableModel;
	private JTable scheduleTable;

	/**
	 * Constructs a new ScheduleDriver, setting up the GUI components and initializing
	 * the necessary data structures.
	 */
	public ScheduleDriver() {
		this.camperRoster = new ArrayList<>();
		this.classRankInputs = new ArrayList<>();
		this.textFieldMap = new HashMap<>();

		this.setTitle("Master Scheduler");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Create input fields
		this.nameField = new JTextField(20);
		this.ageField = new JTextField(2);
		this.swimField = new JTextField(1);

		// Create panel to hold input fields and buttons
		this.infoPanel = new JPanel(new GridLayout(0, 2, 5, 5));
		JLabel nameLabel = new JLabel("Name: ");
		this.infoPanel.add(nameLabel);
		this.infoPanel.add(this.nameField);
		JLabel ageLabel = new JLabel("Age: ");
		this.infoPanel.add(ageLabel);
		ageLabel.setPreferredSize(new Dimension(5, 10));
		this.infoPanel.add(this.ageField);
		JLabel swimLabel = new JLabel("Swim Level: ");
		this.infoPanel.add(swimLabel);
		swimLabel.setPreferredSize(new Dimension(5, 10));
		this.infoPanel.add(this.swimField);

		for (ClassClass class_ : classList) {
			JTextField textField = new JTextField(2);
			infoPanel.add(new JLabel(class_.getTitle()));
			infoPanel.add(textField);
			textFieldMap.put(class_, textField);
			classRankInputs.add(textField);
		}

		// Create buttons
		JButton addButton = new JButton("Add Camper");
		JButton removeButton = new JButton("Remove Camper");
		JButton saveButton = new JButton("Save Roster");
		JButton calculateButton = new JButton("Calculate Schedule");
		JButton readButton = new JButton("Import Roster");
		JButton inputClassesButton = new JButton("Input Classes");

		// Add action listeners
		addButton.addActionListener(e -> {
			if (nameField.getText().isEmpty() || ageField.getText().isEmpty() || swimField.getText().isEmpty()
					|| oneOrMoreInputsBlank()) {
				JOptionPane.showMessageDialog(ScheduleDriver.this, "One or more inputs are blank!");
			} else {
				addCamperToRoster();
			}
		});

		addButton.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					addButton.doClick(); // This simulates a button click
				}
			}
		});
		
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeSelectedCamper();
			}
		});

		saveButton.addActionListener(e -> saveRoster());

		calculateButton.addActionListener(e -> {
			Thread thread = new Thread(() -> {
				if (!camperRoster.isEmpty()) {
					mainAlgorithm();
				}
			});
			thread.start();
		});

		readButton.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser(new File("."));

			FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files", "json");
			fileChooser.setFileFilter(filter);

			int returnVal = fileChooser.showOpenDialog(null);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				importRoster(selectedFile);
			} else {
				JOptionPane.showMessageDialog(ScheduleDriver.this, "No file selected.");
			}
		});

		inputClassesButton.addActionListener(e -> {
			ClassInputGUI classGui = new ClassInputGUI();
			classGui.addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosed(java.awt.event.WindowEvent windowEvent) {
					try {
						importClasses();
						updateClassListInGUI();
					} catch (IOException e) {
						JOptionPane.showMessageDialog(ScheduleDriver.this, "Error importing classes.");
					}

				}
			});
			classGui.setVisible(true);
		});

		this.infoPanel.add(addButton);
		this.infoPanel.add(removeButton);
		this.infoPanel.add(readButton);
		this.infoPanel.add(saveButton);
		this.infoPanel.add(inputClassesButton);
		this.infoPanel.add(calculateButton);

		// Create first table and scroll pane
		String[] columnNames = { "Name", "Age", "Top Choices" };
		this.camperTableModel = new DefaultTableModel(columnNames, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		this.camperTable = new JTable(this.camperTableModel);

		JScrollPane firstScrollPane = new JScrollPane(this.camperTable);
		firstScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		firstScrollPane.setBorder(BorderFactory.createTitledBorder("Campers"));

		// Create second table and scroll pane
		String[] scheduleColumnNames = { "Name", "Period 1", "Period 2", "Period 3" };
		this.scheduleTableModel = new DefaultTableModel(scheduleColumnNames, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		this.scheduleTable = new JTable(this.scheduleTableModel);

		JScrollPane secondScrollPane = new JScrollPane(this.scheduleTable);
		secondScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		secondScrollPane.setBorder(BorderFactory.createTitledBorder("Schedules"));

		// Create a panel with BoxLayout to hold both tables
		JPanel tablePanel = new JPanel();
		tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
		tablePanel.add(firstScrollPane);
		tablePanel.add(secondScrollPane);

		// Create an outer scroll pane to hold the table panel
		JScrollPane outerScrollPane = new JScrollPane(tablePanel);
		outerScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		this.progressBar = new JProgressBar(0, 100);
		this.progressBar.setStringPainted(true);
		this.progressBar.setPreferredSize(new Dimension(300, 30));

		// Set layout and add components
		this.setLayout(new BorderLayout());
		this.add(this.infoPanel, BorderLayout.CENTER);
		this.add(outerScrollPane, BorderLayout.EAST);
		this.add(this.progressBar, BorderLayout.SOUTH);

		// Use pack to size the window
		this.pack();
		this.setLocationRelativeTo(null);
	}

	/**
	 * Clears the input fields for camper details and class rankings.
	 */
	private void clearInputs() {
		this.nameField.setText("");
		this.ageField.setText("");
		this.swimField.setText("");
		for (JTextField input : this.classRankInputs) {
			input.setText("");
		}
	}

	/**
	 * Validates that class ranks are correctly formatted and do not contain
	 * duplicates.
	 *
	 * @return true if class ranks are correctly formatted and unique
	 * @throws IOException if any of the ranks are incorrectly formatted or duplicated
	 */
	private boolean correctlyRankedClasses() throws IOException {
		ArrayList<Integer> ranks = new ArrayList<>();
		for (JTextField input : this.classRankInputs) {
			int rank = -1;
			try {
				rank = Integer.parseInt(input.getText());
			} catch (NumberFormatException e) {
				input.setText("");
				JOptionPane.showMessageDialog(ScheduleDriver.this, "One or more class ranks incorrectly formatted!");
				throw new IOException();
			}
			ranks.add(rank);
		}
		return !Utility.containsDuplicateInteger(ranks);
	}

	/**
	 * Adds a camper to the roster after validating the input fields and class ranks.
	 */
	private void addCamperToRoster() {
		String name = nameField.getText();
		int age = -1;
		try {
			age = Integer.parseInt(ageField.getText());
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(ScheduleDriver.this, "Incorrectly formatted age!");
			this.ageField.setText("");
			return;
		}
		int swimLevel = -1;
		try {
			swimLevel = Integer.parseInt(swimField.getText());
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(ScheduleDriver.this, "Incorrectly formatted swim level!");
			this.swimField.setText("");
			return;
		}
		Camper camper = new Camper(age, name, swimLevel);
		try {
			if (!this.correctlyRankedClasses()) {
				JOptionPane.showMessageDialog(ScheduleDriver.this, "One or more class ranks duplicated!");
				return;
			}
		} catch (IOException e) {
			return;
		}

		for (Map.Entry<ClassClass, JTextField> entry : textFieldMap.entrySet()) {
			camper.addClassChoice(entry.getKey(), Integer.parseInt(entry.getValue().getText().trim()));
		}
		camperRoster.add(camper);
		clearInputs();
		displayCamperList();
	}

	/**
	 * Saves the current camper roster to a JSON file selected by the user.
	 */
	private void saveRoster() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		JFileChooser fileChooser = new JFileChooser(new File("."));
		FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files", "json");
		fileChooser.setFileFilter(filter);
		fileChooser.setDialogTitle("Save Roster");

		int userSelection = fileChooser.showSaveDialog(this);

		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			try {
				mapper.writeValue(fileToSave, this.camperRoster);
				JOptionPane.showMessageDialog(this, "Roster saved!");
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Error saving roster.");
			}
		}
	}

	/**
	 * Imports the class list from a JSON file named "classes.json" and populates
	 * the classList.
	 *
	 * @throws IOException if an error occurs while reading the file
	 */
	private static void importClasses() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			classList = mapper.readValue(new File("classes.json"), new TypeReference<ArrayList<ClassClass>>() {
			});
		} catch (IOException e) {
			throw new IOException();
		}
	}

	/**
	 * Imports a camper roster from the specified JSON file.
	 *
	 * @param rosterFile the file containing the camper roster to import
	 */
	private void importRoster(File rosterFile) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			camperRoster = mapper.readValue(rosterFile, new TypeReference<ArrayList<Camper>>() {
			});
			displayCamperList();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Displays the list of campers in the camper table, sorting them by age.
	 */
	private void displayCamperList() {
		this.camperRoster.sort(new Comparator<Camper>() {
			@Override
			public int compare(Camper c1, Camper c2) {
				return c1.getAge() - c2.getAge();
			}
		});
		this.camperTableModel.setRowCount(0);
		for (Camper camper : this.camperRoster) {
			this.camperTableModel
					.addRow(new Object[] { camper.getName(), camper.getAge(), camper.getTopClassChoicesString() });
		}

	}

	/**
	 * Displays the campers' schedules in the schedule table, sorted by age.
	 */
	private void displayCamperSchedules() {
		this.camperRoster.sort(new Comparator<Camper>() {
			@Override
			public int compare(Camper c1, Camper c2) {
				return c1.getAge() - c2.getAge();
			}
		});
		for (Camper camper : this.camperRoster) {
			String[] periodTitles = new String[3];
			HashSet<ClassPeriod> currentSchedule = camper.getSchedule();
			for (ClassPeriod period : currentSchedule) {
				periodTitles[period.getPeriod() - 1] = period.getTitle();
			}
			scheduleTableModel.addRow(new Object[] { camper.getName(), periodTitles[0], periodTitles[1], periodTitles[2]

			});
		}
	}

	/**
	 * Executes the main scheduling algorithm, generating possible schedules and
	 * updating the GUI with the best schedule found.
	 */
	public void mainAlgorithm() {
		ScheduleCreator creator = new ScheduleCreator(camperRoster);
		ArrayList<Schedule> possibleSchedules = new ArrayList<Schedule>(ScheduleCreator.MAX_SCHEDULE_ATTEMPTS);
		HashSet<Integer> possibleScheduleValues = new HashSet<Integer>();
		SwingWorker<Void, Float> worker = new SwingWorker<Void, Float>() {
			@Override
			protected Void doInBackground() {
				try {
					BigDecimal maxAttempts = new BigDecimal(ScheduleCreator.MAX_SCHEDULE_ATTEMPTS);
					for (int i = 0; i < maxAttempts.intValue(); i++) {
						BigDecimal bigI = new BigDecimal(i);
						float progressValue = bigI.divide(maxAttempts).floatValue();
						this.publish(progressValue * 100);
						Schedule possible = creator.run();
						if (!possibleScheduleValues.contains(possible.getScore())) {
							possibleSchedules.add(possible);
						}
						if (i < maxAttempts.intValue() - 1) {
							creator.clearCamperScheduleAndFinalChoices();
							creator.clearEliminatedClasses();
							creator.shuffleCampers();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void process(List<Float> chunks) {
				float latestProgress = chunks.get(chunks.size() - 1);
				progressBar.setValue((int) latestProgress);
			}

			@Override
			protected void done() {
				Collections.sort(possibleSchedules);
				Schedule best = possibleSchedules.get(0);
				System.out.println(best);
				camperRoster.clear();
				for (Camper camper : best.getCampers()) {
					camperRoster.add(camper);
					if (camper.enrolledInSameClassTwice()) {
						System.out.println(camper.getName());
					}
				}
				progressBar.setValue(100);
				Collections.sort(camperRoster);
				displayCamperSchedules();
			}
		};
		worker.addPropertyChangeListener(evt -> {
		    if ("state".equals(evt.getPropertyName())) {
		        System.out.println("SwingWorker State: " + evt.getNewValue());
		    }
		});
		worker.execute();
	}

	/**
	 * Checks if one or more inputs in the class rank fields are blank.
	 *
	 * @return true if any input fields are blank, false otherwise
	 */
	private boolean oneOrMoreInputsBlank() {
		for (JTextField input : textFieldMap.values()) {
			if (input.getText().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Updates the class list in the GUI by re-adding the class fields and buttons.
	 */
	private void updateClassListInGUI() {
		this.infoPanel.removeAll();
		JLabel nameLabel = new JLabel("Name: ");
		this.infoPanel.add(nameLabel);
		this.infoPanel.add(this.nameField);
		JLabel ageLabel = new JLabel("Age: ");
		this.infoPanel.add(ageLabel);
		ageLabel.setPreferredSize(new Dimension(5, 10));
		this.infoPanel.add(this.ageField);
		JLabel swimLabel = new JLabel("Swim Level: ");
		this.infoPanel.add(swimLabel);
		swimLabel.setPreferredSize(new Dimension(5, 10));
		this.infoPanel.add(this.swimField);

		for (ClassClass class_ : classList) {
			JTextField textField = new JTextField(2);
			infoPanel.add(new JLabel(class_.getTitle()));
			infoPanel.add(textField);
			textFieldMap.put(class_, textField);
			classRankInputs.add(textField);
		}

		JButton addButton = new JButton("Add Camper");
		JButton removeButton = new JButton("Remove Camper");
		JButton saveButton = new JButton("Save Roster");
		JButton calculateButton = new JButton("Calculate Schedule");
		JButton readButton = new JButton("Import Roster");
		JButton inputClassesButton = new JButton("Input Classes");

		addButton.addActionListener(e -> {
			if (nameField.getText().isEmpty() || ageField.getText().isEmpty() || swimField.getText().isEmpty()
					|| oneOrMoreInputsBlank()) {
				JOptionPane.showMessageDialog(ScheduleDriver.this, "One or more inputs are blank!");
			} else {
				addCamperToRoster();
			}
		});

		addButton.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					addButton.doClick(); 
				}
			}
		});
		
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeSelectedCamper();
			}
		});

		saveButton.addActionListener(e -> saveRoster());

		calculateButton.addActionListener(e -> {
			Thread thread = new Thread(() -> {
				if (!camperRoster.isEmpty()) {
					mainAlgorithm();
				}
			});
			thread.start();
		});

		readButton.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser(new File("."));

			FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files", "json");
			fileChooser.setFileFilter(filter);

			int returnVal = fileChooser.showOpenDialog(null);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				importRoster(selectedFile);
			} else {
				JOptionPane.showMessageDialog(ScheduleDriver.this, "No file selected.");
			}
		});

		inputClassesButton.addActionListener(e -> {
			ClassInputGUI classGui = new ClassInputGUI();
			classGui.addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosed(java.awt.event.WindowEvent windowEvent) {
					try {
						importClasses();
						updateClassListInGUI();
					} catch (IOException e) {
						JOptionPane.showMessageDialog(ScheduleDriver.this, "Error importing classes.");
					}

				}
			});
			classGui.setVisible(true);
		});

		this.infoPanel.add(addButton);
		this.infoPanel.add(removeButton);
		this.infoPanel.add(readButton);
		this.infoPanel.add(saveButton);
		this.infoPanel.add(inputClassesButton);
		this.infoPanel.add(calculateButton);

		this.pack();
		this.setLocationRelativeTo(null);
	}


	/**
	 * Returns a copy of the class list.
	 *
	 * @return a new ArrayList containing the classes
	 */
	public static ArrayList<ClassClass> getClassList() {
		return new ArrayList<ClassClass>(classList);
	}
	
	/**
	 * Removes the selected camper from the camper roster and updates the table.
	 */
	private void removeSelectedCamper() {
		int selectedRow = camperTable.getSelectedRow();
		if (selectedRow != -1) {
			String selectedTitle = (String) camperTableModel.getValueAt(selectedRow, 0);
			this.camperRoster.removeIf(c -> c.getName().equals(selectedTitle));
			camperTableModel.removeRow(selectedRow);
			JOptionPane.showMessageDialog(this, "Camper removed: " + selectedTitle);
		} else {
			JOptionPane.showMessageDialog(this, "No camper selected!");
		}
	}
	
	/**
	 * The main method to launch the ScheduleDriver application.
	 *
	 * @param args command-line arguments (not used)
	 */
	public static void main(String[] args) {
		try {
			importClasses();
		} catch (IOException e) {

		}
		SwingUtilities.invokeLater(() -> {
			ScheduleDriver scheduleDriver = new ScheduleDriver();
			scheduleDriver.setVisible(true);
		});
	}
}

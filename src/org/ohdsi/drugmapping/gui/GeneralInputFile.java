package org.ohdsi.drugmapping.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.ohdsi.drugmapping.DrugMappingPreprocessor;

public class GeneralInputFile extends JPanel {
	private static final long serialVersionUID = 7241670501557895511L;
	
	private final int FILE_LABEL_SIZE = 260;
	
	private JPanel fileLabelPanel;
	private JCheckBox fileSelectCheckBox;
	private JLabel fileLabel;
	private JTextField fileNameField;
	private JButton fileSelectButton;
	
	public GeneralInputFile(String labelText, String extension, boolean isRequired, String defaultFileName) {
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		JPanel fileSelectLabelPanel = new JPanel(new BorderLayout());
		fileSelectLabelPanel.setMinimumSize(new Dimension(FILE_LABEL_SIZE, fileSelectLabelPanel.getHeight()));
		fileSelectLabelPanel.setPreferredSize(new Dimension(FILE_LABEL_SIZE, fileSelectLabelPanel.getHeight()));
		fileSelectCheckBox = new JCheckBox();
		fileSelectCheckBox.setSelected(true);
		fileSelectCheckBox.setEnabled(!isRequired);
		fileSelectLabelPanel.add(fileSelectCheckBox, BorderLayout.WEST);
		if ((!isRequired)) {
			DrugMappingPreprocessor.disableWhenRunning(fileSelectCheckBox);
		}
		
		fileLabelPanel = new JPanel(new BorderLayout());
		fileLabel = new JLabel(labelText + ":");
		fileLabelPanel.add(fileLabel, BorderLayout.WEST);
		
		fileSelectLabelPanel.add(fileLabelPanel, BorderLayout.CENTER);
		
		fileNameField = new JTextField();
		fileNameField.setText(defaultFileName == null ? "" : defaultFileName);
		fileNameField.setPreferredSize(new Dimension(10000, fileNameField.getHeight()));
		fileNameField.setEditable(false);

		fileSelectButton = new JButton("Select");

		add(fileSelectLabelPanel);
		add(fileNameField);
		add(new JLabel("  "));
		add(fileSelectButton);
		
		final GeneralInputFile currentInputFile = this;
		fileSelectButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectFile(currentInputFile);
			}
		});
		
		fileLabel.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (fileSelectCheckBox.isEnabled()) {
					fileSelectCheckBox.setSelected(!fileSelectCheckBox.isSelected());
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mousePressed(MouseEvent arg0) {}

			@Override
			public void mouseReleased(MouseEvent arg0) {}
			
		});
		
		DrugMappingPreprocessor.disableWhenRunning(fileSelectButton);
	}
	
	
	public void selectFile(GeneralInputFile generalFile) {
		//TODO
	}
}

package org.ohdsi.drugmapping.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ohdsi.drugmapping.DrugMappingPreprocessor;
import org.ohdsi.drugmapping.gui.files.FolderGUI;
import org.ohdsi.drugmapping.preprocessors.Preprocessor;
import org.ohdsi.drugmapping.preprocessors.aemps.AEMPS;
import org.ohdsi.drugmapping.preprocessors.laegemiddelstyrelsen.Laegemiddelstyrelsen;
import org.ohdsi.drugmapping.preprocessors.medaman.MEDAMAN;
import org.ohdsi.drugmapping.preprocessors.zindex.ZIndex;

public class MainFrame {
	
	private static final String ICON = "/org/ohdsi/drugmapping/gui/OHDSI Icon Picture 048x048.gif"; 
	
	public static int VOCABULARY_ID;
	
	public static int MINIMUM_USE_COUNT;
	public static int MAXIMUM_STRENGTH_DEVIATION;

	public static int PREFERENCE_MATCH_COMP_FORM;
	public static int PREFERENCE_MATCH_INGREDIENTS_TO_COMP;	
	public static int PREFERENCE_NON_ORPHAN_INGREDIENTS;
	public static int PREFERENCE_RXNORM;
	public static int PREFERENCE_ATC;
	public static int PREFERENCE_PRIORITIZE_BY_DATE;
	public static int PREFERENCE_PRIORITIZE_BY_CONCEPT_ID;
	public static int PREFERENCE_TAKE_FIRST_OR_LAST;
	
	public static int SAVE_DRUGMAPPING_LOG;
	public static int SUPPRESS_WARNINGS;
	
	private JFrame frame;
	private JMenuItem loadFileSettingsMenuItem;
	private JMenuItem saveFileSettingsMenuItem;
	private JMenuItem loadGeneralSettingsMenuItem ;
	private JMenuItem saveGeneralSettingsMenuItem;
	private JTabbedPane preprocessorsPane;

	List<Preprocessor> preprocessors = new ArrayList<Preprocessor>();
	

	/**
	 * Sets an icon on a JFrame or a JDialog.
	 * @param container - the GUI component on which the icon is to be put
	 */
	public static void setIcon(Object container){
		URL url = DrugMappingPreprocessor.class.getResource(ICON);
		Image img = Toolkit.getDefaultToolkit().getImage(url);
		if (container.getClass() == JFrame.class ||
				JFrame.class.isAssignableFrom(container.getClass()))
			((JFrame)container).setIconImage(img);
		else if (container.getClass() == JDialog.class  ||
				JDialog.class.isAssignableFrom(container.getClass()))
			((JDialog)container).setIconImage(img);
		else
			((JFrame)container).setIconImage(img);
	}
	
	
	public MainFrame(DrugMappingPreprocessor drugMappingPreprocessor) {
		super();
		
		// Define the available preprocessors
		preprocessors = new ArrayList<Preprocessor>();
		preprocessors.add(new ZIndex(drugMappingPreprocessor, this));
		preprocessors.add(new MEDAMAN(drugMappingPreprocessor, this));
		preprocessors.add(new AEMPS(drugMappingPreprocessor, this));
		preprocessors.add(new Laegemiddelstyrelsen(drugMappingPreprocessor, this));
		
		createInterface();
	}
	
	
	public void show() {
		frame.setVisible(true);
	}
	
	
	private JFrame createInterface() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	String busy = isBusy();
		        if (
		        		(busy == null) ||
		        		(JOptionPane.showConfirmDialog(
		        						frame, 
		        						busy + "\r\n" + "Are you sure you want to exit?", "Exit?", 
		        						JOptionPane.YES_NO_OPTION,
		        						JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
		        ) {
		            System.exit(0);
		        }
		    }
		});
		
		frame.setSize(1000, 800);
		frame.setMinimumSize(new Dimension(800, 600));
		frame.setTitle("OHDSI Drug Mapping Tool");
		MainFrame.setIcon(frame);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());
		
		JMenuBar menuBar = createMenu();
		frame.setJMenuBar(menuBar);
		DrugMappingPreprocessor.disableWhenRunning(menuBar);
		
		preprocessorsPane = new JTabbedPane();
		DrugMappingPreprocessor.disableWhenRunning(preprocessorsPane);
		
		preprocessorsPane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				enableDisableMenus();
			}
		});
		
		Collections.sort(preprocessors);
		for (Preprocessor preprocessor : preprocessors) {
			preprocessorsPane.addTab(preprocessor.getPreprocessorName(), preprocessor);
		}
		
		frame.add(preprocessorsPane, BorderLayout.CENTER);
		
		return frame;
	}
	
	private JMenuBar createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		
		loadFileSettingsMenuItem = new JMenuItem("Load File Settings");
		loadFileSettingsMenuItem.setToolTipText("Load File Settings");
		loadFileSettingsMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				getCurrentPreprocessor().loadFileSettingsFile();
			}
		});
		file.add(loadFileSettingsMenuItem);
		
		saveFileSettingsMenuItem = new JMenuItem("Save File Settings");
		saveFileSettingsMenuItem.setToolTipText("Save File Settings");
		saveFileSettingsMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				getCurrentPreprocessor().saveFileSettingsFile();
			}
		});
		file.add(saveFileSettingsMenuItem);

		loadGeneralSettingsMenuItem = new JMenuItem("Load General Settings");
		loadGeneralSettingsMenuItem.setToolTipText("Load General Settings");
		loadGeneralSettingsMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				getCurrentPreprocessor().loadGeneralSettingsFile();
			}
		});
		file.add(loadGeneralSettingsMenuItem);
		
		saveGeneralSettingsMenuItem = new JMenuItem("Save General Settings");
		saveGeneralSettingsMenuItem.setToolTipText("Save General Settings");
		saveGeneralSettingsMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				getCurrentPreprocessor().saveGeneralSettingsFile();
			}
		});
		file.add(saveGeneralSettingsMenuItem);
		
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.setToolTipText("Exit application");
		exitMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		file.add(exitMenuItem);
		
		menuBar.add(file);
		
		DrugMappingPreprocessor.disableWhenRunning(file);
		
		return menuBar;
	}
	
	
	private String isBusy() {
		String busy = null;
		if (getCurrentPreprocessor().isPreprocessing()) busy = ((busy == null) ? "" : "\r\n") + "Preprocessing " + getCurrentPreprocessor().getName() + " in progress!";
		return busy;
	}
	
	
	private void enableDisableMenus() {
		Preprocessor preprocessor = getCurrentPreprocessor();
		if (preprocessor != null) {
			loadFileSettingsMenuItem.setEnabled(preprocessor.hasFileSettings());
			saveFileSettingsMenuItem.setEnabled(preprocessor.hasFileSettings());
			loadGeneralSettingsMenuItem.setEnabled(preprocessor.hasGeneralSettings());
			saveGeneralSettingsMenuItem.setEnabled(preprocessor.hasGeneralSettings());
		}
	}
	
	
	public Preprocessor getPreprocessor(String preprocessorName) {
		preprocessorName = preprocessorName.toLowerCase();
		Preprocessor result = null;
		for (Preprocessor preprocessor : preprocessors) {
			if (preprocessor.getPreprocessorName().toLowerCase().equals(preprocessorName)) {
				result = preprocessor;
				break;
			}
		}
		return result;
	}
	
	
	public Preprocessor getCurrentPreprocessor() {
		return getPreprocessor(preprocessorsPane.getTitleAt(preprocessorsPane.getSelectedIndex()));
	}
	
	
	public boolean selectPreprocessor(String preprocessorName) {
		preprocessorName = preprocessorName.toLowerCase();
		int index = -1;
		for (int tabNr = 0; tabNr < preprocessorsPane.getTabCount(); tabNr++) {
			if (preprocessorsPane.getTitleAt(tabNr).toLowerCase().equals(preprocessorName)) {
				index = tabNr;
				break;
			}
		}
		if (index != -1) {
			preprocessorsPane.setSelectedIndex(index);
			enableDisableMenus();
		}
		
		return (index != -1);
	}

	
	public void loadFileSettingsFile(List<String> fileSettings) {
		Preprocessor preprocessor = getPreprocessor(preprocessorsPane.getTitleAt(preprocessorsPane.getSelectedIndex()));
		if (preprocessor != null) {
			preprocessor.loadFileSettingsFile(fileSettings);
		}
	}

	
	public void loadGeneralSettingsFile(List<String> generalSettings) {
		Preprocessor preprocessor = getPreprocessor(preprocessorsPane.getTitleAt(preprocessorsPane.getSelectedIndex()));
		if (preprocessor != null) {
			preprocessor.loadGeneralSettingsFile(generalSettings);
		}
	}
	
	
	public List<String> readSettingsFromFile(String settingsFileName, boolean mandatory) {
		List<String> settings = null;
		Preprocessor preprocessor = getPreprocessor(preprocessorsPane.getTitleAt(preprocessorsPane.getSelectedIndex()));
		if (preprocessor != null) {
			settings = preprocessor.readSettingsFromFile(settingsFileName, mandatory);
		}
		return settings;
	}
	
	
	public JFrame getFrame() {
		return frame;
	}
	
	
	public FolderGUI getOutputFolder() {
		return getCurrentPreprocessor().getOutputFolder();
	}
	
	
	public String getOutputFileName() {
		return getCurrentPreprocessor().getOutputFileName();
	}
	
	
	public void setLogFile(String logFile) {
		getCurrentPreprocessor().setLogFile(logFile);
	}
	
	
	public void closeLogFile() {
		getCurrentPreprocessor().closeLogFile();
	}

}

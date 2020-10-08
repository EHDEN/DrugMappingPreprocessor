package org.ohdsi.drugmapping.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import org.ohdsi.drugmapping.DrugMappingPreprocessor;
import org.ohdsi.drugmapping.zindex.ZIndexTab;

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
	
	private DrugMappingPreprocessor drugMappingPreprocessor;
	private JFrame frame;
	JMenuItem loadFileSettingsMenuItem;
	JMenuItem saveFileSettingsMenuItem;
	JMenuItem loadGeneralSettingsMenuItem ;
	JMenuItem saveGeneralSettingsMenuItem;
	private JTabbedPane tabbedPane;

	private Map<String, MainFrameTab> tabs = new HashMap<String, MainFrameTab>();
	

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
		this.drugMappingPreprocessor = drugMappingPreprocessor;
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
		
		tabbedPane = new JTabbedPane();
		DrugMappingPreprocessor.disableWhenRunning(tabbedPane);
		
		addTab("ZIndex", new ZIndexTab(drugMappingPreprocessor, this));
		
		frame.add(tabbedPane, BorderLayout.CENTER);
		
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
// TODO				getCurrentTab().loadFileSettingsFile();
			}
		});
		file.add(loadFileSettingsMenuItem);
		
		saveFileSettingsMenuItem = new JMenuItem("Save File Settings");
		saveFileSettingsMenuItem.setToolTipText("Save File Settings");
		saveFileSettingsMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				getCurrentTab().saveFileSettingsFile();
			}
		});
		file.add(saveFileSettingsMenuItem);

		loadGeneralSettingsMenuItem = new JMenuItem("Load General Settings");
		loadGeneralSettingsMenuItem.setToolTipText("Load General Settings");
		loadGeneralSettingsMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO				getCurrentTab().loadGeneralSettingsFile();
			}
		});
		file.add(loadGeneralSettingsMenuItem);
		
		saveGeneralSettingsMenuItem = new JMenuItem("Save General Settings");
		saveGeneralSettingsMenuItem.setToolTipText("Save General Settings");
		saveGeneralSettingsMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				getCurrentTab().saveGeneralSettingsFile();
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
		/* TODO
		if (GenericMapping.isMapping)              busy = ((busy == null) ? "" : "\r\n") + "Mapping in progress!";
		if (GenericMapping.isSavingDrugMapping)    busy = ((busy == null) ? "" : "\r\n") + "Saving Mapping in progress!";
		if (GenericMapping.isSavingDrugMappingLog) busy = ((busy == null) ? "" : "\r\n") + "Saving Drug Mapping Log in progress!";
		*/
		return busy;
	}
	
	
	private void addTab(String tabName, MainFrameTab tab) {
		tabbedPane.addTab(tabName, tab);
		tabs.put(tabName.toLowerCase(), tab);
	}
	
	
	private void enableDisableMenus(String tabName) {
		MainFrameTab tab = tabs.get(tabName.toLowerCase());
		if (tab != null) {
			loadFileSettingsMenuItem.setEnabled(tab.hasFileSettings());
			saveFileSettingsMenuItem.setEnabled(tab.hasFileSettings());
			loadGeneralSettingsMenuItem.setEnabled(tab.hasGeneralSettings());
			saveGeneralSettingsMenuItem.setEnabled(tab.hasGeneralSettings());
		}
	}
	
	
	public MainFrameTab getTab(String tabName) {
		tabName = tabName.toLowerCase();
		return tabs.get(tabName);
	}
	
	
	public MainFrameTab getCurrentTab() {
		return getTab(tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()));
	}
	
	
	public boolean selectTab(String tabName) {
		tabName = tabName.toLowerCase();
		int index = -1;
		for (int tabNr = 0; tabNr < tabbedPane.getTabCount(); tabNr++) {
			if (tabbedPane.getTitleAt(tabNr).toLowerCase().equals(tabName)) {
				index = tabNr;
				break;
			}
		}
		if (index != -1) {
			tabbedPane.setSelectedIndex(index);
			enableDisableMenus(tabName);
		}
		
		return (index != -1);
	}

	
	public void loadFileSettingsFile(List<String> fileSettings) {
		MainFrameTab tab = getTab(tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()));
		if (tab != null) {
			tab.loadFileSettingsFile(fileSettings);
		}
	}

	
	public void loadGeneralSettingsFile(List<String> generalSettings) {
		MainFrameTab tab = getTab(tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()));
		if (tab != null) {
			tab.loadGeneralSettingsFile(generalSettings);
		}
	}
	
	
	public List<String> readSettingsFromFile(String settingsFileName, boolean mandatory) {
		List<String> settings = null;
		MainFrameTab tab = getTab(tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()));
		if (tab != null) {
			settings = tab.readSettingsFromFile(settingsFileName, mandatory);
		}
		return settings;
	}
	
	
	public JFrame getFrame() {
		return frame;
	}
	
	
	public Folder getOutputFolder() {
		return getCurrentTab().getOutputFolder();
	}
	
	
	public String getOutputFileName() {
		return getCurrentTab().getOutputFileName();
	}
	
	
	public void setLogFile(String logFile) {
		getCurrentTab().setLogFile(logFile);
	}
	
	
	public void closeLogFile() {
		getCurrentTab().closeLogFile();
	}

}

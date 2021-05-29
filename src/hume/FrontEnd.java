package hume;

//import java.applet.*;
//import java.awt.*;
//import java.awt.event.*;
 
import java.util.*;
import java.awt.Image;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import org.math.plot.*;


 
//import org.math.array.*;
//import org.math.plot.*;
 
/**
 * BSD License
 * @author Yann RICHET
 */
 
public class FrontEnd implements IOutputServices, ListSelectionListener, Runnable {
	private JFrame mainWindow;
    private JPanel ctrlPanel;
    private JScrollPane listScrollPane;
    private JTextPane logPane;
    DefaultStyledDocument log;
    int     logLevel = 100; // the higher the more logging occurs
    private JPanel logPanel;
    private JScrollPane logScrollPane; 
    private JPanel mainPanel;
    private JMenuBar menuBar;
    private JProgressBar progressBar;
    private JTextPane reportPane;
    HTMLDocument report;
    private JPanel reportPanel;
    private JScrollPane reportScrollPane;
    private JButton resetButton;
    private JButton saveButton;
    private JList simulationList;
    private JPanel simulationPanel;
    private JSplitPane splitPane;
    private JButton startButton;
    private JLabel statusAnimationLabel;
    private JLabel statusMessageLabel;
    private JPanel statusPanel;
    private JButton stepButton;
    private JButton stopButton;
    private JTabbedPane viewPanel;	

    private JDialog aboutBox;
    
    HashMap<IView, JPanel> views;
    HashMap<String, SimulationDefinition> setups;
    ISimulation            currentSimulation = null;
    String                 currentSimName = "";
    
    class Canvas extends JPanel {
    	private IView view;
        public Canvas(IView myview) {
            setBorder(BorderFactory.createLineBorder(Color.black));
            this.view = myview;
        }
        public void changeView(IView newView) {
        	this.view = newView;
        }
        public Dimension getPreferredSize() {
            return new Dimension(600,600);
        }
        public void paintComponent(Graphics g) {
            super.paintComponent(g);       
            view.paint((Graphics2D)g);
        }  
    }
    
	public FrontEnd (HashMap<String, SimulationDefinition> predefinedSetups)  {
		if (predefinedSetups != null) setups = predefinedSetups;
		else setups = new HashMap<String, SimulationDefinition>();
		views = new HashMap<IView, JPanel>();
		SwingUtilities.invokeLater(this); // alt: .invokeAndWait
	}
	
	public void addView(IView view) {
		JPanel panel;
		if (view instanceof IPlotter) {
			panel = new Plot2DPanel();
			((Plot2DPanel)panel).addLegend("SOUTH");
			((Plot2DPanel)panel).removePlotToolBar();
			((IPlotter)view).assignPlotter((Plot2DPanel)panel);
		} else panel = new Canvas(view);
		panel.setSize(600, 600);
		panel.setPreferredSize(new Dimension(600, 600));		
        panel.setName(view.getName());		
        viewPanel.addTab(view.getName(), panel);          
		assert !views.containsKey(view);
		views.put(view, panel);
	}
	
	public void removeView(IView view) {
		viewPanel.remove(views.get(view));
	}
	
	public void repaint(IView view) {
		views.get(view).repaint();	
	}
	
	// logging
	public void addLogText(String str) {
		try {
			log.insertString(log.getEndPosition().getOffset(), str, SimpleAttributeSet.EMPTY);
		} catch (BadLocationException x) { System.out.println ("BadLocationException!"); };
	}

	public void clearLogText() {
		try {
			log.remove(0, log.getEndPosition().getOffset()-1);
		} catch (BadLocationException x) { System.out.println ("BadLocationException!"); };
	}	
	
	public void setLogLevel(short level) {
		logLevel = level ;
	}
	
	public void addLogText(String str, short level) {
		if (level < logLevel) addLogText(str);
	}
	
	// report generationh
	public void addChapter(String heading) {
	}
	
	public void addText(String htmlText) {

	}
	
	public void addImage(Image image) {
		
	}

	public void valueChanged(ListSelectionEvent e) {
		String key = (String) simulationList.getSelectedValue();
		if (key != currentSimName) {
			if (currentSimulation != null) {
				currentSimulation.terminate();
				for (IView v : new HashSet<IView>(views.keySet())) {
					viewPanel.remove(views.get(v));
					views.remove(v);
				}
			}
			currentSimulation = new Simulation(setups.get(key), this);
			currentSimName = key;
		}
		IView mv = null;
		for (IView v: views.keySet()) {
			if (v instanceof MatchingView) mv = v;
		}
		if (mv != null && views.containsKey(mv)) viewPanel.setSelectedComponent(views.get(mv));
	}
	
	public void createGUI() {
		mainWindow = new JFrame("HUME 1.0");
		mainWindow.setSize(1000, 720);
		mainWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // alt: EXIT_ON_CLOSE
        mainWindow.setLocationRelativeTo(null);		
        
        mainPanel = new javax.swing.JPanel();
        splitPane = new javax.swing.JSplitPane();
        listScrollPane = new javax.swing.JScrollPane();
        simulationList = new javax.swing.JList();
        simulationPanel = new javax.swing.JPanel();
        ctrlPanel = new javax.swing.JPanel();
        stepButton = new javax.swing.JButton();
        startButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        viewPanel = new javax.swing.JTabbedPane();  
        
        reportPanel = new javax.swing.JPanel();
        reportScrollPane = new javax.swing.JScrollPane();
        reportPane = new javax.swing.JTextPane();
        report = new HTMLDocument();
        reportPane.setDocument(report);
        
        logPanel = new javax.swing.JPanel();
        logScrollPane = new javax.swing.JScrollPane();
        logPane = new javax.swing.JTextPane();
        log = new DefaultStyledDocument();
        logPane.setDocument(log);
        
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        final String[] setupList = new String[setups.keySet().size()];
        int i = 0; for (String key: setups.keySet()) setupList[i++] = key;
        simulationList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = setupList;
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        simulationList.setName("simulationList");
        simulationList.addListSelectionListener(this);
        listScrollPane.setViewportView(simulationList);

        splitPane.setDividerLocation(120);
        splitPane.setLeftComponent(listScrollPane);
        simulationPanel.setName("simulationPanel");
        ctrlPanel.setName("ctrlPanel");

        stepButton.setText("step >");    stepButton.setName("stepButton"); 
        startButton.setText("start >>"); startButton.setName("startButton"); 
        stopButton.setText("stop .");    stopButton.setName("stopButton");
        resetButton.setText("reset #");  resetButton.setName("resetButton"); 
        saveButton.setText("save...");   saveButton.setName("saveButton");
        
        startButton.setEnabled(false);
        stopButton.setEnabled(false);
        saveButton.setEnabled(false);
        
        stepButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if (currentSimulation != null) currentSimulation.step();
        	}
        });

        resetButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if (currentSimulation != null) currentSimulation.reset();
        	}
        });        
        
        javax.swing.GroupLayout ctrlPanelLayout = new javax.swing.GroupLayout(ctrlPanel);
        ctrlPanel.setLayout(ctrlPanelLayout);
        ctrlPanelLayout.setHorizontalGroup(
            ctrlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ctrlPanelLayout.createSequentialGroup()
                .addComponent(startButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stepButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stopButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resetButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 249, Short.MAX_VALUE)
                .addComponent(saveButton))
        );
        ctrlPanelLayout.setVerticalGroup(
            ctrlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ctrlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(stepButton)
                .addComponent(stopButton)
                .addComponent(resetButton)
                .addComponent(saveButton))
        );

        viewPanel.setName("viewPanel");
        reportPanel.setName("reportPanel"); 
        reportScrollPane.setName("reportScrollPane"); 
        reportPane.setName("reportPane");
        reportScrollPane.setViewportView(reportPane);

        logPanel.setName("logPanel");
        logScrollPane.setName("logScrollPane");
        logPane.setName("logPane");
        logScrollPane.setViewportView(logPane); 
        
//        matchingScrollPane.setName("matchingScrollPane");
//        matchingPane.setName("matchingPane");
//        matchingScrollPane.setViewportView(matchingPane);
        
        javax.swing.GroupLayout reportPanelLayout = new javax.swing.GroupLayout(reportPanel);
        reportPanel.setLayout(reportPanelLayout);
        reportPanelLayout.setHorizontalGroup(
            reportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(reportScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 544, Short.MAX_VALUE)
        );
        reportPanelLayout.setVerticalGroup(
            reportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(reportScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
        );

      
        viewPanel.addTab("Report", reportPanel);
        viewPanel.addTab("Log", logPanel);
        
        javax.swing.GroupLayout logPanelLayout = new javax.swing.GroupLayout(logPanel);
        logPanel.setLayout(logPanelLayout);
        logPanelLayout.setHorizontalGroup(
            logPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(logScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 544, Short.MAX_VALUE)
        );
        logPanelLayout.setVerticalGroup(
            logPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(logScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
        );

//        javax.swing.GroupLayout matchingPanelLayout = new javax.swing.GroupLayout(matchingPanel);
//        matchingPanel.setLayout(matchingPanelLayout);
//        matchingPanelLayout.setHorizontalGroup(
//            matchingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//            .addComponent(matchingScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 544, Short.MAX_VALUE)
//        );
//        matchingPanelLayout.setVerticalGroup(
//            matchingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//            .addComponent(matchingScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
//        );        
        
        javax.swing.GroupLayout simulationPanelLayout = new javax.swing.GroupLayout(simulationPanel);
        simulationPanel.setLayout(simulationPanelLayout);
        simulationPanelLayout.setHorizontalGroup(
            simulationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ctrlPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(viewPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 552, Short.MAX_VALUE)
        );
        simulationPanelLayout.setVerticalGroup(
            simulationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, simulationPanelLayout.createSequentialGroup()
                .addComponent(viewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ctrlPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        splitPane.setRightComponent(simulationPanel);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 582, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE))
        );

        menuBar.setName("menuBar"); 
        fileMenu.setText("File"); fileMenu.setName("fileMenu"); 
        // exitMenuItem.setAction(actionMap.get("quit")); 
        exitMenuItem.setName("exitMenuItem"); 
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        
        helpMenu.setText("Help"); helpMenu.setName("helpMenu");
        // aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem");
        helpMenu.add(aboutMenuItem);
        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N
        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N
        statusMessageLabel.setName("statusMessageLabel"); // NOI18N
        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N
        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 582, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 398, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );

        mainWindow.getContentPane().add(mainPanel);
        mainWindow.setJMenuBar(menuBar);
        // setStatusBar(statusPanel);		
        mainWindow.pack();
        mainWindow.setVisible(true);
	}
	
	public void run() {
		createGUI();
	}
 
//	public double[][] datas;
// 
//	String[] plots = new String[] { "scatter plot", "bar plot", "line plot", "staircase plot", "box plot", "histogram" };
// 
//	JComboBox choice;
// 
//	Plot2DPanel plotpanel;
// 
//	JPanel panel;
// 
//	//Initialiser le composant
//	public void init() {
//		choice = new JComboBox(plots);
//		plotpanel = new Plot2DPanel();
//		plotpanel.addLegend("SOUTH");
//		plotpanel.setSize(600, 600);
//		plotpanel.setPreferredSize(new Dimension(600, 600));
// 
//		choice.addActionListener(this);
// 
//		panel = new JPanel(new BorderLayout());
//		panel.add(choice, BorderLayout.NORTH);
//		panel.add(plotpanel, BorderLayout.CENTER);
// 
//		this.add(panel);
// 
//	}
// 
//	public void actionPerformed(ActionEvent actionEvent) {
//		int N = 20;
//		int I = choice.getSelectedIndex();
//		plotpanel.removeAllPlots();
//		if (I == 0) {
//			double[][] data = new double[N][2];
//			for (int i = 0; i < data.length; i++) {
//				for (int j = 0; j < data[i].length; j++) {
//					data[i][j] = Math.random();
//				}
//			}
//			plotpanel.addScatterPlot("data", data);
//		} else if (I == 1) {
//			double[][] data = new double[N][2];
//			for (int i = 0; i < data.length; i++) {
//				for (int j = 0; j < data[i].length; j++) {
//					data[i][j] = Math.random();
//				}
//			}
//			plotpanel.addBarPlot("data", data);
//		} else if (I == 2) {
//			double[][] data = new double[N][2];
//			for (int i = 0; i < data.length; i++) {
//				data[i][0] = i + 1;
//				data[i][1] = Math.sin((double) i / N * Math.PI);
//			}
//			plotpanel.addLinePlot("data", data);
//		} else if (I == 3) {
//			double[][] data = new double[N][2];
//			for (int i = 0; i < data.length; i++) {
//				data[i][0] = i + 1;
//				data[i][1] = Math.sin((double) i / N * Math.PI);
//			}
//			plotpanel.addStaircasePlot("data", data);
//		} else if (I == 4) {
//			double[][] data = new double[N][4];
//			for (int i = 0; i < data.length; i++) {
//				for (int j = 0; j < 2; j++) {
//					data[i][j] = Math.random();
//				}
//				data[i][2] = 0.1;
//				data[i][3] = 0.2;
//			}
//			plotpanel.addBoxPlot("data", data);
//		} else if (I == 5) {
//			double[] data = StatisticSample.randomLogNormal(50 * N, 5, 10);
//			plotpanel.addHistogramPlot("data", data, N);
//		}
//	}
 
}

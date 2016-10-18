package gui;
/*
 * CanopyAssimilationTestArea.java
 * 
 * This is the interface for Sunlit/Shaded Canopy Assimilation Module
 *
 * @author Dairui Chen
 */
    
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.*;
import function.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.jfree.data.xy.XYSeriesCollection;


public class CanopyAssimilation extends JPanel implements ActionListener, ItemListener {
	   
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LabelTextfieldGroup tf1;
    private JRadioButton rb1, rb2;
    private CheckBoxGroup cb;
    private JButton C3,start,saveR,parameterfile;
   
    //constructor
    public CanopyAssimilation() {
        
        Border loweredbevel=BorderFactory.createLoweredBevelBorder();
        Border raisedbevel=BorderFactory.createRaisedBevelBorder();
        
        JPanel north=new JPanel(new FlowLayout());
        north.setBorder(loweredbevel);
        
        String title1[]={"Latitude (degree)  ", "Longitude (degree)  ", "Atmospheric O2 (mmol.mol-1)  ",
                         "Atmospheric CO2 (ppm) ", "Year of Simulation (eg. 2014) ", "Day    (1-365)        ",
                          "Start hour (hour) ", "Finish hour (hour) ", 
                         "Interval (hour)  "};
        double default1[]={52,0,210,360,2014,190,0,23.99,1};
        tf1=new LabelTextfieldGroup(9,title1,default1);
        north.add(tf1.createHorizontalLabelTextfieldGroup());
   
        String title2[]={"Canopy assimilation rate (umol.m-2.s-1)", 
                         "Sunlit leaves assimilation rate (umol.m-2.s-1)", 
                         "Shaded leaves assimilation rate (umol.m-2.s-1)",
                     //    "Sunlit leaves nitrogen content (g/m2)", 
                     //    "Shaded leaves nitrogen content (g/m2)", 
                         "LAI of shaded leaves (m2 leaf.m-2)",
                         "LAI of sunlit leaves (m2 leaf.m-2)", 
                         "Average stomatal conductance (mol H2O.m-2.s-1)"};     
        cb =new CheckBoxGroup(6,title2);
        north.add(cb.createCheckBoxGroup());
        cb.setDefault(0);
        cb.setDefault(1);
        cb.setDefault(2);
        
        JPanel center=new JPanel(new FlowLayout());
        center.setBorder(loweredbevel);
        
        JLabel axis=new JLabel("X axis");
        center.add(axis);
        
        ButtonGroup b = new ButtonGroup();
        Box box = Box.createVerticalBox();
        rb1 = new JRadioButton("Time (hrs)");
        rb1.setSelected(true);
        b.add(rb1);
        box.add(rb1);
        
        rb2 = new JRadioButton("Time (days)");
        rb2.setSelected(false);
        b.add(rb2);
     //   box.add(rb2);     
        center.add(box);
            		  
        C3=new JButton("   C3   ");
        Font f1=new Font("Times New Roman", Font.BOLD, 20);
        C3.setFont(f1);
        C3.setBorder(raisedbevel);
        C3.addActionListener(this);
        
        start=new JButton("   Start   ");
        start.setFont(f1);
        start.setBorder(raisedbevel);
        start.addActionListener(this);

        saveR=new JButton("Save Results");
        saveR.setFont(f1);
        saveR.setBorder(raisedbevel);
        saveR.addActionListener(this);
        
        parameterfile=new JButton("Parameter File");
        parameterfile.setFont(f1);
        parameterfile.setBorder(raisedbevel);
        parameterfile.addActionListener(this);
        
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)          
                .addComponent(north)
                .addComponent(center)
                .addGroup(layout.createSequentialGroup()    
                    .addComponent(C3)
                    .addComponent(start) 
                    .addComponent(saveR)
                    .addComponent(parameterfile)
                )
        );
        
        layout.setVerticalGroup(layout.createSequentialGroup()         
                .addComponent(north)
                .addComponent(center)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                  .addComponent(C3)
                  .addComponent(start)
                  .addComponent(saveR)
                  .addComponent(parameterfile)
                )
         );      
             
    }
    
    public JMenuBar getMenuBar() {      
        MenuBarGroup bar=new MenuBarGroup();
        return bar.createMenuBar();
    }
    
    public void calculation() {

    	double Lat = Double.valueOf(tf1.tfHorizontal[0].getText());           
        double Logi = Double.valueOf(tf1.tfHorizontal[1].getText());
        Location lct = new Location();
        lct.Latitude = Lat;
        lct.Longitude = Logi;
        double h_start = Double.valueOf(tf1.tfHorizontal[6].getText());
        double h_end =  Double.valueOf(tf1.tfHorizontal[7].getText());
        double h_step = Double.valueOf(tf1.tfHorizontal[8].getText());
        
        CurrentTime ct  = new CurrentTime();
        double year = Double.valueOf(tf1.tfHorizontal[4].getText());
        ct.year = (int) year;
        double day = Double.valueOf(tf1.tfHorizontal[5].getText());
        ct.day = (int) day;
        
        double O2 = Double.valueOf(tf1.tfHorizontal[2].getText());
        double CO2 = Double.valueOf(tf1.tfHorizontal[3].getText());
        
        RunCanopyModel rcm = new RunCanopyModel();
        final XYSeriesCollection c = new XYSeriesCollection();
        final XYSeriesCollection c2 = new XYSeriesCollection();
        final XYSeriesCollection c3 = new XYSeriesCollection();
        final XYSeriesCollection c4 = new XYSeriesCollection();

       	Environment env = new Environment(ct,lct);
        env.air.O2_concentration = O2;
        env.air.CO2_concentration = CO2;
      	CanopyRes mcr = rcm.all_time_curve(ct, lct, env, h_start, h_end, h_step);
        
      	boolean showFig1 = false;
      	if(cb.cb[0].isSelected()){
      		c.addSeries(mcr.xys_Ac);
      		showFig1 = true;
      	}
      	if(cb.cb[1].isSelected()){
      		c.addSeries(mcr.xys_Ac_sunlit); showFig1 = true;
      	}
      	if(cb.cb[2].isSelected()){
      		c.addSeries(mcr.xys_Ac_shaded);	showFig1 = true;
      	}
      	if (showFig1){
      		JFrame result = new Graph(c,"Sunlit/shaded Canopy Assimilation Module","Time (hours)","A (umol.m-2.s-1)");
            result.setSize(400, 400);
            result.setVisible(true);
      	}
      	
      	
//      	boolean showFig2 = false;
//      	if(cb.cb[3].isSelected()){
//      		c2.addSeries(mcr.xys_N_sunlit);	showFig2 = true;
//      	}
//      	if(cb.cb[4].isSelected()){
//      		c2.addSeries(mcr.xys_N_shaded);	showFig2 = true;
//      	}
//      	if (showFig2){
//      		JFrame result = new Graph(c2,"Sunlit/shaded Canopy Assimilation Module","Time (hours)","N (g/m2)");
//            result.setSize(400, 400);
//            result.setVisible(true);
//      	}
      	
      	boolean showFig3 = false;
      	if(cb.cb[3].isSelected()){
      		c3.addSeries(mcr.xys_LAI_sunlit); showFig3 = true;	
      	}
      	if(cb.cb[4].isSelected()){
      		c3.addSeries(mcr.xys_LAI_shaded);	showFig3 = true;
      	}
      	if(showFig3){
      		JFrame result = new Graph(c3,"Sunlit/shaded Canopy Assimilation Module","Time (hours)","LAI (m2.m-2)");
            result.setSize(400, 400);
            result.setVisible(true);
      	}
      	
      	
      	if(cb.cb[5].isSelected()){
      		c4.addSeries(mcr.xys_conductance);
      		JFrame result = new Graph(c4,"Sunlit/shaded Canopy Assimilation Module","Time (hours)","Average stomatal conductance (mol.m-2.s-1)");
            result.setSize(400, 400);
            result.setVisible(true);
      	}
      	

        
        try {
             PrintWriter pw1=new PrintWriter(new OutputStreamWriter(new FileOutputStream("WIMOVAC_OutputFile_CanopyAssimilation.csv")),true);              
              pw1.println("Time (hour), Canopy assimilation rate (umol.m-1.s-1),Sunlit leaves assimilation rate (umol.m-1.s-1), Shaded leaves assimilation rate (umol.m-1.s-1), LAI at sunlit leaves (m2.m-2),LAI at shaded leaves (m2.m-2), Average stomatal conductance (mol.m-2.s-1)");
             int number=mcr.xys_Ac.getItemCount();
             for (int i=0; i<number; i++){
                  double avggs = mcr.xys_conductance.getY(i).doubleValue()/1000;
                  pw1.println(mcr.xys_Ac.getX(i)+","+mcr.xys_Ac.getY(i)+","+mcr.xys_Ac_shaded.getY(i)+","+mcr.xys_Ac_sunlit.getY(i)+","+mcr.xys_LAI_sunlit.getY(i)+","+mcr.xys_LAI_shaded.getY(i)+","+avggs);
             }
             
             
            
        }
         catch (IOException o) { 
                             // catch io errors from FileInputStream 
              System.out.println("Uh oh, got an IOException error!" + o.getMessage()); 
         }
    } 
     //handle action events from all the components
    public void actionPerformed(ActionEvent e) {
           String text = (String)e.getActionCommand();
           if (text.equals("   C3   ")) {
              C3.setText("   C4   ");
              Constants.C3orC4 ="C4";

           }
           if (text.equals("   C4   ")) {
              C3.setText("   C3   ");
              Constants.C3orC4 ="C3";
           }
           if (text.equals("   Start   ")) {
        	   
        	   
        	   if (isAllFilled()){ 
        		   calculation();
        	   }else{
    		   JOptionPane.showMessageDialog(null, "No parameters for model ! \n You Can OPEN a parameter file from WIMVOAC or directly input from 'Parameter File'");
        	   }
           }    
           if (text.equals("Save Results")) {


            	 //SAVE to a user choose file. 
        	    	JFileChooser fc;
        	    	if(WIMOVAC.ResultDirOpened){
        	    		fc = new JFileChooser(WIMOVAC.ResultDir);
        	    	}else{
        	    		String current="";
        				try {
        					current = new File( "." ).getCanonicalPath();
        				} catch (IOException e1) {
        					// TODO Auto-generated catch block
        					e1.printStackTrace();
        					fc = new JFileChooser();
        				}
            			fc = new JFileChooser(current);
        	    	}

              	   FileNameExtensionFilter filter = new FileNameExtensionFilter(
              		        ".csv", "csv");
              	   fc.setFileFilter(filter);
              	   int returnVal = fc.showSaveDialog(getParent());
              	   if(returnVal == JFileChooser.APPROVE_OPTION) {
              	       System.out.println("You chose to open this file: " +
              	            fc.getSelectedFile().getAbsoluteFile());
              	       
              	       String Absolutefilename = fc.getSelectedFile().getAbsolutePath();
              	       if(!Absolutefilename.endsWith(".csv")){
              	    	   Absolutefilename = Absolutefilename.concat(".csv");
              	    	   
              	       }
              	     WIMOVAC.ResultDir = fc.getSelectedFile().getParent();
            	     WIMOVAC.ResultDirOpened = true;
              	     try {
    					copy("WIMOVAC_OutputFile_CanopyAssimilation.csv",Absolutefilename);
    				} catch (IOException e3) {
    					// TODO Auto-generated catch block
    					e3.printStackTrace();
    				}

              	    }
            	   
               }
               
               // QIngfeng add
           if (text.equals("Parameter File")) {
               ParameterFile pf=new ParameterFile(2);
               pf.customerFrame();
           }
           
    }
    //handle item change events from all the components
    public void itemStateChanged(ItemEvent e) {
         
                
    }

    
    /*
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public void createAndShowGUI() {
        //Use the Java look and feel
        try {
            UIManager.setLookAndFeel(
                UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) { }
    
        JFrame frame = new JFrame("Sunlit/Shaded Canopy Assimilation Module");
    //    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Create and set up the content pane.
        CanopyAssimilation newContentPane = new CanopyAssimilation();
        frame.setJMenuBar(newContentPane.getMenuBar());      
        frame.setContentPane(newContentPane);         
       
        //Display the window
        frame.pack();
        frame.setLocationRelativeTo(null); //center it
        frame.setSize(650,450);
        frame.setVisible(true);
    }   
    
    public static void copy(String sourcePath, String destinationPath) throws IOException {
    	File f3 = new File(destinationPath);
    	FileOutputStream fs = new FileOutputStream(f3);
        Files.copy(Paths.get(sourcePath), fs);
        fs.close();
    }
    private boolean isAllFilled(){
    	if (WIMOVAC.constants.isEmpty())
    		return false;
    	else
    		return true;
    }
    
     
}
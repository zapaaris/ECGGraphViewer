package ecgviewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

public class ECGCharts implements Initializable{

	  final int WINDOW_SIZE = 15;
	  private ScheduledExecutorService scheduledExecutorService;
	
	@FXML private TextField jTextFieldFileName;
	@FXML private Button btnBrowse,btnStartStop,btnPauseResume;
	@FXML private GridPane gridPaneCharts;
	//
    private String filePath="";
    ArrayList<Reading> readings=null;
    int readingNo=0;
    int wavePartNo=0;
    boolean paused=false;
    // charts
    LineChart<Number, Number> lineChartI,lineChartII,lineChartIII,lineChartAvl,lineChartAvr,lineChartAvf,lineChartV1,lineChartV2,lineChartV3,lineChartV4,lineChartV5,lineChartV6;
    XYChart.Series<Number, Number> series1,series2,series3,seriesAvl,seriesAvr,seriesAvf,seriesV1,seriesV2,seriesV3,seriesV4,seriesV5,seriesV6;
    @FXML
    public void browseFile(ActionEvent event) {
    	final FileChooser fileChooser = new FileChooser();
    	File file = fileChooser.showOpenDialog(null);
        if (file != null) {
        	jTextFieldFileName.setText(file.getName());
        	filePath=file.getAbsolutePath();
                btnBrowse.setDisable(true);
                btnStartStop.setDisable(false);
        	//System.out.println(filePath);        	
        }
        else {
        	jTextFieldFileName.setText(""); 
        	filePath="";
        	setupCharts();
                btnStartStop.setDisable(true);
                btnPauseResume.setDisable(true);
        }
    	
//    	processData(filePath);
    }
    
    @FXML
    public void startStop(ActionEvent event) {
        if(btnStartStop.getText().equals("Start"))
        {
                setupCharts();
                readingNo=0;
                wavePartNo=0;
        	if(processData(filePath))
                {
                    btnStartStop.setText("Stop");
                    btnBrowse.setDisable(true);
                    btnPauseResume.setDisable(false);
                }else{
                    btnBrowse.setDisable(false);
                    btnStartStop.setDisable(true);
                    btnPauseResume.setDisable(true);
                }                       
        }
        else {
                scheduledExecutorService.shutdownNow();
                setupCharts();
                readingNo=0;
                wavePartNo=0;
                btnStartStop.setText("Start");
                btnBrowse.setDisable(false);
                btnStartStop.setDisable(false);
                btnPauseResume.setDisable(true);
                paused=false;
                btnPauseResume.setText("Pause");
        }
    }
    
    @FXML
    public void pauseResume(ActionEvent event) {
        if(btnPauseResume.getText().equals("Pause"))
        {
            btnPauseResume.setText("Resume");
            paused=true;
        }
        else{
            btnPauseResume.setText("Pause");
            paused=false;
        }        
    }
	
	private boolean processData(String filePath2) {
		readings.clear();
                boolean ret=false;
		try{
			FileReader fr = new FileReader (filePath2);
			BufferedReader br = new BufferedReader (fr);
			//read the line form file
			String line = br.readLine();
			while (line != null)
			{
				if(line.startsWith("[{id"))
				{
					line=line.replace("[{", "");
					line=line.replace("}}]", "");
					line=line.replace("amplitude={", "");
					String tokens[]=line.split(",");
					Reading r=new Reading();
					
					r.setId(Integer.parseInt(tokens[0].replace("id=", "")));
					r.setTitle(tokens[1].replace("title=", "").replace("\"", ""));
					r.setStart_time(Double.parseDouble(tokens[2].replace("start_time=", "")));
					r.setEnd_time(Double.parseDouble(tokens[3].replace("end_time=", "")));
					r.setAssigned_duration(Double.parseDouble(tokens[4].replace("assigned_duration=", "")));
					r.setCalculated_duration(Double.parseDouble(tokens[5].replace("calculated_duration=", "")));
					
					r.setI(Double.parseDouble(tokens[6].replace("I=", "")));
					if(r.getTitle().equals("Q wave") || r.getTitle().equals("S wave")) r.setI(Double.parseDouble(tokens[6].replace("I=", "-")));
					r.setII(Double.parseDouble(tokens[7].replace("~","-").replace("II=", "")));
					r.setIII(Double.parseDouble(tokens[8].replace("~","-").replace("III=", "")));
					
					r.setaVR(Double.parseDouble(tokens[9].replace("~","-").replace("aVR=", "")));
					r.setaVL(Double.parseDouble(tokens[10].replace("~","-").replace("aVL=", "")));
					r.setaVF(Double.parseDouble(tokens[11].replace("~","-").replace("aVF=", "")));

					r.setV1(Double.parseDouble(tokens[12].replace("~","-").replace("V1=", "")));
					r.setV2(Double.parseDouble(tokens[13].replace("~","-").replace("V2=", "")));
					r.setV3(Double.parseDouble(tokens[14].replace("~","-").replace("V3=", "")));
					r.setV4(Double.parseDouble(tokens[15].replace("~","-").replace("V4=", "")));
					r.setV5(Double.parseDouble(tokens[16].replace("~","-").replace("V5=", "")));
					r.setV6(Double.parseDouble(tokens[17].replace("~","-").replace("V6=", "")));
					readings.add(r);
//					System.out.println(r.getTitle());
				}
				
				line = br.readLine();
				}
			}catch( IOException ex) {
			System.out.println(ex);ret=false;
			}
		if(!readings.isEmpty())	{
                    ret=true;
                    runCharts();                    
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Invalid File for ECG Readings.");
                    ret=false;
                    alert.showAndWait();
                }
                return ret;
	}

	private void runCharts() {
		
	scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            // Update the chart
            Platform.runLater(() -> {
                    if(paused)
                        return;
                    if(readingNo>readings.size()){ this.scheduledExecutorService.shutdownNow();return;}
           	 //+++++++++++
         		XYChart.Data<Number, Number> data = new LineChart.Data<>(readings.get(readingNo).getStart_time(), 0);	    
         		if(wavePartNo%3==0)
         		{
         			series1.getData().add(data);
         			series2.getData().add(data);
         			series3.getData().add(data);
         			seriesAvl.getData().add(data);
         			seriesAvr.getData().add(data);
         			seriesAvf.getData().add(data);
                                seriesV1.getData().add(data);
                                seriesV2.getData().add(data);
                                seriesV3.getData().add(data);
                                seriesV4.getData().add(data);
                                seriesV5.getData().add(data);
                                seriesV6.getData().add(data);
         		}
         		double xc=((readings.get(readingNo).getEnd_time()-readings.get(readingNo).getStart_time())/2)+readings.get(readingNo).getStart_time();
         		//I , II, II, aVL, aVR, aVF 
         		double yc=0;
         		if(wavePartNo%3==1)
         		{
         			yc=readings.get(readingNo).getI();
         			data = new LineChart.Data<>(xc, yc);
         			series1.getData().add(data);
         			
         			yc=readings.get(readingNo).getII();
         			data = new LineChart.Data<>(xc, yc);
         			series2.getData().add(data);
         			
         			yc=readings.get(readingNo).getIII();
         			data = new LineChart.Data<>(xc, yc);
         			series3.getData().add(data);
         			
         			yc=readings.get(readingNo).getaVL();
         			data = new LineChart.Data<>(xc, yc);
         			seriesAvl.getData().add(data);
         			
         			yc=readings.get(readingNo).getaVR();
         			data = new LineChart.Data<>(xc, yc);
         			seriesAvr.getData().add(data);
         			
         			yc=readings.get(readingNo).getaVF();
         			data = new LineChart.Data<>(xc, yc);
         			seriesAvf.getData().add(data);
                                
                                yc=readings.get(readingNo).getV1();
         			data = new LineChart.Data<>(xc, yc);
         			seriesV1.getData().add(data);
                                
                                 yc=readings.get(readingNo).getV2();
         			data = new LineChart.Data<>(xc, yc);
         			seriesV2.getData().add(data);
                                
                                yc=readings.get(readingNo).getV3();
         			data = new LineChart.Data<>(xc, yc);
         			seriesV3.getData().add(data);
                                
                                yc=readings.get(readingNo).getV4();
         			data = new LineChart.Data<>(xc, yc);
         			seriesV4.getData().add(data);
                                
                                yc=readings.get(readingNo).getV5();
         			data = new LineChart.Data<>(xc, yc);
         			seriesV5.getData().add(data);
                                
                                yc=readings.get(readingNo).getV6();
         			data = new LineChart.Data<>(xc, yc);
         			seriesV6.getData().add(data);
         		}
         		
         		data = new LineChart.Data<>(readings.get(readingNo).getEnd_time(), 0);	    
         		if(wavePartNo%3==2)
         		{
         			series1.getData().add(data);
         			series2.getData().add(data);
         			series3.getData().add(data);
         			seriesAvl.getData().add(data);
         			seriesAvr.getData().add(data);
         			seriesAvf.getData().add(data);
         			seriesV1.getData().add(data);
         			seriesV2.getData().add(data);
         			seriesV3.getData().add(data);
         			seriesV4.getData().add(data);
         			seriesV5.getData().add(data);
         			seriesV6.getData().add(data);
         		}
         		double lb=readings.get(readingNo).getStart_time();
         		double ub=readings.get(readingNo).getEnd_time();
         		
            	lineChartI.getXAxis().setAutoRanging(false);
            	((ValueAxis<Number>) lineChartI.getXAxis()).setLowerBound(lb-150);
            	((ValueAxis<Number>) lineChartI.getXAxis()).setUpperBound(ub+150);
            	lineChartII.getXAxis().setAutoRanging(false);
            	((ValueAxis<Number>) lineChartII.getXAxis()).setLowerBound(lb-150);
            	((ValueAxis<Number>) lineChartII.getXAxis()).setUpperBound(ub+150);
            	lineChartIII.getXAxis().setAutoRanging(false);
            	((ValueAxis<Number>) lineChartIII.getXAxis()).setLowerBound(lb-150);
            	((ValueAxis<Number>) lineChartIII.getXAxis()).setUpperBound(ub+150);
            	lineChartAvf.getXAxis().setAutoRanging(false);
            	((ValueAxis<Number>) lineChartAvf.getXAxis()).setLowerBound(lb-150);
            	((ValueAxis<Number>) lineChartAvf.getXAxis()).setUpperBound(ub+150);
            	lineChartAvl.getXAxis().setAutoRanging(false);
            	((ValueAxis<Number>) lineChartAvl.getXAxis()).setLowerBound(lb-150);
            	((ValueAxis<Number>) lineChartAvl.getXAxis()).setUpperBound(ub+150);
            	lineChartAvr.getXAxis().setAutoRanging(false);
            	((ValueAxis<Number>) lineChartAvr.getXAxis()).setLowerBound(lb-150);
            	((ValueAxis<Number>) lineChartAvr.getXAxis()).setUpperBound(ub+150);
            	lineChartV1.getXAxis().setAutoRanging(false);
            	((ValueAxis<Number>) lineChartV1.getXAxis()).setLowerBound(lb-150);
            	((ValueAxis<Number>) lineChartV1.getXAxis()).setUpperBound(ub+150);
            	lineChartV2.getXAxis().setAutoRanging(false);
            	((ValueAxis<Number>) lineChartV2.getXAxis()).setLowerBound(lb-150);
            	((ValueAxis<Number>) lineChartV2.getXAxis()).setUpperBound(ub+150);
            	lineChartV3.getXAxis().setAutoRanging(false);
            	((ValueAxis<Number>) lineChartV3.getXAxis()).setLowerBound(lb-150);
            	((ValueAxis<Number>) lineChartV3.getXAxis()).setUpperBound(ub+150);
            	lineChartV4.getXAxis().setAutoRanging(false);
            	((ValueAxis<Number>) lineChartV4.getXAxis()).setLowerBound(lb-150);
            	((ValueAxis<Number>) lineChartV4.getXAxis()).setUpperBound(ub+150);
            	lineChartV5.getXAxis().setAutoRanging(false);
            	((ValueAxis<Number>) lineChartV5.getXAxis()).setLowerBound(lb-150);
            	((ValueAxis<Number>) lineChartV5.getXAxis()).setUpperBound(ub+150);
            	lineChartV6.getXAxis().setAutoRanging(false);
            	((ValueAxis<Number>) lineChartV6.getXAxis()).setLowerBound(lb-150);
            	((ValueAxis<Number>) lineChartV6.getXAxis()).setUpperBound(ub+150);
           	 //+++++++++++++
            	lineChartI.setTitle("I ("+readings.get(readingNo).getTitle()+")");
            	lineChartII.setTitle("I ("+readings.get(readingNo).getTitle()+")");
            	lineChartIII.setTitle("I ("+readings.get(readingNo).getTitle()+")");
            	lineChartAvf.setTitle("aVF ("+readings.get(readingNo).getTitle()+")");
            	lineChartAvl.setTitle("aVL ("+readings.get(readingNo).getTitle()+")");
            	lineChartAvr.setTitle("aVR ("+readings.get(readingNo).getTitle()+")");
                lineChartV1.setTitle("V1 ("+readings.get(readingNo).getTitle()+")");
                lineChartV2.setTitle("V2 ("+readings.get(readingNo).getTitle()+")");
                lineChartV3.setTitle("V3 ("+readings.get(readingNo).getTitle()+")");
                lineChartV4.setTitle("V4 ("+readings.get(readingNo).getTitle()+")");
                lineChartV5.setTitle("V5 ("+readings.get(readingNo).getTitle()+")");
                lineChartV6.setTitle("V6 ("+readings.get(readingNo).getTitle()+")");
            	
            	if(wavePartNo%3==2)
            		readingNo++;
            	wavePartNo++;
              
              
              if (series1.getData().size() > WINDOW_SIZE)  series1.getData().remove(0);
              if (series2.getData().size() > WINDOW_SIZE)  series2.getData().remove(0);
              if (series3.getData().size() > WINDOW_SIZE)  series3.getData().remove(0);
              if (seriesAvf.getData().size() > WINDOW_SIZE)  seriesAvf.getData().remove(0);
              if (seriesAvl.getData().size() > WINDOW_SIZE)  seriesAvl.getData().remove(0);
              if (seriesAvr.getData().size() > WINDOW_SIZE)  seriesAvr.getData().remove(0);
              if (seriesV1.getData().size() > WINDOW_SIZE)  seriesV1.getData().remove(0);
              if (seriesV2.getData().size() > WINDOW_SIZE)  seriesV2.getData().remove(0);
              if (seriesV3.getData().size() > WINDOW_SIZE)  seriesV3.getData().remove(0);
              if (seriesV4.getData().size() > WINDOW_SIZE)  seriesV4.getData().remove(0);
              if (seriesV5.getData().size() > WINDOW_SIZE)  seriesV5.getData().remove(0);
              if (seriesV6.getData().size() > WINDOW_SIZE)  seriesV6.getData().remove(0);
            });
          }, 0, 400, TimeUnit.MILLISECONDS);
		
	}

	private void setupCharts() {
		
            gridPaneCharts.getChildren().clear();
            if(scheduledExecutorService!=null)
		scheduledExecutorService.shutdownNow();
		//+++++++++++++++ I Chart Start+++++++++++++++            
	    final NumberAxis xAxis1 = new NumberAxis(); 
	    final NumberAxis yAxis1 = new NumberAxis(-6.0,6.0, 0.5);
	    xAxis1.setLabel("Time");
	    yAxis1.setLabel("Amplitude");
	    lineChartI = new LineChart<>(xAxis1, yAxis1);
	    lineChartI.setTitle("I");
	    lineChartI.setCreateSymbols(false);

            series1 = new LineChart.Series<>();
            series1.setName("I");
	    lineChartI.getData().add(series1);
	    gridPaneCharts.add(lineChartI, 0, 0, 1, 1);
		//+++++++++++++++ I Chart End  +++++++++++++++

		//+++++++++++++++ II Chart Start+++++++++++++++
	     final NumberAxis xAxis2 = new NumberAxis(); 
	     final NumberAxis yAxis2 = new NumberAxis(-7.0,7.0, 0.5);
	     xAxis2.setLabel("Time");
	     yAxis2.setLabel("Amplitude");
	     lineChartII = new LineChart<>(xAxis2, yAxis2);
	     lineChartII.setTitle("II");
	     lineChartII.setCreateSymbols(false);

	     series2 = new LineChart.Series<>();
		 series2.setName("II");
		 lineChartII.getData().add(series2);
	        
	     gridPaneCharts.add(lineChartII, 0, 1, 1, 1);
		//+++++++++++++++ II Chart End  +++++++++++++++
	     
		//+++++++++++++++ III Chart Start+++++++++++++++
	     final NumberAxis xAxis3 = new NumberAxis(); 
	     final NumberAxis yAxis3 = new NumberAxis(-6.0,6.0, 0.5);
	     xAxis3.setLabel("Time");
	     yAxis3.setLabel("Amplitude");
	     lineChartIII = new LineChart<>(xAxis3, yAxis3);
	     lineChartIII.setTitle("III");
	     lineChartIII.setCreateSymbols(false);

	     series3 = new LineChart.Series<>();
		 series3.setName("III");
		 lineChartIII.getData().add(series3);
	        
	     gridPaneCharts.add(lineChartIII, 0, 2, 1, 1);
		//+++++++++++++++ III Chart End  +++++++++++++++
	     
		//+++++++++++++++ aVL Chart Start+++++++++++++++
	     final NumberAxis xAxis4 = new NumberAxis(); 
	     final NumberAxis yAxis4 = new NumberAxis(-6.0,6.0, 0.5);
	     xAxis4.setLabel("Time");
	     yAxis4.setLabel("Amplitude");
	     lineChartAvl = new LineChart<>(xAxis4, yAxis4);
	     lineChartAvl.setTitle("aVL");
	     lineChartAvl.setCreateSymbols(false);

	     seriesAvl = new LineChart.Series<>();
		 seriesAvl.setName("aVL");
		 lineChartAvl.getData().add(seriesAvl);
	        
	     gridPaneCharts.add(lineChartAvl, 1, 0, 1, 1);
		//+++++++++++++++ aVL Chart End  +++++++++++++++
	     
		//+++++++++++++++ aVR Chart Start+++++++++++++++
	     final NumberAxis xAxis5 = new NumberAxis(); 
	     final NumberAxis yAxis5 = new NumberAxis(-6.0,6.0, 0.5);
	     xAxis5.setLabel("Time");
	     yAxis5.setLabel("Amplitude");
	     lineChartAvr = new LineChart<>(xAxis5, yAxis5);
	     lineChartAvr.setTitle("aVR");
	     lineChartAvr.setCreateSymbols(false);

	     seriesAvr = new LineChart.Series<>();
		 seriesAvr.setName("aVR");
		 lineChartAvr.getData().add(seriesAvr);
	        
	     gridPaneCharts.add(lineChartAvr, 1, 1, 1, 1);
		//+++++++++++++++ aVR Chart End  +++++++++++++++
	     
		//+++++++++++++++ aVF Chart Start+++++++++++++++
	     final NumberAxis xAxis6 = new NumberAxis(); 
	     final NumberAxis yAxis6 = new NumberAxis(-6.0,6.0, 0.5);
	     xAxis6.setLabel("Time");
	     yAxis6.setLabel("Amplitude");
	     lineChartAvf = new LineChart<>(xAxis6, yAxis6);
	     lineChartAvf.setTitle("aVF");
	     lineChartAvf.setCreateSymbols(false);

	     seriesAvf = new LineChart.Series<>();
		 seriesAvf.setName("aVF");
		 lineChartAvf.getData().add(seriesAvf);
	        
	     gridPaneCharts.add(lineChartAvf, 1, 2, 1, 1);
		//+++++++++++++++ aVF Chart End  +++++++++++++++
                
                //+++++++++++++++ V1 Chart Start+++++++++++++++            
	    final NumberAxis xAxisV1 = new NumberAxis(); 
	    final NumberAxis yAxisV1 = new NumberAxis(-5.0,5.0, 0.5);
	    xAxisV1.setLabel("Time");
	    yAxisV1.setLabel("Amplitude");
	    lineChartV1 = new LineChart<>(xAxisV1, yAxisV1);
	    lineChartV1.setTitle("V1");
	    lineChartV1.setCreateSymbols(false);

            seriesV1 = new LineChart.Series<>();
            seriesV1.setName("V1");
	    lineChartV1.getData().add(seriesV1);
	    gridPaneCharts.add(lineChartV1, 2, 0, 1, 1);
		//+++++++++++++++ V1 Chart End  +++++++++++++++
                
                //+++++++++++++++ V2 Chart Start+++++++++++++++            
	    final NumberAxis xAxisV2 = new NumberAxis(); 
	    final NumberAxis yAxisV2 = new NumberAxis(-5.0,5.0, 0.5);
	    xAxisV2.setLabel("Time");
	    yAxisV2.setLabel("Amplitude");
	    lineChartV2 = new LineChart<>(xAxisV2, yAxisV2);
	    lineChartV2.setTitle("V2");
	    lineChartV2.setCreateSymbols(false);

            seriesV2 = new LineChart.Series<>();
            seriesV2.setName("V2");
	    lineChartV2.getData().add(seriesV2);
	    gridPaneCharts.add(lineChartV2, 2, 1, 1, 1);
		//+++++++++++++++ V2 Chart End  +++++++++++++++
                
                //+++++++++++++++ V3 Chart Start+++++++++++++++            
	    final NumberAxis xAxisV3 = new NumberAxis(); 
	    final NumberAxis yAxisV3 = new NumberAxis(-5.0,5.0, 0.5);
	    xAxisV3.setLabel("Time");
	    yAxisV3.setLabel("Amplitude");
	    lineChartV3 = new LineChart<>(xAxisV3, yAxisV3);
	    lineChartV3.setTitle("V3");
	    lineChartV3.setCreateSymbols(false);

            seriesV3 = new LineChart.Series<>();
            seriesV3.setName("V3");
	    lineChartV3.getData().add(seriesV3);
	    gridPaneCharts.add(lineChartV3, 2, 2, 1, 1);
		//+++++++++++++++ V3 Chart End  +++++++++++++++
                
                //+++++++++++++++ V4 Chart Start+++++++++++++++            
	    final NumberAxis xAxisV4 = new NumberAxis(); 
	    final NumberAxis yAxisV4 = new NumberAxis(-5.0,5.0, 0.5);
	    xAxisV4.setLabel("Time");
	    yAxisV4.setLabel("Amplitude");
	    lineChartV4 = new LineChart<>(xAxisV4, yAxisV4);
	    lineChartV4.setTitle("V4");
	    lineChartV4.setCreateSymbols(false);

            seriesV4 = new LineChart.Series<>();
            seriesV4.setName("V4");
	    lineChartV4.getData().add(seriesV4);
	    gridPaneCharts.add(lineChartV4, 3, 0, 1, 1);
		//+++++++++++++++ V4 Chart End  +++++++++++++++
                                               
                //+++++++++++++++ V5 Chart Start+++++++++++++++            
	    final NumberAxis xAxisV5 = new NumberAxis(); 
	    final NumberAxis yAxisV5 = new NumberAxis(-5.0,5.0, 0.5);
	    xAxisV5.setLabel("Time");
	    yAxisV5.setLabel("Amplitude");
	    lineChartV5 = new LineChart<>(xAxisV5, yAxisV5);
	    lineChartV5.setTitle("V5");
	    lineChartV5.setCreateSymbols(false);

            seriesV5 = new LineChart.Series<>();
            seriesV5.setName("V5");
	    lineChartV5.getData().add(seriesV5);
	    gridPaneCharts.add(lineChartV5, 3, 1, 1, 1);
		//+++++++++++++++ V5 Chart End  +++++++++++++++
                
                //+++++++++++++++ V6 Chart Start+++++++++++++++            
	    final NumberAxis xAxisV6 = new NumberAxis(); 
	    final NumberAxis yAxisV6 = new NumberAxis(-5.0,5.0, 0.5);
	    xAxisV6.setLabel("Time");
	    yAxisV6.setLabel("Amplitude");
	    lineChartV6 = new LineChart<>(xAxisV6, yAxisV6);
	    lineChartV6.setTitle("V6");
	    lineChartV6.setCreateSymbols(false);

            seriesV6 = new LineChart.Series<>();
            seriesV6.setName("V6");
	    lineChartV6.getData().add(seriesV6);
	    gridPaneCharts.add(lineChartV6, 3, 2, 1, 1);
		//+++++++++++++++ V5 Chart End  +++++++++++++++
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		filePath="C:\\Users\\Zaheer Ahmed\\Desktop\\ECG DATA.txt";
		readings=new ArrayList<Reading>();
		setupCharts();
	}


  }

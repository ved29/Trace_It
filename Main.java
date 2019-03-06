import java.util.*;
import javafx.application.Application; 
import javafx.collections.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.*;
import javafx.scene.text.*;
import javafx.geometry.*;
import javafx.event.*;

public class Main extends Application { 
	
	// VARIABLES

	double x_max = 10, x_min = -10, x_increment = 0.1;			// change these to control points
	int no_of_points = (int)((x_max - x_min)/x_increment + 1.0);// changes as per above params
	int i,m,n;													// loop counters
	double x;													// final x values to be plotted
	double[] y = new double [no_of_points];						// final y values to be plotted
	double[][] term = new double[100][no_of_points];			// supported for upto 100 terms
	double power_of_x = 0,coefficient_of_x = 0,prevTerm = 0;	// term control vars
	int term_count = 0,raised = 0, sign = 1,decimal_point = 0,decimal_part = 0;	// control triggers
	int button_number,buttonX,buttonY, curveNo = 0;							// button controls
	String string_eqn = new String("y = ");

	NumberAxis xAxis = new NumberAxis(x_min,x_max,1.0);
	NumberAxis yAxis = new NumberAxis(x_min,x_max,1.0);
	LineChart linechart = new LineChart(xAxis,yAxis);
	List<XYChart.Series> series = new ArrayList<>();
	
	
	// CALCULATE EACH TERM - GENERAL FORM - coeff * x ^ power

	public double[] calc_term()
	{
		double termPlot[] = new  double[no_of_points];
		for(x = x_min, i=0; x<= x_max; x += x_increment,i++)
			termPlot[i] = coefficient_of_x*java.lang.Math.pow(x,power_of_x);
		return termPlot;
	}

	// RESET VARIABLES AND SET CONSTANTS WHEN TERM ENDS

	public void termEnd_reset()
	{	
		term_count+=1;
		decimal_point = 0;
		power_of_x = 0;
		coefficient_of_x = 0;
		prevTerm = 0;
		raised = 0;
	}

	public void eqn_reset()
	{
		for(m=0,x=x_min ;m< no_of_points; m++,x+=x_increment)
		{
			y[m] = 0;
			for(n=0;n<=term_count;n++)
				term[n][m] = 0;
		}
		term_count = 0;
	}

	public void draw()
	{
		if(raised==1 && prevTerm==0)
			return;
		else if(raised==1)
			power_of_x = sign * prevTerm;
		else if(prevTerm!=0)
			coefficient_of_x = sign * prevTerm;
		term[term_count] = calc_term();

		series.get(curveNo).getData().clear();
		linechart.getData().removeAll(series.get(curveNo));
		for(m=0,x=x_min ;m< no_of_points; m++,x+=x_increment)
		{
			y[m] = 0;
			for(n=0;n<=term_count;n++)
				y[m] += term[n][m];
			series.get(curveNo).getData().add(new XYChart.Data(x,y[m]));
		}
		linechart.getData().add(series.get(curveNo));
		linechart.setAnimated(false);
		linechart.setCreateSymbols(false);            //disables the points	
	}

	public void start(Stage stage)
	{
		GridPane operatorGrid = new GridPane();
		GridPane numberGrid = new GridPane();  

		Text variable_txt = new Text("Variable");
		Text operator_txt = new Text("Operator");
		Text number_txt = new Text("Number");
		Text equation = new Text(string_eqn);

		Button x_btn= new Button("x"); 
		Button plus = new Button("+");
		Button minus = new Button("-");
		Button decimal= new Button(".");
		Button power = new Button("^");
		Button newCurve = new Button("add curve");
		Button[] button = new Button[10];			// button array
		Button reset = new Button("Reset");
		
		// initialize each button in array
		for(button_number = 0; button_number < button.length; button_number++)
			button[button_number] = new Button(Integer.toString(button_number));	
		
		// position of operators
		operatorGrid.add(plus,0,0);
		operatorGrid.add(minus,1,0);
		operatorGrid.add(decimal,2,0);
		operatorGrid.add(power,3,0);

		// position of number in mobile keypad form
		for(buttonY=0,button_number=1;buttonY<3;buttonY++)
			for(buttonX=0;buttonX<3;buttonX++,button_number++)
				numberGrid.add(button[button_number],buttonX,buttonY);
		numberGrid.add(button[0],1,3);
		
		// graph set-up
		xAxis.setLabel("x");
		yAxis.setLabel("y");
		linechart.setCreateSymbols(false);

		series.add(new XYChart.Series());

		// layout of all elements on page
		VBox input = new VBox(5);
		input.setPadding(new Insets(10, 10, 10, 10));
		input.getChildren().addAll(variable_txt,x_btn,operator_txt,operatorGrid,number_txt,numberGrid,reset,newCurve);   

		VBox output = new VBox(5);
		output.setPadding(new Insets(10, 10, 10, 10));
		output.getChildren().addAll(equation,linechart);

		HBox finalScreen = new HBox(5);
		finalScreen.setPadding(new Insets(10, 10, 10, 10));
		finalScreen.getChildren().addAll(input,output);

		// BUTTON CLICK EVENTS

		x_btn.setOnMouseClicked((new EventHandler<MouseEvent>() { 
			public void handle(MouseEvent event) {
				coefficient_of_x = sign * 1;
				power_of_x = 1;					//default power of x;
				
				if(prevTerm!=0)
					coefficient_of_x = sign * prevTerm;

				draw();
				prevTerm = 0;
				decimal_point = 0;
				sign = 1;
				
				string_eqn+=" x";
				series.get(curveNo).setName(string_eqn);
				equation.setText(string_eqn);	
			}
		}));

		plus.setOnMouseClicked((new EventHandler<MouseEvent>() { 
			public void handle(MouseEvent event) {
				if(!(raised==1 && prevTerm==0))
					termEnd_reset();
				sign = 1;
				string_eqn+=" +";
				series.get(curveNo).setName(string_eqn);
				equation.setText(string_eqn);		
			}
		}));

		minus.setOnMouseClicked((new EventHandler<MouseEvent>() { 
			public void handle(MouseEvent event) {
				if(!(raised==1 && prevTerm==0))
					termEnd_reset();
				sign = -1;
				string_eqn+=" -";
				series.get(curveNo).setName(string_eqn);
				equation.setText(string_eqn);
			}
		}));

		decimal.setOnMouseClicked((new EventHandler<MouseEvent>() { 
			public void handle(MouseEvent event) {
				decimal_point = 1;
				decimal_part = 0;
				string_eqn+=".";
				series.get(curveNo).setName(string_eqn);
				equation.setText(string_eqn);
			}
		}));

		power.setOnMouseClicked((new EventHandler<MouseEvent>() { 
			public void handle(MouseEvent event) {
				raised = 1;
				string_eqn+="^";
				series.get(curveNo).setName(string_eqn);
				equation.setText(string_eqn);
			}
		}));

		for(button_number = 0;button_number<button.length;button_number++){
			final Button buttonI = button[button_number];
			buttonI.setOnMouseClicked((new EventHandler<MouseEvent>() { 
				public void handle(MouseEvent event) {
							if(decimal_point==1)
							{
								decimal_part-=1;
								prevTerm+= Integer.parseInt(buttonI.getText())*java.lang.Math.pow(10,decimal_part);
							}
							else
								prevTerm = prevTerm*10 + Integer.parseInt(buttonI.getText());
							
							draw();
							string_eqn+=buttonI.getText();
							series.get(curveNo).setName(string_eqn);
							equation.setText(string_eqn);
				}
			}));	
		}

		reset.setOnMouseClicked((new EventHandler<MouseEvent>() { 
			public void handle(MouseEvent event) {			
				termEnd_reset();
				raised = 0;
				sign = 1;
				eqn_reset();
				string_eqn = "y = ";
				series.get(curveNo).setName(string_eqn);
				equation.setText(string_eqn);
				for(i=0;i<=curveNo;i++)
					series.get(i).getData().clear();
				linechart.getData().clear();
				linechart.setAnimated(false);
			}
		}));

		newCurve.setOnMouseClicked((new EventHandler<MouseEvent>() { 
			public void handle(MouseEvent event) {
				termEnd_reset();
				raised = 0;
				sign = 1;
				eqn_reset();
				series.add(new XYChart.Series());
				curveNo++;
				string_eqn = "y = ";
				series.get(curveNo).setName(string_eqn);
				equation.setText(string_eqn);
			}
		}));

		// window set-up
		Scene scene = new Scene(finalScreen,finalScreen.getPrefWidth(),finalScreen.getPrefHeight()); 
		stage.setTitle("Trace_it_!!");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String args[]){
		launch(args);
	}
}

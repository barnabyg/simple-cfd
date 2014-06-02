package com.blizzardtec.cfd;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class GaussSeidelColor extends Applet implements MouseListener, ActionListener{
    /**
     * 
     */
    private static final long serialVersionUID = -6518320878564870704L;
    private ColorPanel meshPanel;
    private ColorPanel errorPanel;
    private GaussSeidel gs;
    private MathFunction2D boundaryValues;
    private Label x_lbl;
    private Label y_lbl;
    private Label value_lbl;
    private Label iterations_lbl;
    private TextField x_txt;
    private TextField y_txt;
    private TextField value_txt;
    private TextField iterations_txt;
    private Button compute_btn;
    private Button renormalize_btn;

    public void init(){
        setLayout(new BorderLayout());

        add(createNorthPanel(), "North");
        add(createCenterPanel(), "Center");
        add(createSouthPanel(), "South");
    }

    public Panel createNorthPanel(){
        Panel northPanel = new Panel();
        northPanel.setLayout(new GridLayout(1,2));
        northPanel.add(new Label("Solution"));
        northPanel.add(new Label("Error"));
        return northPanel;
    }

    public Panel createCenterPanel(){
        Panel centerPanel = new Panel();
        centerPanel.setLayout(new GridLayout(1,2));
        boundaryValues = new MathFunction2D(){
            public double eval(double x, double y){
                return 4*(x*y)*(x-y)*(x+y);
            }
        };
        gs = new GaussSeidel(boundaryValues);
        meshPanel = new ColorPanel(gs.getMesh());
        meshPanel.addMouseListener(this);
        errorPanel = new ColorPanel(findErrorMatrix());
        errorPanel.addMouseListener(this);
        centerPanel.add(meshPanel);
        centerPanel.add(errorPanel);
        return centerPanel;
    }

    public Panel createSouthPanel(){
        Panel southPanel = new Panel();
        southPanel.setLayout(new GridLayout(3,1));

        Panel row1 = new Panel();
        x_lbl = new Label("x");
        y_lbl = new Label("y");
        value_lbl = new Label("value");
        x_txt = new TextField("",5);
        y_txt = new TextField("",5);
        value_txt = new TextField("", 17);
        row1.add(x_lbl);
        row1.add(x_txt);
        row1.add(y_lbl);
        row1.add(y_txt);
        row1.add(value_lbl);
        row1.add(value_txt);
        southPanel.add(row1);

        Panel row2 = new Panel();
        iterations_lbl = new Label("Iterations");
        iterations_txt = new TextField("", 3);
        compute_btn = new Button("Compute");
        compute_btn.addActionListener(this);
        row2.add(iterations_lbl);
        row2.add(iterations_txt);
        row2.add(compute_btn);
        southPanel.add(row2);

        Panel row3 = new Panel();
        renormalize_btn = new Button("Renormalize Color Values");
        renormalize_btn.addActionListener(this);
        row3.add(renormalize_btn);
        southPanel.add(row3);

        return southPanel;
    }

    public double [][] findErrorMatrix(){
        double [][] mesh = gs.getMesh();
        double [][] errorMatrix = new double[mesh.length][mesh[0].length];
        for(int j = 0; j<mesh[0].length; j++){
            for(int i = 0; i< mesh.length; i++){
                errorMatrix[i][j] = Math.abs(mesh[i][j] - boundaryValues.eval(gs.getXCoordinates()[i],gs.getYCoordinates()[j]));
            }
        }
        return errorMatrix;
    }

    public void computeMatrices(){
        meshPanel.repaint();
        errorPanel.setMesh(findErrorMatrix());
        errorPanel.repaint();
    }

    public void actionPerformed(ActionEvent ae){
        if(ae.getSource() == compute_btn){
            int iterations = 0;
            try{
                iterations = Integer.parseInt(iterations_txt.getText());
            }
            catch(NumberFormatException nfe){
                System.out.println("Entered value is not an integer");
            }
            gs.computeGaussSeidel(iterations);
            computeMatrices();
        }
        else if(ae.getSource() == renormalize_btn){
            errorPanel.computeColorScale();
            meshPanel.computeColorScale();
            computeMatrices();
        }
    }

    public void mouseEntered(MouseEvent me){}
    public void mouseExited(MouseEvent me){}
    public void mouseClicked(MouseEvent me){
        Point p = null;
        double value =0.0;
        if(me.getSource() == meshPanel){
            p = meshPanel.findGridNumber(me.getX(), me.getY());
            if(p != null) value = meshPanel.getMesh()[p.x][p.y];
        }
        else if(me.getSource() == errorPanel){
            p = errorPanel.findGridNumber(me.getX(), me.getY());
            if(p != null) value = errorPanel.getMesh()[p.x][p.y];
        }
        if(p!= null){
            x_txt.setText("" + gs.getXCoordinates()[p.x]);
            y_txt.setText("" + gs.getXCoordinates()[p.x]);
            value_txt.setText("" + value);
        }

    }
    public void mousePressed(MouseEvent me){}
    public void mouseReleased(MouseEvent me){}

}
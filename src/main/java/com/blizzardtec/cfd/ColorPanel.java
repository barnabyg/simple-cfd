package com.blizzardtec.cfd;

import java.awt.*;

public class ColorPanel extends Panel{
    /**
     * 
     */
    private static final long serialVersionUID = 1522850018954175817L;
    private double [][] mesh;
    private int xBorder;
    private int yBorder;
    private int gridSize;
    private boolean initialized;
    private float hue;
    private double colorScale;
    private double colorOffset;

    public ColorPanel(double [][] mesh){
        this.mesh = mesh;
        xBorder = 5;
        yBorder = 5;
        initialized = false;
        hue = 0.6f;
        computeColorScale();
    }

    public void paint(Graphics g){
        drawGrid(g);
    }

    public void drawGrid(Graphics g){
        double colorValue;
        Color color;
        if(getHeight() != 0 && getWidth() != 0){
            if(!initialized){
                computeProportions();
                initialized = true;
            }
            for(int i = 0; i< mesh.length; i++){
                for(int j = 0; j<mesh[0].length; j++){
                    colorValue = (colorScale*(mesh[i][j] - colorOffset)); //a value between 0 to 2
                    if(colorValue <= 1.0){
                        color = Color.getHSBColor(hue, 1.0f, (float)colorValue);
                    }
                    else{
                        color = Color.getHSBColor(hue, (float)(2 - colorValue), 1.0f);
                    }
                    g.setColor(color);
                    g.fillRect(i*gridSize + xBorder, getHeight() - ((j+1)*gridSize + yBorder), gridSize, gridSize);
                }
            }
        }

    }

    public Point findGridNumber(int x, int y){
        int i = (x - xBorder)/gridSize;
        int j = (getHeight() - y - yBorder)/gridSize;
        Point p = new Point(i,j);
        if(i<0 || j < 0 || i>=mesh.length || j>=mesh[0].length){
            p = null;
        }
        return p;
    }

    public double [][] getMesh(){
        return this.mesh;
    }

    public void setMesh(double [][] mesh){
        this.mesh = mesh;
    }

    public void computeColorScale(){
        double min = 0.0;
        double max = 0.0;
        for(int i = 0; i< mesh.length; i++){
            for(int j = 0; j<mesh[0].length; j++){
                if(mesh[i][j] > max){
                    max = mesh[i][j];
                }
                else if(mesh[i][j] < min){
                    min = mesh[i][j];
                }
            }
        }
        colorScale = (2.0/(max - min));
        colorOffset = min;

    }

    private void computeProportions(){
        int numPointsX = mesh.length;
        int numPointsY = mesh[0].length; // all of the columns have the same size
        gridSize = 0;
        int gridSizeX = (getWidth() - 2*xBorder)/(numPointsX); //a truncated int
        if(gridSizeX < 0){
            xBorder = 0;
            gridSizeX = getWidth()/(numPointsX); //a truncated int
        }
        int gridSizeY = (getHeight() - 2*yBorder)/(numPointsY); //a truncated int
        if(gridSizeY < 0){
            yBorder = 0;
            gridSizeY = getHeight()/(numPointsY ); //a truncated int
        }
        if(gridSizeX >= gridSizeY){
            gridSize = gridSizeY;
            xBorder = (getWidth() - (numPointsX)*gridSize)/2;
        }
        else{
            gridSize = gridSizeX;
            yBorder = (getHeight() - (numPointsY)*gridSize)/2;
        }
    }

}
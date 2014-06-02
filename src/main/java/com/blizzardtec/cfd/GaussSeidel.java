package com.blizzardtec.cfd;

/**
 * The GaussSeidel class solves Laplace's equation for a rectangular mesh
 * with *square* grids(i.e. non-weighted).The user can compute 1 or many
 * iterations of the method. The user may change the boundary conditions
 * at any time, but the user is warned that the Gauss-Seidel iteration
 * is only valid for *static* cases. Hence, if the boundary conditions
 * change, the user should attempt to change them in at least a quasi-static
 * way (i.e. in near-equilibrium conditions). This may also be used as a
 * subset of a multi-grid method.
 * <p>Title: Gauss-Seidel Iteration</p>
 * <p>Description: Numerical Computation for Laplace's equation</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: JRM Software Development</p>
 * @author Jennifer R. McFatridge
 * @version 1.0
 */

public class GaussSeidel{
    private double [] xCoordinates;
    private double [] yCoordinates;
    private double [][] mesh;
    private int numPointsX;
    private int numPointsY;
    private MathFunction2D boundaryValues;
    private double epsilon;
    private boolean isAtEquilibrium;

    /**
    * This constructs a grid of default size 1x1 with x0,y0 at the origin.
    * The grid has 20 grid points in each of 20 rows, of which 18 are interior
    * points. It has extremely uninteresting boundary values at 0 and the
    * initial value of the interior points are also 0. If these values remain
    * unchanged, the Gauss-Seidel computation will not change the values.  
    */
    public GaussSeidel(){
        //border values are uniformly 0
        boundaryValues = new MathFunction2D(){
            public double eval(double x, double y){
                return 0.0;
            }
        };
        //18 interior points
        numPointsX = 18;
        numPointsY = 18;
        //coordinate values for 20 points in a row, 20 points in a column
        xCoordinates = new double[numPointsX + 2];
        yCoordinates = new double[numPointsY + 2];
        //find the coordinates for a square of size 1 x 1 with bottom
        //left corner at 0,0
        for(int i = 0; i<=numPointsX+1; i++){
            xCoordinates[i] = i*1.0/(numPointsX + 1);
            yCoordinates[i] = i*1.0/(numPointsX + 1);
        }
        this.mesh = new double[numPointsX + 2][numPointsY + 2];
        setInitialGuess(0.0);
        computeBoundaryValues();
        isAtEquilibrium = false;
        epsilon = 0.1;

    }

    /**
    * This constructs a grid of default size 1x1 with x0,y0 at the origin.
    * The grid has 20 grid points in each of 20 rows, of which 18 are
    * interior points. The boundary values are set with the MathFunction2D.
    * The initial values of the interior points are 0.
    * @param borderValues the MathFunction2D which holds the value at the
    *  boundaries
    */
    public GaussSeidel(MathFunction2D boundaryValues){
        this.boundaryValues = boundaryValues;
        //18 interior points
        numPointsX = 18;
        numPointsY = 18;
        //coordinate values for 20 points in a row, 20 points in a column
        xCoordinates = new double[numPointsX + 2];
        yCoordinates = new double[numPointsY + 2];
        //find the coordinates for a square of size 1 x 1 with bottom left corner at 0,0
        for(int i = 0; i<=numPointsX+1; i++){
            xCoordinates[i] = i*1.0/(numPointsX + 1);
            yCoordinates[i] = i*1.0/(numPointsX + 1);
        }
        this.mesh = new double[numPointsX + 2][numPointsY + 2];
        setInitialGuess(0.0);
        computeBoundaryValues();
        isAtEquilibrium = false;
        epsilon = 0.1;
    }

    /**
    * This constructs a grid using coordinates specified by the user.
    * A printed warning is given if the shape of the resulting grid
    * is not composed of squares (the overall grid may be rectangular
    * as long as the composing shapes are square). However, no exception
    * is thrown in the event of non-square grids. The performance loss
    * for a try/catch block is deemed more important than the possible
    * incorrect result that would result for non-square grids. Hence, this
    * constructor is Use At Own Risk.
    * The initial values of the interior points are 0.
    * @param boundaryValues the MathFunction2D which holds the value at
    *       the boundaries
    * @param xCoordinates the x-coordinates of the grid points
    * @param yCoordinates the y-coordinates of the grid points
    */
    public GaussSeidel(MathFunction2D boundaryValues, double [] xCoordinates,
            double [] yCoordinates){
        this.boundaryValues = boundaryValues;
        this.xCoordinates = xCoordinates;
        this.yCoordinates = yCoordinates;
        this.numPointsX = xCoordinates.length - 2;
        this.numPointsY = yCoordinates.length - 2;
        double width = Math.abs(xCoordinates[numPointsX + 1] - xCoordinates[0]);
        double height = Math.abs(yCoordinates[numPointsY + 1] - yCoordinates[0]);
        if( Math.abs(width/numPointsX - height/numPointsY) > 0.1){
            System.out.println("Error: grids not square. Computations are incorrect.");
        }
        this.mesh = new double[numPointsX + 2][numPointsY + 2];
        setInitialGuess(0.0);
        computeBoundaryValues();
        isAtEquilibrium = false;
        epsilon = 0.1;
    }
    /**
    * This constructs a grid using coordinates specified by the user.
    * The boundary values ares pecified by the user for each of the
    * four sides. A printed warning is given if the shape of the
    * resulting grid is not composed of squares (the overall grid may
    * be rectangular as long as the composing shapes are square).
    * However, no exception is thrown in the event of non-square grids.
    * The performance loss for a try/catch block is deemed more important
    * than the possible incorrect result that would result for non-square
    * grids. Hence, this constructor is Use At Own Risk.
    * The initial values of the interior points are 0.
    * @param xCoordinates the x-coordinates of the grid points
    * @param yCoordinates the y-coordinates of the grid points
    * @param x0Values the values on the boundary at x_0,y_j
    * @param y0Values the values on the boundary at x_i,y_0
    * @param xn1Values the values on the boundary at x_n+1,y_j
    * @param yn1Values the values on the boundary at x_i,y_n+1
    */
    public GaussSeidel(double [] xCoordinates, double [] yCoordinates,
            double [] x0Values, double [] y0Values, double [] xn1Values,
            double [] yn1Values){
        this.boundaryValues = null;
        this.xCoordinates = xCoordinates;
        this.yCoordinates = yCoordinates;
        this.numPointsX = xCoordinates.length - 2;
        this.numPointsY = yCoordinates.length - 2;
        double width = Math.abs(xCoordinates[numPointsX + 1] - xCoordinates[0]);
        double height = Math.abs(yCoordinates[numPointsY + 1] - yCoordinates[0]);
        if( Math.abs(width/numPointsX - height/numPointsY) > 0.1){
            System.out.println("Error: grids not square. Computations are incorrect.");
        }
        this.mesh = new double[numPointsX + 2][numPointsY + 2];
        for(int j = 0; j < yCoordinates.length; j++){
            mesh[0][j] = x0Values[j];
            mesh[numPointsX + 1][j] = xn1Values[j];
        }
        for(int i = 1; i < yCoordinates.length-1; i++){
            mesh[i][0] = y0Values[i];
            mesh[i][numPointsY + 1] = yn1Values[i];
        }
        setInitialGuess(0.0);
        isAtEquilibrium = false;
        epsilon = 0.1;
    }


    public double [][] computeGaussSeidel(){
        return computeGaussSeidel(1);
    }

    public double [][] computeGaussSeidel(int numIterations){
        double tempValue;
        for(int k = 1; k<= numIterations; k++){
            for(int j = 1; j <= numPointsX; j++){
                for(int i = 1; i <= numPointsY; i++){
                    tempValue = mesh[i][j];
                    mesh[i][j] = (mesh[i-1][j] + mesh[i+1][j] + mesh[i][j-1] + mesh[i][j+1])/4.0;
                    if(Math.abs(mesh[i][j] - tempValue) > epsilon) isAtEquilibrium = false;
                }
            }
        }
        return mesh;

    }

    public double [][] completeGaussSeidel(){
        double tempValue;
        while(!isAtEquilibrium){
            for(int j = 1; j <= numPointsX; j++){
                for(int i = 1; i <= numPointsY; i++){
                    tempValue = mesh[i][j];
                    mesh[i][j] = (mesh[i-1][j] + mesh[i+1][j] + mesh[i][j-1] + mesh[i][j+1])/4.0;
                    if(Math.abs(mesh[i][j] - tempValue) > epsilon) isAtEquilibrium = false;
                }
            }
        }
        return mesh;
    }

    public void setInitialGuess(double interiorValue){
        for(int i = 1; i <= numPointsX ; i++){
            for(int j = 1; j <= numPointsY; j++){
                mesh[i][j] = interiorValue;
            }
        }
    }

    public void setInitialGuess(double [][] mesh){
        this.mesh = mesh;
        computeBoundaryValues(); //find the boundary values if they have been overwritten with the initial guess
    }

    public void setInitialGuess(MathFunction2D function){
        for(int i = 1; i <= numPointsX ; i++){
            for(int j = 1; j <= numPointsY; j++){
                mesh[i][j] = function.eval(xCoordinates[i], yCoordinates[j]);
            }
        }

    }

    public double [] getXCoordinates(){
        return this.xCoordinates;
    }

    public double [] getYCoordinates(){
        return this.yCoordinates;
    }

    public void setBoundaryValues(MathFunction2D boundaryValues){
        this.boundaryValues = boundaryValues;
        computeBoundaryValues();
    }

    public void setBoundaryValueAt(double value, int i,int j){
        mesh[i][j] = value;
    }

    private void computeBoundaryValues(){
        for(int j = 0; j < yCoordinates.length; j++){
            mesh[0][j] = boundaryValues.eval(xCoordinates[0], yCoordinates[j]);
            mesh[numPointsX + 1][j] = boundaryValues.eval(xCoordinates[numPointsX + 1], yCoordinates[j]);
        }
        for(int i = 1; i < yCoordinates.length-1; i++){
            mesh[i][0] = boundaryValues.eval(xCoordinates[i], yCoordinates[0]);
            mesh[i][numPointsY + 1] = boundaryValues.eval(xCoordinates[i], yCoordinates[numPointsY + 1]);
        }

    }

    public void setEquilibriumConstant(double epsilon){
        this.epsilon = epsilon;
    }

    public boolean isAtEquilibrium(){
        return this.isAtEquilibrium;
    }
    public double[][] getMesh(){
        return this.mesh;
    }
}
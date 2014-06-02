package com.blizzardtec.cfd;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Point;


/**
 * blah.
 * @author Barnaby Golden
 *
 */
public final class ColorPanel extends Panel {

    /**
     * hue.
     */
    private static final float HUE = 0.6f;
    /**
     * uid.
     */
    private static final long serialVersionUID = 1522850018954175817L;
    /**
     * blah.
     */
    private double [][] mesh;
    /**
     * blah.
     */
    private int xBorder;
    /**
     * blah.
     */
    private int yBorder;
    /**
     * blah.
     */
    private int gridSize;
    /**
     * blah.
     */
    private boolean initialized;
    /**
     * blah.
     */
    private float hue;
    /**
     * blah.
     */
    private double colorScale;
    /**
     * blah.
     */
    private double colorOffset;

    /**
     * blah.
     * @param mesh param
     */
    public ColorPanel(final double [][] mesh) {
        this.mesh = mesh;
        xBorder = 5;
        yBorder = 5;
        initialized = false;
        hue = HUE;
        computeColorScale();
    }

    /**
     *
     * {@inheritDoc}
     */
    public void paint(final Graphics g) {
        drawGrid(g);
    }

    /**
     * blah.
     * @param g param
     */
    public void drawGrid(final Graphics g) {
        double colorValue;
        Color color;
        if (getHeight() != 0 && getWidth() != 0) {
            if (!initialized) {
                computeProportions();
                initialized = true;
            }
            for (int i = 0; i < mesh.length; i++) {
                for (int j = 0; j < mesh[0].length; j++) {
                  //a value between 0 to 2
                    colorValue = (colorScale * (mesh[i][j] - colorOffset));
                    if (colorValue <= 1.0) {
                        color = Color.getHSBColor(
                                hue, 1.0f, (float) colorValue);
                    } else {
                        color = Color.getHSBColor(
                                hue, (float) (2 - colorValue), 1.0f);
                    }
                    g.setColor(color);

                    g.fillRect(
                      i * gridSize + xBorder, getHeight()
                        - ((j + 1) * gridSize + yBorder), gridSize, gridSize);
                }
            }
        }
    }

    /**
     * blah.
     * @param x param
     * @param y param
     * @return val
     */
    public Point findGridNumber(final int x, final int y) {

        int i = (x - xBorder) / gridSize;
        int j = (getHeight() - y - yBorder) / gridSize;
        Point p = new Point(i, j);
        if (i < 0 || j < 0 || i >= mesh.length || j >= mesh[0].length) {
            p = null;
        }
        return p;
    }

    /**
     * blah.
     * @return val
     */
    public double [][] getMesh() {
        return this.mesh;
    }

    /**
     * blah.
     * @param mesh param
     */
    public void setMesh(final double [][] mesh) {
        this.mesh = mesh;
    }

    /**
     * blah.
     */
    public void computeColorScale() {
        double min = 0.0;
        double max = 0.0;
        for (int i = 0; i < mesh.length; i++) {
            for (int j = 0; j < mesh[0].length; j++) {
                if (mesh[i][j] > max) {
                    max = mesh[i][j];
                } else if (mesh[i][j] < min) {
                    min = mesh[i][j];
                }
            }
        }
        colorScale = (2.0 / (max - min));
        colorOffset = min;

    }

    /**
     * blah.
     */
    private void computeProportions() {
        int numPointsX = mesh.length;
     // all of the columns have the same size
        int numPointsY = mesh[0].length;
        gridSize = 0;
      //a truncated int
        int gridSizeX = (getWidth() - 2 * xBorder) / (numPointsX);
        if (gridSizeX < 0) {
            xBorder = 0;
          //a truncated int
            gridSizeX = getWidth() / (numPointsX);
        }
      //a truncated int
        int gridSizeY = (getHeight() - 2 * yBorder) / (numPointsY);
        if (gridSizeY < 0) {
            yBorder = 0;
          //a truncated int
            gridSizeY = getHeight() / (numPointsY);
        }
        if (gridSizeX >= gridSizeY) {
            gridSize = gridSizeY;
            xBorder = (getWidth() - (numPointsX) * gridSize) / 2;
        } else {
            gridSize = gridSizeX;
            yBorder = (getHeight() - (numPointsY) * gridSize) / 2;
        }
    }
}

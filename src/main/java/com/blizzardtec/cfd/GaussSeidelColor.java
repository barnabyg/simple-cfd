package com.blizzardtec.cfd;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * blah.
 * @author Barnaby Golden
 *
 */
public final class GaussSeidelColor
    extends Applet implements MouseListener, ActionListener {

    /**
     * val.
     */
    private static final int TXT_VAL = 17;
    /**
     * uid.
     */
    private static final long serialVersionUID = -6518320878564870704L;
    /**
     * blah.
     */
    private ColorPanel meshPanel;
    /**
     * blah.
     */
    private ColorPanel errorPanel;
    /**
     * blah.
     */
    private GaussSeidel gs;
    /**
     * blah.
     */
    private MathFunction2D boundaryValues;
    /**
     * blah.
     */
    private Label xLbl;
    /**
     * blah.
     */
    private Label yLbl;
    /**
     * blah.
     */
    private Label valueLbl;
    /**
     * blah.
     */
    private Label iterationsLbl;
    /**
     * blah.
     */
    private TextField xTxt;
    /**
     * blah.
     */
    private TextField yTxt;
    /**
     * blah.
     */
    private TextField valueTxt;
    /**
     * blah.
     */
    private TextField iterationsTxt;
    /**
     * blah.
     */
    private Button computeBtn;
    /**
     * blah.
     */
    private Button renormalizeBtn;

    /**
     *
     * {@inheritDoc}
     */
    public void init() {
        setLayout(new BorderLayout());

        add(createNorthPanel(), "North");
        add(createCenterPanel(), "Center");
        add(createSouthPanel(), "South");
    }

    /**
     * blah.
     * @return val
     */
    public Panel createNorthPanel() {
        Panel northPanel = new Panel();
        northPanel.setLayout(new GridLayout(1, 2));
        northPanel.add(new Label("Solution"));
        northPanel.add(new Label("Error"));
        return northPanel;
    }

    /**
     * blah.
     * @return val
     */
    public Panel createCenterPanel() {
        Panel centerPanel = new Panel();
        centerPanel.setLayout(new GridLayout(1, 2));
        boundaryValues = new MathFunction2D() {
            public double eval(final double x, final double y) {
                return 4 * (x * y) * (x - y) * (x + y);
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

    /**
     * blah.
     * @return val
     */
    public Panel createSouthPanel() {
        Panel southPanel = new Panel();
        southPanel.setLayout(new GridLayout(3, 1));

        Panel row1 = new Panel();
        xLbl = new Label("x");
        yLbl = new Label("y");
        valueLbl = new Label("value");
        xTxt = new TextField("", 5);
        yTxt = new TextField("", 5);
        valueTxt = new TextField("", TXT_VAL);
        row1.add(xLbl);
        row1.add(xTxt);
        row1.add(yLbl);
        row1.add(yTxt);
        row1.add(valueLbl);
        row1.add(valueTxt);
        southPanel.add(row1);

        Panel row2 = new Panel();
        iterationsLbl = new Label("Iterations");
        iterationsTxt = new TextField("", 3);
        computeBtn = new Button("Compute");
        computeBtn.addActionListener(this);
        row2.add(iterationsLbl);
        row2.add(iterationsTxt);
        row2.add(computeBtn);
        southPanel.add(row2);

        Panel row3 = new Panel();
        renormalizeBtn = new Button("Renormalize Color Values");
        renormalizeBtn.addActionListener(this);
        row3.add(renormalizeBtn);
        southPanel.add(row3);

        return southPanel;
    }

    /**
     * blah.
     * @return val
     */
    public double [][] findErrorMatrix() {
        double [][] mesh = gs.getMesh();
        double [][] errorMatrix = new double[mesh.length][mesh[0].length];
        for (int j = 0; j < mesh[0].length; j++) {
            for (int i = 0; i < mesh.length; i++) {
                errorMatrix[i][j] = Math.abs(mesh[i][j]
                       - boundaryValues.eval(
                           gs.getXCoordinates()[i], gs.getYCoordinates()[j]));
            }
        }
        return errorMatrix;
    }

    /**
     * blah.
     */
    public void computeMatrices() {
        meshPanel.repaint();
        errorPanel.setMesh(findErrorMatrix());
        errorPanel.repaint();
    }

    /**
     *
     * {@inheritDoc}
     */
    public void actionPerformed(final ActionEvent ae) {
        if (ae.getSource() == computeBtn) {
            int iterations = 0;
            try {
                iterations = Integer.parseInt(iterationsTxt.getText());
            } catch (NumberFormatException nfe) {
                System.out.println("Entered value is not an integer");
            }
            gs.computeGaussSeidel(iterations);
            computeMatrices();
        } else if (ae.getSource() == renormalizeBtn) {
            errorPanel.computeColorScale();
            meshPanel.computeColorScale();
            computeMatrices();
        }
    }

    /**
     *
     * {@inheritDoc}
     */
    public void mouseEntered(final MouseEvent me) {
        // empty
    }

    /**
     *
     * {@inheritDoc}
     */
    public void mouseExited(final MouseEvent me) {
        // empty
    }

    /**
     *
     * {@inheritDoc}
     */
    public void mouseClicked(final MouseEvent me) {
        Point p = null;
        double value = 0.0;
        if (me.getSource() == meshPanel) {
            p = meshPanel.findGridNumber(me.getX(), me.getY());

            if (p != null) {
                value = meshPanel.getMesh()[p.x][p.y];
            }
        } else if (me.getSource() == errorPanel) {
            p = errorPanel.findGridNumber(me.getX(), me.getY());
            if (p != null) {
                value = errorPanel.getMesh()[p.x][p.y];
            }
        }

        if (p != null) {
            xTxt.setText("" + gs.getXCoordinates()[p.x]);
            yTxt.setText("" + gs.getXCoordinates()[p.x]);
            valueTxt.setText("" + value);
        }

    }

    /**
     *
     * {@inheritDoc}
     */
    public void mousePressed(final MouseEvent me) {
        // empty
    }

    /**
     *
     * {@inheritDoc}
     */
    public void mouseReleased(final MouseEvent me) {
        // empty
    }
}

/*
 * Haqua - a collection of hacks to work around issues in the Aqua look and feel
 * Copyright (C) 2014  Trejkaz, Haqua Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.trypticon.haqua.demo.problems;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.FlowLayout;

public class ProgressBarTextPositionTest implements Runnable {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new ProgressBarTextPositionTest());
    }

    @Override
    public void run() {
        JProgressBar progressBar = new JProgressBar();
        progressBar.setString("Progress Bar");
        progressBar.setStringPainted(true);
        progressBar.putClientProperty("JComponent.sizeVariant", "mini"); // most noticeable at mini size

        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.add(progressBar);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

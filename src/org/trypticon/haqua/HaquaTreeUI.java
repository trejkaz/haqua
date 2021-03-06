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

package org.trypticon.haqua;

import com.apple.laf.AquaTreeUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.IconUIResource;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

/**
 * @author trejkaz
 */
public class HaquaTreeUI extends AquaTreeUI {
    private Icon selectedCollapsedIcon;
    private Icon selectedRtlCollapsedIcon;
    private Icon selectedExpandedIcon;
    private Icon pressedSelectedCollapsedIcon;
    private Icon pressedSelectedRtlCollapsedIcon;
    private Icon pressedSelectedExpandedIcon;
    private int selectionBackgroundForIcons;

    private boolean paintingSelectedRow;

    private AdditionalHandler handler;

    @NotNull
    @SuppressWarnings("UnusedDeclaration") // called via reflection
    public static ComponentUI createUI(JComponent component) {
        return new HaquaTreeUI();
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        if (handler == null) {
            handler = new AdditionalHandler();
        }
        tree.addMouseListener(handler);
    }

    @Override
    protected void uninstallListeners() {
        tree.removeMouseListener(handler);
        super.uninstallListeners();
    }

    private void lazyInitialiseIcons() {
        // We keep track of which colour the selection background was set for so that we can generate
        // new icons if the user changes their theme while we're running.
        Color selectionForeground = UIManager.getColor("Tree.selectionForeground");
        Color selectionBackground = UIManager.getColor("Tree.selectionBackground");
        if (selectedCollapsedIcon == null || selectionBackgroundForIcons != selectionBackground.getRGB()) {
            selectionBackgroundForIcons = selectionBackground.getRGB();

            Icon normalCollapsedIcon = UIManager.getIcon("Tree.collapsedIcon");
            Icon normalRtlCollapsedIcon = UIManager.getIcon("Tree.rightToLeftCollapsedIcon");
            Icon normalExpandedIcon = UIManager.getIcon("Tree.expandedIcon");

            selectedCollapsedIcon = createAlternateColourVersion(normalCollapsedIcon, selectionForeground);
            selectedRtlCollapsedIcon = createAlternateColourVersion(normalRtlCollapsedIcon, selectionForeground);
            selectedExpandedIcon = createAlternateColourVersion(normalExpandedIcon, selectionForeground);

            // Colour of the disclosure triangle when you are pressing it is subtly different.
            Color pressedDisclosureTriangleForeground = new Color(
                    derivePressedColourComponent(selectionBackground.getRed()),
                    derivePressedColourComponent(selectionBackground.getGreen()),
                    derivePressedColourComponent(selectionBackground.getBlue()));
            pressedSelectedCollapsedIcon = createAlternateColourVersion(normalCollapsedIcon, pressedDisclosureTriangleForeground);
            pressedSelectedRtlCollapsedIcon = createAlternateColourVersion(normalRtlCollapsedIcon, pressedDisclosureTriangleForeground);
            pressedSelectedExpandedIcon = createAlternateColourVersion(normalExpandedIcon, pressedDisclosureTriangleForeground);
        }
    }

    private int derivePressedColourComponent(int component) {
        // Experiment shows that the slightly shaded colour you get when pressed is:
        //  X' = 153 + (255-153) * X / 255 = 164
        // There is probably a way to do this in a single shot for all three components...
        return (int) Math.floor(153 + (255 - 153) * (double) component / 255);
    }

    @NotNull
    private Icon createAlternateColourVersion(@NotNull Icon icon, @NotNull final Color colour) {
        class RecolourFilter extends PerPixelFilter {
            @Override
            protected void manipulatePixels(@NotNull int[] pixels) {
                int rgb = colour.getRGB();
                for (int i = 0; i < pixels.length; i++) {
                    // Leave the alpha alone, change all RGB values to the target.
                    pixels[i] = (pixels[i] & 0xFF000000) | (rgb & 0xFFFFFF);
                }
            }
        }

        // Same image type used by AquaIcon itself.
        final BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics2D graphics = image.createGraphics();
        try {
            icon.paintIcon(tree, graphics, 0, 0);
        } finally {
            graphics.dispose();
        }

        new RecolourFilter().filter(image, image);
        return new IconUIResource(new ImageIcon(image));
    }

    @Override
    protected void paintRow(@NotNull Graphics g, @NotNull Rectangle clipBounds, Insets insets, @NotNull Rectangle bounds, TreePath path, int row, boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf) {
        // Paint the full row in the appropriate background colour, not just the node.
        if (tree.isPathSelected(path)) {
            Color selectionBackground = UIManager.getColor(
                    FocusUtils.isInActiveWindow(tree) ? "Tree.selectionBackground" : "Tree.selectionInactiveBackground");
            g.setColor(selectionBackground);
            // A purist might say this should be a round-rect with arc diameter equal to the row height
            // and flat edges above/below if the row in that direction is also selected.
            g.fillRect(clipBounds.x, bounds.y, clipBounds.width, bounds.height);
        }

        // This was already called by DefaultTreeUI, but before calling paintRow(), so we just painted over it.
        // I guess ask the Swing developers what they were smoking when they decided to paint them that way around.
        paintExpandControl(g, clipBounds, insets, bounds, path, row, isExpanded, hasBeenExpanded, isLeaf);

        // This will then paint the node itself.
        super.paintRow(g, clipBounds, insets, bounds, path, row, isExpanded, hasBeenExpanded, isLeaf);
    }

    @NotNull
    @Override
    protected TreeCellRenderer createDefaultCellRenderer() {
        // Custom renderer fixes the colours of the tree node itself when the window is inactive.
        return new HaquaTreeCellRenderer();
    }

    @Override
    protected void handleExpandControlClick(TreePath path, final int mouseX, final int mouseY) {
        if (tree.isPathSelected(path)) {
            // Trick the handler the superclass will create into storing the selection background colour
            // into a field it uses when it clears the background before drawing the icon.
            Color oldBackground = tree.getBackground();
            try {
                tree.setBackground(UIManager.getColor("Tree.selectionBackground"));
                super.handleExpandControlClick(path, mouseX, mouseY);
            } finally {
                tree.setBackground(oldBackground);
            }
        } else {
            super.handleExpandControlClick(path, mouseX, mouseY);
        }
    }

    @Override
    protected void paintExpandControl(final Graphics g, final Rectangle clipBounds,
                                      final Insets insets, Rectangle bounds,
                                      final TreePath path, final int row,
                                      final boolean isExpanded, final boolean hasBeenExpanded, final boolean isLeaf) {

        // The caller in AquaTreeUI messes up by passing the wrong path, resulting in momentarily
        // painting the control in the wrong location while it is animating.
        bounds = getPathBounds(tree, path);

        boolean oldPaintingSelectedRow = paintingSelectedRow;
        paintingSelectedRow = tree.isPathSelected(path) && FocusUtils.isInActiveWindow(tree);
        try {
            lazyInitialiseIcons();

            // There is some suspicious code in AquaTreeUI#paintExpandControl which is used
            // only when orientation is right to left:
            //      middleXOfKnob = clipBounds.x + clipBounds.width / 2;
            //
            // This should probably be something like:
            //      middleXOfKnob = bounds.x + bounds.width + (getRightChildIndent() - 1);
            //
            // Funnily enough, BasicTreeUI seems to get it right, so I think someone at Apple
            // was confusing the bounds with the clip bounds.
            // The clip bounds isn't used for anything *other* than this dodgy code, so I
            // figure we can massage it so that the values Apple see are the values they
            // should have been using in the first place.

            int correctValue = bounds.x + bounds.width + (getRightChildIndent() - 1);
            int appleValue = clipBounds.x + clipBounds.width / 2;
            Rectangle newClipBounds = new Rectangle(clipBounds);
            newClipBounds.x += (correctValue - appleValue);

            super.paintExpandControl(g, newClipBounds, insets, bounds, path, row, isExpanded, hasBeenExpanded, isLeaf);

        } finally {
            paintingSelectedRow = oldPaintingSelectedRow;
        }
    }

    @Override
    public Icon getCollapsedIcon() {
        boolean ltr = tree.getComponentOrientation().isLeftToRight();
        if (paintingSelectedRow) {
            return fIsPressed ? (ltr ? pressedSelectedCollapsedIcon : pressedSelectedRtlCollapsedIcon)
                    : (ltr ? selectedCollapsedIcon : selectedRtlCollapsedIcon);
        } else {
            return super.getCollapsedIcon();
        }
    }

    @Override
    public Icon getExpandedIcon() {
        if (paintingSelectedRow) {
            return fIsPressed ? pressedSelectedExpandedIcon : selectedExpandedIcon;
        } else {
            return super.getExpandedIcon();
        }
    }

    private class AdditionalHandler implements MouseListener {
        @Override
        public void mousePressed(MouseEvent event) {
            TreePath pressedPath = getClosestPathForLocation(tree, event.getX(), event.getY());
            if (pressedPath != null &&
                    !isLocationInExpandControl(pressedPath, event.getX(), event.getY())) {
                selectPathForEvent(pressedPath, event);
            }
        }

        @Override
        public void mouseExited(MouseEvent event) {
        }

        @Override
        public void mouseEntered(MouseEvent event) {
        }

        @Override
        public void mouseReleased(MouseEvent event) {
        }

        @Override
        public void mouseClicked(MouseEvent event) {
        }
    }
}

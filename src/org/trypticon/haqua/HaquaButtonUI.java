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

import com.apple.laf.AquaButtonUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;

/**
 * @author trejkaz
 */
public class HaquaButtonUI extends AquaButtonUI {
    @NotNull
    @SuppressWarnings("UnusedDeclaration") // called via reflection
    public static ComponentUI createUI(JComponent component) {
        return new HaquaButtonUI();
    }

    @Override
    protected void installDefaults(@NotNull AbstractButton b) {
        super.installDefaults(b);

        fixSegmentPositionBug(b);
    }

    /**
     * Works around a bug where switching the look and feel at runtime clears the border for segmented buttons.
     *
     * @param button the button.
     */
    static void fixSegmentPositionBug(AbstractButton button) {
        final Object segmentProp = button.getClientProperty("JButton.segmentPosition");
        if (segmentProp != null) {
            final Border border = button.getBorder();
            if (border == null) {
                button.setBorder(HaquaButtonExtendedTypes.getBorderForPosition(
                        button, button.getClientProperty("JButton.buttonType"), segmentProp));
            }
        }
    }
}

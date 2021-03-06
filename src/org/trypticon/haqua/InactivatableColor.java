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

import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import javax.swing.plaf.UIResource;
import java.awt.Color;

/**
 * A magic colour object which changes colour when the component is not in an active window.
 */
public class InactivatableColor extends Color implements UIResource {

    private final JComponent component;
    @NotNull
    private final Color activeColor;
    private final Color inactiveColor;

    public InactivatableColor(JComponent component, @NotNull Color activeColor, Color inactiveColor) {
        super(activeColor.getRGB());

        this.component = component;
        this.activeColor = activeColor;
        this.inactiveColor = inactiveColor;
    }

    @Override
    public int getRGB() {
        if (FocusUtils.isInActiveWindow(component)) {
            return activeColor.getRGB();
        } else {
            return inactiveColor.getRGB();
        }
    }
}

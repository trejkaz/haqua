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
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicViewportUI;

/**
 * @author trejkaz
 */
public class HaquaViewportUI extends BasicViewportUI {
    @NotNull
    @SuppressWarnings("UnusedDeclaration") // called via reflection
    public static ComponentUI createUI(JComponent component) {
        return new HaquaViewportUI();
    }

    @Override
    protected void installDefaults(@NotNull JComponent component) {
        // LookAndFeel.installProperty *should* work, but the JRE itself has a bug where JViewport
        // sets itself opaque without resetting the flag indicating that it wasn't set by the caller.
        component.setOpaque(UIManager.getBoolean("Viewport.opaque"));
        //LookAndFeel.installProperty(component, "opaque", UIManager.get("Viewport.opaque"));
    }
}

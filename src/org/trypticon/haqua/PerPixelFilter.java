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

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * @author trejkaz
 */
abstract class PerPixelFilter extends AbstractFilter {

    @Override
    public BufferedImage filter(BufferedImage source, BufferedImage destination) {
        if (destination == null) {
            destination = createCompatibleDestImage(source, null);
        }

        int width = source.getWidth();
        int height = source.getHeight();

        int[] pixels = new int[width * height];
        getPixels(source, 0, 0, width, height, pixels);
        manipulatePixels(pixels);
        setPixels(destination, 0, 0, width, height, pixels);
        return destination;
    }

    protected abstract void manipulatePixels(int[] pixels);

    private int[] getPixels(BufferedImage image, int x, int y, int w, int h, int[] pixels) {
        if (w == 0 || h == 0) {
            return new int[0];
        }

        if (pixels == null) {
            pixels = new int[w * h];
        } else if (pixels.length < w * h) {
            throw new IllegalArgumentException("pixels array must have a length >= w * h");
        }

        int imageType = image.getType();
        if (imageType == BufferedImage.TYPE_INT_ARGB || imageType == BufferedImage.TYPE_INT_RGB) {
            Raster raster = image.getRaster();
            return (int[]) raster.getDataElements(x, y, w, h, pixels);
        }

        return image.getRGB(x, y, w, h, pixels, 0, w);
    }

    private void setPixels(BufferedImage image, int x, int y, int w, int h, int[] pixels) {
        if (pixels == null || w == 0 || h == 0) {
            return;
        } else if (pixels.length < w * h) {
            throw new IllegalArgumentException("pixels array must have a length >= w * h");
        }

        int imageType = image.getType();
        if (imageType == BufferedImage.TYPE_INT_ARGB || imageType == BufferedImage.TYPE_INT_RGB) {
            WritableRaster raster = image.getRaster();
            raster.setDataElements(x, y, w, h, pixels);
        } else {
            image.setRGB(x, y, w, h, pixels, 0, w);
        }
    }

}

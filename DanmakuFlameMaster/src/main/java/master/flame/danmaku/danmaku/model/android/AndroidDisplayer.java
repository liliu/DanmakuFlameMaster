/*
 * Copyright (C) 2013 Chen Hui <calmer91@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package master.flame.danmaku.danmaku.model.android;

import android.graphics.Canvas;
import android.graphics.Color;
import android.text.TextPaint;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.IDisplayer;

/**
 * Created by ch on 13-7-5.
 */
public class AndroidDisplayer implements IDisplayer {

    public static TextPaint PAINT;

    static {
        PAINT = new TextPaint();
        PAINT.setColor(Color.RED);
        PAINT.setTextSize(50);
        // PAINT.setAntiAlias(true);
        PAINT.setAntiAlias(true);
        // TODO: load font from file
    }

    public Canvas canvas;

    public int width;

    public int height;

    public float density = 1;
    
    public float scaledDensity = 1;
    public void update(Canvas c) {
        canvas = c;
        if (c != null) {
            width = c.getWidth();
            height = c.getHeight();
        }
    }

    @Override
    public int getWidth() {

        return width;
    }

    @Override
    public int getHeight() {

        return height;
    }

    @Override
    public float getDensity() {
        return density;
    }

    @Override
    public void draw(BaseDanmaku danmaku) {
        if (canvas != null) {
            TextPaint paint = getPaint(danmaku);
            if (danmaku.paintHeight > danmaku.textSize) {
                String[] titleArr = danmaku.text.split(BaseDanmaku.DANMAKU_BR_CHAR);
                if (titleArr.length == 1) {
                    canvas.drawText(titleArr[0], danmaku.getLeft(),
                            danmaku.getTop() - paint.ascent(), paint);
                } else {
                    for (int t = 0; t < titleArr.length; t++) {
                        if (titleArr[t].length() > 0) {
                            canvas.drawText(titleArr[t], danmaku.getLeft(), (t)
                                    * (danmaku.textSize + 1) + danmaku.getTop() - paint.ascent(),
                                    paint);
                        }
                    }
                }
            } else {
                canvas.drawText(danmaku.text, danmaku.getLeft(), danmaku.getTop() - paint.ascent(),
                        paint);
            }
        }
    }

    private static TextPaint getPaint(BaseDanmaku danmaku) {
        PAINT.setTextSize(danmaku.textSize); // TODO: fixme
        PAINT.setColor(danmaku.textColor);
        // TODO: set the text shadow color
        return PAINT;
    }

    @Override
    public void measure(BaseDanmaku danmaku) {
        TextPaint paint = getPaint(danmaku);
        float[] wh = calcPaintWH(danmaku.text, paint);
        danmaku.paintWidth = wh[0]; // paint.measureText(danmaku.text);
        danmaku.paintHeight = wh[1]; // paint.getTextSize();
    }

    private float[] calcPaintWH(String text, TextPaint paint) {
        float w = 0;
        float textHeight = paint.getTextSize();
        if (!text.contains(BaseDanmaku.DANMAKU_BR_CHAR)) {
            w = paint.measureText(text);
            return new float[]{
                    w, textHeight
            };
        }

        int stPos = 0, endPos = -1;
        String tempStr;
        int t = 0;

        while ((endPos = text.indexOf(BaseDanmaku.DANMAKU_BR_CHAR, stPos)) != -1) {
            t++;
            tempStr = text.substring(stPos, endPos);
            if (tempStr.length() > 0) {
                float tr = paint.measureText(tempStr);
                if (tr > 0) {
                    w = tr > w ? tr : w;
                }
            }
            stPos = endPos + 2;
        }
        if (stPos < text.length() - 1) {
            t++;
            tempStr = text.substring(stPos);
            if (tempStr.equals(BaseDanmaku.DANMAKU_BR_CHAR) == false && tempStr.length() > 0) {
                float tr = paint.measureText(tempStr);
                w = Math.max(tr, w);
            }
        }

        return new float[]{
                w, (t + 1) * textHeight
        };
    }

	@Override
	public float getScaledDensity() {
		return scaledDensity;
	}

}

package com.Naos.Boxed;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daimon on 6/18/2015.
 */
public class Polygon {
    private List<Float> xVertices = new ArrayList<Float>();
    private List<Float> yVertices = new ArrayList<Float>();

    Polygon(float... Points) {
        boolean bCoords = false;
        int i = 0;
        for (; i < Points.length; i++) {
            float f = Points[i];
            if (bCoords) {
                yVertices.add(f);
            } else {
                xVertices.add(f);
            }
            bCoords = !bCoords;
        }
        if (i % 2 != 0)  {
            xVertices.remove(xVertices.size() - 1);
        }
    }

    public void MoveX(float fLeft) {
        for (int i = 0; i < xVertices.size();i++) {
            xVertices.set(i,xVertices.get(i) + fLeft);
        }
    }

    public void MoveY(float fUp) {
        for (int i = 0; i < yVertices.size();i++) {
            yVertices.set(i,yVertices.get(i) + fUp);
        }
    }

    public float GetX(int i) {
        return xVertices.get(i);
    }

    public float GetY(int i) {
        return yVertices.get(i);
    }

    public void SetX(int i, float f) {
        xVertices.set(i,f);
    }

    public void SetY(int i, float f) {
        yVertices.set(i,f);
    }

}

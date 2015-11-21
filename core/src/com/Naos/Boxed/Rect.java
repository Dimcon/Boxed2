package com.Naos.Boxed;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.*;

/**
 * Created by Daimon on 6/18/2015.
 */
public class Rect extends Polygon {
    static ArrayList<Rect> AllRects = new ArrayList<Rect>();

    public float a = 1f;
    private Boolean RelativeToBottom = true,
            RelativeToLeft = true,
            Changed = true;
    static boolean Debugg = false;
    public static int RectsDrawn = 0;
    public static void setDebug(Boolean Value) {
        Debugg = Value;
    }
    public static Rect rScreen = new Rect(0, Gdx.graphics.getHeight(),Gdx.graphics.getWidth(),0);
    public Texture imgRect;

    Rect(float Left, float top, float right,float bottom) {
        super(Left,top,right,top,right,bottom,Left,bottom);
        AllRects.add(this);
    }

    public float l(){
        return (GetX(0) + GetX(3)) / 2;
    }

    public float r(){
        return (GetX(1) + GetX(2)) / 2;
    }

    public float t(){
        return (GetY(0) + GetY(1)) / 2;
    }

    public float b(){
        return (GetY(2) + GetY(3)) / 2;
    }
    public void setl(float fLeft){
        SetX(0, fLeft);
        SetX(3, fLeft);
    }

    public void sett(float fTop){
        SetY(0, fTop);
        SetY(1, fTop);
    }

    public void setr(float fRight){
        SetX(1,fRight);
        SetX(2,fRight);
    }

    public void setb(float fBottom){
        SetY(2, fBottom);
        SetY(3, fBottom);
    }

    public void setAlpha(float Alpha) {
        a = Alpha;
    }

    public float CenterX() {
        /** Get center of Rect (horizontal) */
        return (l() + r())/2;
    }
    public float CenterY() {
        /** Get center of Rect (Vertical)   */
        return (t() + b())/2;
    }
    public float width() {
        /** Get width of Rect   */
        if (RelativeToLeft) {
            return r() - l();
        } else {
            return l() - r();
        }
    }
    public float height() {
    /* Get height of Rect */
        if (RelativeToBottom) {
            return t() - b();
        } else {
            return b() - t();
        }
    }
    public void OffScreen() {
    /* Move all points to negative. */
        Changed = true;

        setl( - 10);
        setr(- 10);
        sett(- 10);
        setb(-10);
    }

    public boolean IsInside(Rect rTest) {
        Boolean bResult = false;
        if (rTest.t() > b() && rTest.b() < t() ) {
            bResult = true;
        }
        if (!(rTest.l() < r() && rTest.r() > l()) && bResult) {
            bResult = false;
        }
        return bResult;
    }

    public void RectCopy(Rect rP) {
        /** Copy numerical values of given rect.
         *  Saying Rect1 = Rect2 seems to make Rect1 point to Rect2 instead of
         *  just copying it.    */
        setl(rP.l());
        setr(rP.r());
        sett(rP.t());
        setb(rP.b());
        Changed = true;
    }
    public void CopySquare(Rect rP,float rPadding) {
        Changed = true;
        /** Turn into square as large as possible within rectangle
         *  - - - - - - -_-_-_-_-_- - - - - - - -
         * | Rectangle  | New     |              |
         * |            | Square  |              |
         * |            |         |              |
         * |            |         |              |
         *  _ _ _ _ _ _ _-_-_-_-_-_ _ _ _ _ _ _ _
         */
        float w = ((rP.width()/2) - rPadding);
        float h = ((rP.height()/2) - rPadding);
            if (rP.height() >= rP.width()) {
                sett(rP.CenterY() + w);
                setb(rP.CenterY() - w);
            } else {
                sett(rP.CenterY() + h);
                setb(rP.CenterY() - h);
            }
            if (rP.height() >= rP.width()) {
                setl(rP.CenterX() - w);
                setr(rP.CenterX() + w);
            } else {
                setl(rP.CenterX() - h);
                setr(rP.CenterX() + h);
            }
    }

    public Rect clone() {
        Rect r = new Rect(l(),t(),r(),b());
        return r;
    }
    public void Draw(com.badlogic.gdx.graphics.Texture tx, SpriteBatch sBtch) {
        DrawWithAlpha(tx,sBtch,a);
    }
    public void DrawWithOffset(float fFromLeft, float fFromBottom,com.badlogic.gdx.graphics.Texture tx, SpriteBatch sBtch) {
        DrawWithOffsetWithAlpha(fFromLeft, fFromBottom, tx, sBtch, a);
    }

    public void DrawWithOffsetWithAlpha(float fFromLeft, float fFromBottom,com.badlogic.gdx.graphics.Texture tx,SpriteBatch sBtch, float fAlpha) {
        /** Draw using LIBGDX Spritebatch. LibGDX uses the bottom left of
         * the screen as the reference  */
        if (IsInside(rScreen)) {
            if (Debugg) {
                com.badlogic.gdx.graphics.Color newcol = sBtch.getColor();
                sBtch.setColor(1, 1, 1, 1);
                sBtch.draw(tx, l() + fFromLeft, b() + fFromBottom, width(), height());
                sBtch.setColor(newcol);
            } else {
                com.badlogic.gdx.graphics.Color newcol = sBtch.getColor();
                sBtch.setColor(1, 1, 1, fAlpha);
                sBtch.draw(tx, l() + fFromLeft, b() + fFromBottom, width(), height());
                sBtch.setColor(newcol);
            }
            Rect.RectsDrawn++;
        }
    }
    public void Draw(com.badlogic.gdx.graphics.Texture tx, SpriteBatch sBtch,float fAlphaP) {
        DrawWithAlpha(tx,sBtch,fAlphaP);
    }
    public void DrawWithAlpha(com.badlogic.gdx.graphics.Texture tx,SpriteBatch sBtch, float fAlpha) {
        /** Draw using LIBGDX Spritebatch. LibGDX uses the bottom left of
         * the screen as the reference  */
        if (IsInside(rScreen)) {
            if (Debugg) {
                com.badlogic.gdx.graphics.Color newcol = sBtch.getColor();
                sBtch.setColor(1, 1, 1, 1);
                sBtch.draw(tx, l(), b(), width(), height());
                sBtch.setColor(newcol);
            } else {
                com.badlogic.gdx.graphics.Color newcol = sBtch.getColor();
                sBtch.setColor(1, 1, 1, fAlpha);
                sBtch.draw(tx, l(), b(), width(), height());
                sBtch.setColor(newcol);
            }
            Rect.RectsDrawn++;
        }

    }

    public Sprite ToSprite(Texture img) {
        Sprite s = new Sprite(img);
        s.setPosition(l(),b());
        s.setOrigin(0,0);
        s.setScale(width()/img.getWidth(),height()/img.getHeight());
        return s;
    }

    protected float pt, pb, pl, pr;    /* Post animation position.                 */
    boolean animAlpha = false,
            animTranslate = false;

    ArrayList<Burrito> Animators = new ArrayList<Burrito>();
    Burrito AlphaAnim;

    public Interpolator Interp;

    public void StartAnimT(Rect rDest,Interpolator InterpolatorP,float Timemillis) {
        /** Amount to increase AnimTime by on each cycle, assuming Animate will be
         * called 60fps. Target Angle (90 degrees, 0.5 radians) divided by

        *//** Store the requested ending values to ensure the animation ends where
         *  requested.   */
        animTranslate = true;
        Animators.add(new Burrito(l(),rDest.l(),Timemillis,InterpolatorP));
        Animators.add(new Burrito(t(),rDest.t(),Timemillis,InterpolatorP));
        Animators.add(new Burrito(r(),rDest.r(),Timemillis,InterpolatorP));
        Animators.add(new Burrito(b(),rDest.b(),Timemillis,InterpolatorP));
    }

    public void StartAnimA(float TargetAlpha,Interpolator InterpolatorP,float Timemillis) {
        /*preA = a;
        *//** Refer to StartAnimT ^^  */
        animAlpha = true;
        AlphaAnim = new Burrito(a,TargetAlpha,Timemillis,InterpolatorP);
    }

    public void Animate() {
        /** The distance that the rect needs to be moved is kept as a whole. AnimTime
         *  is 90 degrees of a circle. AnimTime is increased at a constant rate so that
         *  it reaches 90 degrees in the time asked for. Using Sin and Cos on the 'angle'
         *  returns a value that either increases (Cos) or decreases (Sin) with acceleration
         *  */
        if (animTranslate) {
            int iDone = 0;
            if (!Animators.get(0).Animate()) setl(Animators.get(0).GetValue()); else iDone++;
            if (!Animators.get(1).Animate()) sett(Animators.get(1).GetValue()); else iDone++;
            if (!Animators.get(2).Animate()) setr(Animators.get(2).GetValue()); else iDone++;
            if (!Animators.get(3).Animate()) setb(Animators.get(3).GetValue()); else iDone++;
            if (iDone == 4) {
                animTranslate = false;
                Animators.clear();
            }
        }
        if (animAlpha) {
            if (!AlphaAnim.Animate()) setAlpha(AlphaAnim.GetValue());
            else {
                animAlpha = false;
            }
        }
    }

/** Touch interface for Rect. Relies on TouchHandler to record touches.
     *  isTouched will be true if the current rect has been touched and is now
     *  under the users pointer. isTouching will be true if there is currently a
     *  pointer in this rect, regardless of where the pointer touched first.
     * */

    public int pointer;
    public Boolean IsTouched() {
        /** AHHOC -Naos
         *  If touchHandler reports a new touch occurring in this rect and there
         *  is no currently assigned pointer, then assign that pointer to this rect.
         *  If this rects assigned pointer isn't in touchHandlers system, it's safe to assume
         *  this rect has been abandoned and should report as alone (-1).
         * */

        if (TouchHandler.TouchDownAtRect(this) > -1 && pointer == -1) {
            pointer = TouchHandler.TouchDownAtRect(this);
        }
        if (pointer > -1 && !TouchHandler.PointerIsHere(pointer)) {
            pointer = -1;
        }
        return pointer != -1;
    }

    public float TouchedX() {
        /** Reports the X coord of the pointer assigned to the current Rect. Returns
         * -1 if this rect is lonely (no assigned pointers)*/
        if (IsTouched()) {
            return TouchHandler.TouchMap.get(pointer).TouchPosX;
        }
        return -1;
    }
    public float TouchedY() {
        /** Reports the Y coord of the pointer assigned to the current Rect. Returns
         * -1 if this rect is lonely (no assigned pointers)*/
        if (IsTouched()) {
            return TouchHandler.TouchMap.get(pointer).TouchPosY;
        }
        return -1;
    }
    public Boolean IsTouching() {
        /** This makes the rect desperate. It will report true if there is ANY
         *  touch within the rect. */
        return TouchHandler.TouchDownAtRect(this) != -1;
    }
    public float TouchingX() {
        /** Reports the X coord of a pointer here. Any pointer in the vicinity of this
         *  Rect will count as a touch, whether TouchHandler reports the touchdown
         *  here or not. */
        int Lpointer = TouchHandler.TouchDownAtRect(this);
        if (TouchHandler.TouchMap.get(Lpointer) == null)
            return -1;
        else return TouchHandler.TouchMap.get(Lpointer).TouchPosX;
    }
    public float TouchingY() {
        /** Reports the Y coord of a pointer here. Any pointer in the vicinity of this
         *  Rect will count as a touch, whether TouchHandler reports the touchdown
         *  here or not. */
        int Lpointer = TouchHandler.TouchDownAtRect(this);
        if (TouchHandler.TouchMap.get(Lpointer) == null)
            return -1;
        else return TouchHandler.TouchMap.get(Lpointer).TouchPosY;
    }


    private void SetPostAnim() {
        /** Sets the values of the rect before it is animated. */
        pt = t();
        pb = b();
        pl = l();
        pr = r();
    }

}


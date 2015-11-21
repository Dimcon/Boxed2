package com.Naos.Boxed;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daimon on 7/1/2015.
 */
public class RectList {
    VolatileAtomicList<ListItem> items = new VolatileAtomicList<ListItem>();
    Rect rOutline;
    float fVerVelocity = 0;
    Rect rListMover;
    float fVerPadding = 0,fHorPadding = 0;
    boolean bAtEdge = false;
    boolean bKeepAtBottom = false;
    boolean bDrawTopShadow = false;
    boolean bDrawBottomShadow = false;
    public RectList(DeltaBatch batch,List<ListItem> items,float fLeft, float fTop, float fRight, float fBottom) {
        this.items.Get().addAll(items);
        this.rOutline = new Rect(fLeft, fTop, fRight, fBottom);
        Init(batch);
    }

    public List<ListItem> GetItems() {
        return items.Get();
    }
    public RectList(DeltaBatch batch,float fLeft, float fTop, float fRight, float fBottom) {
        this.rOutline = new Rect(fLeft, fTop, fRight, fBottom);
        Init(batch);
    }

    public void EnableTopShadow(boolean bP,String sPathToTexture) {
        rltTerm = new ListTerminator(0,10*(rOutline.height()/100),rOutline.width(),0,sPathToTexture);
        bDrawTopShadow = true;
    }

    public void EnableBottomShadow(boolean bP,String sPathToTexture) {
        rltTerm = new ListTerminator(0,10*(rOutline.height()/100),rOutline.width(),0,sPathToTexture);
        bDrawBottomShadow = true;
    }

    public void dispose(DeltaBatch batch) {
        Screen.DisableClip(batch);
    }

    public void MoveTo(Rect r) {
        rListMover.setl(r.l());
        rListMover.setr(r.r());
        float fHeight = rListMover.height();
        float fTop = rListMover.t() - rOutline.t();
        this.rOutline.RectCopy(r);
        rListMover.sett(rOutline.t() + fTop);
        rListMover.setb((rOutline.t() + fTop) - fHeight);
    }

    public void Init(DeltaBatch batch) {
        Screen.EnableClip(batch);
        rListMover = rOutline.clone();
        fListOldTop = rListMover.t();
    }

    public void AddItem(ListItem rP) {
        items.Add(rP);
        float fTop = rListMover.t() - fVerPadding,fLeft = rListMover.l() + fHorPadding;
        for (ListItem r : items.Get()) {
            fTop -= (r.height() + fVerPadding);
        }
        rListMover.setb(fTop);
        if (bKeepAtBottom || bAtEdge) rListMover.MoveY(rOutline.b() - rListMover.b());
    }

    public void SetVertPadding(float fVertPadding) {
        this.fVerPadding =fVertPadding;
    }

    public void setHorPadding(float fHorPadding) {
        this.fHorPadding =fHorPadding;
    }

    ListTerminator rltTerm;

    public void Empty() {
        items.Clear();
    }

    public void Draw(DeltaBatch batch) {
        Screen.BeginClip(batch);
        Screen.ClipRect(rOutline);
        float fTop = rListMover.t() - fVerPadding,
                fLeft = rListMover.l() + fHorPadding;
        if (bDrawTopShadow) if (rltTerm.rTemp.IsInside(rOutline))rltTerm.DrawWithOffset(fLeft, rOutline.t() - rltTerm.height(), batch.batch);
        List<ListItem> get = items.Get();
        for (int i = 0; i < get.size(); i++) {
            ListItem r = get.get(i);
            r.rTemp.setb(fTop - r.height());
            r.rTemp.setl(fLeft);
            r.rTemp.setr(fLeft + r.width());
            r.rTemp.sett(fTop - r.height() + r.height());
            if (r.rTemp.IsInside(rOutline)) {
                r.DrawWithOffset(fLeft, fTop - r.height(), batch.batch);
            }
            fTop -= (r.height() + fVerPadding);
        }
        if (bDrawBottomShadow) if (rltTerm.rTemp.IsInside(rOutline))rltTerm.DrawWithOffset(fLeft, (fTop+fVerPadding) - rltTerm.height(), batch.batch);
        Screen.EndClip(batch);
        HandleTouches(rListMover);
    }
    float fListOldTop;
    boolean isTouched = false;
    float fFirstTouchY = 0;
    float fLastYTouchPos = 0;
    Rect rOnTouch;
    Rect rStill;
    float fMaxVelocity = 10f;
    int iItemTouched = 0;

    public void HandleTouches(Rect rList) {
        if (rList.IsTouched() && rOutline.IsTouched()) {
            if (isTouched) {
                rList.sett(rOnTouch.t());
                rList.setb(rOnTouch.b());
                rList.MoveY(rList.TouchingY() - fFirstTouchY);
                fVerVelocity = rList.TouchingY() - fLastYTouchPos;
                fLastYTouchPos = rList.TouchingY();
                bAtEdge = false;
            } else {
                for (int i = 0; i < items.Get().size(); i++) {
                    ListItem li = items.Get().get(i);
                    if (li.rTemp.IsTouching()) {
                        iItemTouched = i;
                        break;
                    }
                }
                rOnTouch = rList.clone();
                rStill = rOnTouch.clone();
                rStill.sett(rStill.b() + (Gdx.graphics.getHeight() * 0.001f));
                rStill.setb(rStill.b() - (Gdx.graphics.getHeight() * 0.001f));
                fFirstTouchY = rList.TouchingY();
                isTouched = true;
            }
        } else {
            if (isTouched) {
                isTouched = false;
                if (rStill.IsInside(new Rect(rList.l(),rList.b(),rList.r(),rList.b()))) {
                    if (items.Get().size() > 0) items.Get().get(iItemTouched).OnTouch();
                }
            } else {

            }
            if (fVerVelocity > 0) fVerVelocity = Math.max(0, fVerVelocity - (float)Math.sqrt(fVerVelocity * 0.1f));
            if (fVerVelocity < 0) fVerVelocity = Math.min(0, fVerVelocity - -(float)Math.sqrt(-fVerVelocity * 0.1f));
            if (rList.b() <= rOutline.b()) {
                rList.MoveY(fVerVelocity);
            } else if (rList.b() > rOutline.b()) {
                //rList.MoveY(rOutline.b() - rList.b());
                rList.MoveY(-(rList.b() - rOutline.b())/10);
                fVerVelocity = 0;
                bAtEdge = true;
            }
            if (rList.t() >= rOutline.t()) {
                rList.MoveY(fVerVelocity);
            } else if (rList.t() < rOutline.t()) {
                fVerVelocity = 0;
                rList.MoveY((rOutline.t() - rList.t()) /10);

            }
        }
    }


}

class ListTerminator extends ListItem {
    ListTerminator(float Left, float top, float right, float bottom,String sPathToTexture) {
        super(Left, top, right, bottom);
        tx = new Texture(sPathToTexture);
    }
}

class ListItem extends Rect {
    Rect rTemp;
    ListItem(float Left, float top, float right, float bottom) {
        super(Left, top, right, bottom);
        rTemp = new Rect(Left,top + height(),Left + width(),bottom);
    }
    Texture tx;

    public void OnTouch() {
        System.out.println("YOUCH!!");
    }

    public void DrawWithOffset(float fFromLeft, float fFromBottom,SpriteBatch sBtch) {
        //DrawWithOffsetWithAlpha(fFromLeft, fFromBottom, tx, sBtch, a);
    }
}

package com.Naos.Boxed;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Daimon on 11/21/2015.
 */
public class Game extends Screen {
    PlayerType playertype = null;
    Sprite spPlayer = new Sprite();
    Rect r;
    BoxGenerator bxGen;
    @Override
    public void OnBackKeyPress() {
        super.OnBackKeyPress();
    }

    @Override
    public void BeforeAll(DeltaBatch batch) {
        super.BeforeAll(batch);
    }

    @Override
    public void AfterAll(DeltaBatch batch) {
        super.AfterAll(batch);
    }

    @Override
    public Boolean Create(DeltaBatch batch) {
        CreateAgain(true);
        if (playertype == null) playertype = playertype.normal;
        r = new Rect(45*fXunit,(10*fYunit) + (10*fXunit),(55*fXunit),(10*fYunit));
        spPlayer = playertype.getSpriteFromRect(r);
        bxGen = new BoxGenerator((int)(0.5f*fYunit),10,(int)(100*fYunit));
        return super.Create(batch);
    }

    private void OnDeath(int iCause) {

    }

    @Override
    public Boolean AnimIn(DeltaBatch batch) {
        return super.AnimIn(batch);
    }

    @Override
    public Boolean Draw(DeltaBatch batch) {
        spPlayer.draw(batch.batch);
        bxGen.Update();
        bxGen.Draw(batch);

        if (rFullscreen.IsTouched()) {
            float fX = rFullscreen.TouchedX();
            float fY = rFullscreen.TouchedY();
            float fGradient = (float)/*Math.toDegrees*/(/*Math.atan*/(((fY - spPlayer.getY())/(fX - spPlayer.getX()))));
            bxGen.LineImpact(spPlayer.getX(),spPlayer.getY(),fGradient);
        }
        return super.Draw(batch);
    }

    @Override
    public Boolean AnimOut(DeltaBatch batch) {
        return super.AnimOut(batch);
    }

    @Override
    public Boolean Destroy(DeltaBatch batch) {
        return super.Destroy(batch);
    }

    class BoxGenerator {
        Texture imgBox1 = new Texture("badlogic.jpg");
        int iSpeed = 2;
        int iCrowdedness = 10;
        List<Box> BOXES = new ArrayList<Box>();
        List<Integer> iSpawnPoints = new ArrayList<Integer>();
        int iNumBoxTypes = 1;
        int iDivisions = 15;
        int iMetre = (int)((100*fXunit) / (iDivisions));
        int iStartY = 0;
        int iStartX = 0;
        int iSpaceTimer = 0;

        public BoxGenerator(int iSpeed, int iCrowdedness,int fStartY) {
            this.iSpeed = iSpeed;
            this.iStartY = fStartY;
            this.iCrowdedness = iCrowdedness;
            for (int i = 0; i < iDivisions ; i++) {
                iSpawnPoints.add(0);
            }
        }

        public float[] LineImpact(float fOriginX, float fOriginY,float fAngle) {
            for (Box b: BOXES) {
                float[] fImpact = b.Impact(fOriginX,fOriginY,fAngle);
                if (fImpact[0] >= 0) {
                    return fImpact;
                }
            }
            float[] f = {-1,-1};
            return f;
        }

        public void Update() {
            boolean bUpdatePoints = false;
            for (Box b : BOXES) {
                b.translateY(-iSpeed);
            }
            if (iSpaceTimer > 0) {
                iSpaceTimer -= iSpeed;
            } else {
                bUpdatePoints = true;
                iSpaceTimer = iMetre;
            }
            for (int i = 0; i < iSpawnPoints.size();i++) {
                // Work through the spawnpoints form the bottom up
                int iWaitTime = iSpawnPoints.get(i);
                if (iWaitTime == 0) { // Need to spawn boxes
                    int iHorSpace = 1;
                    // Find out how much space you have going up
                    for (int j = i + 1; j < iSpawnPoints.size();j++) {
                        if (iSpawnPoints.get(j) != 0) {
                            break;
                        } else {
                            iHorSpace++;
                        }
                    } // Found it

                    Box b = null;
                    int iBoxType = new Random().nextInt(iNumBoxTypes) + 1;
                    int iXSize = new Random().nextInt(iHorSpace) + 1;
                    int iYsize = new Random().nextInt(iHorSpace) + 1;
                    switch (iBoxType) {
                        case 1:
                            b = new Box(imgBox1,iMetre,iXSize,iYsize,iStartX+ (i*iMetre),iStartY );
                            iXSize = Math.min(iXSize,b.MaxSize());
                            iYsize = Math.min(iYsize,b.MaxSize());
                    }
                    BOXES.add(b);
                    for (int k = i; k < i + iXSize;k++) {
                        iSpawnPoints.set(k, iYsize);
                    }
                } else if (bUpdatePoints){
                    iSpawnPoints.set(i,iWaitTime-1);
                }
            }
        }

        public void Draw(DeltaBatch batch) {
            for (Box b: BOXES) {
                b.Animate(batch);
            }
        }

        class Box extends Sprite {
            int iXsize, iYsize;
            float fMetre;
            boolean bHit = false;

            public float[] Impact(float fOriginX, float fOriginY, float fAngle) {
                Rect r = new Rect(getX(),getY() + getHeight(),getX() + getWidth(), getY());
                float fC = fOriginY - (fOriginX * (float)fAngle);
                float fXPos = (float)((r.b() - fC)/(fAngle));
                if (fXPos > r.l() && fXPos < r.r() && !bHit) {
                    float[] f = {fXPos,r.b()};
                    setAlpha(0.1f);
                    bHit = true;
                    return f;
                } else {
                    float[] f = {-1,-1};
                    return f;
                }
            }

            Box(Texture tx, float fMetre,int iXSize, int iYsize, float fStartPointx, float fStartPointy) {
                super(tx);
                this.fMetre = fMetre;
                this.iXsize = Math.min(iXSize,MaxSize());
                this.iYsize = Math.min(iYsize,MaxSize());
                Rect r = new Rect(fStartPointx,fStartPointy + (this.iYsize*fMetre),
                        fStartPointx + (this.iXsize*fMetre),fStartPointy);
                setPosition(r.l(), r.b());
                setOrigin(0, 0);
                setScale(r.width() / tx.getWidth(), r.height() / tx.getHeight());
            }
            public void Animate(DeltaBatch batch) {
                this.draw(batch.batch);
            }
            public int MaxSize() {
                return 4;
            }
            public int MinSize() {
                return 1;
            }
            public boolean MustRetainProportion() {
                return true;
            }
        }
    }

}

enum PlayerType {
    normal;
    Texture imgTexture;
    public Sprite getSpriteFromRect(Rect r) {
        switch (this) {
            case normal:
                imgTexture = new Texture("badlogic.jpg");
                return r.ToSprite(imgTexture);
        }
        return null;
    }
}

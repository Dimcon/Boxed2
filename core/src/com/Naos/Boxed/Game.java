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
        r = new Rect(20*fXunit,54*fYunit,(20*fXunit) + 8*fYunit,46*fYunit);
        spPlayer = playertype.getSpriteFromRect(r);
        bxGen = new BoxGenerator(1,10,(int)(100*fXunit));
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
        int iDivisions = 10;
        int iMetre = (int)(100*fYunit) / (iDivisions + 4);
        int iStartY = 2 * iMetre;
        int iStartX = 0;
        int iSpaceTimer = 0;

        public BoxGenerator(int iSpeed, int iCrowdedness,int fStartX) {
            this.iSpeed = iSpeed;
            this.iStartX = fStartX;
            this.iCrowdedness = iCrowdedness;
            for (int i = 0; i < 10 ; i++) {
                iSpawnPoints.add(0);
            }
        }

        public void Update() {
            boolean bUpdatePoints = false;
            for (Box b : BOXES) {
                b.translateX(-iSpeed);
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
                    int iVertSpace = 1;
                    // Find out how much space you have going up
                    for (int j = i + 1; j < iSpawnPoints.size();j++) {
                        if (iSpawnPoints.get(j) != 0) {
                            break;
                        } else {
                            iVertSpace++;
                        }
                    } // Found it

                    Box b = null;
                    int iBoxType = new Random().nextInt(iNumBoxTypes) + 1;
                    int iXSize = new Random().nextInt(iVertSpace) + 1;
                    int iYsize = new Random().nextInt(iVertSpace) + 1;
                    switch (iBoxType) {
                        case 1:
                            b = new Box(imgBox1,iMetre,iXSize,iYsize,iStartX,iStartY + (i*iMetre));
                            iXSize = Math.min(iXSize,b.MaxSize());
                            iYsize = Math.min(iYsize,b.MaxSize());
                    }
                    BOXES.add(b);
                    for (int k = i; k < i + iYsize;k++) {
                        iSpawnPoints.set(k, iXSize);
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

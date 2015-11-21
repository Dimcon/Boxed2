package com.Naos.Boxed;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

import java.awt.Point;
import java.util.HashMap;

/**
 * Created by daimonsewell on 7/11/14.
 */
public class TouchHandler implements InputProcessor {
    int ScreenX = Gdx.graphics.getWidth(),ScreenY= Gdx.graphics.getHeight();
    public static HashMap<Integer,TouchID> TouchMap = new HashMap<Integer,TouchID>();
    public static HashMap<Integer,TouchID> TouchDown = new HashMap<Integer,TouchID>();

    public static boolean PointerExists(int Pointer) {
        return (TouchMap.get(Pointer) != null);
    }

    public static boolean PointerIsHere(int Pointer) {
        return (TouchMap.get(Pointer) != null);
    }

    public static int TouchDownAtRect(Rect rTest) {
        for (Integer key : TouchMap.keySet()) {
            if (rTest.IsInside(new Rect(TouchDown.get(key).TouchPosX,
                    TouchDown.get(key).TouchPosY,
                    TouchDown.get(key).TouchPosX,
                    TouchDown.get(key).TouchPosY))) {
                return key;
            }
        }
        return -1;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (ScreenManager.batch.DrawStage.keyDown(keycode)) {
            return true;
        }
        if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
            ScreenManager.OnBackKeyPress();
        }
        if (keycode == Input.Keys.SPACE) {
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (ScreenManager.batch.DrawStage.keyUp(keycode)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if (ScreenManager.batch.DrawStage.keyTyped(character)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (ScreenManager.batch.DrawStage.touchDown(screenX,screenY,pointer,button)) {
            //return true;
        }
        TouchMap.put(pointer,new TouchID(screenX,ScreenY-screenY));
        TouchDown.put(pointer,new TouchID(screenX,ScreenY-screenY));
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (ScreenManager.batch.DrawStage.touchUp(screenX,screenY,pointer,button)) {
            //return true;
        }
        TouchMap.remove(pointer);
        TouchDown.remove(pointer);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (ScreenManager.batch.DrawStage.touchDragged(screenX,screenY,pointer)) {
            //return true;
        }
        TouchMap.get(pointer).TouchPosX = screenX;
        TouchMap.get(pointer).TouchPosY = ScreenY - screenY;
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

class TouchID {
    float TouchPosX, TouchPosY;

    TouchID(float touchPosX, float touchPosY) {
        TouchPosX = touchPosX;
        TouchPosY = touchPosY;
    }
}
package com.Naos.Boxed;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

import java.util.HashMap;

/**
 * Created by daimonsewell on 7/10/14.
 */
public class ScreenManager {
    static HashMap<String,Screen> ScreenStore = new HashMap<String, Screen>();
    static long Now = 000000000;
    static final long
                FramesPerSecond = 60,
                UpdateRate = 100000000/FramesPerSecond;
    static DeltaBatch batch = new DeltaBatch();
    static SpriteBatch Sbatch = new SpriteBatch();
    static int RectsPerRound = 0,SmallImagesCalled,LargeImagesCalled;
    static boolean Enabled3D = false;
    static final String DESKTOP_TAG = "LIBGDX_NAOS_DESKTOP_APP";
    static final String ANDROID_TAG = "LIBGDX_NAOS_ANDROID_APP";
    static final String OTHER_DEVICE_TAG = "LIBGDX_NAOS_NOTANDROIDDESKTOP_APP";
    static String DeviceType = "";


    public static void FirstScreen(Screen First,String sNameOfScreen,boolean b3DEnabled) {
        if ((Gdx.app.getType() + "").equals("Desktop")) {
            DeviceType = DESKTOP_TAG;
        } else if (Gdx.app.getType().equals("Android ")) {
            DeviceType = ANDROID_TAG;
        } else {
            DeviceType = OTHER_DEVICE_TAG;
        }
        Gdx.input.setInputProcessor(batch.toucher);
        First.stage = CycleStage.Create;
        AddScreen(First,sNameOfScreen);
        batch.batch = Sbatch;
        if (b3DEnabled) Setup3D();
        Enabled3D = b3DEnabled;
    }
    public static boolean isAndroid() {
        return DeviceType.equals(ANDROID_TAG);
    }
    public static boolean isDesktop() {
        return DeviceType.equals(DESKTOP_TAG);
    }

    private static void Setup3D() {
        batch.mdlBatch = new ModelBatch();
        batch.mdbModelBuilder = new ModelBuilder();
        batch.pcrCamera = new PerspectiveCamera(67,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        batch.pcrCamera.position.set(10f,10f,10f);
        batch.pcrCamera.lookAt(0,0,0);
        batch.pcrCamera.near = 1f;
        batch.pcrCamera.far = 300f;
        batch.pcrCamera.update();
    }

    public static void OnBackKeyPress() {
        for (String key : ScreenStore.keySet()) {
            Screen LocalScreen = ScreenStore.get(key);
            if (LocalScreen.stage == CycleStage.Draw) {
                LocalScreen.OnBackKeyPress();
            }
        }
    }

    public static void AddScreen(Screen NewScreen,String sNameOfScreen) {
        NewScreen.Name = sNameOfScreen;
        ScreenStore.put(NewScreen.Name,NewScreen);
    }
    public  static void RemoveScreen(String sName) {
        ScreenStore.remove(sName);
    }

    public  static void StartScreen(String sScreen) {
        ScreenStore.get(sScreen).stage = CycleStage .Create;
    }
    public  static void EndScreen(String sScreen) {
        ScreenStore.get(sScreen).stage = CycleStage .AnimateOut;
    }
    public  static void KillScreen(String sScreen) {
        ScreenStore.get(sScreen).stage = CycleStage .Destroy;
    }

    public static void Destroy() {
        batch.Destroy();
    }

    public static void Update() {
        Rect.RectsDrawn = 0;
        batch.Delta = (System.nanoTime() - Now)/UpdateRate;
        Now = System.nanoTime();
        //////// Iterate through each screen.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glViewport(0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        for (String key : ScreenStore.keySet()) {
            /** Allow Clipping all drawing to rDisplay */
            batch.batch.begin();
            Screen LocalScreen = ScreenStore.get(key);
            if (LocalScreen.ClipToRDisplay && !(LocalScreen.b3dEnabled && Enabled3D)) {
                LocalScreen.BeginClip(batch);
                LocalScreen.ClipRect(ScreenStore.get(key).rDisplay);
            }
            if (LocalScreen.b3dEnabled&& Enabled3D) {
                batch.batch.end();
                batch.mdlBatch.begin(batch.pcrCamera);
            }
            if (!(LocalScreen.b3dEnabled&& Enabled3D)) batch.batch.setColor(batch.batch.getColor().r,batch.batch.getColor().g,batch.batch.getColor().b,LocalScreen.rDisplay.a);
            /** NB NB NB NB NB
             * Multiple batch sessions in a single frame may decrease performance dramatically. */
            switch (LocalScreen.stage) {
                case Deactivated: default:
                    break;
                case Create:
                    if (!LocalScreen.Created || LocalScreen.CreateAgain) {
                        if (LocalScreen.Create(batch)) {
                            LocalScreen.stage = CycleStage.AnimateIn;
                        }
                        LocalScreen.Created = true;
                    } else {
                        LocalScreen.stage = CycleStage.AnimateIn;
                    }
                    if (LocalScreen.Debugger) {
                        LocalScreen.CreateDebug();
                    }
                    break;
                case AnimateIn:
                    LocalScreen.BeforeAll(batch);
                    if (LocalScreen.AnimIn(batch))
                    {LocalScreen.stage = CycleStage .Draw;} else
                        LocalScreen.AfterAll(batch);
                    break;
                case Draw:
                    LocalScreen.BeforeAll(batch);
                    if (LocalScreen.Draw(batch))
                    {LocalScreen.stage = CycleStage .AnimateOut;}else
                        LocalScreen.AfterAll(batch);
                    break;
                case AnimateOut:
                    LocalScreen.BeforeAll(batch);
                    if (LocalScreen.AnimOut(batch))
                    {LocalScreen.stage = CycleStage .Destroy;} else
                        LocalScreen.AfterAll(batch);
                    break;
                case Destroy:
                    if (LocalScreen.Destroy(batch))
                    {LocalScreen.stage = CycleStage .Deactivated;}
                    break;
            }
            LocalScreen.ResetUnits();
            if (LocalScreen.Debugger && LocalScreen.stage != CycleStage.Deactivated) {
                LocalScreen.DrawDebug(batch.batch);
            }
            if (LocalScreen.b3dEnabled && Enabled3D) {
                batch.mdlBatch.end();
            } else {
                batch.batch.end();
            }
            if (LocalScreen.ClipToRDisplay && !(LocalScreen.b3dEnabled && Enabled3D)) {
                LocalScreen.EndClip(batch);
            }
        }//////////////////////////////////
        RectsPerRound = Rect.RectsDrawn;
        batch.DrawStage.act();
        batch.DrawStage.draw();

    }
}

class DeltaBatch {
    SpriteBatch batch;
    ModelBuilder mdbModelBuilder;
    ModelBatch mdlBatch;
    PerspectiveCamera pcrCamera;
    float Delta;
    Stage DrawStage = new Stage();
    TouchHandler toucher = new TouchHandler();
    AssetManager Assets = new AssetManager();

    public void Destroy() {
        batch.dispose();
        mdlBatch.dispose();
        DrawStage.dispose();
        Assets.dispose();
    }
}

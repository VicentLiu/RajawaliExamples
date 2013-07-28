package com.monyetmabuk.rajawali.tutorials.examples.general;

import java.util.ArrayList;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import rajawali.BaseObject3D;
import rajawali.Camera;
import rajawali.animation.Animation3D;
import rajawali.animation.EllipticalOrbitAnimation3D;
import rajawali.animation.EllipticalOrbitAnimation3D.OrbitDirection;
import rajawali.lights.DirectionalLight;
import rajawali.materials.DiffuseMaterial;
import rajawali.math.vector.Vector3;
import rajawali.primitives.Cube;
import rajawali.primitives.Sphere;
import rajawali.scene.RajawaliScene;
import rajawali.scene.scenegraph.IGraphNode.GRAPH_TYPE;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.monyetmabuk.rajawali.tutorials.examples.AExampleFragment;

public class SceneFragment extends AExampleFragment implements OnClickListener {

private SceneRenderer mRenderer;
	
	private Button mAddObject;
	private Button mRemoveObject;
	private Button mSwitchCamera;
	private Button mSwitchScene;
	private Button mNextFrame; 
	
	private TextView mObjectCount;
	private TextView mTriCount;
	
	private LinearLayout mLL;
	
	@Override
	protected AExampleRenderer createRenderer() {
		return new SceneRenderer(getActivity(), new Handler(), mObjectCount, mTriCount);
	}
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*LinearLayout ll = new LinearLayout(this.getActivity());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);

        mAddObject = new Button(getApplicationContext());
        mAddObject.setGravity(Gravity.CENTER);
        mAddObject.setHeight(50);
        mAddObject.setWidth(200);
        mAddObject.setOnClickListener(this);
        mAddObject.setText("Add Object");
        ll.addView(mAddObject);
        
        mRemoveObject = new Button(getApplicationContext());
        mRemoveObject.setGravity(Gravity.CENTER);
        mRemoveObject.setHeight(50);
        mRemoveObject.setWidth(200);
        mRemoveObject.setOnClickListener(this);
        mRemoveObject.setText("Remove Object");
        ll.addView(mRemoveObject);
        
        mSwitchCamera = new Button(getApplicationContext());
        mSwitchCamera.setGravity(Gravity.CENTER);
        mSwitchCamera.setHeight(50);
        mSwitchCamera.setWidth(200);
        mSwitchCamera.setOnClickListener(this);
        mSwitchCamera.setText("Switch Camera");
        ll.addView(mSwitchCamera);
        
        mSwitchScene = new Button(getApplicationContext());
        mSwitchScene.setGravity(Gravity.CENTER);
        mSwitchScene.setHeight(50);
        mSwitchScene.setWidth(200);
        mSwitchScene.setOnClickListener(this);
        mSwitchScene.setText("Switch Scene");
        ll.addView(mSwitchScene);
        
        mNextFrame = new Button(getApplicationContext());
        mNextFrame.setGravity(Gravity.CENTER);
        mNextFrame.setHeight(50);
        mNextFrame.setWidth(200);
        mNextFrame.setOnClickListener(this);
        mNextFrame.setText("Next Frame");
        ll.addView(mNextFrame);
        
        TextView objectCount = new TextView(getApplicationContext());
        objectCount.setGravity(Gravity.CENTER);
        objectCount.setText("Object Count: 0");
        ll.addView(objectCount);
        
        TextView triCount = new TextView(getApplicationContext());
        triCount.setGravity(Gravity.CENTER);
        triCount.setText("   Triangle Count: 0");
        ll.addView(triCount);
        
        mLayout.addView(ll);
        
        mSurfaceView.setOnTouchListener(this);*/
    }

	@Override
	public void onClick(View arg0) {
		if (arg0.equals(mAddObject)) {
			//Add an object
			mRenderer.addObject(0, 0);
		} else if (arg0.equals(mRemoveObject)) {
			mRenderer.removeObject();
		} else if (arg0.equals(mSwitchCamera)) {
			mRenderer.nextCamera();
		} else if (arg0.equals(mSwitchScene)) {
			mRenderer.nextScene();
		} else if (arg0.equals(mNextFrame)) {
			mRenderer.nextFrame();
		}
	}

	private final class SceneRenderer extends AExampleRenderer {

		private DirectionalLight mLight1, mLight2;
		private DiffuseMaterial mMaterial;
		private BaseObject3D mInitialSphere;
		private BaseObject3D mInitialCube;
		private BaseObject3D mPoint;
		private EllipticalOrbitAnimation3D mCameraAnim;
		private Vector3 mFocal;
		private Vector3 mPeriapsis;

		private RajawaliScene mScene1;
		private RajawaliScene mScene2; 

		private Handler mHandler;
		private TextView mObjectCount;
		private TextView mTriCount;

		private Camera mCamera1;
		private Camera mCamera2;

		private Random mRandom = new Random();
		private ArrayList<BaseObject3D> mSpheres = new ArrayList<BaseObject3D>();
		private ArrayList<BaseObject3D> mCubes = new ArrayList<BaseObject3D>();

		public SceneRenderer(Context context, Handler handler, TextView obj, TextView tri) {
			super(context);
			setFrameRate(60);
			mHandler = handler;
			mObjectCount = obj;
			mTriCount = tri;
		}

		protected void initScene() {
			mCamera1 = getCurrentCamera(); //We will utilize the initial camera
			mCamera1.setPosition(10, 0, 10);
			mCamera1.setLookAt(0, 0, 10);
			mCamera1.setFieldOfView(60);
			mCamera1.setNearPlane(1);
			mCamera1.setFarPlane(50);

			mCamera2 = new Camera(); //Lets create a second camera for the scene.
			mCamera2.setPosition(0, 3, 10);
			mCamera2.setLookAt(0.0f, 0.0f, 0.0f);
			mCamera2.setFarPlane(15);
			mCamera2.setFieldOfView(30);

			//We are going to use our own scene, not the default
			mScene1 = new RajawaliScene(this, GRAPH_TYPE.OCTREE); 
			mScene1.displaySceneGraph(true);
			//Since we created a new scene, it has a default camera we need to replace
			mScene1.replaceAndSwitchCamera(mCamera1, 0); 
			mScene1.addCamera(mCamera2); //Add our second camera to the scene
			//mScene1.switchCamera(mCamera2);

			//We are creating a second scene
			mScene2 = new RajawaliScene(this, GRAPH_TYPE.OCTREE); 
			mScene2.displaySceneGraph(true);
			//Since we created a new scene, it has a default camera we need to replace
			mScene2.replaceAndSwitchCamera(mCamera1, 0);
			//mScene2.addCamera(mCamera2); //Add our second camera to the scene

			mLight1 = new DirectionalLight(0.75f, 0, 1);
			mLight2 = new DirectionalLight(0.75f, 0, -1);

			mMaterial = new DiffuseMaterial();
			mMaterial.setUseSingleColor(true);

			mInitialSphere = new Sphere(1, 10, 10);
			mInitialSphere.setScale(0.250f);
			mInitialSphere.setColor(0xFF00BFFF);
			mInitialSphere.setMaterial(mMaterial);
			mInitialSphere.addLight(mLight1);
			mInitialSphere.addLight(mLight2);
			mInitialSphere.setPosition(0, 1, 0);
			mInitialSphere.setRotation(45f, 45f, 45f);

			mInitialCube = new Cube(1);
			//mInitialCube.setScale(0.150f);
			mInitialCube.setColor(0xFF00BFFF);
			mInitialCube.setMaterial(mMaterial);
			mInitialCube.addLight(mLight1);
			mInitialCube.addLight(mLight2);
			mInitialCube.setPosition(0, 0, 0);
			mInitialCube.setRotation(45f, 45f, 45f);

			mPoint = new Sphere(1, 10, 10);
			mPoint.setScale(0.1250f);
			mPoint.setColor(0xFF00FFFF);
			mPoint.setMaterial(mMaterial);
			mPoint.addLight(mLight1);
			mPoint.addLight(mLight2);
			mPoint.setPosition(mCamera2.getPosition());
			mScene1.addChild(mPoint);

			mSpheres.add(mInitialSphere);
			mCubes.add(mInitialCube);
			mScene1.addChild(mInitialCube); //Add our cube to scene 1
			mScene2.addChild(mInitialSphere); //Add our sphere to scene 2

			Animation3D anim = new EllipticalOrbitAnimation3D(new Vector3(5, 1.5, -4), new Vector3(-5, 1.5, -4), 0.0,
					360, OrbitDirection.CLOCKWISE);
			//Create a camera animation for camera 1
			mFocal = new Vector3(0, 0, 0);
			mPeriapsis = new Vector3(0, 0, 20);
			mCameraAnim = new EllipticalOrbitAnimation3D(mFocal, mPeriapsis, 0.0,
					360, OrbitDirection.CLOCKWISE);
			mCameraAnim.setDuration(20000);
			mCameraAnim.setRepeatMode(Animation3D.RepeatMode.INFINITE);
			mCameraAnim.setTransformable3D(mCamera1);
			mCameraAnim.play();
			//Register the animation with BOTH scenes
			mScene1.registerAnimation(mCameraAnim);
			mScene2.registerAnimation(mCameraAnim);
			anim.setDuration(10000);
			anim.setRepeatMode(Animation3D.RepeatMode.REVERSE_INFINITE);
			anim.setTransformable3D(mInitialCube);
			//anim.play();
			//mScene1.registerAnimation(anim);

			//Replace the default scene with our scene 1 and switch to it
			replaceAndSwitchScene(getCurrentScene(), mScene1);
			//Add scene 2 to the renderer
			addScene(mScene2);
		}

		public void onDrawFrame(GL10 glUnused) {
			super.onDrawFrame(glUnused);
			Vector3 tMin = getCurrentScene().getSceneMinBound();
			Vector3 tMax = getCurrentScene().getSceneMaxBound();
			/*mFocal.x = tMin.x + (tMax.x - tMin.x) * .5f;
			mFocal.y = tMin.y + (tMax.y - tMin.y) * .5f;
			mFocal.z = tMin.z + (tMax.z - tMin.z) * .5f;*/
			//mPeriapsis.y = mFocal.y;
			//mPeriapsis.x = mFocal.x;
			mCamera1.setLookAt(mFocal);
			/*if (mFrameCount % 20 == 0) { 
				mHandler.post(new Runnable() {
					public void run() {
						mObjectCount.setText("Object Count: " + getCurrentScene().getNumChildren());
						mTriCount.setText("   Triangle Count: " + getCurrentScene().getNumTriangles());
					}
				});
			}*/
		}

		public void addObject(float x, float y) {
			BaseObject3D obj = null;
			if (getCurrentScene().equals(mScene2)) {
				obj = new Sphere(1, 10, 10);
				mSpheres.add(obj);
			} else if (getCurrentScene().equals(mScene1)) {
				obj = new Cube(1);
				mCubes.add(obj);
			}
			obj.addLight(mLight1);
			obj.addLight(mLight2);
			obj.setMaterial(mMaterial);
			obj.setScale(mRandom.nextFloat()*0.5f+0.1f);
			obj.setColor(new Vector3(mRandom.nextInt(255), mRandom.nextInt(255), mRandom.nextInt(255)));
			boolean positive = mRandom.nextBoolean();
			int sign1 = 1;
			int sign2 = 1;
			if (positive) {sign1 = 1;} else {sign1 = -1;}
			positive = mRandom.nextBoolean();
			if (positive) {sign2 = 1;} else {sign2 = -1;}
			obj.setPosition(sign1*mRandom.nextFloat()*4, sign2*mRandom.nextFloat()*2, -mRandom.nextFloat()*10);
			obj.setRotation(45f, 45f, 45f);
			addChild(obj);
		}

		public void removeObject() {
			if (getCurrentScene().equals(mScene2)) {
				if (!mCubes.isEmpty()) {
					BaseObject3D child = mSpheres.get(0);
					removeChild(child);
					mSpheres.remove(child);
				}
			} else if (getCurrentScene().equals(mScene1)) {
				if (!mCubes.isEmpty()) {
					BaseObject3D child = mCubes.get(0);
					removeChild(child);
					mCubes.remove(child);
				}
			}
		}

		public void nextCamera() {
			if (getCurrentCamera().equals(mCamera1)) {
				getCurrentScene().switchCamera(mCamera2);
			} else {
				getCurrentScene().switchCamera(mCamera1);
			}
		}

		public void nextScene() {
			if (getCurrentScene().equals(mScene1)) {
				switchScene(mScene2);
			} else {
				switchScene(mScene1);
			}
		}

		public void nextFrame() {
			//this.stopRendering();
			mSurfaceView.requestRender();
		}

	}

}
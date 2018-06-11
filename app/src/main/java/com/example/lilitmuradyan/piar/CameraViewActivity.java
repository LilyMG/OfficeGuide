package com.example.lilitmuradyan.piar;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.location.Location;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.location.LocationListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraViewActivity extends Activity implements
		SurfaceHolder.Callback, OnLocationChangedListener, OnAzimuthChangedListener, LocationListener{

	private Camera mCamera;
	private SurfaceHolder mSurfaceHolder;
	private boolean isCameraviewOn = false;
	private AugmentedPOI mPoi;

	private double mAzimuthReal = 0;
	private double mAzimuthTeoretical = 0;
	private static double AZIMUTH_ACCURACY = 5;
	private double mMyLatitude = 0;
	private double mMyLongitude = 0;

	private MyCurrentAzimuth myCurrentAzimuth;
	private MyCurrentLocation myCurrentLocation;

	private LottieAnimationView lottieAnimationView;

	TextView descriptionTextView;
	ImageView pointerIcon;
	ArrayList<PAItem> depItems = new ArrayList<>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_view);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		setupListeners();
		setupLayout();
		setAugmentedRealityPoint();
		setupLottieView();
		init();
	}

	void init(){
		depItems.add(new PAItem(40.1964590, 44.4796499, "Camera"));
		depItems.add(new PAItem(40.1964664, 44.4796063, "Labs"));
		depItems.add(new PAItem(40.1964859, 44.4796397, "Drawing"));
		depItems.add(new PAItem(40.1964776, 44.4796431, "Editor"));
		depItems.add(new PAItem(40.1964661, 44.4796799, "Image processing"));
		depItems.add(new PAItem(40.1964657, 44.4797144, "Seo Localization"));
		depItems.add(new PAItem(40.1964686, 44.4796803, "Profile networking"));
		depItems.add(new PAItem(40.1964706, 44.4797161, "Challenges Messaging"));
		depItems.add(new PAItem(40.1964696, 44.4797488, "Creative Team"));
		depItems.add(new PAItem(40.1964560, 44.4797053, "Search"));
		depItems.add(new PAItem(40.1964270, 44.4797496, "Web"));
		depItems.add(new PAItem(40.1964761, 44.4797462, "Automation"));
		depItems.add(new PAItem(40.1964596, 44.4798011, "Ops"));
		depItems.add(new PAItem(40.1964890, 44.48800704, "Support"));
		depItems.add(new PAItem(40.1964874, 44.48800696, "Support"));
		depItems.add(new PAItem(40.1964874, 44.48800696, "CMS"));
		depItems.add(new PAItem(40.1964913, 44.48800866, "Shop"));
		depItems.add(new PAItem(40.1964923, 44.48800849, "Shop"));
		depItems.add(new PAItem(40.1964980, 44.48012460, "Analytics"));
		depItems.add(new PAItem(40.1964952, 44.48012450, "Ads"));
		depItems.add(new PAItem(40.1964927, 44.48012468, "Engagement"));
		depItems.add(new PAItem(40.1965054, 44.48011820, "Share"));
		depItems.add(new PAItem(40.1964921, 44.4801421, "LifeCycle"));
	}

	private void setAugmentedRealityPoint() {
		mPoi = new AugmentedPOI(
				"test",
				"desc",
				50.06169631,
				19.93919566
		);
	}

	public double calculateTeoreticalAzimuth() {
		double dX = mPoi.getPoiLatitude() - mMyLatitude;
		double dY = mPoi.getPoiLongitude() - mMyLongitude;

		double phiAngle;
		double tanPhi;
		double azimuth = 0;

		tanPhi = Math.abs(dY / dX);
		phiAngle = Math.atan(tanPhi);
		phiAngle = Math.toDegrees(phiAngle);

		if (dX > 0 && dY > 0) { // I quater
			return azimuth = phiAngle;
		} else if (dX < 0 && dY > 0) { // II
			return azimuth = 180 - phiAngle;
		} else if (dX < 0 && dY < 0) { // III
			return azimuth = 180 + phiAngle;
		} else if (dX > 0 && dY < 0) { // IV
			return azimuth = 360 - phiAngle;
		}

		return phiAngle;
	}
	
	private List<Double> calculateAzimuthAccuracy(double azimuth) {
		double minAngle = azimuth - AZIMUTH_ACCURACY;
		double maxAngle = azimuth + AZIMUTH_ACCURACY;
		List<Double> minMax = new ArrayList<Double>();

		if (minAngle < 0)
			minAngle += 360;

		if (maxAngle >= 360)
			maxAngle -= 360;

		minMax.clear();
		minMax.add(minAngle);
		minMax.add(maxAngle);

		return minMax;
	}

	private boolean isBetween(double minAngle, double maxAngle, double azimuth) {
		if (minAngle > maxAngle) {
			if (isBetween(0, maxAngle, azimuth) && isBetween(minAngle, 360, azimuth))
				return true;
		} else {
			if (azimuth > minAngle && azimuth < maxAngle)
				return true;
		}
		return false;
	}

	private void updateDescription() {
		descriptionTextView.setText(mPoi.getPoiName() + " azimuthTeoretical "
				+ mAzimuthTeoretical + " azimuthReal " + mAzimuthReal + " latitude "
				+ mMyLatitude + " longitude " + mMyLongitude);
	}

	@Override
	public void onLocationChanged(Location location) {
		mMyLatitude = location.getLatitude();
		mMyLongitude = location.getLongitude();
		mAzimuthTeoretical = calculateTeoreticalAzimuth();

//		Toast.makeText(this,"latitude: "+findItem().latitude+" longitude: "+findItem().longitude + findItem().name + " area", Toast.LENGTH_SHORT).show();
		Toast.makeText(this,findItem().name + " area", Toast.LENGTH_SHORT).show();
//		Toast.makeText(this,"latitude: "+location.getLatitude()+" longitude: "+location.getLongitude(), Toast.LENGTH_SHORT).show();
		updateDescription();
	}

	PAItem findItem() {
		PAItem result = new PAItem(0, 0, "PA office area");

		for (int i = 0; i < depItems.size(); i++) {
			PAItem paItem = depItems.get(i);
			if (paItem.latitude > mMyLatitude - 0.00001 && paItem.latitude < mMyLatitude + 0.00001
					&& paItem.longitude > mMyLongitude - 0.00001 && paItem.longitude < mMyLongitude + 0.00001) {
				result = paItem;
			}
		}
		return result;
	}

	@Override
	public void onAzimuthChanged(float azimuthChangedFrom, float azimuthChangedTo) {
		mAzimuthReal = azimuthChangedTo;
		mAzimuthTeoretical = calculateTeoreticalAzimuth();

		pointerIcon = (ImageView) findViewById(R.id.icon);

		double minAngle = calculateAzimuthAccuracy(mAzimuthTeoretical).get(0);
		double maxAngle = calculateAzimuthAccuracy(mAzimuthTeoretical).get(1);

		if (isBetween(minAngle, maxAngle, mAzimuthReal)) {
			pointerIcon.setVisibility(View.VISIBLE);
		} else {
			pointerIcon.setVisibility(View.INVISIBLE);
		}

		updateDescription();
		Toast.makeText(this, "azimuth changed" + "latitude: " + mMyLatitude + " longitude: " + mMyLongitude, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onStop() {
		myCurrentAzimuth.stop();
		myCurrentLocation.stop();
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		myCurrentAzimuth.start();
		myCurrentLocation.start();
	}

	private void setupListeners() {
		myCurrentLocation = new MyCurrentLocation(this);
		myCurrentLocation.buildGoogleApiClient(this);
		myCurrentLocation.start();

		myCurrentAzimuth = new MyCurrentAzimuth(this, this);
		myCurrentAzimuth.start();
	}

	private void setupLayout() {
		descriptionTextView = (TextView) findViewById(R.id.cameraTextView);

		getWindow().setFormat(PixelFormat.UNKNOWN);
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.cameraview);
		mSurfaceHolder = surfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(CameraViewActivity.this, "sorry, search is not available yet", Toast.LENGTH_SHORT).show();
			}
		});

		(findViewById(R.id.picsart)).animate().alpha(0).setDuration(400).setStartDelay(5000);
		(findViewById(R.id.labs)).animate().alpha(0).setDuration(400).setStartDelay(5000);
	}

	private void setupLottieView() {
		lottieAnimationView = findViewById(R.id.lottie_view);
		lottieAnimationView.playAnimation();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
							   int height) {
		if (isCameraviewOn) {
			mCamera.stopPreview();
			isCameraviewOn = false;
		}

		if (mCamera != null) {
			try {
				mCamera.setPreviewDisplay(mSurfaceHolder);
				mCamera.startPreview();
				isCameraviewOn = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
		mCamera.setDisplayOrientation(90);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
		isCameraviewOn = false;
	}
}

package org.dsi.ifc.androidauto2;

import com.mst2.phonescale.ProjectedScale;

/** ABI-faithful P0478T Android Auto 2.x configuration with physical-size scaling. */
public class ServiceConfiguration {
    public int displayResolutionX, displayResolutionY, driverPosition;
    public boolean touchpadAvailable;
    public int touchpadResolutionX, touchpadResolutionY;
    public boolean touchscreenAvailable;
    public int touchscreenResolutionX, touchscreenResolutionY;
    public int physicalDisplayHeight, physicalDisplayWidth;
    public boolean rotaryControllerAvailable, externalBluetoothPairing;
    public BluetoothServiceAnnouncement pairingAnnoncement;
    public int windowResolutionX, windowResolutionY, windowOffsetX, windowOffsetY;

    public ServiceConfiguration(){ProjectedScale.apply(this);}
    public ServiceConfiguration(int rx,int ry,int dp,boolean tpa,int tpx,int tpy,boolean tsa,
            int tsx,int tsy,int ph,int pw,boolean rotary,boolean external,
            BluetoothServiceAnnouncement pairing){
        this(rx,ry,dp,tpa,tpx,tpy,tsa,tsx,tsy,ph,pw,rotary,external,pairing,0,0,0,0);
    }
    public ServiceConfiguration(int rx,int ry,int dp,boolean tpa,int tpx,int tpy,boolean tsa,
            int tsx,int tsy,int ph,int pw,boolean rotary,boolean external,
            BluetoothServiceAnnouncement pairing,int wrx,int wry,int wox,int woy){
        displayResolutionX=rx; displayResolutionY=ry; driverPosition=dp;
        touchpadAvailable=tpa; touchpadResolutionX=tpx; touchpadResolutionY=tpy;
        touchscreenAvailable=tsa; touchscreenResolutionX=tsx; touchscreenResolutionY=tsy;
        physicalDisplayHeight=ph; physicalDisplayWidth=pw; rotaryControllerAvailable=rotary;
        externalBluetoothPairing=external; pairingAnnoncement=pairing;
        windowResolutionX=wrx; windowResolutionY=wry; windowOffsetX=wox; windowOffsetY=woy;
        ProjectedScale.apply(this);
    }
    public int getDisplayResolutionX(){return displayResolutionX;}
    public int getDisplayResolutionY(){return displayResolutionY;}
    public int getDriverPosition(){return driverPosition;}
    public boolean isTouchpadAvailable(){return touchpadAvailable;}
    public int getTouchpadResolutionX(){return touchpadResolutionX;}
    public int getTouchpadResolutionY(){return touchpadResolutionY;}
    public boolean isTouchscreenAvailable(){return touchscreenAvailable;}
    public int getTouchscreenResolutionX(){return touchscreenResolutionX;}
    public int getTouchscreenResolutionY(){return touchscreenResolutionY;}
    public int getPhysicalDisplayHeight(){return physicalDisplayHeight;}
    public int getPhysicalDisplayWidth(){return physicalDisplayWidth;}
    public boolean isRotaryControllerAvailable(){return rotaryControllerAvailable;}
    public boolean isExternalBluetoothPairing(){return externalBluetoothPairing;}
    public BluetoothServiceAnnouncement getPairingAnnoncement(){return pairingAnnoncement;}
    public int getWindowResolutionX(){return windowResolutionX;}
    public int getWindowResolutionY(){return windowResolutionY;}
    public int getWindowOffsetX(){return windowOffsetX;}
    public int getWindowOffsetY(){return windowOffsetY;}
    public String toString(){return "ServiceConfiguration(displayResolutionX="+displayResolutionX+",displayResolutionY="+displayResolutionY+",physicalDisplayHeight="+physicalDisplayHeight+",physicalDisplayWidth="+physicalDisplayWidth+")";}
}

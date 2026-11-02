package org.dsi.ifc.androidauto;

import com.mst2.phonescale.ProjectedScale;

/** ABI-faithful P0478T Android Auto 1.x configuration with physical-size scaling. */
public class ServiceConfiguration {
    public AppState[] initialAppState;
    public Resource[] initialResources;
    public int displayResolutionX, displayResolutionY, displayOffsetX, displayOffsetY;
    public String displayName;
    public boolean useRightHandDrive, touchpadAvailable;
    public int touchpadResolutionX, touchpadResolutionY;
    public boolean touchscreenAvailable;
    public int touchscreenResolutionX, touchscreenResolutionY;
    public boolean startInNightMode;
    public int physicalDisplayHeight, physicalDisplayWidth;

    public ServiceConfiguration() {
        initialAppState=null; initialResources=null; displayName="";
        ProjectedScale.apply(this);
    }

    public ServiceConfiguration(AppState[] a, Resource[] r, int rx, int ry, int ox, int oy,
            String name, boolean rhd, boolean tpa, int tpx, int tpy, boolean tsa, int tsx,
            int tsy, boolean night, int ph, int pw) {
        initialAppState=a; initialResources=r; displayResolutionX=rx; displayResolutionY=ry;
        displayOffsetX=ox; displayOffsetY=oy; displayName=name; useRightHandDrive=rhd;
        touchpadAvailable=tpa; touchpadResolutionX=tpx; touchpadResolutionY=tpy;
        touchscreenAvailable=tsa; touchscreenResolutionX=tsx; touchscreenResolutionY=tsy;
        startInNightMode=night; physicalDisplayHeight=ph; physicalDisplayWidth=pw;
        ProjectedScale.apply(this);
    }

    public AppState[] getInitialAppState(){return initialAppState;}
    public Resource[] getInitialResources(){return initialResources;}
    public int getDisplayResolutionX(){return displayResolutionX;}
    public int getDisplayResolutionY(){return displayResolutionY;}
    public int getDisplayOffsetX(){return displayOffsetX;}
    public int getDisplayOffsetY(){return displayOffsetY;}
    public String getDisplayName(){return displayName;}
    public boolean isUseRightHandDrive(){return useRightHandDrive;}
    public boolean isTouchpadAvailable(){return touchpadAvailable;}
    public int getTouchpadResolutionX(){return touchpadResolutionX;}
    public int getTouchpadResolutionY(){return touchpadResolutionY;}
    public boolean isTouchscreenAvailable(){return touchscreenAvailable;}
    public int getTouchscreenResolutionX(){return touchscreenResolutionX;}
    public int getTouchscreenResolutionY(){return touchscreenResolutionY;}
    public boolean isStartInNightMode(){return startInNightMode;}
    public int getPhysicalDisplayHeight(){return physicalDisplayHeight;}
    public int getPhysicalDisplayWidth(){return physicalDisplayWidth;}
    public String toString(){return "ServiceConfiguration(displayResolutionX="+displayResolutionX+",displayResolutionY="+displayResolutionY+",physicalDisplayHeight="+physicalDisplayHeight+",physicalDisplayWidth="+physicalDisplayWidth+")";}
}

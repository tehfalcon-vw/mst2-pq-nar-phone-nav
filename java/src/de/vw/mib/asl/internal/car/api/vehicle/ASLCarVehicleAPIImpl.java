/*
 * Stock P0478T vehicle-service dispatcher with one fail-safe gear observer.
 * The complete stock API and all client forwarding behavior are preserved.
 */
package de.vw.mib.asl.internal.car.api.vehicle;

import de.vw.mib.asl.api.car.vehicle.ASLCarVehicleAPI;
import de.vw.mib.asl.api.car.vehicle.VehicleService;
import de.vw.mib.asl.api.car.vehicle.VehicleServiceListener;
import de.vw.mib.asl.internal.car.api.ServiceClient;
import de.vw.mib.asl.internal.car.framework.services.ServiceFactory;
import java.util.HashSet;
import java.util.Iterator;

public class ASLCarVehicleAPIImpl
implements ASLCarVehicleAPI, ServiceClient, VehicleServiceListener {
    private HashSet registeredCarClients;
    static /* synthetic */ Class class$de$vw$mib$asl$api$car$vehicle$VehicleService;
    static /* synthetic */ Class class$de$vw$mib$asl$api$car$vehicle$VehicleServiceListener;

    private HashSet getRegisteredCarClients() {
        if (this.registeredCarClients == null) this.registeredCarClients = new HashSet();
        return this.registeredCarClients;
    }

    public VehicleService getVehicleService() {
        VehicleService service = (VehicleService)ServiceFactory.getInstance().getServiceClientRegistry().getServiceProvider(
                class$de$vw$mib$asl$api$car$vehicle$VehicleService == null
                ? (class$de$vw$mib$asl$api$car$vehicle$VehicleService = ASLCarVehicleAPIImpl.class$("de.vw.mib.asl.api.car.vehicle.VehicleService"))
                : class$de$vw$mib$asl$api$car$vehicle$VehicleService);
        if (service == null) service = new NotAvailableVehicleService();
        return service;
    }

    public void addVehicleServiceListener(VehicleServiceListener listener) {
        this.getRegisteredCarClients().add(listener);
    }

    public void removeVehicleServiceListener(VehicleServiceListener listener) {
        this.getRegisteredCarClients().remove(listener);
    }

    public void informClientsThatProviderIsAvailable(Class clazz) {
        if (clazz == (class$de$vw$mib$asl$api$car$vehicle$VehicleService == null
                ? (class$de$vw$mib$asl$api$car$vehicle$VehicleService = ASLCarVehicleAPIImpl.class$("de.vw.mib.asl.api.car.vehicle.VehicleService"))
                : class$de$vw$mib$asl$api$car$vehicle$VehicleService)) {
            ServiceFactory.getInstance().getServiceClientRegistry().registerServiceClient(
                    class$de$vw$mib$asl$api$car$vehicle$VehicleServiceListener == null
                    ? (class$de$vw$mib$asl$api$car$vehicle$VehicleServiceListener = ASLCarVehicleAPIImpl.class$("de.vw.mib.asl.api.car.vehicle.VehicleServiceListener"))
                    : class$de$vw$mib$asl$api$car$vehicle$VehicleServiceListener, this);
        }
    }

    public void updateValetParkingActive(boolean value) {
        Iterator it = this.getRegisteredCarClients().iterator();
        while (it.hasNext()) ((VehicleServiceListener)it.next()).updateValetParkingActive(value);
    }

    public void updateValetParkingAvailable(boolean value) {
        Iterator it = this.getRegisteredCarClients().iterator();
        while (it.hasNext()) ((VehicleServiceListener)it.next()).updateValetParkingAvailable(value);
    }

    public void updateOilLevel(int value) {
        Iterator it = this.getRegisteredCarClients().iterator();
        while (it.hasNext()) ((VehicleServiceListener)it.next()).updateOilLevel(value);
    }

    public void updateOilLevelAvailability(int value) {
        Iterator it = this.getRegisteredCarClients().iterator();
        while (it.hasNext()) ((VehicleServiceListener)it.next()).updateOilLevelAvailability(value);
    }

    public void updateOilRefillQuantity(int value) {
        Iterator it = this.getRegisteredCarClients().iterator();
        while (it.hasNext()) ((VehicleServiceListener)it.next()).updateOilRefillQuantity(value);
    }

    public void updateOilRefillUnit(int value) {
        Iterator it = this.getRegisteredCarClients().iterator();
        while (it.hasNext()) ((VehicleServiceListener)it.next()).updateOilRefillUnit(value);
    }

    public void updateOilWarning(int value) {
        Iterator it = this.getRegisteredCarClients().iterator();
        while (it.hasNext()) ((VehicleServiceListener)it.next()).updateOilWarning(value);
    }

    public void updateWarningCount(int value) {
        Iterator it = this.getRegisteredCarClients().iterator();
        while (it.hasNext()) ((VehicleServiceListener)it.next()).updateWarningCount(value);
    }

    public void updateCarStatusTileSFunctionAvailabilty(int index, boolean value) {
        Iterator it = this.getRegisteredCarClients().iterator();
        while (it.hasNext()) ((VehicleServiceListener)it.next()).updateCarStatusTileSFunctionAvailabilty(index, value);
    }

    public void updateCarStatusTileSFunctionExistence(int index, boolean value) {
        Iterator it = this.getRegisteredCarClients().iterator();
        while (it.hasNext()) ((VehicleServiceListener)it.next()).updateCarStatusTileSFunctionExistence(index, value);
    }

    public void updateOillevelFunctionAvailableReason(int value) {
        Iterator it = this.getRegisteredCarClients().iterator();
        while (it.hasNext()) ((VehicleServiceListener)it.next()).updateOillevelFunctionAvailableReason(value);
    }

    public void updateRDKFunctionAvailableReason(int value) {
        Iterator it = this.getRegisteredCarClients().iterator();
        while (it.hasNext()) ((VehicleServiceListener)it.next()).updateRDKFunctionAvailableReason(value);
    }

    public void updateCarGearType(int value) {
        Iterator it = this.getRegisteredCarClients().iterator();
        while (it.hasNext()) ((VehicleServiceListener)it.next()).updateCarGearType(value);
    }

    public void updateAutomaticGearShiftTransMode(int mode) {
        try { com.mst2.rvc.RvcFollowUp.updateGear(mode); } catch (Throwable ignored) {}
        Iterator it = this.getRegisteredCarClients().iterator();
        while (it.hasNext()) ((VehicleServiceListener)it.next()).updateAutomaticGearShiftTransMode(mode);
    }

    public void updateClutchState(int value) {
        Iterator it = this.getRegisteredCarClients().iterator();
        while (it.hasNext()) ((VehicleServiceListener)it.next()).updateClutchState(value);
    }

    public void updateParkingBrakeState(int value) {
        Iterator it = this.getRegisteredCarClients().iterator();
        while (it.hasNext()) ((VehicleServiceListener)it.next()).updateParkingBrakeState(value);
    }

    public void updateSystemState(boolean initializing, boolean clamp15) {
        Iterator it = this.getRegisteredCarClients().iterator();
        while (it.hasNext()) ((VehicleServiceListener)it.next()).updateSystemState(initializing, clamp15);
    }

    static /* synthetic */ Class class$(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            try {
                throw new NoClassDefFoundError().initCause(e);
            } catch (Throwable cause) {
                throw new RuntimeException(cause);
            }
        }
    }
}

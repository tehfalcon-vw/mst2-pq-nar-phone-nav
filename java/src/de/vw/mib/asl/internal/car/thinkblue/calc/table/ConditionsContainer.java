/*
 * Stock P0478T ConditionsContainer with one fail-safe reverse-state observer.
 * All original fields, methods and condition calculations are preserved.
 */
package de.vw.mib.asl.internal.car.thinkblue.calc.table;

import org.dsi.ifc.carvehiclestates.DynamicVehicleInfoHighFrequent;
import org.dsi.ifc.carvehiclestates.DynamicVehicleInfoMidFrequent;

public final class ConditionsContainer {
    public static final int COND_CURR_GEAR_NULL = 0;
    public static final int COND_CORR_GEAR = 1;
    public static final int COND_WRONG_GEAR = 2;
    public static final int COND_CLUTCH_IN = 3;
    public static final int COND_CLUTCH_OUT = 4;
    public static final int COND_ACCPEDAL_HIGH = 5;
    public static final int COND_ACCPEDAL_LOW = 6;
    public static final int COND_NO_STDSTILL_NO_RGEAR = 7;
    public static final int COND_NO_BRAKE = 8;
    public static final int COND_BRAKE = 9;
    public static final int COND_NO_KICKDOWN = 10;
    public static final int COND_KICKDOWN = 11;
    public static final int COND_CORR_GEAR_LONGTERM = 12;
    public static final int COND_CURR_CONS = 13;
    public static final int COND_CURR_GEAR_ASG = 14;
    public static final int COND_AUTO_GEAR_ASG_D_E = 15;
    public static final int COND_AUTO_GEAR_B = 16;
    public static final int COND_RECUP_LEVEL_ON = 17;
    public static final int COND_AUTO_GEAR_D = 18;
    public static final int COND_CONSUMPTION1_GREATER_THEN_ZERO_COMBUSTION_ELECTRIC = 19;
    public static final int COND_CONSUMPTION2_GREATER_THEN_ZERO_COMBUSTION_ELECTRIC = 20;
    public static final int COND_AUTO_GEAR_ASG_S = 21;
    public static final int COND_AUTO_GEAR_ASG_N = 22;
    public static final int COND_COASTING_ACTIVE = 23;
    public static final int COND_AUTO_GEAR_OFFROAD = 24;
    public static final int COND_AUTO_GEAR_E = 25;
    public static final int COND_AUTO_GEAR_ASG_MS = 26;
    public static final int COND_AUTO_GEAR_ASG_S_PLUS = 27;
    public static final int COND_AUTO_GEAR_ASG_MS_PLUS = 28;
    private static final int RGEAR_NOT_RECEIVED = -1;
    private static final int RGEAR_TRUE = 1;
    private static final int RGEAR_FALSE = 0;
    private int rgear = -1;
    private static final int STANDSTILL_NOT_RECEIVED = -1;
    private static final int STANDSTILL_TRUE = 1;
    private static final int STANDSTILL_FALSE = 0;
    private static final byte ACC_PEDAL_POS = 2;
    private int standstill = -1;
    private static ConditionsContainer instance;
    public boolean[] conditions = new boolean[29];

    private ConditionsContainer() {}

    public static ConditionsContainer getInstance() {
        if (instance == null) instance = new ConditionsContainer();
        return instance;
    }

    public void refreshConditions(DynamicVehicleInfoHighFrequent value) {
        this.conditions[5] = value.acceleratorPosition > 2;
        this.conditions[6] = value.acceleratorPosition <= 2;
    }

    public void refreshConditions(DynamicVehicleInfoMidFrequent value) {
        this.conditions[0] = value.currentGear == 0;
        this.conditions[1] = value.currentGear == value.recommendedGear && value.currentGear > 0;
        this.conditions[2] = value.recommendedGear != value.currentGear;
        this.conditions[3] = value.clutch == 1;
        this.conditions[4] = value.clutch == 0;
        this.conditions[8] = !value.driverIsBraking;
        this.conditions[9] = value.driverIsBraking;
        this.conditions[10] = !value.acceleratorKickDown;
        this.conditions[11] = value.acceleratorKickDown;
        this.conditions[14] = value.currentGear >= 1 && value.currentGear <= 9;
        this.conditions[16] = value.automaticGearShiftTransMode == 9;
        this.conditions[18] = value.automaticGearShiftTransMode == 4;
        this.conditions[25] = value.automaticGearShiftTransMode == 8;
        this.conditions[24] = value.automaticGearShiftTransMode == 13;
        this.conditions[17] = value.recuperationLevel > 0;
        this.conditions[15] = value.automaticGearShiftTransMode == 4 || value.automaticGearShiftTransMode == 8;
        this.conditions[21] = value.automaticGearShiftTransMode == 5;
        this.conditions[27] = value.automaticGearShiftTransMode == 11;
        this.conditions[26] = value.automaticGearShiftTransMode == 10;
        this.conditions[28] = value.automaticGearShiftTransMode == 12;
        this.conditions[22] = value.automaticGearShiftTransMode == 3 || value.coastingIsActive;
        this.conditions[23] = value.coastingIsActive;
    }

    public void refreshConditionsStandStill(boolean value) {
        this.standstill = value ? 1 : 0;
        if (this.rgear == -1) return;
        this.conditions[7] = !value && this.rgear == 0;
    }

    public void refreshConditionsRGear(boolean value) {
        this.rgear = value ? 1 : 0;
        try { com.mst2.rvc.RvcFollowUp.updateReverse(value); } catch (Throwable ignored) {}
        if (this.standstill == -1) return;
        this.conditions[7] = this.standstill == 0 && !value;
    }

    public void refreshConditionCorrectGearLong(boolean value) {
        this.conditions[12] = this.conditions[1] && value;
    }

    public boolean refreshConditionsConsumption1(double value) {
        return this.refreshConditionsConsumption(value, 19);
    }

    public boolean refreshConditionsConsumption2(double value) {
        return this.refreshConditionsConsumption(value, 20);
    }

    private boolean refreshConditionsConsumption(double value, int index) {
        boolean oldValue = this.conditions[index];
        boolean newValue = value > 0.0 && value < 6553.5;
        boolean changed = oldValue != newValue;
        if (changed) this.conditions[index] = newValue;
        return changed;
    }

    public String toString() {
        return new StringBuffer().append("ConditionsContainer [conditions=").append(this.conditions != null ? this.arrayToString(this.conditions, this.conditions.length) : null).append("]").toString();
    }

    private String arrayToString(Object object, int length) {
        StringBuffer result = new StringBuffer();
        result.append("[");
        for (int i = 0; i < length; ++i) {
            if (i > 0) result.append(", ");
            if (object instanceof boolean[]) result.append(((boolean[])object)[i]);
        }
        result.append("]");
        return result.toString();
    }
}

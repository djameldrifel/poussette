package com.theteam.zf.poussette;

/**
 * Created by DAOUDR on 26/08/2015.
 */
public interface CustomEventListener {
    final static int BLUETOOTH_PAIRED = 101;
    final static int STROLLER_GYRO_POSITION = 102;
    final static int STROLLER_GEO_POSITION = 103;
    final static int STROLLER_HANDLED = 104;
    final static int STROLLER_BABY = 105;
    final static int STROLLER_DISTANCE = 106;

    public void doEvent(int e,Object o);
}

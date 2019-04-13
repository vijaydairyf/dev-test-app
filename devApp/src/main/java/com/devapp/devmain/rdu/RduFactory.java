package com.devapp.devmain.rdu;

import android.content.Context;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.peripherals.factories.DeviceFactory;
import com.devapp.devmain.peripherals.interfaces.Device;


/**
 * Created by x on 28/12/17.
 */

public class RduFactory {
    static String mRdu;
    static RduManager rduManager;
//    public RduFactory(Context context){
//        this.context = context;
//    }

    public static RduManager getRdu(String rdu, Context context) {
        Device device = DeviceFactory.getDevice(context, DeviceName.RDU);
        if (device != null && (mRdu == null || !mRdu.equals(rdu) || rduManager == null)) {
            mRdu = rdu;
            switch (rdu) {
                case AppConstants.RDU.AKASHGANGA_16: {
                    rduManager = new RduManagerImpl(context, new Akashganga16Rdu(), device);
                    break;
                }
                case AppConstants.RDU.AKASHGANGA_32: {
                    rduManager = new RduManagerImpl(context, new Akashganga32Rdu(), device);
                    break;
                }
                case AppConstants.RDU.ESSAE: {
                    rduManager = new RduManagerImpl(context, new EssaeRdu(), device);
                    break;
                }
                case AppConstants.RDU.EVEREST: {
                    rduManager = new RduManagerImpl(context, new EverestRdu(), device);
                    break;
                }
                case AppConstants.RDU.HATSUN: {
                    rduManager = new RduManagerImpl(context, new HatsunRdu(), device);
                    break;
                }
                case AppConstants.RDU.SMART: {
                    rduManager = new RduManagerImpl(context, new SmartRdu(), device);
                    break;
                }
                case AppConstants.RDU.SMART2: {
                    rduManager = new RduManagerImpl(context, new Smart2Rdu(), device);
                    break;
                }
                case AppConstants.RDU.STELLAPPS: {
                    rduManager = new RduManagerImpl(context, new DevAppRdu(), device);
                    break;
                }
                case AppConstants.RDU.STELLAPPSV2: {
                    rduManager = new RduManagerImpl(context, new DevAppV2Rdu(), device);
                    break;
                }
                case AppConstants.RDU.VECTOR: {
                    rduManager = new RduManagerImpl(context, new VectorRdu(), device);
                    break;
                }
                default: {
                    rduManager = null;
                }
            }
        } else if (device == null) {
            rduManager = null;
        }
        return rduManager;
    }

    public static void resetManagers() {
        rduManager = null;
    }

}

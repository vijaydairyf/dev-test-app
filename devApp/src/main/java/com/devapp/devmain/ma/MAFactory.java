package com.devapp.devmain.ma;

import android.content.Context;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.ma.parser.Akashganga2400;
import com.devapp.devmain.ma.parser.Akashganga9600;
import com.devapp.devmain.ma.parser.Ekobond;
import com.devapp.devmain.ma.parser.EkomilkEven;
import com.devapp.devmain.ma.parser.EkomilkUltra;
import com.devapp.devmain.ma.parser.EkomilkUltraPro;
import com.devapp.devmain.ma.parser.EkomilkV2;
import com.devapp.devmain.ma.parser.Essae;
import com.devapp.devmain.ma.parser.Indifoss;
import com.devapp.devmain.ma.parser.Kamdhenu;
import com.devapp.devmain.ma.parser.Ksheeraa;
import com.devapp.devmain.ma.parser.LM2;
import com.devapp.devmain.ma.parser.LactoScanV2;
import com.devapp.devmain.ma.parser.Lactoscan;
import com.devapp.devmain.ma.parser.Laktan240;
import com.devapp.devmain.ma.parser.Nuline;
import com.devapp.devmain.peripherals.factories.DeviceFactory;
import com.devapp.devmain.peripherals.interfaces.Device;
import com.devapp.devmain.server.AmcuConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by x on 20/10/16.
 */

public class MAFactory {
    static Map<String, MaManager> maManagerMap;

    static {
        maManagerMap = new HashMap<>();
    }

    public static MaManager getMA(String deviceName, Context context) {
        if (maManagerMap.get(deviceName) != null) {
            return maManagerMap.get(deviceName);
        } else {
            MaManager maManager;
            String model = getMaModel(deviceName, context);
            Device device = DeviceFactory.getDevice(context, deviceName);
            switch (model) {
                case AppConstants.MA.LACTOSCAN: {
                    maManager = new MaManagerImpl(new Lactoscan(), device, context);
                    break;
                }
                case AppConstants.MA.ESSAE: {
                    maManager = new MaManagerImpl(new Essae(), device, context);
                    break;
                }
                case AppConstants.MA.EKOMILK_ULTRA_PRO: {
                    maManager = new MaManagerImpl(new EkomilkUltraPro(), device, context);
                    break;
                }
                case AppConstants.MA.EKOMILK: {
                    maManager = new MaManagerImpl(new EkomilkUltra(), device, context);
                    break;
                }
                case AppConstants.MA.LM2: {
                    maManager = new MaManagerImpl(new LM2(), device, context);
                    break;
                }
                case AppConstants.MA.LACTOPLUS: {
                    maManager = new MaManagerImpl(new LM2(), device, context);
                    break;
                }
                case AppConstants.MA.KAMDHENU: {
                    maManager = new MaManagerImpl(new Kamdhenu(), device, context);
                    break;
                }
                case AppConstants.MA.AKASHGANGA: {
                    if (device != null) {
                        if (device.getDeviceParams().getBaudRate() == 9600) {
                            maManager = new MaManagerImpl(new Akashganga9600(), device, context);
                            break;
                        } else if (device.getDeviceParams().getBaudRate() == 2400) {
                            maManager = new MaManagerImpl(new Akashganga2400(), device, context);
                            break;
                        }
                    }
                    maManager = null;
                    break;
                }
                case AppConstants.MA.INDIFOSS: {
                    maManager = new MaManagerImpl(new Indifoss(), device, context);
                    break;
                }
                case AppConstants.MA.NULINE: {
                    maManager = new MaManagerImpl(new Nuline(), device, context);
                    break;
                }
                case AppConstants.MA.EKOBOND: {
                    maManager = new MaManagerImpl(new Ekobond(), device, context);
                    break;
                }
                case AppConstants.MA.LACTOSCAN_V2: {
                    maManager = new MaManagerImpl(new LactoScanV2(), device, context);
                    break;
                }
                case AppConstants.MA.KSHEERAA: {
                    maManager = new MaManagerImpl(new Ksheeraa(), device, context);
                    break;
                }
                case AppConstants.MA.EKOMILK_EVEN: {
                    maManager = new MaManagerImpl(new EkomilkEven(), device, context);
                    break;
                }
                case AppConstants.MA.EKOMILK_V2: {
                    maManager = new MaManagerImpl(new EkomilkV2(), device, context);
                    break;
                }
                case AppConstants.MA.LAKTAN_240: {
                    maManager = new MaManagerImpl(new Laktan240(), device, context);
                    break;
                }
                default: {
                    maManager = null;
                }

            }
            maManagerMap.put(deviceName, maManager);
            return maManager;

        }

    }

    public static String getMaModel(String deviceName, Context context) {
        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        switch (deviceName) {
           /* case DeviceName.MILK_ANALYSER: {
                return saveSession.getMA();
            }*/
            case DeviceName.MA1: {
                return amcuConfig.getMa1Name();
            }
            case DeviceName.MA2: {
                return amcuConfig.getMa2Name();
            }
            default: {
                return amcuConfig.getMA();
            }
        }
    }

    public static void resetManagers() {
        maManagerMap.clear();
    }

}

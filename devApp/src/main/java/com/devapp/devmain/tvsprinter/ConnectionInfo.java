package com.devapp.devmain.tvsprinter;

import com.andprn.port.android.DeviceConnection;

public class ConnectionInfo {
    private static ConnectionInfo connectionInfo;
    private DeviceConnection connection;
    //private AndroidMSR androidMSR;

    private ConnectionInfo() {
    }

    public static ConnectionInfo getInstance() {
        if (connectionInfo == null)
            connectionInfo = new ConnectionInfo();
        return connectionInfo;
    }

    public DeviceConnection getConnection() {
        return connection;
    }

    public void setConnection(DeviceConnection connection) {
        this.connection = connection;
    }

	/*public AndroidMSR getAndroidMSR()
    {
		if((androidMSR == null) && (connection != null))
		{
			androidMSR = new AndroidMSR(connection);
		}
		return androidMSR;
	}*/
}

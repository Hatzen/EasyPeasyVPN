package de.hartz.vpn.Helper;

import java.io.File;

/**
 * Global and statics variables that are needed  often at different locations.
 */
public final class Statics {

    public static final String SOFTWARE_NAME = "EasyPeasyVPN";

    public static final int MEDIATION_SERVER_PORT = 13267;
    public static final String DEFAULT_CLIENT_NAME = "DefaultClient";

    public static final String USER_DATA_FILE_PATH = System.getProperty("user.home") + File.separator + "." + SOFTWARE_NAME +  File.separator + "userData.ser";

    /**
     * Useful values needed to work with openvpn.
     */
    enum OpenVpnValues {
        CONFIG_FOLDER("config"),
        CONFIG_EXTENSION_LINUX(".conf"),
        CONFIG_EXTENSION_WINDOWS(".ovpn");
        private String value;

        OpenVpnValues(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}

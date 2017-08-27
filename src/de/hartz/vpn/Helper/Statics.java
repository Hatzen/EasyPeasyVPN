package de.hartz.vpn.Helper;

/**
 * Created by kaiha on 02.07.2017.
 */
public final class Statics {

    public static final int MEDIATION_SERVER_PORT = 13267;
    public static final String DEFAULT_CLIENT_NAME = "DefaultClient";

    /**
     * Useful values needed to work with openvpn.
     */
    enum OpenVpnValues {
        CONFIG_FOLDER("config"),
        CONFIG_EXTENSION_LINUX(".conf"),
        CONFIG_EXTENSION_WINDOWS(".ovpn"),
        ;
        private String value;

        OpenVpnValues(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}

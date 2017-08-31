package de.hartz.vpn.MainApplication.Server;

import java.io.Serializable;

/**
 * Configfile that represents the configuration of a network. Used by client and server.
 */
public class ConfigState implements Serializable {

    public enum Adapter {
        OpenVPN,
        IPsec,
        FreeLan
    }

    public enum Tunnel {
        CLOSED,
        SPLIT
    }

    public enum NetworkType {
        SITE_TO_SITE,
        SITE_TO_END,
        END_TO_END,
    }

    private Tunnel tunnel;
    private Adapter adapter;
    private NetworkType networkType;

    private boolean needsAuthentication;

    // Only for showing valid ip configuration, if NetworkType == SITE_TO_SITE.
    private String possibleIp;
    private int subnetmask;

    /**
     * Default Constructor for Express Configuration.
     */
    public ConfigState() {
        tunnel = Tunnel.SPLIT;
        adapter = Adapter.OpenVPN;
        networkType = NetworkType.END_TO_END;

        needsAuthentication = false;
    }

    public ConfigState(Tunnel tunnel, Adapter adapter, NetworkType networkType, boolean needsAuthentication, String possibleIp, int subnetmask) {
        this.tunnel = tunnel;
        this.adapter = adapter;
        this.networkType = networkType;
        this.needsAuthentication = needsAuthentication;
        this.possibleIp = possibleIp;
        this.subnetmask = subnetmask;
    }

    public Tunnel getTunnel() {
        return tunnel;
    }

    public void setTunnel(Tunnel tunnel) {
        this.tunnel = tunnel;
    }

    public Adapter getAdapter() {
        return adapter;
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }

    public NetworkType getNetworkType() {
        return networkType;
    }

    public void setNetworkType(NetworkType networkType) {
        this.networkType = networkType;
    }

    public boolean isNeedsAuthentication() {
        return needsAuthentication;
    }

    public void setNeedsAuthentication(boolean needsAuthentication) {
        this.needsAuthentication = needsAuthentication;
    }

    public String getPossibleIp() {

        return possibleIp;
    }

    public void setPossibleIp(String possibleIp) {
        this.possibleIp = possibleIp;
    }

    public int getSubnetmask() {
        return subnetmask;
    }

    public void setSubnetmask(int subnetmask) {
        this.subnetmask = subnetmask;
    }
}

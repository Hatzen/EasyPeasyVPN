package de.hartz.vpn.main.server;

import de.hartz.vpn.mediation.Mediator;
import de.hartz.vpn.utilities.NetworkUtilities;

import java.io.Serializable;

import static de.hartz.vpn.main.server.ConfigState.Protocol.UDP;

/**
 * Configfile that represents the configuration of a network. Used by client and server.
 */
public class ConfigState implements Serializable {

    public enum Adapter {
        OpenVPN,
        IPsec,
        FreeLan
    }

    // TODO: Tcp not supported by mediator.
    public enum Protocol {
        TCP,
        UDP
    }

    // TODO: Closed tunnel not supported by software. Needs networkbridge between adapters (?)
    public enum Tunnel {
        CLOSED,
        SPLIT
    }

    // TODO: Site connections needs traffic redirection by every computer or the gateway to vpn-gateway. Not really supported by Software.
    public enum NetworkType {
        SITE_TO_SITE,
        SITE_TO_END,
        END_TO_END,
    }

    private String networkName;
    private Protocol protocol;

    private Mediator mediator;

    private Tunnel tunnel;
    private Adapter adapter;
    private NetworkType networkType;

    private boolean needsAuthentication;

    private String netaddress;
    private short subnetmask;

    /**
     * Default Constructor for Express Configuration.
     */
    public ConfigState() {
        tunnel = Tunnel.SPLIT;
        adapter = Adapter.OpenVPN;
        networkType = NetworkType.END_TO_END;
        protocol = UDP;
        netaddress = NetworkUtilities.getRecommendedIp();
        subnetmask = 24;

        needsAuthentication = false;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
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

    public String getNetaddress() {
        return netaddress;
    }

    public String getProtocol() {
        return protocol.name().toLowerCase();
    }

    public void setNetaddress(String netaddress) {
        this.netaddress = netaddress;
    }

    public int getSubnetmask() {
        return subnetmask;
    }

    public void setSubnetmask(short subnetmask) {
        this.subnetmask = subnetmask;
    }

    public Mediator getMediator() {
        return mediator;
    }

    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

}

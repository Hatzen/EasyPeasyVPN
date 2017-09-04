package de.hartz.vpn.MediationServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;

/**
 * Created by kaiha on 02.09.2017.
 */
public class Mediator implements Serializable {

    private String mediatorName;
    private String url;
    // Only needed if mediationserver hosts network specific files.
    private int metaServerPort;
    private int udpHolePunchingPort;

    // If true the url is a rest api showing the original ip of the mediator, needed for dynamic ips.
    private boolean redirectFromUrl;

    public Mediator(String mediatorName,String url, int metaServerPort, int udpHolePunchingPort, boolean redirectFromUrl) {
        this.mediatorName = mediatorName;
        this.url = url;
        this.metaServerPort = metaServerPort;
        this.udpHolePunchingPort = udpHolePunchingPort;
        this.redirectFromUrl = redirectFromUrl;
    }

    public String getMediatorName() {
        return mediatorName;
    }

    public int getMetaServerPort() {
        return metaServerPort;
    }

    public int getUdpHolePunchingPort() {
        return udpHolePunchingPort;
    }

    public boolean isRedirectFromUrl() {
        return redirectFromUrl;
    }

    /**
     * Get the real url or ip of the mediator server.
     * @returns the url of the mediator.
     */
    public String getUrl() {
        if (!redirectFromUrl) {
            return url;
        }
        try {
            URL whatIsMyIp = new URL(url);
            BufferedReader in = new BufferedReader(new InputStreamReader(whatIsMyIp.openStream()));
            String ip = in.readLine();
            return ip;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

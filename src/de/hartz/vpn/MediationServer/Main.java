package de.hartz.vpn.MediationServer;

import de.hartz.vpn.Helper.NetworkHelper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by kaiha on 31.08.2017.
 */
public class Main {

    private boolean hasStaticIp;
    private String ipRefreshURL;

    /**
     * Default constructor is used for development with non static ip mediator.
     */
    public Main() {
        setupPseudoDNSServer();
        new MediationServer();
    }

    private void setupPseudoDNSServer() {

        hasStaticIp = false;
        ipRefreshURL = "http://hartzkai.freehostia.com/thesis/changeip.php";

        final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                String urlParameters = "";
                NetworkHelper.executePost(ipRefreshURL, urlParameters);
                // System.out.println("Refreshed Ip at:" + ipRefreshURL);
            }
        }, 0, 1, TimeUnit.MINUTES);
    }


}

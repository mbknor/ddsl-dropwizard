package com.kjetland.ddsl.dropwizard;

import com.kjetland.ddsl.DdslClient;
import com.kjetland.ddsl.DdslClientCacheReadsImpl;
import com.kjetland.ddsl.DdslClientImpl;
import com.kjetland.ddsl.model.*;
import com.kjetland.ddsl.utils.NetUtils;
import io.dropwizard.lifecycle.ServerLifecycleListener;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class DdslService implements ServerLifecycleListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    // Will get value when jetty is started
    public Integer httpPort = null; // lazy
    private final AtomicBoolean serviceUpExecuted = new AtomicBoolean(false);
    private final DdslConfig ddslConfig;

    private final DdslClient ddslClient;

    private Service serviceObject = null;

    public DdslService(DdslConfig ddslConfig) {
        this.ddslConfig = ddslConfig;

        DdslClient realDdslClient = new DdslClientImpl(new DropwizardDdslConfig());
        this.ddslClient = new DdslClientCacheReadsImpl( realDdslClient, ddslConfig.serviceLookupCacheMills);
    }

    protected class DropwizardDdslConfig implements com.kjetland.ddsl.config.DdslConfig {
        @Override
        public String hosts() {
            return ddslConfig.zookeeperHosts;
        }

        @Override
        public String getStaticUrl(ServiceId serviceId) {
            String key = serviceId.environment() + ":" + serviceId.name() + ":" + serviceId.version() + ":" + serviceId.serviceType();
            if ( ddslConfig.failoverServiceId2UrlMapping != null ) {
                String url = ddslConfig.failoverServiceId2UrlMapping.get(key);
                if ( url != null) {
                    return url;
                }
            }
            throw new RuntimeException("Failed to lookup fail-over url from DdslConfig-section under 'failoverServiceId2UrlMapping' with key '"+key+"' - Add this key with usefull url to manually resolve this ServiceId to a url");
        }
    }

    @Override
    public void serverStarted(Server server) {
        // Detect the port jetty is listening on - works with configured- and random-port
        for (Connector connector : server.getConnectors()) {
            if ( "application".equals( connector.getName() )) {
                this.httpPort = ((ServerConnector)connector).getLocalPort();
                executeServiceUp();
            }
        }
    }

    private void executeServiceUp() {
        // We might want to call this method again after we have the jetty port..
        if ( httpPort != null && serviceUpExecuted.compareAndSet(false, true) ) {
            logger.info("Using DDSL with zookeeper hosts: " + ddslConfig.zookeeperHosts);

            if ( ddslConfig.serviceId == null) {
                logger.debug("serviceId not specified for this service - not announcing it to DDSL");
                return ;
            }
            // Do we use configured url or do we resolve it?
            String url = ddslConfig.serviceUrl;
            if ( url == null ) {
                url = "http://" + NetUtils.resolveLocalPublicIP() + ":" + httpPort +"/";
                logger.debug("serviceUrl not specified - resolving url: " + url);
            }

            logger.info("Announcing serviceUp to ddsl for service " + " with url: " + url);

            ServiceLocation sl = new ServiceLocation(url, ddslConfig.serviceQuality, new DateTime(), null);

            ServiceId serviceId = translate(ddslConfig.serviceId);

            this.serviceObject = new Service(serviceId, sl);

            ddslClient.serviceUp(serviceObject);
        }
    }

    protected ServiceId translate(DdslServiceId ddslServiceId) {
        return new ServiceId(ddslServiceId.environment, ddslServiceId.serviceType, ddslServiceId.name, ddslServiceId.version);
    }

    public DdslClient getDdslClient() {
        return ddslClient;
    }

    public String getBestLocationUrl(DdslServiceId ddslServiceId) {

        if ( ddslConfig.serviceId == null ) {
            throw new RuntimeException("When asking DDSL for other services, you also have to specify who your service is - ddslConfig.serviceId is not specified in config.");
        }

        ServiceId serviceId = translate(ddslServiceId);

        ClientId clientId = new ClientId( ddslConfig.serviceId.environment, ddslConfig.serviceId.name, ddslConfig.serviceId.version, null );
        ServiceRequest sr = new ServiceRequest(serviceId, clientId);

        ServiceLocation sl = ddslClient.getBestServiceLocation(sr);

        return sl.url();
    }
}

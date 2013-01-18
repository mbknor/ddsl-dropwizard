package com.kjetland.ddsl.dropwizard;

import com.kjetland.ddsl.DdslClient;
import com.kjetland.ddsl.DdslClientCacheReadsImpl;
import com.kjetland.ddsl.DdslClientImpl;
import com.kjetland.ddsl.model.Service;
import com.kjetland.ddsl.model.ServiceId;
import com.kjetland.ddsl.model.ServiceLocation;
import com.kjetland.ddsl.utils.NetUtils;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.HttpConfiguration;
import com.yammer.dropwizard.lifecycle.Managed;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DdslService implements Managed{

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final HttpConfiguration httpConfiguration;
    private final DdslConfig ddslConfig;

    private final DdslClient ddslClient;

    private Service serviceObject = null;

    public DdslService(HttpConfiguration httpConfiguration, DdslConfig ddslConfig) {
        this.httpConfiguration = httpConfiguration;
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
    public void start() throws Exception {

        logger.info("Using DDSL with zookeeper hosts: " + ddslConfig.zookeeperHosts);

        if ( ddslConfig.serviceId == null) {
            logger.debug("serviceId not specified for this service - not announcing it to DDSL");
            return ;
        }
        // Do we use configured url or do we resolve it?
        String url = ddslConfig.serviceUrl;
        if ( url == null ) {
            url = "http://" + NetUtils.resolveLocalPublicIP() + ":" + httpConfiguration.getPort()+"/";
            logger.debug("serviceUrl not specified - resolving url: " + url);
        }

        logger.info("Announcing serviceUp to ddsl for service " + " with url: " + url);

        ServiceLocation sl = new ServiceLocation(url, ddslConfig.serviceQuality, new DateTime(), null);

        ServiceId serviceId = new ServiceId(ddslConfig.serviceId.environment, ddslConfig.serviceId.serviceType, ddslConfig.serviceId.name, ddslConfig.serviceId.version);

        this.serviceObject = new Service(serviceId, sl);

        ddslClient.serviceUp(serviceObject);
    }

    @Override
    public void stop() throws Exception {
        if ( serviceObject != null) {
            logger.info("Removing this service from DDSL");
            ddslClient.serviceDown(serviceObject);
        }
    }
}

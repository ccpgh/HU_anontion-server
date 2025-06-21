package com.anontion.asterisk.bean;

import org.springframework.stereotype.Component;

import com.anontion.asterisk.model.AsteriskEndpoint;

@Component
public class AsteriskEndpointBean {

  AsteriskEndpointBean() {
    
  }

  public AsteriskEndpoint createAsteriskEndppint(String id, String name) {
    
    String transport = "transport-id";

    String aor = id;

    String auth = id;

    String context = "external";

    String disallow = "all";

    String allow = "h264,g729,gsm";
    
    String directMedia = "no";

    String trustIdOutbound = "yes";

    String dtmfMode = "rfc4733";
    
    String forceRport = "yes";

    String rtpSymmetric = "yes";
    
    String sendRpid = "yes";
    
    String iceSupport = "yes";
    
    String tosVideo = "af41";
    
    Integer cosVideo = 4;
    
    String allowSubscribe = "yes";
    
    String callerId = name.substring(0, 39);

    if (callerId.length() > 1) {
      
      callerId = callerId.substring(0, 1).toUpperCase() + callerId.substring(1);
    }
    
    return new AsteriskEndpoint(id, transport, aor, auth, context, disallow,
        allow, directMedia, trustIdOutbound, dtmfMode, forceRport, rtpSymmetric, sendRpid, iceSupport,
        tosVideo, cosVideo, allowSubscribe, callerId);
  }
}

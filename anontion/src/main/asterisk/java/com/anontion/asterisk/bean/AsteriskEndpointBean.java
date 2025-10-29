package com.anontion.asterisk.bean;

import org.springframework.stereotype.Component;

import com.anontion.asterisk.model.AsteriskEndpoint;

@Component
public class AsteriskEndpointBean {

  AsteriskEndpointBean() {
    
  }

  public AsteriskEndpoint createAsteriskEndppint(String pub, String foreignKey) {
    
    String transport = "transport-id";

    String aor = pub;

    String auth = foreignKey;

    String context = "external";

    String messageContext = "messages";

    String disallow = "all";

    String allow = "h264,g729,gsm,opus,speex";
    
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
    
    String callerId = null;

    String mediaEncryption = "sdes";
    
    String mediaEncryptionOptimistic = "no";

    if (pub.length() > 1) {

      callerId = pub.substring(0, Math.min(39, pub.length())); // TODO magic !!
    
    } else {
    
      callerId = pub;
    }
    
    return new AsteriskEndpoint(pub, transport, aor, auth, context, messageContext, disallow,
        allow, directMedia, trustIdOutbound, dtmfMode, forceRport, rtpSymmetric, sendRpid, iceSupport,
        tosVideo, cosVideo, allowSubscribe, callerId, mediaEncryption, mediaEncryptionOptimistic);
  }
}

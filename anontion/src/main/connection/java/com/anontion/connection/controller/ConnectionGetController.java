package com.anontion.connection.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.anontion.asterisk.model.AsteriskAuth;
import com.anontion.asterisk.model.AsteriskEndpoint;
import com.anontion.asterisk.repository.AsteriskAuthRepository;
import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.ResponseHeaderDTO;
import com.anontion.common.dto.response.ResponsePostDTO;
import com.anontion.common.dto.response.Responses;
import com.anontion.common.misc.AnontionConfig;
import com.anontion.common.misc.AnontionGPS;
import com.anontion.common.misc.AnontionGPSBox;
import com.anontion.common.misc.AnontionLog;
import com.anontion.common.misc.AnontionStrings;
import com.anontion.common.misc.AnontionTime;
import com.anontion.common.security.AnontionSecurity;
import com.anontion.common.security.AnontionSecurityECDSA;
import com.anontion.common.security.AnontionSecurityECIES_ECDH;
import com.anontion.models.connection.model.AnontionConnection;
import com.anontion.models.connection.repository.AnontionConnectionRepository;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.anontion.models.account.model.AnontionAccount;
import com.anontion.models.account.repository.AnontionAccountRepository;
import com.anontion.common.dto.response.ResponseGetConnectionBodyDTO;
import com.anontion.common.dto.response.ResponseGetConnectionBroadcastBodyDTO;
import com.anontion.asterisk.repository.AsteriskEndpointRepository;
import com.anontion.common.dto.response.ResponseGetConnectionBroadcastsBodyDTO;

@RestController
public class ConnectionGetController {
    
  @Autowired
  private AnontionConnectionRepository connectionRepository;

  @Autowired
  private AnontionAccountRepository accountRepository;

  @Autowired
  private AsteriskAuthRepository authRepository;
  
  @Autowired
  private AsteriskEndpointRepository endpointRepository;
  
  private Cache<String, Boolean> nonceCache = Caffeine.newBuilder()
      .expireAfterWrite(AnontionConfig._REQUEST_TS_VALIDITY_MARGIN, TimeUnit.SECONDS)
      .build();
  
  @GetMapping(path = "/connection/exists")
  public ResponseEntity<ResponseDTO> isExists(@RequestParam("sipUsername1") String sipUsername1, @RequestParam("sipUsername2") String sipUsername2) {

    _logger.info("/connection/exists with '" + sipUsername1 + "', '" + sipUsername2 + "'");

    if (sipUsername1.isBlank() ||
        sipUsername2.isBlank()) {

      _logger.info("failed param check blank");

      return Responses.getBAD_REQUEST(
          "Failed param check");
    }

    String convertedSipUsername1 = AnontionSecurity.fromSipAddressToSipname(sipUsername1);
    
    String convertedSipUsername2 = AnontionSecurity.fromSipAddressToSipname(sipUsername2);

    if (convertedSipUsername1.isBlank() ||
        convertedSipUsername2.isBlank()) {

      _logger.info("failed param check converted blank");

      return Responses.getBAD_REQUEST(
          "Failed param check");
    }

    if ((!AnontionSecurity.isBase64(convertedSipUsername1) && !AnontionSecurity.isSafeBase64(convertedSipUsername1)) ||
        (!AnontionSecurity.isBase64(convertedSipUsername2) && !AnontionSecurity.isSafeBase64(convertedSipUsername2))) {

      _logger.info("failed param check type");

      return Responses.getBAD_REQUEST(
          "Failed param check");
    }

    String sipEndpointA = AnontionSecurity.encodeToSafeBase64(convertedSipUsername1);

    String sipEndpointB = AnontionSecurity.encodeToSafeBase64(convertedSipUsername2);

    if (!AnontionSecurity.isSafeBase64(sipEndpointA) ||
        !AnontionSecurity.isSafeBase64(sipEndpointB)) {

      _logger.info("failed param check converted");

      return Responses.getBAD_REQUEST(
          "Failed param check");
    }
    
    boolean isALessThanB = sipEndpointA.compareTo(sipEndpointB) < 0;
    
    String lowerEndppint = isALessThanB ? sipEndpointA : sipEndpointB;
    
    String upperEndppint = !isALessThanB ? sipEndpointA : sipEndpointB;
    
    Optional<AnontionConnection> connection0 = connectionRepository.findBySipEndpointAAndSipEndpointB(lowerEndppint, upperEndppint);

    if (connection0.isEmpty()) {

      _logger.info("failed ordered search sipEndpointA '" + sipEndpointA + "', sipEndpointB '" + sipEndpointB + "' = trying broadcast target");

      Long nowTs = AnontionTime.tsN();
      
      Optional<AnontionConnection> broadcast = connectionRepository.findBroadcastBySipEndpoint(sipEndpointB, nowTs);

      if (!broadcast.isEmpty()) {
          
        return isExistsAuthorizedAccount(sipEndpointA);
      }
        
      return Responses.getBAD_REQUEST(
          "failed searches ordered and broadcast");
    }
      
    AnontionConnection connection = connection0.get();
    
    if (connection.getConnectionType().equals("multiple")) {
      
      Optional<AnontionConnection> connection1 = connectionRepository.findMultipleBySipEndpoint(sipEndpointB);
      
      if (connection1.isEmpty()) {

        _logger.info("failed matched ordered search multiple lowerEndppint '" + lowerEndppint + "' upperEndppint '" + upperEndppint + "'");

        return Responses.getBAD_REQUEST(
            "failed searches ordered and multiple");
      }
      
      _logger.info("ok matched ordered search multiple lowerEndppint '" + lowerEndppint + "' upperEndppint '" + upperEndppint + "'");
    
    } else {
      
      _logger.info("ok matched ordered search lowerEndppint '" + lowerEndppint + "' upperEndppint '" + upperEndppint + "'");
    }
    
    return Responses.getOK();    
  }
  
  private ResponseEntity<ResponseDTO> isExistsAuthorizedAccount(String sipUsername) {
    
    String safeSipUsername = AnontionSecurity.encodeToSafeBase64(sipUsername);
    
    String unsafeSipUsername = AnontionSecurity.decodeFromSafeBase64(safeSipUsername);
    
    Optional<AnontionAccount> account0 = accountRepository.findByClientPubAuthorized(unsafeSipUsername);

    if (!account0.isEmpty()) {
      
      return Responses.getOK();
    }

    return Responses.getBAD_REQUEST(
        "failed searches ordered and broadcast");
  }
  
  @GetMapping(path = "/connection/")
  public ResponseEntity<ResponseDTO> getConnection(@RequestParam("sipUsername") String sipUsername,
      @RequestParam("nowTs") Long nowTs, @RequestParam("nonce") String nonce, @RequestParam("signature") String signature) {
    
    if (sipUsername.isBlank() ||
        nonce.isBlank() ||
        //signature.isBlank() ||
        nowTs <= 0) {

      _logger.info("failed params check");

      return Responses.getBAD_REQUEST(
          "Failed params check");
    }
    
    Long diff = nowTs - AnontionTime.tsN();

    if (diff > AnontionConfig._REQUEST_TS_VALIDITY_MAX ||
        diff < AnontionConfig._REQUEST_TS_VALIDITY_MIN) {

      return Responses.getBAD_REQUEST(
          "Invalid parameters time", 
          "bad nowTs"); 
    }
    
    if (nonce.isBlank() || nonceCache.getIfPresent(nonce) != null) {

      return Responses.getBAD_REQUEST("Bad nonce");
    }
    
    nonceCache.put(nonce, true);
    
    String sipEndpoint = AnontionSecurity.encodeToSafeBase64(sipUsername);
    
    if (!AnontionSecurity.isSafeBase64(sipEndpoint)) {

      _logger.info("failed param check converted");

      return Responses.getBAD_REQUEST(
          "Failed param check");
    }
    
    // NYI missing signature check
    
    Optional<AnontionConnection> connection0 = connectionRepository.findByRollSipEndpoint(sipEndpoint); 
    
    if (connection0.isEmpty()) {

      _logger.info("failed search '" + sipEndpoint + "'");

      return Responses.getBAD_REQUEST(
          "failed search for " + sipEndpoint);
    }
        
    AnontionConnection connection = connection0.get();
    
    ResponseGetConnectionBodyDTO body = new ResponseGetConnectionBodyDTO(connection.getSipEndpointA(), 
        connection.getSipEndpointB(), 
        connection.getRollSipEndpoint() != null ? connection.getRollSipEndpoint() : "");
    
    ResponsePostDTO response = new ResponsePostDTO(
        new ResponseHeaderDTO(true, 0, "Ok."), body);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping(path = "/connection/broadcast")
  public ResponseEntity<ResponseDTO> getBroadcasts(@RequestParam("latitude") Double latitude, @RequestParam("longitude") Double longitude, @RequestParam("token") String token, 
      @RequestParam("nowTs") Long nowTs, @RequestParam("nonce") String nonce, @RequestParam("signature") String signature) {

    //_logger.info("broadcasts latitude '" + latitude + "', longitude '" + longitude + "' token = '" + token + "' nonce '" + nonce + "' signature '" + signature + "' nowTs " + nowTs);
    
    _logger.info("broadcasts latitude '" + latitude + "', longitude '" + longitude + "'");

    if (token.isBlank() ||
        nonce.isBlank() ||
        //signature.isBlank() ||
        nowTs <= 0) {

      _logger.info("failed params check blank");

      return Responses.getBAD_REQUEST(
          "Failed params check");
    }
    
    Long diff = nowTs - AnontionTime.tsN();

    if (diff > AnontionConfig._REQUEST_TS_VALIDITY_MAX ||
        diff < AnontionConfig._REQUEST_TS_VALIDITY_MIN) {

      return Responses.getBAD_REQUEST(
          "Invalid parameters time", 
          "bad nowTs"); 
    }
    
    if (nonce.isBlank() || nonceCache.getIfPresent(nonce) != null) {

      return Responses.getBAD_REQUEST("Bad nonce");
    }
    
    nonceCache.put(nonce, true);
    
    // NYI missing signature check
    
    if (!AnontionGPS.isValidGPS(latitude, longitude)) {

      _logger.info("failed param gps check invalid");

      return Responses.getBAD_REQUEST(
          "Failed param gps check invalid");
    }
    
    String unsafeToken = AnontionSecurity.decodeFromSafeBase64(token);
    
    String cleartext = AnontionSecurityECIES_ECDH.decrypt(AnontionSecurityECDSA.root(), unsafeToken);
    
    if (cleartext.isEmpty() || cleartext.isBlank()) {
      
      _logger.info("failed param token invalid");

      return Responses.getBAD_REQUEST(
          "Failed param token invalid");
    }
    
    String[] tokens = cleartext.split("\n");
    
    if (tokens.length != 4 || 
        tokens[0].isEmpty() ||
        tokens[1].isEmpty() ||
        tokens[2].isEmpty() ||
        tokens[3].isEmpty()) {
      
      _logger.info("failed param token wrong size or content");

      return Responses.getBAD_REQUEST(
          "Failed param token wrong size or content");
    }

    String userId = tokens[0];
    
    String password = tokens[1];

    String nonce_DEFUNCT = tokens[2];

    String ts = tokens[3];

    if (!AnontionStrings.isDigits(ts)) {
      
      _logger.info("failed param ts not digits");

      return Responses.getBAD_REQUEST(
          "Failed param ts not digits");
    }
    
    try {
    
      Long timestamp = Long.parseLong(ts);
      
      Long gap = AnontionTime.tsN() - timestamp;
      
      if (gap < AnontionConfig._REQUEST_TS_VALIDITY_MIN || gap > AnontionConfig._REQUEST_TS_VALIDITY_MAX ) {
        
        _logger.info("failed param ts not valid");

        return Responses.getBAD_REQUEST(
            "Failed param ts not valid");
      }
    
    } catch (Exception e) {
      
      _logger.exception(e);
      
      return Responses.getBAD_REQUEST(
          "Exception " + e.getMessage() + "'");      
    }
    
    Optional<AsteriskAuth> auth0 = authRepository.findByUsername(userId);
    
    if (auth0.isEmpty()) {
      
      _logger.info("failed param token lookup failed for username '" + userId + "'");

      return Responses.getBAD_REQUEST(
          "Failed param token lookup failed for username '" + userId + "'");
    }
    
    AsteriskAuth auth = auth0.get();
    
    String md5Password = AnontionSecurity.generateMD5Password(userId, password);
    
    if (!auth.getMd5Cred().equals(md5Password)) {
      
      _logger.info("failed param bad password");

      return Responses.getBAD_REQUEST(
          "Failed param bad password");
    }

    Optional<AsteriskEndpoint> endpoint0 = endpointRepository.findByAuth(auth.getId());
    
    if (endpoint0.isEmpty()) {
      
      _logger.info("failed param endpoint lookup failed");

      return Responses.getBAD_REQUEST(
          "Failed param endpoint lookup failed");
    }

    AsteriskEndpoint endpoint = endpoint0.get();
    
    String id = endpoint.getId();
    
    String unsafeId = AnontionSecurity.decodeFromSafeBase64(AnontionSecurity.encodeToSafeBase64(id));

    Optional<AnontionAccount> account0 = accountRepository.findByClientPubAuthorized(unsafeId);
    
    if (account0.isEmpty()) {
      
      _logger.info("failed param account lookup failed");

      return Responses.getBAD_REQUEST(
          "Failed param account lookup failed");
    }
    
    AnontionAccount account = account0.get();
    
    ECPublicKeyParameters publicKey = 
        AnontionSecurityECDSA.decodeECPublicKeyParametersFromBase64XY(account.getClientPub());

    if (publicKey == null) {

      return Responses.getBAD_REQUEST(
          "Bad pub.", 
          "Pub invalid.");
    }
    
    if (!AnontionSecurityECDSA.checkSignature(nonce, signature, publicKey)) {

      return Responses.getBAD_REQUEST(
          "Bad signature.", 
          "Signature invalid.");
    } 

    AnontionGPSBox box = new AnontionGPSBox(latitude, longitude, AnontionGPS._CONNECTION_SEARCH_CIRCLE);

    List<AnontionConnection> connections = connectionRepository.findByGPSBox(
        box.getLatitudeMin(), box.getLatitudeMax(), box.getLongitudeMin(), box.getLongitudeMax(), AnontionTime.tsN()); 

    List<ResponseGetConnectionBroadcastBodyDTO> broadcasts = new ArrayList<ResponseGetConnectionBroadcastBodyDTO>();
    
    for (AnontionConnection connection: connections) {
      
      double distance = AnontionGPS.getDistance(latitude, longitude, connection.getLatitude(), connection.getLongitude());
      
      if (distance < AnontionGPS._CONNECTION_SEARCH_CIRCLE) {

        ResponseGetConnectionBroadcastBodyDTO broadcast = 
            new ResponseGetConnectionBroadcastBodyDTO(connection.getConnectionId(), connection.getLatitude(), 
                connection.getLongitude(), distance, connection.getSipEndpointA(), connection.getTimeoutTs());
        
        broadcasts.add(broadcast);
      }      
    }
    
    ResponseGetConnectionBroadcastsBodyDTO body = 
        new ResponseGetConnectionBroadcastsBodyDTO(latitude, longitude, AnontionGPS._CONNECTION_SEARCH_CIRCLE, broadcasts); 
    
    ResponsePostDTO response = new ResponsePostDTO(
        new ResponseHeaderDTO(true, 0, "Ok."), body);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }
  
  final private static AnontionLog _logger = new AnontionLog(ConnectionGetController.class.getName());
}


package com.rsi.rvia.rest.client;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.glassfish.jersey.internal.util.Base64;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;


/** Creación y validación de tokens JWT */
public class ManageJWToken
{
	//private static String publicKey = "MIIDbzCCAlegAwIBAgIJAMF3fE59gCNlMA0GCSqGSIb3DQEBCwUAME4xCzAJBgNVBAYTAkVTMQswCQYDVQQIDAJNQTEPMA0GA1UEBwwGTWFkcmlkMSEwHwYDVQQKDBhJbnRlcm5ldCBXaWRnaXRzIFB0eSBMdGQwHhcNMTYwOTIzMTQwMjI4WhcNMTYxMDIzMTQwMjI4WjBOMQswCQYDVQQGEwJFUzELMAkGA1UECAwCTUExDzANBgNVBAcMBk1hZHJpZDEhMB8GA1UECgwYSW50ZXJuZXQgV2lkZ2l0cyBQdHkgTHRkMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzDIXML9VYmdCMZsgSuH3sRWmyeDKU0P9LHhwLxm0QGUmYjANS6W11loY+Wglu/Q8nbkdUzTxplsGUiVghgPlEJWmbaVpxbI6jgATywubbg2n+pxD+c7tw2WWOzTM1/HoKBU5a+T0uFN5gLBcm52KH2V/i16VU/HE/WDOZRWk8awezU+j71Fz8JO0V2hTmF7g1mRJTm4OetDFePnnmSWVJXk4qA+IOuD6yd7owRbW2Ompk3XdY9OkKeiVtaDsTSWlfPPxdTL/z542qUXBWrattBBh/eYpc+Zwy2nMEV6Ee0hi7ztGvw7hbPhCywT4Mf38ZCDXTR9secGdwSgg5GvX7wIDAQABo1AwTjAdBgNVHQ4EFgQUfcjjl4VVTbNE7BLZl+OpDTx/u8gwHwYDVR0jBBgwFoAUfcjjl4VVTbNE7BLZl+OpDTx/u8gwDAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQsFAAOCAQEApToZRc3d7Q9gFnYI1tKq17xEGKyadwMIOs5RxTy6Bbvab2tOYA5ANVJqxP27wzVb0geAipLtsW+MVSQi/KmXpuKZHjRdhUmwH6kFPFO4QsvisSTmy5A6/J3jGIEB8+iB4UDVs4/cX/g+kEwvr0hKtLrMPkP83mySuEBUKBK4am6VpsCbdq8xPdZYu3vSwxK4pjsydSuGolGAU04FeykyKz0gB0Up1ThqdkvToU3SzSg79/KZlOogin3T2vP3dXohFGRkwHjfXg/c4eGTWPIX+2FLosi1iEUSbrI/02OomNTNy1byLF9u2+pBAAW+tO7p6/eSto8Z0edOcSs6ylcQlA==";
	private static String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDMMhcwv1ViZ0IxmyBK4fexFabJ4MpTQ/0seHAvGbRAZSZiMA1LpbXWWhj5aCW79DyduR1TNPGmWwZSJWCGA+UQlaZtpWnFsjqOABPLC5tuDaf6nEP5zu3DZZY7NMzX8egoFTlr5PS4U3mAsFybnYofZX+LXpVT8cT9YM5lFaTxrB7NT6PvUXPwk7RXaFOYXuDWZElObg560MV4+eeZJZUleTioD4g64PrJ3ujBFtbY6amTdd1j06Qp6JW1oOxNJaV88/F1Mv/PnjapRcFatq20EGH95ilz5nDLacwRXoR7SGLvO0a/DuFs+ELLBPgx/fxkINdNH2x5wZ3BKCDka9fvAgMBAAECggEAJeDA1oUXlKps6ADLq7hhFxNaHia5wVpiU2aAAOy2uB9LUHlzbe3uXjXWXiWIomX6/JMDMpUmomJOONzAG/wK+NmZxsshZM/b9s1i3o5npbGY0G8+WM+e4Vkt1gvEV+aC1zSgszgEgVDSwCF2/FLr9UkBsNu5vZminhHgF3Y9Y6rh/f2Qt6J8fVKsmHuigutfs/N9t125VCyPhVh72VrCHLa2sEtZPtZkjhw4eBn89ia87FdZhnb9ZKDEat3qEzN9ZUZuI1DUrCTOhLm1Dy2ycvenm3mY/0ibII73Df61acisEgQCkqG2lBormpZ5sMR8uPdz22Q4EZRoDDYzH1CGKQKBgQD1efOV+tbIcAAIbTO5d0CLzIA3hKFSS1PmbcvWPMwQYcghBOMgeseH65tdmRt33s1SzwyECy7aPLepSw7ZeY3i1QkfSFM03rYAnMdPoFP24QmznNC51Er1jlXc3S/U0JoVFfEP+6gpjGV4EO40mugRKKx2wO32oeMFVtV4AMnjAwKBgQDU8xevlzWz81FHJlzixGc+CWGk8YFq7O0jD/slWRgPoX9dx+XEFcUwJXxzYAc5qNrY91LXTApPwc8A7y+Q9eJVqmJ74rrxGS48SrGpUrkZ6JDp6C+6MRVK0rvg8tLoWBG4nljrw0o4/JXqJnjNq/ijX5YNZMATRppFRBkEdXctpQKBgQCnFZr3OwV26MUc9zd9xe3pOgkLwPsNSLiS36ke6cL8Y5IU536xXODzYysdSlmn/b9D3ipmtjXt1eYtFSnGXTZ2bwsGf/QiyHSZdh0z1a1RHVu9Jb/svNNI0NkgvG0SLeqy73xlpKWRoj5uuxdoNwg+pbC+JBPVC9u6hqBGTkMcgQKBgCk6+/4KZ+J0nOsKuXdKkbbIODiobedXAj8ErBq6GleEbFWrC4igsnI0/9iS4jM+x0i08jwvQ9vXaY4DwZDumqj9eV4FMy7VMkeIKsfe9WnXxp8TTpJdSIMZNDlEqqND0gKN3iefwzIvbZNNaZxgzJ5NNo9XT30r8Vix66oaWD3lAoGAKtgJpQlHUpdt+ahDAlwxsRVISBJcFe+QgAFitBTxiwNCJrmzbMwyycZ5C74Hs+/hKuL6BjBmc4+xCFlqNjXUs5t1Xp2VGyvdF9AJq/GTpZjBYQx5JwVPq8wilrT1zcm6JeotHlhb1hABD/a5Q0hlULs7mEr/qHiVcxW8BeQwcHI=";
			 
	private static PrivateKey forSign;
	private static PublicKey forVerify;
	
	private static String JWTCreator = "RSI";
	private static String JWTAudience = "Caja7";
	private static String JWTSubject = "Asunto del contexto";
	
	private static Logger		pLog	= LoggerFactory.getLogger(ManageJWToken.class);
	private static RsaJsonWebKey rsaJsonWebKey;

	private static boolean charged = false;

	/** Preparar JWTkeys desde clave privada formato pem
	 * 
	 */
	private static void prepareRsaJsonWebKey() throws JoseException, NoSuchAlgorithmException, InvalidKeySpecException, IOException{
		if(charged)
			return;
	   String pemString = privateKey;	
	   byte [] decoded = Base64.decode (pemString.getBytes());	   
	   PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
	   KeyFactory kf = KeyFactory.getInstance("RSA");	
	   PrivateKey pkey = kf.generatePrivate(keySpec);	  
	   
	    RSAPrivateCrtKey privk = (RSAPrivateCrtKey)pkey;
	    RSAPublicKeySpec publicKeySpec = new java.security.spec.RSAPublicKeySpec(privk.getModulus(), privk.getPublicExponent());
	    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	    PublicKey myPublicKey = keyFactory.generatePublic(publicKeySpec);	 
	    
	   forSign = pkey;
	   forVerify = myPublicKey;
	    rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
	    rsaJsonWebKey.setKeyId("k1");	
	    rsaJsonWebKey.setPrivateKey(forSign);
	    
	    charged = true;
	}

	/** Generar un token asociado a los valores de entrada
	 * 
	 * @param fields
	 *           Campos a insertar en la sección Claims del token. 
	 * @return Token generado */
	public static String generateJWT(HashMap<String, String> fields) throws JoseException, NoSuchAlgorithmException, InvalidKeySpecException, IOException{
		 // Cargamos clave privada y generamos clave pública
		 prepareRsaJsonWebKey();

	    JwtClaims claims = new JwtClaims();
	    claims.setIssuer(JWTCreator);  // who creates the token and signs it
	    claims.setAudience(JWTAudience); // to whom the token is intended to be sent
	    claims.setExpirationTimeMinutesInTheFuture(100); // time when the token will expire (10 minutes from now)
	    claims.setGeneratedJwtId(); // a unique identifier for the token
	    claims.setIssuedAtToNow();  // when the token was issued/created (now)
	    claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)
	    claims.setSubject(JWTSubject); // the subject/principal is whom the token is about

	    claims.setClaim("context", (Map<String, String>) fields); 
	    
	    JsonWebSignature jws = new JsonWebSignature();
	    jws.setPayload(claims.toJson());
	    jws.setKey(rsaJsonWebKey.getPrivateKey());	    
	    jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
	    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

	    String jwt = jws.getCompactSerialization();

	    // Now you can do something with the JWT. Like send it to some other party
	    // over the clouds and through the interwebs.
	    pLog.info("Nuevo JWT: " + jwt);	
	    return jwt;
	}
	/** Generar un token asociado a los valores de entrada
	 * 
	 * @param jwt
	 *           Token a validar
	 * @return hashMap con los campos de la sección claims 
	 * @throws MalformedClaimException 
	 * @throws IOException 
	 * @throws JoseException 
	 * @throws InvalidKeySpecException 
	 * @throws NoSuchAlgorithmException */
	public static HashMap<String,String> validateJWT (String jwt) throws MalformedClaimException, NoSuchAlgorithmException, InvalidKeySpecException, JoseException, IOException{
	    // Use JwtConsumerBuilder to construct an appropriate JwtConsumer, which will
	    // be used to validate and process the JWT.
	    // The specific validation requirements for a JWT are context dependent, however,
	    // it typically advisable to require a (reasonable) expiration time, a trusted issuer, and
	    // and audience that identifies your system as the intended recipient.
	    // If the JWT is encrypted too, you need only provide a decryption key or
	    // decryption key resolver to the builder.

		 RsaJsonWebKey rjwk = new RsaJsonWebKey((RSAPublicKey) forVerify);		  
	    JwtConsumer jwtConsumer = new JwtConsumerBuilder()
	            .setRequireExpirationTime() // the JWT must have an expiration time
	            .setMaxFutureValidityInMinutes(300) // but the  expiration time can't be too crazy
	            .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
	            .setRequireSubject() // the JWT must have a subject claim
	            .setExpectedIssuer(JWTCreator) // whom the JWT needs to have been issued by
	            .setExpectedAudience(JWTAudience) // to whom the JWT is intended for
	            .setVerificationKey(rjwk.getKey()) // verify the signature with the public key
	            .build(); // create the JwtConsumer instance
	    try
	    {
	        //  Validate the JWT and process it to the Claims
	        JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt);	        
	        // Si la edad del token esta entre 8 y 10 minutos, hay que renovarlo
	        HashMap claims = JwtClaimsToHashMap(jwtClaims);
           long exp = jwtClaims.getExpirationTime().getValueInMillis() ;
           long iat = jwtClaims.getIssuedAt().getValueInMillis();          
	        if( (exp - iat) < 120 * 1000){	      	  
	      	  String newJWT = generateJWT(claims);
	      	  jwtClaims = jwtConsumer.processToClaims(newJWT);
	      	  claims = JwtClaimsToHashMap(jwtClaims);
	        }	        
	        return claims;
	    }
	    catch (InvalidJwtException e)
	    {
	        // InvalidJwtException will be thrown, if the JWT failed processing or validation in anyway.
	        // Hopefully with meaningful explanations(s) about what went wrong.
	        return null;
	    }  
	}	

	/** Transforma JwtClaims en HashMap
	 * 
	 * @param jwtClaims
	 *           JwtClaims a transformar en HashMap
	 * @return hashMap con los campos de la sección context, que son los que guardan los parámetros a propagar
	 * @throws MalformedClaimException */
	private static HashMap<String,String> JwtClaimsToHashMap(JwtClaims jwtClaims) throws MalformedClaimException{
		HashMap<String,String> context = jwtClaims.getClaimValue("context", HashMap.class);
		return context;
	}
	
}

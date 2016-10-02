package com.ragna.conekta;

import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.util.Time;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import com.mongodb.BasicDBObject;


/**
 * Created by ramsescarbajal on 01/10/16.
 */
public class Utils {
    final static Logger logger = Logger.getLogger(Utils.class);
    private String secretKey;
    String creditCardmatchformat = "([0-9]{11,12})([0-9]{4})";
    String creditCardformat = "[0-9]{15,16}";
    String BUYER_NANE_RE = "[a-zA-Z_áéíóúñ\\s]*";
    String BIN_RE = "[0-9a-zA-Z]*";
    String DATE_RE = "[0-9]{2}/[0-9]{2}";
    String DATE_RE_MATCH = "(([0-9]{2})/([0-9]{2}))";
    String CREDIT_CARD_MARCA = "[1-3]{1}";
    String CREDIT_CARD_SCHEMA = "[1-2]{1}";
    String AMOUNT_RE = "[0-9]+\\.[0-9]{1,3}";

    public Utils(String secretKey){
        this.secretKey = secretKey;

    }

    /*
    * Nombre del tarjeta-habiente
	El número BIN.
	Últimos 4 dígitos de la tarjeta.
	Fecha de expiración.
	Esquema de la tarjeta (crédito/débito).
	Marca de la tarjeta (AMEX, VISA, MC).

    *
    * */
    public void validateTokenRequest(Exchange exchange) {
        Message in = exchange.getIn();
        Map<String, String> mensaje = (Map<String, String>)in.getBody();

        String creditCarNumber = mensaje.get("creditCard");
        if(creditCarNumber == null || !validateCreditCard(creditCarNumber,in)){
            in.setHeader("validData",false);
            logger.info("invalid credit Card");
            return;
        }
        Pattern p = Pattern.compile(creditCardmatchformat);
        Matcher m = p.matcher(creditCarNumber);
        String last4Digits = null;
        if (m.find( )) {
            last4Digits = m.group(2);
        }
        logger.info("last4Digits are :: " + last4Digits);

        String buyerName = mensaje.get("buyerName");
        if(buyerName == null || !validatebuyerName(buyerName,in)){
            in.setHeader("validData",false);
            logger.info("invalid buyerName");
            return;
        }


        String bin = mensaje.get("bin");
        if(bin == null || !validateBin(bin,in)){
            in.setHeader("validData",false);
            logger.info("invalid bin");
            return;
        }

        String fechaExp = mensaje.get("fechaExp");
        if(fechaExp == null || !validateDateExp(fechaExp,in)){
            in.setHeader("validData",false);
            logger.info("invalid date exp");
            return;
        }

        String marcaCardMarca = mensaje.get("marcaCardMarca");
        if(marcaCardMarca == null || !validateCreditCardmarca(marcaCardMarca,in)){
            in.setHeader("validData",false);
            logger.info("invalid marcaCardMarca");
            return;
        }

        String creditCardSchema = mensaje.get("creditCardSchema");
        if(creditCardSchema == null || !validateCreditCardSchema(creditCardSchema,in)){
            in.setHeader("validData",false);
            logger.info("invalid creditCardSchema");
            return;
        }




        Map<String, String> allData = new HashMap<String, String>();
        allData.put("last4Digits",last4Digits);
        allData.put("buyerName",buyerName);
        allData.put("bin",bin);
        allData.put("fechaExp",fechaExp);
        allData.put("marcaCardMarca",marcaCardMarca);
        allData.put("creditCardSchema",creditCardSchema);


        String token = String.valueOf(Math.random());

        allData.put("token",token);

        BasicDBObject encriptedMap = encriptJsonToMongo(allData);




        in.setHeader("validData",true);
        in.setHeader("token",encriptedMap.get("token"));
        logger.info("token :: " +token);
        in.setBody(encriptedMap);


    }


    protected boolean validateCreditCard(String creditCard, Message in){
        if(!creditCard.matches(creditCardformat)){
            logger.info("invalid validateCreditCard method");
            in.setHeader("errorReason","invalid credit number");
            return false;
        }
        return true;
    }


    protected boolean validatebuyerName(String buyerName, Message in){
        if(!buyerName.matches(BUYER_NANE_RE)){
            logger.info("invalid buyer name method");
            in.setHeader("errorReason","invalid buyer name");
            return false;
        }
        return true;
    }

    protected boolean validateBin(String bin, Message in){
        if(!bin.matches(BIN_RE)){
            logger.info("invalid bin");
            in.setHeader("errorReason","invalid bin");
            return false;
        }
        return true;
    }

    protected boolean validateDateExp(String dateExp, Message in){
        if(!dateExp.matches(DATE_RE)){
            logger.info("invalid date exp");
            in.setHeader("errorReason","invalid date exp");
            return false;
        }


        String[] dateInArray = dateExp.split("/");
        Integer month = Integer.valueOf(dateInArray[0]);

        Integer year = Integer.valueOf(dateInArray[1]);
        if(month < 1 || month > 12 ){
            logger.info("invalid month");
            in.setHeader("errorReason","the month most be between 1 and 12");
            return false;
        }


        if(year <= 16 ){
            logger.info("invalid year" + year);
            in.setHeader("errorReason","the year must by great than  or equals 16");
            return false;
        }


        return true;
    }

    protected BasicDBObject encriptJsonToMongo(Map<String, String> buyerData){

        Set<String> keys = buyerData.keySet();
        BasicDBObject newMap = new BasicDBObject();
        for(String key : keys){

            String value = encriptar(buyerData.get(key));
            newMap.put(key, value);


        }



        return newMap;
    }

    protected boolean validateCreditCardmarca(String creditCardSchema, Message in){
        if(!creditCardSchema.matches(CREDIT_CARD_MARCA)){
            logger.info("invalid creditCardMarca");
            in.setHeader("errorReason","invalid creditCardMarca, the this data must by 1 for visa, 2 for masterCard, 3 for amex");
            return false;
        }
        return true;
    }
    protected boolean validateCreditCardSchema(String creditCardSchema, Message in){
        if(!creditCardSchema.matches(CREDIT_CARD_SCHEMA)){
            logger.info("invalid creditCardSchema");
            in.setHeader("errorReason","invalid creditCardSchema, the this data must by 1 for credit, 2 for debit");
            return false;
        }
        return true;
    }




    public void testEncriptar(Exchange exchange) {
        Message in = exchange.getIn();
        String mensaje = (String)in.getBody();
        in.setBody(encriptar(mensaje));

    }
    public void testDesifrar(Exchange exchange) {
        Message in = exchange.getIn();
        String mensaje = (String)in.getBody();
        try {
            in.setBody(desencriptar(mensaje));
        }catch (Exception ex){

        }


    }


    public void validatePurchase(Exchange exchange){
        Message in = exchange.getIn();
        String mensaje = (String)in.getBody();

        if(mensaje == null || mensaje.isEmpty()){
            in.setHeader("validData",false);

            in.setHeader("errorReason","invalid token");
            return;
        }

        if(!validateAmount((String)in.getHeader("amount"),in)){
            in.setHeader("validData",false);

            return;
        }

        in.setHeader("validData",true);
    }

    protected boolean validateAmount(String amount, Message in){
        logger.info("iamount :: " + amount);
        if(!amount.matches(AMOUNT_RE)){
            logger.info("invalid amount");
            in.setHeader("errorReason","invalid amount");
            return false;
        }
        return true;
    }

    protected String encriptar(String cadena) {

        String base64EncryptedString = "";

        try {

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digestOfPassword = md.digest(secretKey.getBytes("utf-8"));
            byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

            SecretKey key = new SecretKeySpec(keyBytes, "DESede");
            Cipher cipher = Cipher.getInstance("DESede");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] plainTextBytes = cadena.getBytes("utf-8");
            byte[] buf = cipher.doFinal(plainTextBytes);
            byte[] base64Bytes = Base64.encodeBase64(buf);
            base64EncryptedString = new String(base64Bytes);

        } catch (Exception ex) {

        }
        return base64EncryptedString;

    }
    protected String desencriptar(String textoEncriptado) throws Exception {


        String base64EncryptedString = "";

        try {
            byte[] message = Base64.decodeBase64(textoEncriptado.getBytes("utf-8"));
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digestOfPassword = md.digest(secretKey.getBytes("utf-8"));
            byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
            SecretKey key = new SecretKeySpec(keyBytes, "DESede");

            Cipher decipher = Cipher.getInstance("DESede");
            decipher.init(Cipher.DECRYPT_MODE, key);

            byte[] plainText = decipher.doFinal(message);

            base64EncryptedString = new String(plainText, "UTF-8");

        } catch (Exception ex) {
        }
        return base64EncryptedString;
    }
}

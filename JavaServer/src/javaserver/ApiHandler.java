package javaserver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.json.simple.*;

import org.bouncycastle.util.io.pem.*;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import org.bouncycastle.openssl.*;
/**
 * This class handles all api input
 * @author Brad Minogue
 */
public class ApiHandler implements HttpHandler{
    @Override
    public void handle(HttpExchange he) throws IOException {
        Headers header = he.getRequestHeaders();
        JSONObject headerValues = new JSONObject();
        Set<Map.Entry<String, List<String>>> params = header.entrySet();

        if(headerValues.containsKey("Client-verify"))
        {
            System.out.println(headerValues.get("Client-verify").toString());

        }
        String response = "";
        try{
            JSONObject obj = JSONHelper.convertToJson(he.getRequestBody());
            if(obj != null)
            {
                response += switchAction(obj).toString();
            }
            else
            {
                obj = new JSONObject();
                obj.put("reason", Definitions.NO_API_INPUT);
                response += obj.toString();
                System.out.println(Definitions.NO_API_INPUT);
            }
        }
        catch(Exception e)
        {
            response = e.toString();
        }
        he.sendResponseHeaders(200, response.length());
        OutputStream oout = he.getResponseBody();
        oout.write(response.getBytes());
        oout.close();
    }
    /**
     * This function switches to the aproriate function based on action
     * @param obj
     * @return
     */
    public JSONObject switchAction(JSONObject obj)
    {
        JSONObject retVal = new JSONObject();
        if(!obj.containsKey("action"))
        {
            retVal.put("success", false);
            retVal.put("reason", "Bad Input");;
            System.out.println(Definitions.BAD_OR_NO_ACTION_INPUT);
            return retVal;
        }
        switch((String)obj.get("action"))
        {
            case "register":
                retVal = runRegister(obj);
                break;
            default:
                retVal.put("successs", false);
                retVal.put("reason", Definitions.BAD_OR_NO_ACTION_INPUT);
                break;
        }
        return retVal;
    }
    /**
     * Run the register action
     * @param obj containing the json data
     * @return success condition and necisary data in json format
     */
    public JSONObject runRegister(JSONObject obj)
    {
        JSONObject retVal = new JSONObject();
        retVal.put("success", false);
        boolean flag = obj.containsKey("CN") && obj.containsKey("csr");
        if(!flag)
        {
            retVal.put("reason", "Bad Input");
            System.out.println(Definitions.BAD_CSR_CN_API_INPUT);
            return retVal;
        }
        String userName = (String)obj.get("CN");
        convertToAlpha(userName);
        try
        {/*
            String[] commandList = {"openssl", "ca", "-keyfile",
                "/etc/ssl/ca/ca.key", "-batch", "-cert", "/etc/ssl/ca/ca.crt",
            "-extensions", "usr_cert", "-notext", "-md", "sha256", "-in",
            "/dev/stdin", "-subj",
            "/countryName=US/stateOrProvinceName=Washington/localityName="
                    +"Bothell/organizationName=JustChat/JustChat "
                    +"Enterprises/commonName="+ userName};
            Process command = new ProcessBuilder(commandList).start();
            PrintWriter pw = new PrintWriter(command.getOutputStream());
            pw.print((String)obj.get("csr"));
            InputStreamReader isr = new InputStreamReader(command.getErrorStream());
                BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
              System.out.println("> " + line);
            }
            try
            {
                command.waitFor();
            }
            catch(Exception e)
            {
                System.out.println("failed to wait for openssl");
            }
            int extValue = -999;

            try{
                extValue = command.exitValue();
            }
            catch(Exception e)
            {
                System.out.println("error grabing exit code");
            }
            if(extValue == 0)
            {*/
            X509Certificate cert = genKey(userName);
            retVal.remove("success");
            retVal.put("success", true);
            retVal.put("cert", cert);
            retVal.put("CN", userName);
            System.out.println("Signed cert for: " + userName);

        }
        catch(Exception e)
        {
            retVal.remove("success");
            System.out.println("unkown error" + e.toString());
            retVal.put("reason", "Internal Failure, Try Again Later");
            retVal.put("CN", userName);
        }
        return retVal;
    }
    private String convertToAlpha(String test)
    {
        String retVal = "";
        for(int i = 0; i < test.length(); i++)
        {
            if(isCharAlphaNum(test.charAt(i)))
            {
                retVal+=test.charAt(i);
            }
        }
        return retVal;
    }
    private boolean isCharAlphaNum(char test)
    {
        return Character.isLetter(test) || Character.isDigit(test);
    }
    /**
     * Prints out response from executing command
     * @param inputSource response from executing command
     * @throws IOException
     */
    private String outPutProccessOutput(Process inputSource)
    {
            String retVal = "";
        try {
            retVal = "";
            BufferedReader stdInput = new BufferedReader(
                    new InputStreamReader(inputSource.getInputStream()));
            BufferedReader stdError = new BufferedReader(
                    new InputStreamReader(inputSource.getErrorStream()));
            String tempVal = "";
            while((tempVal = stdInput.readLine()) != null)
            {
                retVal +=tempVal;
            }
            tempVal = "";
            while((tempVal = stdError.readLine()) != null)
            {
                retVal +=tempVal;
            }
            return retVal;
        } catch (IOException ex) {
            retVal = ex.toString();
        }
        return retVal;
    }
    private X509Certificate genKey(String userName) throws Exception
    {
        Security.addProvider(new BouncyCastleProvider());
        KeyPair keyPair = readKeyPair(new File("/etc/ssl/ca/ca.key"));
        X500NameBuilder x500NameBld = new X500NameBuilder(BCStyle.INSTANCE);
        x500NameBld.addRDN(BCStyle.C, "US");
        x500NameBld.addRDN(BCStyle.O, "JustChat Enterprises");
        X500Name subject = x500NameBld.build();
        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
        X500Principal dnName = new X500Principal(userName);
        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setSubjectDN(dnName);
        certGen.setIssuerDN(X509Name.getInstance(subject));
        certGen.setNotBefore(new Date());
        certGen.setPublicKey(keyPair.getPublic());
        certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");
        return certGen.generate(keyPair.getPrivate(), "BC");
    }
    private static KeyPair readKeyPair(File privateKey) throws IOException {
        FileReader fileReader = new FileReader(privateKey);
        PEMParser r = new PEMParser(fileReader);
        try {
            return (KeyPair) r.readObject();
        } catch (IOException ex) {
            throw new IOException("The private key could not be decrypted", ex);
        } finally {
            r.close();
            fileReader.close();
        }
    }
}

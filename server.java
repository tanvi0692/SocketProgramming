import java.io.BufferedReader;
	import java.io.FileInputStream;
	import java.io.InputStream;
	import java.io.InputStreamReader;
	import java.net.DatagramPacket;
	import java.net.DatagramSocket;
	import java.net.InetAddress;
public class server{
	public static boolean Syntaxcheck(String syntax,String id, String mid)
        {
		 int id1=Integer.parseInt(id);
		 int mid1=Integer.parseInt(mid);
		 
		 String s1="<request>"+ "<id>"+"</id>"+ "<measurement>"+ "</measurement>"+ "</request>"; // default template
               
		 if(s1.matches(syntax)){
			 if(id1>=0&&id1<=65535){
				 if(mid1>=0&&mid1<=65535){  //check range and unsigned
					 return true;}
			 }
			 
		 }

		 return false;
        }

	public static String checksumcal(String str)
        {
		int s=0,i=0,c=7919,d=65536;
		int buff,x,index;
		String str1;
		if(str.length()%2==0){
			str1=str;
		}
		else{
			str1=str+'0';
		}
		for(i=0;i<str1.length()-1;i++){
			//buff=((int)str1.charAt(i)*10)+(int)str1.charAt(i+1);
			String buffstr=Integer.toHexString((int)str1.charAt(i))+Integer.toHexString((int)str1.charAt(i+1));
			//System.out.println(buffstr);
			buff=Integer.parseInt(buffstr, 16);
			
			s=Integer.parseInt(Integer.toHexString(s), 16);
			index=s^buff;
			s=(c*index)%d;
		}
		return Integer.toString(s);
			
        }
	

public static void main(String[] args) throws Exception
{
		    String line;
	            DatagramSocket s3=new DatagramSocket(2520); 
                    int i;
			
			while(true)
	                {
                        
			byte[] rcvd=new byte[1024];
			byte[] responseMessageBytes=new byte[1024];
                        
			DatagramPacket s1=new DatagramPacket( rcvd,rcvd.length);
			
                             String temperature = "0";
                             String code = "0";
                             String mid = "0";
				s3.receive(s1);
				System.out.println("Request message received from the client");
				String receivedData = new String(rcvd);
                                receivedData= receivedData.trim();
                               // System.out.println(receivedData);
				String[] id1 = receivedData.split("</id>");
                                String temp = id1[0];
                                String[] id2 = temp.split("<id>");
                                String id = id2[1]; // extract request ID
	                        //verifyChecksum(receiveData);
	                        
	                        receivedData=receivedData.trim();
	                        String[] cs1 = receivedData.split("</request>");
	                        String checksum = cs1[1];
                                 String  verifychecksum=checksumcal(cs1[0]+"</request>");
                                 //System.out.println("checksumvalue on the server :"+ verifychecksum +" : " + checksum);
	                   if(!checksum.equals(verifychecksum))
	                        {code = "1";}
	                   else{
	                        String[] mid1 = receivedData.split("</measurement>");                      
	                        String temp1 = mid1[0];
	                        String[] mid2 = temp1.split("<measurement>");
	                        mid = mid2[1];
	                        String syntax=receivedData;
                                syntax=syntax.replaceAll(cs1[1], "");
	                        syntax=syntax.replaceAll(mid, "");
	                        syntax=syntax.replaceAll(id, ""); //strip string of numerical substrings for syntax comparison
                                
	                        if(!Syntaxcheck(syntax,id,mid)){code="2";}
	                        else{
                                        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("data.txt")));
	                        	while ((line = br.readLine()) != null)
		                	    { String[] data = line.split("\t");
                                              
		                              if(mid.equals(data[0]))
		                              {temperature = data[1];  // read file to get data
                                               }        
		                            }  
		                        if(temperature=="0")
		                        {code ="3";}
		                        
	                        }
	                        
	                    }
                           
                           String response="<response>"+ "<id>"+id+ "</id>"+"<code>"+code+"</code>"+ "</response>"; // error message
	                        
                            if(code.equals("0"))
                                    { response="<response>"+ "<id>"+id+ "</id> "+"<code>"+code+"</code>"+ "<measurement>"+mid+ "</measurement>"+"<value>"+ temperature + "</value>"+ "</response>";
		                            }
                                
	                        String responseMessage= response+checksumcal(response);  
	                        responseMessageBytes=responseMessage.getBytes();
	                        InetAddress Address = s1.getAddress();
	                        int Port = s1.getPort();
	                        DatagramPacket s2=new DatagramPacket( responseMessageBytes,responseMessageBytes.length,Address,Port );
	                        s3.send(s2);
                                System.out.println("Response message sent to the client");
	                        
			}
	                
	                
	}

}

import java.net.*;
import java.io.*;
import java.util.Scanner;
public class client{
	public static String checksumcal(String str){
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
			String buffstr=Integer.toHexString((int)str1.charAt(i))+Integer.toHexString((int)str1.charAt(i+1)); //usigned integer
			buff=Integer.parseInt(buffstr, 16);
			
			s=Integer.parseInt(Integer.toHexString(s), 16);
			index=s^buff;
			s=(c*index)%d;
		}
		return Integer.toString(s);
			
		}
	
	

	public static void main(String[] args)throws Exception {
		
            while(true)  // while #1 perpetual execution
            {
                int mid,i=0,j=0;
                int ret=0;
                int to =1000;
		int id = (int )(Math.random() * 100 + 1);
                
		int[] midarray={20316,7431,55969,24370,62125,42143,63353,14795,34689,20172,5157,20330,16922,6573,62709,26977,6843,58071,235,13596,19210,54798,64801,8279,60101,58292,18400,32853,13245,47533,45499,2851,20715,10116,33990,2353,51820,18926,36145,61178,17277,11749,46320,40180,2529,56780,4527,14216,38441,40534,62982,28437,59583,36926,17765,26747,20976,27064,32569,16741,51191,19746,5733,12704,35268,60182,3805,41868,46325,21989,1409,45468,30634,23197,22503,26857,13267,48589,60105,38117,65523,63799,16047,58039,58206,12915,47351,63631,8701,38751,36296,11647,20671,29850,31919,17121,20489,10624,17731,11855};
		mid=midarray[id-1];
		
		
		String msgs="<request>"+"<id>"+id+"</id>"+"<measurement>"+mid+"</measurement>"+"</request>";
		String requestMessage=msgs+checksumcal(msgs);
		//System.out.println(requestMessage);
		
		byte[] requestMessageInBytes=requestMessage.getBytes();//to be constructed per msg
		byte[] responseMessage=new byte[1024];

		InetAddress Address = InetAddress.getLocalHost();	
		DatagramPacket s1=new DatagramPacket( requestMessageInBytes,  requestMessageInBytes.length,  Address,  2520);
		DatagramPacket s3=new DatagramPacket( responseMessage, responseMessage.length);
		DatagramSocket s2=new DatagramSocket();
		
              
                while(true){ // to construct reTx as required while #2 
                                //ret=0;
                                //i=0;
                                s2.send(s1);
                                System.out.println("Request message sent to the server");
                                s2.setSoTimeout(to);
                     
			    try{
                                
				s2.receive(s3);
				if(responseMessage[1]!=0)
				{
					ret=0;
					i=0;
				System.out.println("Response message received from the server");
				String responseMessageString=new String(responseMessage);
                                responseMessageString = responseMessageString.trim();   
				String[] checksumValue=responseMessageString.split("</response>");
                                responseMessageString = responseMessageString.replace(checksumValue[1],"");
                                //System.out.println(responseMessageString);
                                             
                                
                                //verify checksum
				String c1=checksumcal(responseMessageString);
                               // System.out.println("checksum :" + checksumValue[1]+ " and "+ c1);  
				
                                    if(c1.equals(checksumValue[1]))
                                    {  	//System.out.println("checksum is correct");
                                        String[] temp= responseMessageString.split("</code>");  
                                        String[] code=temp[0].split("<code>");
                                        int codeValue=Integer.parseInt(code[1]);
                                        
                                        switch(codeValue)
                                        {
                                            case 0: 
                                                 String[] temp1 = responseMessageString.split("</value>"); 
                                                 String[] temperatureValue=temp1[0].split("<value>");
                                                 System.out.println("The temperature is: "+temperatureValue[1]);
                                                 //System.out.println("request ID"+id);
                                                 break;
                                            case 1: System.out.println("error. integrity check failure");
                                                    Scanner in = new Scanner(System.in);
                                                    System.out.println("Do you want to send the request again(Y/N): ");
                                                    String userResponse = in.next();
                                                    if(userResponse.equals("Y")) 
                                                    {ret++;}
                                                   // break;
                                                    else{break;}
                                            case 2: System.out.println("error. syntax of reuest is incorrect");
                                                    break;
                                            case 3: System.out.println("error. non existent measurement id.");
                                                    break;
                                        }
                                       }
				
                                     //else{s2.send(s1);}
				
				}
              	
		            }
			
                            catch(IOException e)
				{
				 if(i==3)
                                     {System.out.println(" COMMUNICATION FAILURE. timeout reached");
                                      j=1;
                                     break;
                                     }
			         else{  to=to*2;
                                        ret++;  //retransmission parameters
                                        
					i+=1;}
				}
			 // System.out.println(i); // TIMEOUT LIMIT COUNTER
			 // System.out.println("Timeout value "+to); // TIMEOUT VALUE
                            if(ret==0){break;}// next request
                }
                if(j==1){break;}//terminate
            }
	}
}
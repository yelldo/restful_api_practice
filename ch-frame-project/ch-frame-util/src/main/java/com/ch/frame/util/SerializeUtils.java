package com.ch.frame.util;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.*;

public class SerializeUtils {
	public static Object hessianDeserialize(byte[] by) throws IOException{  
	    if(by==null) throw new NullPointerException();  
	    return hessianDeserialize(new ByteArrayInputStream(by));  
	}
	
	public static Object hessianDeserialize(InputStream input) throws IOException{  
	    return new HessianInput(input).readObject();
	}
	
	public static byte[] hessianSerialize(Object obj) throws IOException{  
	    if(obj==null) throw new NullPointerException();  
	      
	    ByteArrayOutputStream os = new ByteArrayOutputStream();  
	    HessianOutput ho = new HessianOutput(os);
	    ho.writeObject(obj);  
	    return os.toByteArray();  
	}
	public static void hessianSerialize(Object obj, OutputStream out) throws IOException{  
	    HessianOutput ho = new HessianOutput(out);
	    ho.writeObject(obj);
	}
	
	public static byte[] javaSerialize(Object obj) throws Exception {  
	    if(obj==null) throw new NullPointerException();  
	      
	    ByteArrayOutputStream os = new ByteArrayOutputStream();  
	    ObjectOutputStream out = new ObjectOutputStream(os);  
	    out.writeObject(obj);  
	    return os.toByteArray();  
	}  
	  
	public static Object javaDeserialize(byte[] by) throws Exception {  
	    if(by==null) throw new NullPointerException();  
	      
	    ByteArrayInputStream is = new ByteArrayInputStream(by);  
	    ObjectInputStream in = new ObjectInputStream(is);  
	    return in.readObject();  
	}  
}
package cn.ict.zyq.bestConf.cluster.InterfaceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.ho.yaml.Yaml;

import ch.ethz.ssh2.Connection;
import cn.ict.zyq.bestConf.cluster.Interface.ConfigReadin;
import cn.ict.zyq.bestConf.cluster.Utils.PropertiesUtil;

public class MySQLConfigReadin implements ConfigReadin {
	
	private Connection connection;
	private String server;
	private String username;
	private String password;
	private String remotePath;
	private String localPath;
	private String filepath;
	
	@Override
	public void initial(String server, String username, String password, String localPath, String remotePath) {
		// TODO Auto-generated method stub
		this.server = server;
		this.username = username;
		this.password = password;
		this.localPath = localPath;
		this.remotePath = remotePath;
		filepath = localPath + "/my_default.cnf";
	}
	public Connection getConnection(){
		try{
			connection = new Connection(server);
			connection.connect();
			boolean isAuthenticated = connection.authenticateWithPassword(username, password);
			if (isAuthenticated == false) {
				throw new IOException("Authentication failed...");
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		return connection;
	}
    
    public void closeConnection() {
		try {
			if (connection.connect() != null) {
				connection.close();
			}
		} catch (IOException e) {

		} finally {
			connection.close();
		}
	}
	@Override
	public void downLoadConfigFile(String fileName) {
		// TODO Auto-generated method stub
		
	}
	public HashMap loadFileToHashMap(String filePath) {
		HashMap hashmap = null; 
		try {
			 Properties pps = PropertiesUtil.GetAllProperties(filePath);
			 hashmap = new HashMap((Map) pps); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hashmap;
	}
	public void modify(HashMap toModify, HashMap updatedValues) {
		boolean flag;
		Iterator itrUpdated = updatedValues.entrySet().iterator();
		while(itrUpdated.hasNext()){
			Map.Entry entUpdated = (Map.Entry)itrUpdated.next();
			Object tempObj = toModify.get(entUpdated.getKey().toString());
			if(tempObj!=null){
				if (!entUpdated.getValue().toString().equals("NaN")) {
					if (Double.parseDouble(entUpdated.getValue().toString()) < 1.0 && Double.parseDouble(entUpdated.getValue().toString()) != 0.0) {
						toModify.put(entUpdated.getKey().toString(), entUpdated.getValue().toString());
					} else if(Double.parseDouble(entUpdated.getValue().toString()) == 0.0){
						 		toModify.put(entUpdated.getKey().toString(), Integer.parseInt("0"));
					} else {
							toModify.put(entUpdated.getKey().toString(), (int)Math.floor(Double.parseDouble(entUpdated.getValue().toString())));
						 }
				}
			}else
				System.out.println(entUpdated.getKey().toString() + "Doesn't exit in original list��");
		}
	}
	@Override
	public HashMap modifyConfigFile(HashMap hm, String filepath) {
		HashMap ori = loadFileToHashMap(filepath);
		modify(ori, hm);
		return ori;
	}

	@Override
	public HashMap modifyConfigFile(HashMap hm) {
		HashMap ori = loadFileToHashMap(filepath);
		modify(ori, hm);
		return ori;
	}
	
}
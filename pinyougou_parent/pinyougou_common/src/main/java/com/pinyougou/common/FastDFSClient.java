package com.pinyougou.common;

import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

public class FastDFSClient {

	private TrackerClient trackerClient = null;
	private TrackerServer trackerServer = null;
	private StorageServer storageServer = null;
	private StorageClient1 storageClient = null;
	
	public FastDFSClient(String conf) throws Exception {
		if (conf.contains("classpath:")) {
			conf = conf.replace("classpath:", this.getClass().getResource("/").getPath());
		}
		ClientGlobal.init(conf);
		trackerClient = new TrackerClient();
		trackerServer = trackerClient.getConnection();
		storageServer = null;
		storageClient = new StorageClient1(trackerServer, storageServer);
	}
	
	/**
	 * 上传文件方法
	 * <p>Title: uploadFile</p>
	 * <p>Description: </p>
	 * @param fileName 文件全路径
	 * @param extName 文件扩展名，不包含（.）
	 * @param metas 文件扩展信息
	 * @return
	 * @throws Exception
	 */
	public String uploadFile(String fileName, String extName, NameValuePair[] metas) throws Exception {
		String result = storageClient.upload_file1(fileName, extName, metas);
		return result;
	}
	
	public String uploadFile(String fileName) throws Exception {
		return uploadFile(fileName, null, null);
	}

	public String uploadFile(String fileName, String extName) throws Exception {
		return uploadFile(fileName, extName, null);
	}
	
	/**
	 * 上传文件方法
	 * <p>Title: uploadFile</p>
	 * <p>Description: </p>
	 * @param fileContent 文件的内容，字节数组
	 * @param extName 文件扩展名
	 * @param metas 文件扩展信息
	 * @return
	 * @throws Exception
	 */
	public String uploadFile(byte[] fileContent, String extName, NameValuePair[] metas) throws Exception {
		
		String result = storageClient.upload_file1(fileContent, extName, metas);
		return result;
	}
	
	public String uploadFile(byte[] fileContent) throws Exception {
		return uploadFile(fileContent, null, null);
	}
	//

	/**上传文件方法
	 * 动态获取Storage的ip地址:
	 * @param fileContent 文件的内容，字节数组
	 * @param extName 文件扩展名
	 * @return 							返回值是storageClient上的文件存储位置，
	 * @throws Exception
	 */
	public String uploadFile(byte[] fileContent, String extName) throws Exception {
		/*//获取storageClient上的文件上传的存储位置，多个文件上传
		String[] strings = storageClient.upload_file(fileContent, extName, null);
		//客户端上传文件后存储服务器将文件 ID 返回给客户端，此文件 ID 用于以后访问该文 件的索引信息。
		// 文件索引信息包括：组名，虚拟磁盘路径，数据两级目录，文件名。
		//获取storage上的文件信息
		if(strings!=null && strings.length>0){
			FileInfo file_info = storageClient.get_file_info(strings[0], strings[1]);//strings[0]是组名，strings[1]是虚拟磁盘路径
			//获取ip地址
			String sourceIpAddr = file_info.getSourceIpAddr();
			//将获取到的ip和文件索引 信息拼接为一个URL路径，并返回
			StringBuilder sb =new StringBuilder();
			sb.append("http://").append(sourceIpAddr).append("/").append(strings[0]).append("/").append(strings[1]);
			return toString();
		}

		return null;*/


		return uploadFile(fileContent, extName, null);



	}
}

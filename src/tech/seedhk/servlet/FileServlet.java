package tech.seedhk.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tech.seedhk.utils.FileUtils;

public class FileServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		String param=req.getParameter("param");
		StringBuilder sb=new StringBuilder();
		if(FileUtils.isEmpty(param)){
			String[] strs=FileUtils.listRoots();
			if(strs.length>0){
				for(String s:strs){
					sb.append("<a href=\"/FileManager/FileServlet?param="+s+"\">磁盘"+s+"</a><br/>");
				}
			}
			//System.out.println(sb.toString());
			resp=setHtml("根目录", sb.toString(), resp);
		}else{
			List<Map<String, String>> list = FileUtils.listDirectory(param);
			sb.append("<a href=\"/FileManager/FileServlet?param="+FileUtils.getParent(param)+"\">回到上级</a><br/>");
			sb.append("<a>"+param+"</a><br/>");
			for(Map m:list){
				if("1".equals(m.get("type"))){
					//是目录
					sb.append("<a href=\"/FileManager/FileServlet?param="+m.get("path")+"\">|-"+m.get("path")+"</a><br/>");
				}else{
					sb.append("<a>|-"+m.get("path")+"</a><br/>");
				}
			}
			resp=setHtml(param, sb.toString(), resp);
		}
		
		
		//resp.sendRedirect("file.jsp");
	}
	
	
	public static HttpServletResponse setHtml(String head,String body,HttpServletResponse response){
		 try {
			response.setContentType("text/html;charset=utf-8");  
			response.getWriter().println("<html>");  
	        response.getWriter().println("<head>");     
	        response.getWriter().println("<title>"+head+"</title>");      
	        response.getWriter().println("</head>");    
	        response.getWriter().println("<body>");     
			response.getWriter().println(body);
	        response.getWriter().println("</body>");    
	        response.getWriter().println("</html>"); 
	
		 } catch (IOException e) {
				e.printStackTrace();
			}    
		return response;
	}

}
